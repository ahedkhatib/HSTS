package org.group7.client.Events;

import org.group7.entities.ExecutableExam;
import org.group7.entities.Message;

import java.util.List;

public class ExecutableExamEvent {

    List<ExecutableExam> exams;

    public ExecutableExamEvent(Message message){
        this.exams = (List<ExecutableExam>) message.getObject();
    }

    public List<ExecutableExam> getExams(){
        return exams;
    }

}
