package org.group7.client.Boundary;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.client.Control.CheckTimeRequestsController;
import org.group7.entities.ExecutableExam;
import org.group7.entities.ExtraTime;

import java.util.Comparator;

public class CheckTimeRequestsBoundary extends Boundary {

    @FXML
    private ListView<ExtraTime> requestList;

    @FXML
    private AnchorPane listAP;

    @FXML
    private AnchorPane requestAP;

    @FXML
    private Button approveBtn;

    @FXML
    private Button backBtn;

    @FXML
    private TextArea commentText;

    @FXML
    private Button denyBtn;

    @FXML
    private Text titleText;

    @FXML
    private AnchorPane emptyAP;

    @FXML
    private Text emptyText;

    private CheckTimeRequestsController controller;

    private ExtraTime activeReq;

    private ExecutableExam activeExam;

    @Override
    public CheckTimeRequestsController getController() {
        return controller;
    }

    public AnchorPane getEmptyAP() {
        return this.emptyAP;
    }

    public AnchorPane getListAP() {
        return this.listAP;
    }

    public ExtraTime getActiveReq() {
        return activeReq;
    }

    public ExecutableExam getActiveExam() {
        return activeExam;
    }

    @FXML
    public void backToList(ActionEvent event) {

        controller.getAllRequests("#GetAllTimeRequests");

        activeReq = null;
        activeExam = null;

        requestAP.setDisable(true);
        requestAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);
    }

    @FXML
    public void approveReq(ActionEvent event) {
        controller.updateRequest("approve", activeReq);
        backToList(event);
    }

    @FXML
    public void denyReq(ActionEvent event) {
        controller.updateRequest("deny", activeReq);
        backToList(event);
    }

    public ListView<ExtraTime> getRequestList() {
        return requestList;
    }

    @FXML
    public void initialize() {
        controller = new CheckTimeRequestsController(this);
        super.setController(controller);

        requestAP.setDisable(true);
        requestAP.setVisible(false);

        requestList.setCellFactory(param -> new ListCell<ExtraTime>() {
            @Override
            public void updateItem(ExtraTime req, boolean empty) {
                super.updateItem(req, empty);

                if (empty || req == null) {
                    setStyle("-fx-background-color: transparent;");
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    String teacherMsg = req.getTeacherMessage();

                    Text reqText = new Text((getIndex() + 1) + ": " + teacherMsg.substring(0,
                            Math.min(teacherMsg.length(), 20)) + "...");
                    reqText.setFont(Font.font("Arial", 24));
                    Text arrow = new Text(">");
                    arrow.setFont(Font.font("Arial", 24));

                    hbox.setPrefHeight(50);
                    hbox.getStyleClass().add("list-view-item");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    hbox.getChildren().addAll(reqText, spacer, arrow);

                    if (req.isStatus()) {
                        hbox.getStyleClass().add("list-view-item-selected");
                        getStyleClass().add("list-view-item-selected");
                        setDisable(true);
                        reqText.setStrikethrough(true);
                    }

                    setGraphic(hbox);
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);

                if (selected && !isEmpty()) {
                    activeReq = getItem();

                    activeExam = activeReq.getExam();

                    Platform.runLater(()->{
                        requestAP.setDisable(false);
                        requestAP.setVisible(true);

                        listAP.setDisable(true);
                        listAP.setVisible(false);

                        titleText.setText("Request for exam: " + activeExam.getExam().getExamName() + "\nId: "
                                + activeExam.getExamId() + ", Time: " + activeReq.getExtra()
                                + " / " + activeExam.getExam().getDuration());

                        commentText.setText(activeReq.getTeacherMessage());
                    });
                }
            }
        });

        controller.getAllRequests("#GetAllTimeRequests");
    }

    public void setExam(ExecutableExam exam) {
        activeExam = exam;
    }
}

