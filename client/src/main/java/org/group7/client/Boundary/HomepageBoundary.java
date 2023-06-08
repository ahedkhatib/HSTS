package org.group7.client.Boundary;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.group7.client.App;
import org.group7.client.Control.Controller;
import org.group7.client.Control.HomepageController;
import org.group7.entities.User;

import java.io.IOException;

public class HomepageBoundary extends Boundary {

    private HomepageController controller;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Text titleText;

    @FXML
    private AnchorPane mainPage;

    @FXML
    private Pane transparentPane;

    @FXML
    private Rectangle maskRectangle;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button menuBtn;

    @FXML
    private Button newExamBtn;

    @FXML
    private Button setExecutableBtn;

    @FXML
    private Button newTimeRequestBtn;

    @FXML
    private Button newQuestionBtn;

    @FXML
    private Button prinCourseReportsBtn;

    @FXML
    private Button prinShowData;

    @FXML
    private Button prinStudentReportsBtn;

    @FXML
    private Button prinTeacherReportsBtn;

    @FXML
    private Button principalReqBtn;

    @FXML
    private AnchorPane sidePanel;

    @FXML
    private Button startExamBtn;

    @FXML
    private Button studentReportsBtn;

    @FXML
    private Button teacherReportsBtn;

    @FXML
    private Button editExamBtn;

    @FXML
    private Button approveResultBtn;

    @FXML
    private Button editQuestionBtn;

    @FXML
    private StackPane buttonContainer;

    @FXML
    private VBox studentButtons;

    @FXML
    private VBox teacherButtons;

    @FXML
    private VBox prinButtons;

    @FXML
    private AnchorPane topPane;

    private FXMLLoader fxmlLoader;

    @FXML
    public void goToPage(ActionEvent event) throws IOException {
        toggleMenu(event);

        Button pressed = (Button) event.getSource();

        Boundary boundary = fxmlLoader.getController();

        if (boundary != null) {
            Controller control = boundary.getController();
            control.unregisterController();
        }

        if (pressed == newTimeRequestBtn) {
            loadPage("timeRequest");
            titleText.setText("Extra Time Request");
        } else if (pressed == principalReqBtn) {
            loadPage("checkTimeRequests");
            titleText.setText("Extra Time Requests");
        } else if (pressed == setExecutableBtn) {
            loadPage("createExecutable");
            titleText.setText("Create Executable");
        } else if (pressed == startExamBtn) {
            loadPage("startExam");
            titleText.setText("Start Exam");
        } else if (pressed == newExamBtn) {
            loadPage("createExam");
            titleText.setText("New Exam");
        } else if (pressed == newQuestionBtn) {
            loadPage("newQuestion");
            titleText.setText("Add Question");
        } else if (pressed == studentReportsBtn) {
            loadPage("studentReports");
            titleText.setText("Your Exams");
        } else if (pressed == teacherReportsBtn) {
            loadPage("teacherReports");
            titleText.setText("Show Reports");
        } else if (pressed == prinCourseReportsBtn) {
            loadPage("prinCourseReport");
            titleText.setText("Course Reports");
        } else if (pressed == prinTeacherReportsBtn) {
            loadPage("prinTeacherReport");
            titleText.setText("Teacher Reports");
        } else if (pressed == prinShowData) {
            loadPage("prinShowData");
            titleText.setText("HSTS Data");
        } else if (pressed == prinStudentReportsBtn) {
            loadPage("prinStudentReports");
            titleText.setText("Student Reports");
        } else if (pressed == editExamBtn) {
            loadPage("editExam");
            titleText.setText("Edit Exam");
        } else if (pressed == approveResultBtn) {
            loadPage("teacherApproval");
            titleText.setText("Results Approval");
        } else if (pressed == editQuestionBtn) {
            loadPage("editQuestion");
            titleText.setText("Edit Question");
        } else {
            loadPage("bye");
            titleText.setText("bye");
        }
    }

    @Override
    public HomepageController getController(){
        return controller;
    }

    @FXML
    void logout(ActionEvent event) {

        Boundary boundary = fxmlLoader.getController();

        if (boundary != null) {
            Controller control = boundary.getController();
            control.unregisterController();
        }

        controller.logout();
        App.switchScreen("login");
    }

    @FXML
    void toggleMenu(ActionEvent event) {

        if (!sidePanel.isDisable()) {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.1));
            slide.setNode(sidePanel);

            slide.setToX(-220.0);
            slide.play();

            sidePanel.setDisable(true);
            mainPage.setDisable(false);

            transparentPane.setDisable(false);
            transparentPane.setVisible(false);
            maskRectangle.setVisible(false);
            maskRectangle.setDisable(false);

            slide.setOnFinished((ActionEvent e) -> {
                sidePanel.setVisible(false);
                borderPane.setLeft(null);
            });

        } else {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.1));
            slide.setNode(sidePanel);

            slide.setToX(0.0);

            slide.play();

            mainPage.setDisable(true);

            borderPane.setLeft(sidePanel);
            sidePanel.setVisible(true);
            sidePanel.setDisable(false);

            transparentPane.setDisable(true);
            transparentPane.setVisible(true);
            maskRectangle.setVisible(true);
            maskRectangle.setDisable(true);
        }
    }

    public void loadPage(String path) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource(path + ".fxml"));
        if (mainPage.getChildren().size() == 0)
            mainPage.getChildren().add(fxmlLoader.load());
        else
            mainPage.getChildren().set(0, fxmlLoader.load());
    }

    @FXML
    public void initialize() throws IOException {

        controller = new HomepageController(this);
        super.setController(controller);

        sidePanel.setTranslateX(-220.0);

        sidePanel.setVisible(false);
        sidePanel.setDisable(true);

        borderPane.setLeft(null);

        User user = controller.getUser();

        // Setup side panel
        switch (user.getType()) {
            case 1 -> {
                studentButtons.setVisible(true);
                teacherButtons.setVisible(false);
                prinButtons.setVisible(false);
                StackPane.setMargin(studentButtons, null);
                buttonContainer.getChildren().setAll(studentButtons);
            }
            case 2 -> {
                studentButtons.setVisible(false);
                teacherButtons.setVisible(true);
                prinButtons.setVisible(false);
                StackPane.setMargin(teacherButtons, null);
                buttonContainer.getChildren().setAll(teacherButtons);
            }
            case 3 -> {
                studentButtons.setVisible(false);
                teacherButtons.setVisible(false);
                prinButtons.setVisible(true);
                StackPane.setMargin(prinButtons, null);
                buttonContainer.getChildren().setAll(prinButtons);
            }
        }

        // Setup Welcome Page
        titleText.setText("Welcome " + user.getFirstName() + " " + user.getLastName());

        maskRectangle.widthProperty().bind(mainPage.widthProperty());
        maskRectangle.heightProperty().bind(mainPage.heightProperty());

        transparentPane.prefWidthProperty().bind(mainPage.widthProperty());
        transparentPane.prefHeightProperty().bind(mainPage.heightProperty());

        loadPage("welcome");
    }


}
