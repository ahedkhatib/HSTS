package org.group7.client.Events;

import org.group7.entities.ExecutableExam;

public class StartExamEvent  {

    private ExecutableExam exam;

    private boolean exists;

    private int type;

    public StartExamEvent(Object exam, String msg){
        if(exam instanceof String){
            this.exam = null;
            this.type = -1;
        } else {
            this.exam = (ExecutableExam) exam;
            this.type = ((ExecutableExam) exam).getExam().getType();
        }

        this.exists = !msg.substring(11).equals("Incorrect");
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ExecutableExam getExam() {
        return exam;
    }

    public void setExam(ExecutableExam exam) {
        this.exam = exam;
    }
}
