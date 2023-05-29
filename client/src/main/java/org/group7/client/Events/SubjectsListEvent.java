package org.group7.client.Events;

import org.group7.entities.Message;
import org.group7.entities.Subject;

import java.util.List;

public class SubjectsListEvent {
    List<Subject> subjects;

    public List<Subject> getSubjects() {
        return subjects;
    }

    public SubjectsListEvent(Message message){
        this.subjects = (List<Subject>) message.getObject();
    }
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
