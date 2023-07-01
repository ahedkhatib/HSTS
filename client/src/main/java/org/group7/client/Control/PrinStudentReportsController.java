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
import java.util.Collections;
import java.util.List;

public class PrinStudentReportsController extends Controller {

    PrinStudentReportsBoundary boundary;

    public PrinStudentReportsController(PrinStudentReportsBoundary boundary) {
        this.boundary = boundary;

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void getStudents(String req){

        if(!req.equals("#GetData"))
            return;

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

        boundary.updateStudentsCB(students);
    }

    public double getAvg(Student selectedStudent){
        int examNum=0,gradesSum=0;
        if(selectedStudent.getResultList() != null)
        {
            for(Result r: selectedStudent.getResultList()){
                gradesSum+=r.getGrade();
                examNum++;
            } }else{return 0;}
        return gradesSum/examNum;
    }
    public double getMedian(Student selectedStudent){
        int examNum = selectedStudent.getResultList().size();
        List<Integer> grades = new ArrayList<>();
        for(Result r : selectedStudent.getResultList()){
            grades.add(r.getGrade());
        }
        Collections.sort(grades);
        double median = 0;

        if(examNum % 2 == 0){
            int midIndex = examNum / 2;
            median = (grades.get(midIndex - 1) + grades.get(midIndex)) / 2;
        } else {
            int midIndex = examNum / 2;
            median = (grades.get(midIndex ));
        }
        return median;
    }
    public int[] getDistribution(Student selectedStudent){
        int[] updatedDistribution = new int[10];
        List<Result> newList = selectedStudent.getResultList();
        int[] gradeRanges = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        for( int j = 0; j< selectedStudent.getResultList().size(); j++) {
            int grade = newList.get(j).getGrade();
            for (int i = 0; i < gradeRanges.length - 1; i++) {
                if (grade >= gradeRanges[i] && grade < gradeRanges[i + 1]) {
                    updatedDistribution[i]++;
                    break;
                }
            }
        }
        return updatedDistribution;
    }


}



