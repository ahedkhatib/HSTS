package org.group7.client.Control;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.TeacherApprovalBoundary;
import org.group7.client.Client;
import org.group7.client.Events.ExecutableExamEvent;
import org.group7.entities.Exam;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Message;
import org.group7.entities.Result;

import java.util.List;

public class TeacherApprovalController extends Controller {
    private TeacherApprovalBoundary boundary;

    public TeacherApprovalController(TeacherApprovalBoundary boundary) {
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void GetResult(String req) {

        if(!req.equals("#getExecutableExam"))
            return;

        try {
            Client.getClient().sendToServer(new Message(null, "#getExecutableExam"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void update_execExam(ExecutableExamEvent event) {
        List<ExecutableExam> executable = event.getExecutableExam();
        boundary.executableExams = executable;
        boundary.updateExecutableExamsCombo();
    }

    public boolean isValidNumber(String numberString) {
        try {
            Integer.parseInt(numberString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void save(Result result, boolean gradeChanged, String theNewGrade, String theNewGradeNote, double avg, double median, int[] distribution) {
        int grade = result.getGrade();
        if (!theNewGrade.equals("")) {
            grade = Integer.parseInt(theNewGrade);
        }

        Object[] obj = {result, gradeChanged, grade, theNewGradeNote, avg, median, distribution};

        try {
            Client.getClient().sendToServer(new Message(obj, "#approveResult"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
