package org.group7.client.Control;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.EditQuestionBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CoursesEvent;
import org.group7.client.Events.QuestionEvent;
import org.group7.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditQuestionController extends Controller{

    EditQuestionBoundary boundary;

    public EditQuestionController(EditQuestionBoundary boundary){
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
        boundary.backToList(null);
    }

    @Override
    public void unregisterController(){
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void setQuestions(CoursesEvent event){

        List<Subject> subjects = new ArrayList<>();
        List<Course> courses = event.getCourses();

        for(Course course : courses){
            if(!subjects.contains(course.getSubject())){
                subjects.add(course.getSubject());
            }
        }

        List<Question> questions = new ArrayList<>();
        for(Subject subject : subjects){
            questions.addAll(subject.getQuestionList());
        }

        if(subjects.size() == 0) {

            boundary.getEmptyAP().setVisible(true);
            boundary.getListAP().setVisible(false);
            boundary.getListAP().setDisable(true);
        } else {

            boundary.getEmptyAP().setVisible(false);

            if(boundary.getActiveQuestion() == null) {
                boundary.getListAP().setVisible(true);
                boundary.getListAP().setDisable(false);
            }

            this.boundary.getQuestionListView().setItems(FXCollections.observableArrayList(questions));
            this.boundary.getQuestionListView().refresh();
        }
    }
}
