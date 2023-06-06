package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.group7.client.Control.PrinCourseReportsController;
import org.group7.entities.Course;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Teacher;

import java.util.List;
import java.util.ArrayList;

public class PrinCourseReportsBoundary extends Boundary{
    PrinCourseReportsController controller;
    public List<Teacher> teachers;
    public List<ExecutableExam> exams;

    public List<String> teachersNames;
    public List<String> examsNames;
    public List<Course> courses;
    public List<String> coursesNames;
    @FXML
    private ComboBox<String> coursesCB;
    @FXML
    private Label averageLabel;

    @FXML
    private NumberAxis countAxis;
    @FXML
    private ComboBox<String> examsCB;

    @FXML
    private Label coursesInfo;

    @FXML
    private CategoryAxis gradeAxis;

    @FXML
    private BarChart<String, Integer> gradeChart;

    @FXML
    private Label medianLabel;

    @FXML
    private ComboBox<String> teachersCB;

    @FXML
    void initialize() {
        controller = new PrinCourseReportsController(this);
        super.setController(controller);
        coursesInfo.setVisible(false);
        examsCB.setVisible(false);
        teachersCB.setVisible(false);

    }
    @FXML
    void selectCourse(ActionEvent event) {
        Course selectedCourse = new Course();
        coursesInfo.setText("");
        examsCB.setVisible(false);
        teachersCB.setVisible(false);
        updateGradeChart(new int[10]); // reInitialize to 0's
        averageLabel.setText("Average: ");
        medianLabel.setText("Median: ");
        for (Course c : courses) {
            if (coursesCB.getSelectionModel().getSelectedItem().equals(c.getCourseName())) {
                selectedCourse = c;
            }
        }

        if (selectedCourse.getTeacherList().isEmpty()) {
            coursesInfo.setVisible(true);
            coursesInfo.setText("There is no teachers belong"+ "\n" + "to"+ coursesCB.getSelectionModel().getSelectedItem()+ "course");
            examsCB.setVisible(false);
            teachersCB.setVisible(false);
        } else {
            teachersCB.getItems().clear();
            teachersCB.setVisible(true);
            List<Teacher> teachers = selectedCourse.getTeacherList();
            List<String> names = new ArrayList<>();
            for (Teacher t : teachers) {
                names.add(t.getFirstName() + " " + t.getLastName());
            }
            this.teachers = teachers;
            this.teachersNames = names;
            updateTeachersComboBox();
        }

    }
    @FXML
    void selectTeacher(ActionEvent event) {
        Teacher selectedTeacher = new Teacher();
        examsCB.setVisible(false);
        coursesInfo.setText("");
        updateGradeChart(new int[10]); // reInitialize to 0's
        averageLabel.setText("Average: ");
        medianLabel.setText("Median: ");
        for (Teacher t : teachers) {
            if(teachersCB.getSelectionModel().getSelectedItem() != null) {
                if (teachersCB.getSelectionModel().getSelectedItem().equals(t.getFirstName() + " " + t.getLastName())) {
                    selectedTeacher = t;
                }
            }
        }
        List<ExecutableExam> exams = new ArrayList<>();
        if(selectedTeacher != null && selectedTeacher.getExamList() != null) {
            if (selectedTeacher.getExamList().isEmpty() ) {
                coursesInfo.setVisible(true);
                coursesInfo.setText("There is no exams belong" + "\n" + teachersCB.getSelectionModel().getSelectedItem() + " teacher");
                examsCB.setVisible(false);
            } else {
                for (ExecutableExam e : selectedTeacher.getExamList()) {
                    if (e.getExam().getCourse().getCourseName().equals(coursesCB.getSelectionModel().getSelectedItem())) {
                        exams.add(e);
                    }
                }
                if (exams.isEmpty()) {
                    coursesInfo.setVisible(true);
                    coursesInfo.setText("There is no exams belong" + "\n" + "to " + teachersCB.getSelectionModel().getSelectedItem() + " in" + "\n" + coursesCB.getSelectionModel().getSelectedItem() + " course");
                    examsCB.setVisible(false);
                } else {
                    examsCB.getItems().clear();
                    examsCB.setVisible(true);
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
    }
    @FXML
    void selectExam(ActionEvent event) {
        ExecutableExam selectedExam = new ExecutableExam();
        averageLabel.setText("Average: ");
        medianLabel.setText("Median: ");
        updateGradeChart(new int[10]); // reInitialize to 0's
        for (ExecutableExam e : exams) {
            if(examsCB.getSelectionModel().getSelectedItem() != null) {
                if (examsCB.getSelectionModel().getSelectedItem().equals(e.getExamId() + " " + e.getExam().getExamName())) {
                    selectedExam = e;
                }
            }
        }
        if(selectedExam != null && selectedExam.getDistribution() != null) {
            updateGradeChart(selectedExam.getDistribution());
            averageLabel.setText("Average: " + selectedExam.getAverage());
            medianLabel.setText("Median: " + selectedExam.getMedian());
        }
    }
    public void updateTeachersComboBox() {
        if(!teachersNames.isEmpty()) {
            for (String s : teachersNames) {
                teachersCB.getItems().add(s);
            }
        }
    }
    public void updateExamsComboBox() {
        if(!examsNames.isEmpty()) {
            for (String s : examsNames) {
                examsCB.getItems().add(s);
            }
        }
    }
    public void updateCoursesComboBox() {
        if(!coursesNames.isEmpty()) {
            for (String s : coursesNames) {
                coursesCB.getItems().add(s);
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
