package org.group7.client.Boundary;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.group7.client.Control.StartExamController;
import org.group7.entities.Exam;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Question;

public class StartExamBoundary extends Boundary {

    private StartExamController controller;

    // Intro Pane
    @FXML
    private Button startButton;

    @FXML
    private AnchorPane introAp;

    @FXML
    private TextField examNumber;

    // Automated Exam Pane
    @FXML
    private AnchorPane autoAp;

    @FXML
    private TextField idTf;

    @FXML
    private Button idBtn;

    @FXML
    private Button autoFinishBtn;

    @FXML
    private Text autoTimerText;

    private IntegerProperty timeSeconds = new SimpleIntegerProperty();

    @FXML
    private ScrollPane scrollPane;

    // Manual Exam Pane
    @FXML
    private AnchorPane manualAp;

    private List<ToggleGroup> toggleGroups = new ArrayList<>();

    public int getTimeSeconds() {
        return timeSeconds.get();
    }

    public IntegerProperty timeSecondsProperty() {
        return timeSeconds;
    }

    public void setTimeSeconds(int timeSeconds) {
        this.timeSeconds.set(timeSeconds);
    }

    @FXML
    void startExam(ActionEvent event) {
        controller.getExam(examNumber.getText());
    }

    @FXML
    void finishExam(ActionEvent event){}

    @FXML
    void idEntered(ActionEvent event){
        boolean flag = controller.checkId(idTf.getText());

        if(flag)
            scrollPane.setDisable(false);
    }

    public AnchorPane openAutoExam() {
        introAp.setDisable(true);
        introAp.setVisible(false);

        autoAp.setDisable(false);
        autoAp.setVisible(true);

        return autoAp;
    }

    public AnchorPane openManualExam() {
        introAp.setDisable(true);
        introAp.setVisible(false);

        manualAp.setDisable(false);
        manualAp.setVisible(true);

        return manualAp;
    }

    @FXML
    void initialize() {
        controller = new StartExamController(this);
        super.setController(controller);

        autoTimerText.textProperty().bind(Bindings.createStringBinding(
                () -> formatTime(timeSeconds.get()),
                timeSeconds));
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public VBox questionCard(int questionNum, Question question){

        VBox card = new VBox();
        card.setPrefWidth(600);
        card.setSpacing(35);
        card.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-color: gray; -fx-border-width: 1px;");

        Text questionText = new Text(questionNum + ".   " + question.getInstructions());
        questionText.setStyle("-fx-font-size: 24;");

        VBox answersBox = new VBox();
        answersBox.setSpacing(15);

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroups.add(toggleGroup);

        for(int i = 0; i < 4; i++){
            HBox answer = new HBox();
            answer.setSpacing(25);

            Text answerText = new Text(question.getAnswerList()[i]);
            answerText.setStyle("-fx-font-size: 20;");

            RadioButton radioButton = new RadioButton();
            radioButton.setToggleGroup(toggleGroup);

            answer.getChildren().addAll(radioButton, answerText);
            answersBox.getChildren().add(answer);
        }

        card.getChildren().addAll(questionText, answersBox);

        return card;
    }

    public void setQuestions(ExecutableExam executableExam){

        Exam exam = executableExam.getExam();
        List<Question> questions = exam.getQuestionList();

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        int count = 1;

        Pane pane = new Pane();
        pane.setPrefWidth(scrollPane.getPrefWidth());

        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(50);
        content.setLayoutX(300);

        for(Question q : questions){
            VBox card = questionCard(count, q);
            content.getChildren().add(card);
            count++;
        }

        pane.getChildren().add(content);

        scrollPane.setContent(pane);
        scrollPane.setDisable(true);
    }

}
