package org.group7.client.Control;

import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.PrepareQuestionBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CoursesEvent;
import org.group7.client.Events.QuestionEvent;
import org.group7.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrepareQuestionController extends Controller {
    PrepareQuestionBoundary boundary;
    public List<Course> course;
    public Subject subject;

    public PrepareQuestionController(PrepareQuestionBoundary boundary){
        this.boundary = boundary;
        EventBus.getDefault().register(this);


        Message message = new Message(Client.getClient().getUser(), "#GetTeacherCourses");
        try {
            Client.getClient().sendToServer(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String instructions, List<Course> courses, Subject subject, int correctAnswer, String[] answers) {

        if (correctAnswer == -1) {
            alert();
        } else if (Objects.equals(instructions, "")) {
            alert();
        } else  if (Objects.equals(answers[0], "")) {
            alert();
        }else  if (Objects.equals(answers[1], "")) {
            alert();
        }
        else  if (Objects.equals(answers[2], "")) {
            alert();
        }
        else  if (Objects.equals(answers[3], "")) {
            alert();
        }
        else {

            Question question = new Question(instructions, courses, subject, correctAnswer, answers);

            Message message = new Message(question, "#SaveQuestion");

            try {
                Client.getClient().sendToServer(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void getCourses(CoursesEvent event){

        List<Course> courses = event.getCourses();

        List<Subject> subjects = new ArrayList<>();

        for(Course course : courses){
            if(!subjects.contains(course.getSubject())){
                subjects.add(course.getSubject());
            }
        }

        List<String> names=new ArrayList<>();
        for(Subject subject : subjects){
            names.add(subject.getSubjectName());
        }
        subject = subjects.get(0);
        course = subjects.get(0).getCourseList();
        boundary.subjects = names;
        boundary.sub = subjects;
        boundary.update_Subject();
    }

    public void alert(){
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "incorrect question!"
        );
        alert.show();
    }

    @Subscribe
    public void checkQues(QuestionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Questions Added!"
        );
        alert.show();
        boundary.done();
    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

}
