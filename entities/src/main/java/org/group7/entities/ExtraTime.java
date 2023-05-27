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

    @Column(name = "exam_id")
    private String examId;

    @Column(name = "teacher_message")
    private String teacherMessage;

    @Column(name = "extra")
    private int extra;

    public ExtraTime() {

    }

    public ExtraTime(String examId, String teacherMessage, int extra) {
        this.status = false;
        this.examId = examId;
        this.teacherMessage = teacherMessage;
        this.extra = extra;
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

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getTeacherMessage() {
        return teacherMessage;
    }

    public void setTeacherMessage(String teacherMessage) {
        this.teacherMessage = teacherMessage;
    }
}
