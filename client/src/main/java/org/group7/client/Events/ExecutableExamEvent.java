package org.group7.client.Events;

import org.group7.entities.ExecutableExam;
import org.group7.entities.Message;
import java.util.List;

public class ExecutableExamEvent {
    List<ExecutableExam> executable;

    public List<ExecutableExam> getExecutableExam() {
        return executable;
    }

    public void setExecutable(List<ExecutableExam> executableExams) {
        this.executable = executableExams;
    }

    public ExecutableExamEvent(Message message) {
        this.executable = (List<ExecutableExam>) message.getObject();
    }
}