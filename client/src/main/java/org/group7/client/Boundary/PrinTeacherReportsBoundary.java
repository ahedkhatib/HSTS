package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.group7.client.Control.LoginController;
import org.group7.client.Control.PrinTeacherReportsController;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Teacher;

import java.util.ArrayList;
import java.util.List;

public class PrinTeacherReportsBoundary extends Boundary {
    PrinTeacherReportsController controller;
    public List<Teacher> teachers;
    public List<ExecutableExam> exams;

    public List<String> teacherNames;
    public List<String> examsNames;
    @FXML
    private Label averageLabel;

    @FXML
    private NumberAxis countAxis;

    @FXML
    private ComboBox<String> examsCB;

    @FXML
    private Label examsInfo;

    @FXML
    private CategoryAxis gradeAxis;

    @FXML
    private BarChart<String, Integer> gradeChart;

    @FXML
    private Label medianLabel;

    @FXML
    private ComboBox<String> teachersComboBox;

    @Override
    public PrinTeacherReportsController getController() {
        return controller;
    }

    @FXML
    void initialize() {
        controller = new PrinTeacherReportsController(this);
        super.setController(controller);
        examsInfo.setVisible(false);
        examsCB.setVisible(false);

        controller.getTeachers("#GetTeachers");

    }

    @FXML
    void selectTeacher(ActionEvent event) {
        Teacher selectedTeacher = new Teacher();
        examsCB.setVisible(false);
        examsInfo.setText("");
        updateGradeChart(new int[10]); // reInitialize to 0's
        averageLabel.setText("Average: ");
        medianLabel.setText("Median: ");
        for (Teacher t : teachers) {
            if (teachersComboBox.getSelectionModel().getSelectedItem() != null) {
                if (teachersComboBox.getSelectionModel().getSelectedItem().equals(t.getFirstName() + " " + t.getLastName())) {
                    selectedTeacher = t;
                }
            }
        }
        List<ExecutableExam> exams = new ArrayList<>();
        if (selectedTeacher != null && selectedTeacher.getExamList() != null) {
            if (selectedTeacher.getExamList().isEmpty()) {
                examsInfo.setVisible(true);
                examsInfo.setText("There is no exams belong" + "\n" + teachersComboBox.getSelectionModel().getSelectedItem() + " teacher");
                examsCB.setVisible(false);
            } else {
                examsCB.getItems().clear();
                examsCB.setVisible(true);
                exams = selectedTeacher.getExamList();
                List<String> names = new ArrayList<>();
                for (ExecutableExam e : exams) {
                    names.add(e.getExamId() + " " + e.getExam().getExamName());
                }
                this.exams = exams;
                this.examsNames = names;
                updateExamsComboBox();
            }
        }
    }

    @FXML
    void selectExam(ActionEvent event) {
        ExecutableExam selectedExam = new ExecutableExam();
        averageLabel.setText("Average: ");
        medianLabel.setText("Median: ");
        updateGradeChart(new int[10]); // reInitialize to 0's
        for (ExecutableExam e : exams) {
            if (examsCB.getSelectionModel().getSelectedItem() != null) {
                if (examsCB.getSelectionModel().getSelectedItem().equals(e.getExamId() + " " + e.getExam().getExamName())) {
                    selectedExam = e;
                }
            }
        }
        if (selectedExam != null && selectedExam.getDistribution() != null) {
            updateGradeChart(selectedExam.getDistribution());
            averageLabel.setText("Average: " + selectedExam.getAverage());
            medianLabel.setText("Median: " + selectedExam.getMedian());
        }

    }

    public void updateTeachersComboBox() {
        if (!teacherNames.isEmpty()) {
            for (String s : teacherNames) {
                teachersComboBox.getItems().add(s);
            }
        }
    }

    public void updateExamsComboBox() {
        if (!examsNames.isEmpty()) {
            for (String s : examsNames) {
                examsCB.getItems().add(s);
            }
        }
    }

    private void updateGradeChart(int[] distribution) {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Grade Range");

        String[] gradeRanges = {"0-10", "11-20", "21-30", "31-40", "41-50", "51-60", "61-70", "71-80", "81-90", "91-100"};

        for (int i = 0; i < gradeRanges.length; i++) {
            series.getData().add(new XYChart.Data<>(gradeRanges[i], distribution[i]));
        }

        gradeChart.getData().setAll(series);

        countAxis.setTickUnit(1);
        countAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(countAxis) {
            @Override
            public String toString(Number object) {
                int intValue = object.intValue();
                if (object.doubleValue() == intValue) {
                    return String.valueOf(intValue);
                } else {
                    return "";
                }
            }
        });
    }

}
