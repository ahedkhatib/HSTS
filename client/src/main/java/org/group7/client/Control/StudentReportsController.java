package org.group7.client.Control;

import org.group7.client.Boundary.StudentReportsBoundary;
import org.group7.client.Client;
import org.group7.entities.Student;

public class StudentReportsController extends Controller{

    private StudentReportsBoundary boundary;

    public StudentReportsController(StudentReportsBoundary boundary){
        this.boundary = boundary;
    }

    public void getExams(){
        boundary.setExamList(((Student)Client.getClient().getUser()).getExamList());
    }

}
