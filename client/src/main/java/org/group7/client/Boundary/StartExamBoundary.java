package org.group7.client.Boundary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.group7.client.Control.StartExamController;
import org.group7.entities.Exam;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Question;
import org.group7.entities.Teacher;

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

    @FXML
    private TextField fileTf;

    @FXML
    private Button uploadBtn;

    @FXML
    private Button downloadBtn;

    @FXML
    private TextField statusTf;

    @FXML
    private Text manualTimerText;

    @FXML
    private Button manualFinishBtn;

    private List<ToggleGroup> toggleGroups = new ArrayList<>();

    @Override
    public StartExamController getController() {
        return controller;
    }

    public AnchorPane getAutoAp() {
        return autoAp;
    }

    public AnchorPane getManualAp() {
        return manualAp;
    }

    public int getTimeSeconds() {
        return timeSeconds.get();
    }

    public IntegerProperty timeSecondsProperty() {
        return timeSeconds;
    }

    public void setTimeSeconds(int timeSeconds) {
        this.timeSeconds.set(timeSeconds);
    }

    public List<ToggleGroup> getToggleGroups() {
        return toggleGroups;
    }

    @FXML
    void startExam(ActionEvent event) {
        controller.getExam(examNumber.getText());
    }

    @FXML
    void finishExam(ActionEvent event) {
        controller.finishExam(false);
    }

    @FXML
    void finishManExam(ActionEvent event) {
        controller.finishExam(false);
    }

    @FXML
    public String getManualSolution(){
        return manualSolution;
    }

    public String manualSolution;

    private String readWordFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             StringWriter sw = new StringWriter()) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            sw.write(extractor.getText());
            return sw.toString();
        }
    }

    @FXML
    void uploadExam(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Word Files", "*.docx")
        );

        Stage stage = new Stage();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null && !files.isEmpty()) {
            File uploadedFile = files.get(0);
            try {
                String uploadedAnswer = readWordFile(uploadedFile);
                fileTf.setText(uploadedFile.getName());
                statusTf.setText("Upload successful: " + uploadedFile.getName());

                manualSolution = uploadedAnswer;

                controller.finishExam(false);

            } catch (IOException e) {
                statusTf.setText("Error: " + e.getMessage());
            }
        } else {
            statusTf.setText("Upload canceled.");
        }

    }

    @FXML
    void downloadExam(ActionEvent event) {
        controller.createWord();

        uploadBtn.setVisible(true);
        fileTf.setVisible(true);
        statusTf.setVisible(true);
        manualFinishBtn.setVisible(true);

        downloadBtn.setDisable(true);
        downloadBtn.setVisible(false);
    }

    @FXML
    void idEntered(ActionEvent event) {
        boolean flag = controller.checkId(idTf.getText());

        if (flag)
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

        uploadBtn.setVisible(false);
        fileTf.setVisible(false);
        statusTf.setVisible(false);
        manualFinishBtn.setVisible(false);

        return manualAp;
    }

    @FXML
    void initialize() {
        controller = new StartExamController(this);
        super.setController(controller);

        autoTimerText.textProperty().bind(Bindings.createStringBinding(
                () -> formatTime(timeSeconds.get()),
                timeSeconds));

        manualTimerText.textProperty().bind(Bindings.createStringBinding(
                () -> formatTime(timeSeconds.get()),
                timeSeconds));
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public VBox questionCard(int questionNum, Question question) {

        VBox card = new VBox();
        card.setPrefWidth(scrollPane.getPrefWidth() / 2);
        card.setSpacing(35);
        card.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-color: gray; -fx-border-width: 1px;");

        Text questionText = new Text(questionNum + ".   " + question.getInstructions());
        questionText.setStyle("-fx-font-size: 24;");

        VBox answersBox = new VBox();
        answersBox.setSpacing(15);

        ToggleGroup toggleGroup = new ToggleGroup();

        for (int i = 0; i < 4; i++) {
            HBox answer = new HBox();
            answer.setSpacing(25);

            Text answerText = new Text(question.getAnswerList()[i]);
            answerText.setStyle("-fx-font-size: 20;");

            RadioButton radioButton = new RadioButton();
            radioButton.setToggleGroup(toggleGroup);

            answer.getChildren().addAll(radioButton, answerText);
            answersBox.getChildren().add(answer);
        }

        toggleGroups.add(toggleGroup);

        card.getChildren().addAll(questionText, answersBox);

        return card;
    }

    public void setQuestions(ExecutableExam executableExam) {

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

        // Add teacher's notes
        String examNote = executableExam.getExam().getStudentComments();

        VBox card = new VBox();
        card.setPrefWidth(scrollPane.getPrefWidth() / 2);
        card.setPrefHeight(scrollPane.getPrefHeight() / 2);
        card.setSpacing(35);
        card.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-color: gray; -fx-border-width: 1px;");

        Teacher creator = executableExam.getExam().getCreator();

        Text noteText = new Text("Teacher " + creator.getFirstName() + " " + creator.getLastName() + " says: " + examNote);
        noteText.setStyle("-fx-font-size: 20;");
        card.getChildren().add(noteText);

        if (examNote != null && !examNote.equals(""))
            content.getChildren().add(card);

        // Add questions
        for (Question q : questions) {
            VBox questionCard = questionCard(count, q);
            content.getChildren().add(questionCard);
            count++;
        }

        pane.getChildren().add(content);

        scrollPane.setContent(pane);
        scrollPane.setDisable(true);
    }

}
