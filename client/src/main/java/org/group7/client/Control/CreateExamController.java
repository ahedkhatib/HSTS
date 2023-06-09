package org.group7.client.Control;

import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.CreateExamBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CoursesEvent;
import org.group7.client.Events.MessageEvent;
import org.group7.entities.*;

import java.util.ArrayList;
import java.util.List;

public class CreateExamController extends Controller {

    CreateExamBoundary boundary;

    public CreateExamController(CreateExamBoundary boundary) {
        this.boundary = boundary;
        EventBus.getDefault().register(this);

    }

    @Subscribe
    public void getTeacherCourses(String req) {

        if(!req.equals("#GetTeacherCourses")){
            return;
        }

        Message message = new Message(Client.getClient().getUser(), "#GetTeacherCourses");
        try {
            Client.getClient().sendToServer(message);
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void setData(CoursesEvent event) {

        List<Course> courses = event.getCourses();

        List<Subject> subjects = new ArrayList<>();

        for (Course course : courses) {
            if (!subjects.contains(course.getSubject())) {
                subjects.add(course.getSubject());
            }
        }

        List<String> names = new ArrayList<>();
        for (Subject s : subjects) {
            names.add(s.getSubjectName());
        }

        boundary.subjects = subjects;
        boundary.subjectsNames = names;
        boundary.updateSubjectsComboBox();
    }

    public boolean isValidNumber(String numberString) {
        try {
            Integer.parseInt(numberString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void save(String name, int type, int duration, Teacher creator, String teacherComments, String studentComments, Course course, List<Question> questionList, List<Integer> questionPoints) {

        Exam exam = new Exam(name, type, duration, creator, teacherComments, studentComments, course, questionList, questionPoints);
        Object obj = exam;

        try {
            Client.getClient().sendToServer(new Message(obj, "#saveExam"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void examSaved(MessageEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Exam Saved!"
        );
        alert.show();
        boundary.done();
    }
}


