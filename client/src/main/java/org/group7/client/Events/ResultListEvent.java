package org.group7.client.Events;

import org.group7.entities.*;

import java.util.List;

public class ResultListEvent {

    List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public ResultListEvent(Message message){
        Student student = (Student) message.getObject();
        this.results = student.getGrades();
    }

}
