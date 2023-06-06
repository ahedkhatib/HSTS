package org.group7.client.Events;

import org.group7.entities.Course;
import org.group7.entities.Message;
import org.group7.entities.Teacher;

import java.util.List;

public class CoursesListEvent {
    List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public CoursesListEvent(Message message){
        this.courses = (List<Course>) message.getObject();
    }
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
