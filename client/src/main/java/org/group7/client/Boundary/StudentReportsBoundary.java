package org.group7.client.Boundary;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.group7.client.Control.StudentReportsController;
import org.group7.entities.ExecutableExam;

import java.util.List;

public class StudentReportsBoundary extends Boundary{

    @FXML
    private AnchorPane emptyAP;

    @FXML
    private Text emptyText;

    @FXML
    private AnchorPane listAP;

    @FXML
    private ListView<ExecutableExam> examListView;

    @FXML
    private AnchorPane examAP;

    @FXML
    private Button backBtn;

    @FXML
    private Text titleText;

    @FXML



    private StudentReportsController controller;

    private List<ExecutableExam> examList;

    public void setExamList(List<ExecutableExam> examList){
        this.examList = examList;
    }

    @FXML
    public void initialize(){
        controller = new StudentReportsController(this);
        super.setController(controller);

        controller.getExams();
    }
}
