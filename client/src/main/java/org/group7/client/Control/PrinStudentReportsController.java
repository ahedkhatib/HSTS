package org.group7.client.Control;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.PrinStudentReportsBoundary;
import org.group7.client.Client;
import org.group7.client.Events.StudentListEvent;
import org.group7.entities.Message;
import org.group7.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrinStudentReportsController extends Controller {

    PrinStudentReportsBoundary boundary;

    public PrinStudentReportsController(PrinStudentReportsBoundary boundary) {
        this.boundary = boundary;

        EventBus.getDefault().register(this);

        try {
            Client.getClient().sendToServer(new Message(null, "#GetStudents"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void gotStudents(StudentListEvent event){

        List<Student> students = event.getStudents();
        List<String> names = new ArrayList<>();
        for(Student s : students){
            names.add(s.getFirstName() + " "+ s.getLastName());
        }
        boundary.students = students;
        boundary.studentsNames = names;
        boundary.updateStudentsCB();
    }


}



