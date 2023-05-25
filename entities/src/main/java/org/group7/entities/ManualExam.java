package org.group7.entities;

import java.io.Serializable;

public class ManualExam extends Exam implements Serializable {

    float time;

    public ManualExam(){
        super();
        super.setType(2);
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
