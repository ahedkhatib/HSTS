package org.group7.client.Events;

import org.group7.entities.Message;
import org.group7.entities.Exam;

import java.util.List;

public class ExamsListEvent {

    List<Exam> exams;

    public List<Exam> getExams() {
        return exams;
    }

    public ExamsListEvent(Message message){
        this.exams = (List<Exam>) message.getObject();
    }
    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }
}
