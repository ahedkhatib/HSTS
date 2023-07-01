package org.group7.client.Control;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.PrinCourseReportsBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CoursesListEvent;
import org.group7.entities.Course;
import org.group7.entities.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrinCourseReportsController extends Controller {
    PrinCourseReportsBoundary boundary;

    public PrinCourseReportsController(PrinCourseReportsBoundary boundary) {
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void getCourses(String req){

        if(!req.equals("#GetData"))
            return;

        try {
            Client.getClient().sendToServer(new Message(null, "#GetCourses"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void setCourses(CoursesListEvent event) {

        List<Course> courses = event.getCourses();

        boundary.updateCoursesComboBox(courses);
    }
}
