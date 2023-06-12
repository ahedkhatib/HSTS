package org.group7.client.Boundary;
import java.lang.String;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.*;
import org.group7.client.Control.PrinStudentReportsController;
import org.group7.entities.*;

import java.util.List;

public class PrinStudentReportsBoundary extends Boundary {
    PrinStudentReportsController controller;
    public List<Student> students;
    public List<String> studentsNames;
    @FXML
    private Label averageLabel;

    @FXML
    private NumberAxis countAxis;

    @FXML
    private CategoryAxis gradeAxis;

    @FXML
    private BarChart<String, Integer> gradeChart;

    @FXML
    private Label medianLabel;

    @FXML
    private Label reportTA;

    @FXML
    private ComboBox<String> studentsCB;

    @Override
    public PrinStudentReportsController getController() {
        return controller;
    }

    @FXML
    public void initialize() {
        controller = new PrinStudentReportsController(this);
        super.setController(controller);
        reportTA.setVisible(false);

        controller.getStudents("#GetData");
    }

    public void updateStudentsCB() {
        if (!studentsNames.isEmpty()) {
            for (String s : studentsNames) {
                studentsCB.getItems().add(s);
            }
        }
    }

    @FXML
    void selectStudent(ActionEvent event) {
        String selected = studentsCB.getSelectionModel().getSelectedItem();
        String studentName;
        Student selectedStudent = new Student();
        averageLabel.setText("Average: ");
        medianLabel.setText("Median: ");
        updateGradeChart(new int[10]); // reInitialize to 0's
        for (Student s : students) {
            studentName = s.getFirstName() + " " + s.getLastName();
            if (selected.equals(studentName)) {
                selectedStudent = s;
            }
        }

            if (selectedStudent.getResultList() == null || selectedStudent.getResultList().isEmpty()) {
                reportTA.setVisible(true);
                reportTA.setText("Sorry,it seems that this"+ "\n" +"student doesn't have any results");
            } else {
                updateGradeChart(controller.getDistribution(selectedStudent));
                averageLabel.setText("Average: " + controller.getAvg(selectedStudent));
                medianLabel.setText("Median: " + controller.getMedian(selectedStudent));
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
