package org.group7.entities;

import java.io.Serializable;

public class AutomatedExam extends Exam implements Serializable {

    float time;

    public AutomatedExam(){
        super();
        super.setType(1);
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
