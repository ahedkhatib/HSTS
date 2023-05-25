package org.group7.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "automated_exams")
public class AutomatedExam extends Exam implements Serializable {

    float time;

    public AutomatedExam(){
        super();
        super.setType(1);
    }

    public AutomatedExam(int examId){
        super(examId);
        super.setType(1);
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
