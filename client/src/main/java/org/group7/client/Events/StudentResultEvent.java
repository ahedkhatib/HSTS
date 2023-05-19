package org.group7.client.Events;

import org.group7.entities.Student;
import org.group7.entities.Message;
import org.group7.entities.Result;

import java.util.List;

public class StudentResultEvent {

    List<Result> results;

    Student student;

    public List<Result> getResults() {
        return results;
    }

    public Student getStudent() {
        return student;
    }

    public StudentResultEvent(Message message){
        Student student = (Student) message.getObject();

        this.student = student;
        this.results = student.getResultList();
    }

}
