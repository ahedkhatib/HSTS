package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.client.Control.CreateExamController;
import org.group7.client.Control.CreateExecutableController;
import org.group7.entities.Exam;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Student;

import java.util.List;

public class CreateExecutableBoundary extends Boundary {

    CreateExecutableController controller;

    @FXML
    private TextField examCodeTF;

    @FXML
    private ComboBox<Exam> examsCombo;

    @FXML
    private Button saveBtn;

    public TextField getExamCodeTF() {
        return examCodeTF;
    }

    public void setExamCodeTF(TextField examCodeTF) {
        this.examCodeTF = examCodeTF;
    }

    public ComboBox<Exam> getExamsCombo() {
        return examsCombo;
    }

    public void setExamsCombo(ComboBox<Exam> examsCombo) {
        this.examsCombo = examsCombo;
    }

    public Button getSaveBtn() {
        return saveBtn;
    }

    public void setSaveBtn(Button saveBtn) {
        this.saveBtn = saveBtn;
    }

    @FXML
    public void saveExecutable(ActionEvent event){
        controller.saveExecutable(examsCombo.getSelectionModel().getSelectedItem(), examCodeTF.getText());
    }

    @Override
    public CreateExecutableController getController() {
        return controller;
    }

    @FXML
    public void initialize(){
        controller = new CreateExecutableController(this);
        super.setController(controller);

        examsCombo.setCellFactory(param -> new ComboBoxListCell<Exam>() {
            @Override
            public void updateItem(Exam exam, boolean empty) {
                super.updateItem(exam, empty);

                if (empty || exam == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                   setText(exam.getExamName());
                }
            }

        });

        examsCombo.setButtonCell(new ListCell<Exam>() {
            @Override
            protected void updateItem(Exam exam, boolean empty) {
                super.updateItem(exam, empty);
                if (exam == null || empty) {
                    setText(null);
                } else {
                    setText(exam.getExamName());
                }
            }
        });

        controller.getTeacherExams("#GetTeacherExams");

    }

}

