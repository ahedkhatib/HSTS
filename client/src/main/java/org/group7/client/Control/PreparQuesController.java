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
import org.group7.entities.Course;
import org.group7.entities.Message;
import org.group7.entities.Question;
import org.group7.entities.Subject;

import java.util.ArrayList;
import java.util.List;

public class PreparQuesController extends Controller {
    PreparQuesBoundary boundary;
    public List<Course> cours;
    public Subject s;

    public PreparQuesController(PreparQuesBoundary  boundary){
        this.boundary = boundary;
        EventBus.getDefault().register(this);
        getCourses();
    }
    public void save(Question  q) {
        Object obj = q;
        if (q.getCorrectAnswer() == 0) {
            alert();
        } else if (q.getInstructions() == "") {
            alert();
        } else  if (q.getAnswerList()[0] == "") {
            alert();
        }else  if (q.getAnswerList()[1] == "") {
            alert();
        }
        else  if (q.getAnswerList()[2] == "") {
            alert();
        }
        else  if (q.getAnswerList()[3] == "") {
            alert();
        }
        else {
            Message message = new Message(obj, "#preparQues");
            try {
                Client.getClient().sendToServer(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getCourses(){
        Message message = new Message(null, "#getSubject");
        try {
            Client.getClient().sendToServer(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void alert(){
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "incorrect question!"
        );
        alert.show();
    }
    @Subscribe
    public void gotCourses(CoursesEvent event){

        List<Subject> c =event.getSubjects();
        List<String> names=new ArrayList<>();
        for(Subject K:c){
            names.add(K.getSubjectName());
        }
        s=c.get(0);
        cours=c.get(0).getCourseList();
        boundary.subjects=names;
        boundary.sub=c;
        boundary.update_Subject();
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
