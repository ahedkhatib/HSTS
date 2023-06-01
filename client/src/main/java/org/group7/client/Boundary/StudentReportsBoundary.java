package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.client.Control.StudentReportsController;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Result;

import java.util.List;

public class StudentReportsBoundary extends Boundary{

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

    private StudentReportsController controller;

    private List<Result> resultList;

    private Result activeResult;

    public void setResultList(List<Result> resultList){
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

        controller.getResults();

        examAP.setDisable(true);
        examAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);
    }

    @Override
    public StudentReportsController getController() {
        return controller;
    }

    @FXML
    public void initialize(){
        controller = new StudentReportsController(this);
        super.setController(controller);

        resultListView.setCellFactory(param -> new ListCell<Result>() {
            @Override
            public void updateItem(Result result, boolean empty) {
                super.updateItem(result, empty);

                if (empty || result == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    setHeight(35);

                    HBox hbox = new HBox();

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

                    titleText.setText(activeResult.getExam().getExam().getExamName() + "\nGrade: "
                            + activeResult.getGrade() + ", Time: " + activeResult.getElapsed());
                }
            }
        });


        controller.getResults();
    }
}
