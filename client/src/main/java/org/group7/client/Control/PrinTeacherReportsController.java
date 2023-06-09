package org.group7.client.Control;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.PrinCourseReportsBoundary;
import org.group7.client.Boundary.PrinTeacherReportsBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CoursesListEvent;
import org.group7.client.Events.TeachersListEvent;
import org.group7.entities.Course;
import org.group7.entities.Message;
import org.group7.entities.Teacher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrinTeacherReportsController extends Controller{
    PrinTeacherReportsBoundary boundary;

    public PrinTeacherReportsController(PrinTeacherReportsBoundary boundary) {
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void getTeachers(String req){

        if(!req.equals("#GetTeachers"))
            return;

        try {
            Client.getClient().sendToServer(new Message(null, "#GetTeachers"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void setTeachers(TeachersListEvent event){

        List<Teacher> teachers = event.getTeachers();
        List<String> names = new ArrayList<>();
        String teacherName;
        for(Teacher t : teachers){
            teacherName = t.getFirstName() + " " + t.getLastName();
            names.add(teacherName);
        }
        boundary.teachers = teachers;
        boundary.teacherNames = names;
        boundary.updateTeachersComboBox();
    }
}
