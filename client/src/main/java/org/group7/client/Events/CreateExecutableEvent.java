package org.group7.client.Events;

import org.group7.entities.Exam;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Message;

import java.util.List;

public class CreateExecutableEvent {

    private List<Exam> examList;

    private List<ExecutableExam> executableExamList;

    public CreateExecutableEvent(Message message) {

        Object[] objects = (Object[]) message.getObject();

        this.examList = (List<Exam>) objects[0];
        this.executableExamList = (List<ExecutableExam>) objects[1];
    }

    public List<Exam> getExamList() {
        return examList;
    }

    public void setExamList(List<Exam> examList) {
        this.examList = examList;
    }

    public List<ExecutableExam> getExecutableExamList() {
        return executableExamList;
    }

    public void setExecutableExamList(List<ExecutableExam> executableExamList) {
        this.executableExamList = executableExamList;
    }
}
