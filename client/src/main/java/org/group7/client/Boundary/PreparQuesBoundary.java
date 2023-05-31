package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.group7.client.App;
import org.group7.client.Control.PreparQuesController;
import org.group7.entities.Course;
import org.group7.entities.Question;
import org.group7.entities.Subject;

import javax.swing.text.html.ImageView;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreparQuesBoundary extends Boundary {
    private int correct_asnwer = 0;
    PreparQuesController controller;
    public List<Course> cur;
    public List<Subject> sub;
    public List<String> all_courses;
    public List<Course> selected_courses;
    public List<Course> courses_after_selection;
    public List<String> subjects;
    public Subject selected_subject;
    @FXML
    private AnchorPane screen;
    @FXML
    private ImageView image;
    @FXML
    private Label labelsubject;
    @FXML
    private Label labelC;

    @FXML
    private Label labelQ;


    @FXML
    private ComboBox<String> courses;
    @FXML
    private ComboBox<String> subject;

    @FXML
    private CheckBox correct1;

    @FXML
    private CheckBox correct2;

    @FXML
    private CheckBox correct3;

    @FXML
    private CheckBox correct4;

    @FXML
    private Button prev;

    @FXML
    private TextField qustion;

    @FXML
    private Button save_ques;

    @FXML
    private TextField sultion_1;

    @FXML
    private TextField sultion_2;

    @FXML
    private TextField sultion_3;

    @FXML
    private TextField sultion_4;
    @FXML
    private ListView<String> list;

    @FXML
    void prev(ActionEvent event) {
        App.switchScreen("homepage");
    }

    @FXML
    void selectCourse(javafx.scene.input.MouseEvent mouseEvent) {

        String selected = list.getSelectionModel().getSelectedItem();


        if (selected != null) {
            if (all_courses.contains(selected)) {
                // Item is already selected, so remove it from selectedQuestions
                all_courses.remove(selected);
            } else {
                // Item is not selected, so add it to selectedQuestions
                all_courses.add(selected);
            }
            updateListView();

            courses_after_selection.clear();
            for (String s : all_courses) {
                for (Course d : selected_courses) {
                    if (Objects.equals(s, d.getCourseName())) {
                        courses_after_selection.add(d);
                    }
                }
            }

            list.setVisible(true);
            sultion_1.setVisible(true);
            sultion_2.setVisible(true);
            sultion_3.setVisible(true);
            sultion_4.setVisible(true);
            qustion.setVisible(true);
            correct1.setVisible(true);
            correct2.setVisible(true);
            correct3.setVisible(true);
            correct4.setVisible(true);
            labelQ.setVisible(true);
        }

    }

    void updateListView() {
        list.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && all_courses.contains(item)) {
                    // Apply selected style
                    setStyle("-fx-background-color: lightblue;");
                } else {
                    // Clear style
                    setStyle("");
                }

                setText(item);
            }
        });
    }

    @FXML
    void select_subject(ActionEvent event) {
        String select = subject.getSelectionModel().getSelectedItem();
        Subject subject_selected = new Subject();

        for (Subject d : sub) {
            if (Objects.equals(select, d.getSubjectName())) {
                subject_selected = d;
                selected_subject = d;
            }
        }

        cur = subject_selected.getCourseList();
        selected_courses = cur;
        list.getItems().removeAll();
        for (Course k : cur) {
            list.getItems().add(k.getCourseName());

        }
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subject.setVisible(false);
        labelsubject.setVisible(false);
        list.setVisible(true);
        labelC.setVisible(true);


    }

    @FXML
    void correct1(ActionEvent event) {
        correct1.setSelected(true);
        correct2.setSelected(false);
        correct3.setSelected(false);
        correct4.setSelected(false);
        correct_asnwer = 1;
    }

    @FXML
    void correct2(ActionEvent event) {
        correct1.setSelected(false);
        correct2.setSelected(true);
        correct3.setSelected(false);
        correct4.setSelected(false);
        correct_asnwer = 2;
    }

    @FXML
    void correct3(ActionEvent event) {
        correct1.setSelected(false);
        correct2.setSelected(false);
        correct3.setSelected(true);
        correct4.setSelected(false);
        correct_asnwer = 3;
    }

    @FXML
    void correct4(ActionEvent event) {
        correct1.setSelected(false);
        correct2.setSelected(false);
        correct3.setSelected(false);
        correct4.setSelected(true);
        correct_asnwer = 4;
    }

    @FXML
    void save_ques(ActionEvent event) {
        String[] answerList = new String[4];
        answerList[0] = sultion_1.getText();
        answerList[1] = sultion_2.getText();
        answerList[2] = sultion_3.getText();
        answerList[3] = sultion_4.getText();
        String questions = qustion.getText();

        Question ques = new Question(questions, courses_after_selection, selected_subject, correct_asnwer, answerList);

        controller.save(ques);
    }

    @FXML
    public void initialize() {
        courses_after_selection = new ArrayList<Course>();

        all_courses = new ArrayList<>();

        controller = new PreparQuesController(this);
        super.setController(controller);

        list.setVisible(false);
        sultion_1.setVisible(false);
        sultion_2.setVisible(false);
        sultion_3.setVisible(false);
        sultion_4.setVisible(false);
        qustion.setVisible(false);
        correct1.setVisible(false);
        correct2.setVisible(false);
        correct3.setVisible(false);
        correct4.setVisible(false);
        labelC.setVisible(false);
        labelQ.setVisible(false);

        controller.getCourses();
    }

    public void update_Subject() {
        if (!subjects.isEmpty()) {
            for (String c : subjects) {
                subject.getItems().add(c);
            }
        }
    }

    public void done() {
        screen.setVisible(false);
    }
}
