package org.group7.client.Events;

import org.group7.entities.Course;
import org.group7.entities.Message;
import org.group7.entities.Student;
import org.group7.entities.Subject;

import java.util.List;

public class CoursesEvent {
    List<Subject> subjects;

    public List<Subject> getSubjects() {
        return subjects;
    }

    public CoursesEvent(Message message){
        this.subjects = (List<Subject>) message.getObject();;
    }

}
