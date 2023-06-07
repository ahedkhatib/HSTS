package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "extra_time")
public class ExtraTime implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private int requestId;

    @Column(name = "status")
    private boolean status;

    @Transient
    private ExecutableExam exam;

    @Column(name = "teacher_message")
    private String teacherMessage;

    @Column(name = "extra")
    private int extra;

    public ExtraTime() {

    }

    public ExtraTime(ExecutableExam exam, String teacherMessage, int extra) {
        this.status = false;
        this.exam = exam;
        this.teacherMessage = teacherMessage;
        this.extra = extra;
    }

    public ExecutableExam getExam() {
        return exam;
    }

    public void setExam(ExecutableExam exam) {
        this.exam = exam;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTeacherMessage() {
        return teacherMessage;
    }

    public void setTeacherMessage(String teacherMessage) {
        this.teacherMessage = teacherMessage;
    }
}
