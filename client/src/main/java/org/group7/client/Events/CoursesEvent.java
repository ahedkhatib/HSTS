package org.group7.client.Events;

import org.group7.entities.Course;
import org.group7.entities.Message;
import org.group7.entities.Student;
import org.group7.entities.Subject;

import java.util.List;

public class CoursesEvent {
    List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public CoursesEvent(Message message){
        this.courses = (List<Course>) message.getObject();;
    }

}
