package org.group7.client.Boundary;

import java.lang.String;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.*;
import org.group7.client.Control.PrinShowDataController;
import org.group7.entities.Subject;
import org.group7.entities.*;

import java.util.List;

public class PrinShowDataBoundary extends Boundary{

    PrinShowDataController controller;

    public List<Exam> exams;

    public List<Student> students;
    public List<String> studentsNames;
    public List<String> examNames;
    public List<Subject> subjects;
    public List<String> subjectsNames;

    @FXML
    private ComboBox<String> examsComboBox;
    @FXML
    private TextArea examTF;

    @FXML
    private ComboBox<String> subjectsCB;

    @FXML
    private ComboBox<String> studentsCB;

    @Override
    public PrinShowDataController getController() {
        return controller;
    }

    @FXML
    public void initialize() {
        controller = new PrinShowDataController(this);
        super.setController(controller);
        examTF.setVisible(false);
        examTF.setEditable(false);

        controller.getData("#GetData");
    }

    @FXML
    void selectExam(ActionEvent event) {
        String select= examsComboBox.getSelectionModel().getSelectedItem();

        Exam selectedExam = new Exam();
        for (Exam e : exams) {
            if (select == e.getExamName()) {
                selectedExam = e;
            }
        }
        examTF.setVisible(true);
        examTF.setText(controller.getStringExam(selectedExam));

    }

    public void updateExamsComboBox() {
        if(!examNames.isEmpty()) {
            for (String e : examNames) {
                examsComboBox.getItems().add(e);
            }
        }
    }

    @FXML
    void selectSubject(ActionEvent event){
        String selected= subjectsCB.getSelectionModel().getSelectedItem();

        Subject selectedSubject = new Subject();
        for (Subject s : subjects) {
            if (selected.equals(s.getSubjectName())) {
                selectedSubject = s;
            }
        }
        examTF.setVisible(true);

        StringBuilder questionDetails = new StringBuilder("The questions belong to this subjects: "+"\n");
        for (Question q : selectedSubject.getQuestionList()) {
            questionDetails.append(controller.getStringQues(q, null,-1));
        }
        examTF.setText(questionDetails.toString());

    }
    public void updateSubjectsComboBox() {
        if(!subjectsNames.isEmpty()) {
            for (String s : subjectsNames) {
                subjectsCB.getItems().add(s);
            }
        }
    }

    @FXML
    void selectStudent(ActionEvent event) {
        String selected = studentsCB.getSelectionModel().getSelectedItem();
        String studentName;
        Student selectedStudent = new Student();
        examTF.setText(selected);
        for (Student s : students) {
            studentName= s.getFirstName() + " " + s.getLastName();
            if (selected.equals(studentName)) {
                selectedStudent = s;
            }
            examTF.setVisible(true);
            StringBuilder resultDetails = new StringBuilder("This student has the follwing results: " + "\n");
            if (selectedStudent.getResultList()==null || selectedStudent.getResultList().isEmpty()) {
                examTF.setText("Sorry,it seems that this student doesn't have any results.");
            } else {
                for (Result r : selectedStudent.getResultList()) {
                    resultDetails.append(controller.getStringResult(r) + "\n");
                }
                examTF.setText(resultDetails.toString());
            }

        }
    }

    public void updateStudentsCB() {
        if(!studentsNames.isEmpty()) {
            for (String s : studentsNames) {
                studentsCB.getItems().add(s);
            }
        }
    }
}





