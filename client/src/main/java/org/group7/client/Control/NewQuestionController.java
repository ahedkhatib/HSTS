package org.group7.client.Control;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import jdk.jfr.Event;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.NewQuestionBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CoursesEvent;
import org.group7.client.Events.QuestionEvent;
import org.group7.entities.Course;
import org.group7.entities.Message;
import org.group7.entities.Question;
import org.group7.entities.Subject;

import java.util.ArrayList;
import java.util.List;

public class NewQuestionController extends Controller{

    NewQuestionBoundary boundary;

    public NewQuestionController(NewQuestionBoundary boundary){
        this.boundary = boundary;

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void getQuestions(String req){

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
    public void setSubjectList(CoursesEvent event){
        List<Subject> subjects = new ArrayList<>();

        for(Course course : event.getCourses()){
            if(!subjects.contains(course.getSubject())){
                subjects.add(course.getSubject());
            }
        }

        boundary.getSubjectCombo().setItems(FXCollections.observableList(subjects));
    }

    public void saveQuestion(String instructions, List<Course> courses, Subject subject, int correctAnswer, String[] answers){
        Question question = new Question(instructions, courses, subject, correctAnswer, answers);

        try {
            Client.getClient().sendToServer(new Message(question, "#SaveQuestion"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void questionSaved(QuestionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Question Saved!"
        );
        alert.show();
        boundary.done();
    }

}
