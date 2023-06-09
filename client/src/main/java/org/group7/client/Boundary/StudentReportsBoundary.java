package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.client.Control.StudentReportsController;
import org.group7.entities.Question;
import org.group7.entities.Result;
import org.group7.entities.Teacher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentReportsBoundary extends Boundary {

    @FXML
    private AnchorPane emptyAP;

    @FXML
    private Text emptyText;

    @FXML
    private AnchorPane listAP;

    @FXML
    private ListView<Result> resultListView;

    @FXML
    private AnchorPane examAP;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button backBtn;

    @FXML
    private Text titleText;

    @FXML
    private Text noteText;

    private StudentReportsController controller;

    private List<Result> resultList;

    private Result activeResult;

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }

    public AnchorPane getEmptyAP() {
        return emptyAP;
    }

    public void setEmptyAP(AnchorPane emptyAP) {
        this.emptyAP = emptyAP;
    }

    public AnchorPane getListAP() {
        return listAP;
    }

    public void setListAP(AnchorPane listAP) {
        this.listAP = listAP;
    }

    public ListView<Result> getResultListView() {
        return resultListView;
    }

    public void setResultListView(ListView<Result> resultListView) {
        this.resultListView = resultListView;
    }

    @FXML
    public void backToList(ActionEvent event) {

        controller.getResults("#GetStudentResults");

        examAP.setDisable(true);
        examAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);

        activeResult = null;
    }

    public Result getActiveResult() {
        return activeResult;
    }

    @Override
    public StudentReportsController getController() {
        return controller;
    }

    @FXML
    public void initialize() {
        controller = new StudentReportsController(this);
        super.setController(controller);

        resultListView.setCellFactory(param -> new ListCell<Result>() {
            @Override
            public void updateItem(Result result, boolean empty) {
                super.updateItem(result, empty);

                if (empty || result == null) {
                    setStyle("-fx-background-color: transparent;");
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    hbox.setPrefHeight(50);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.getStyleClass().add("list-view-item");

                    String examName = result.getExam().getExam().getExamName();

                    Text reqText = new Text(examName);
                    reqText.setFont(Font.font("Arial", 24));
                    Text arrow = new Text(">");
                    arrow.setFont(Font.font("Arial", 24));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    hbox.getChildren().addAll(reqText, spacer, arrow);

                    setGraphic(hbox);
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);

                if (selected && !isEmpty()) {
                    activeResult = getItem();

                    examAP.setDisable(false);
                    examAP.setVisible(true);

                    listAP.setDisable(true);
                    listAP.setVisible(false);

                    String timeString = Double.toString(activeResult.getElapsed());
                    int decimalIndex = timeString.indexOf('.');

                    String decimalPart;

                    if (decimalIndex != -1) {
                        decimalPart = timeString.substring(0, decimalIndex + 2);
                    } else {
                        decimalPart = timeString;
                    }

                    titleText.setText(activeResult.getExam().getExam().getExamName() + "\nGrade: "
                            + activeResult.getGrade() + ", Time: " + decimalPart);

                    setQuestions(activeResult);

                    noteText.setText("Teacher's Note: " + activeResult.getTeacherNote());
                }
            }
        });

        controller.getResults("#GetStudentResults");
    }

    public VBox questionCard(int questionNum, Question question, int correctIndex, int toggleIndex) {

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
            answer.setAlignment(Pos.CENTER_LEFT);
            answer.setSpacing(25);

            Text answerText = new Text(question.getAnswerList()[i]);
            answerText.setStyle("-fx-font-size: 20;");

            RadioButton radioButton = new RadioButton();
            radioButton.setToggleGroup(toggleGroup);

            if (toggleIndex == i) {
                radioButton.setSelected(true);

                if (correctIndex != i) {
                    answerText.setStyle("-fx-text-fill: white; -fx-font-size: 20;");
                    answer.setStyle("-fx-background-color: F67280; ");
                }
            }

            if (correctIndex == i) {
                answerText.setStyle("-fx-text-fill: white; -fx-font-size: 20;");
                answer.setStyle("-fx-background-color: #91c79c; ");
            }

            answer.getChildren().addAll(radioButton, answerText);
            answersBox.getChildren().add(answer);
        }

        card.getChildren().addAll(questionText, answersBox);

        card.setDisable(true);

        return card;
    }

    public void setQuestions(Result result) {

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        int count = 1;

        Pane pane = new Pane();
        pane.setPrefWidth(scrollPane.getPrefWidth());

        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(50);
        content.setLayoutX(scrollPane.getPrefWidth() / 4);

        // Add teacher's notes
        String examNote = result.getExam().getExam().getStudentComments();

        VBox card = new VBox();
        card.setPrefWidth(scrollPane.getPrefWidth() / 2);
        card.setPrefHeight(scrollPane.getPrefHeight() / 2);
        card.setSpacing(35);
        card.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-color: gray; -fx-border-width: 1px;");

        Teacher creator = result.getExam().getExam().getCreator();

        Text noteText = new Text("Teacher " + creator.getFirstName() + " " + creator.getLastName() + " says: " + examNote);
        noteText.setStyle("-fx-font-size: 20;");
        card.getChildren().add(noteText);

        if(examNote != null && !examNote.equals(""))
            content.getChildren().add(card);

        // Add Questions
        HashMap<Question, Integer> map = result.getAnswers();

        for (Map.Entry<Question, Integer> entry : map.entrySet()) {
            Question q = entry.getKey();
            card = questionCard(count, q, q.getCorrectAnswer(), result.getAnswers().get(q));
            content.getChildren().add(card);
            count++;
        }

        pane.getChildren().add(content);

        scrollPane.setStyle("-fx-padding: 10px;");
        scrollPane.setContent(pane);
        scrollPane.setDisable(false);
    }
}
