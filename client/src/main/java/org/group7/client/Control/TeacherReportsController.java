package org.group7.client.Control;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.CreateExamBoundary;
import org.group7.client.Boundary.TeacherReportsBoundary;
import org.group7.client.Client;
import org.group7.client.Events.ExecutableExamEvent;
import org.group7.client.Events.SubjectsListEvent;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Message;
import org.group7.entities.Subject;
import org.group7.entities.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeacherReportsController extends Controller{

    TeacherReportsBoundary boundary;

    public TeacherReportsController(TeacherReportsBoundary boundary){
        this.boundary = boundary;

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void GetResult(String req) {

        if(!req.equals("#getExecutableExam"))
            return;

        try{
            Client.getClient().sendToServer(new Message(null, "#getExecutableExam"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void update_execExam( ExecutableExamEvent event){
        List<ExecutableExam> executable = event.getExecutableExam();
        boundary.executableExams= executable;
        boundary.updateExecutableExamsCombo();
    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }
}
