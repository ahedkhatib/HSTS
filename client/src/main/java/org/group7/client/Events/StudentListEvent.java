package org.group7.client.Events;

import org.group7.entities.Message;
import org.group7.entities.Student;

import java.util.List;

public class StudentListEvent {

    List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public StudentListEvent(Message message){
        this.students = (List<Student>) message.getObject();
    }

}
