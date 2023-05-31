package org.group7.client.Control;

import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.HomepageBoundary;
import org.group7.client.Boundary.PreparQuesBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CoursesEvent;
import org.group7.client.Events.LoginEvent;
import org.group7.client.Events.QuestionEvent;
import org.group7.entities.*;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreparQuesController extends Controller {
    PreparQuesBoundary boundary;
    public List<Course> course;
    public Subject subject;

    public PreparQuesController(PreparQuesBoundary  boundary){
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    public void save(Question  q) {
        if (q.getCorrectAnswer() == 0) {
            alert();
        } else if (Objects.equals(q.getInstructions(), "")) {
            alert();
        } else  if (Objects.equals(q.getAnswerList()[0], "")) {
            alert();
        }else  if (Objects.equals(q.getAnswerList()[1], "")) {
            alert();
        }
        else  if (Objects.equals(q.getAnswerList()[2], "")) {
            alert();
        }
        else  if (Objects.equals(q.getAnswerList()[3], "")) {
            alert();
        }
        else {

            Message message = new Message(q, "#preparQues");
            try {
                Client.getClient().sendToServer(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getCourses(){

        Teacher teacher = (Teacher) Client.getClient().getUser();

        List<Course> courses = teacher.getCourseList();

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
            boundary.done();
    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

}
