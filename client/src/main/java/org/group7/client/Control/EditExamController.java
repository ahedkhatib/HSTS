package org.group7.client.Control;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.EditExamBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CoursesEvent;
import org.group7.client.Events.MessageEvent;
import org.group7.entities.*;

import java.util.ArrayList;
import java.util.List;

public class EditExamController extends Controller{

    EditExamBoundary boundary;

    public EditExamController(EditExamBoundary boundary){
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void getExams(String req){

        if(!req.equals("#GetTeacherCourses"))
            return;

        try {
            Client.getClient().sendToServer(new Message(Client.getClient().getUser(),"#GetTeacherCourses"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterController(){
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void setExamList(CoursesEvent event){

        List<Exam> exams = new ArrayList<>();
        List<Course> courses = event.getCourses();
        List<Subject> subjects = new ArrayList<>();

        for(Course course : courses){
            if(!subjects.contains(course.getSubject())){
                subjects.add(course.getSubject());
            }
        }

        for(Subject subject : subjects){
            for(Course course : subject.getCourseList()){
                for(Exam ex : course.getExamList()){
                    if(!exams.contains(ex)){
                        exams.add(ex);
                    }
                }
            }
        }

        if(exams.size() == 0) {
            boundary.getEmptyAP().setVisible(true);
            boundary.getListAP().setVisible(false);
            boundary.getListAP().setDisable(true);
        } else {

            boundary.getEmptyAP().setVisible(false);

            if(boundary.getActiveExam() == null) {
                boundary.getListAP().setVisible(true);
                boundary.getListAP().setDisable(false);
            }

            this.boundary.getExamListView().setItems(FXCollections.observableArrayList(exams));
            this.boundary.getExamListView().refresh();
        }

    }

    public void save(String name, int type, String duration, Teacher creator, String teacherComments, String studentComments, Course course, List<Question> questionList, List<String> questionPoints){

        List<Integer> points = new ArrayList<>();

        for(String s : questionPoints) {

            int grade = 0;

            try {
                grade = Integer.parseInt(s);

                if(grade < 0 || grade > 100){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            String.format("Grade has to be an integer between 0 - 100! for question: " + s)
                    );
                    alert.show();

                    return;
                }

                points.add(grade);

            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        String.format("Grade has to be an integer between 0 - 100! for question: " + s)
                );
                alert.show();

                return;
            }
        }

        int sum = 0;
        for(Integer i : points){
            sum += i;
        }

        if(sum != 100){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Sum of grades must equal 100!"
            );
            alert.show();
            return;
        }

        int durationInt = 0;

        try {
            durationInt = Integer.parseInt(duration);

            if(durationInt <= 0 ){
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Durations has to be a positive integer!"
                );
                alert.show();
                return;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Durations has to be a positive integer!"
            );
            alert.show();
            return;
        }

        Exam exam = new Exam(name, type, durationInt, creator, teacherComments, studentComments, course, questionList, points );
        Object obj = exam;

        try {
            Client.getClient().sendToServer(new Message(obj, "#saveExam"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void examSaved(MessageEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Exam Saved!"
        );
        alert.show();
        boundary.backToList(null);
    }

}
