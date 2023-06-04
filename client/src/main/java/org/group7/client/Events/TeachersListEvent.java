package org.group7.client.Events;
import org.group7.entities.Message;
import org.group7.entities.Teacher;

import java.util.List;
public class TeachersListEvent {
    List<Teacher> teachers;

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public TeachersListEvent(Message message){
        this.teachers = (List<Teacher>) message.getObject();
    }
    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }
}
