package org.group7.client.Control;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.CreateExamBoundary;
import org.group7.client.Client;
import org.group7.client.Events.SubjectsListEvent;
import org.group7.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateExamController extends Controller{

    CreateExamBoundary boundary;

    public CreateExamController(CreateExamBoundary boundary){
        this.boundary = boundary;

        EventBus.getDefault().register(this);

        try {
            Client.getClient().sendToServer(new Message(null, "#GetSubjects"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void gotSubjects(SubjectsListEvent event){

        List<Subject> subjects = event.getSubjects();
        List<String> names = new ArrayList<>();
        for(Subject s : subjects){
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

    public void save(String name, int type, String duration, Teacher creator, String teacherComments, String studentComments, Course course, List<Question> questionList, List<Integer> questionPoints){

        Exam exam = new Exam(name, type, Integer.parseInt(duration), creator, teacherComments, studentComments, course, questionList, questionPoints );
        Object obj = exam;

        try {
            Client.getClient().sendToServer(new Message(obj, "#saveExam"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


