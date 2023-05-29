package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "results")
public class Result implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private int resultId;

    @Column(name = "grade")
    private int grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private Student student;

    @Column(name = "exam")
    private ExecutableExam exam;

    @Column(name = "teacher_note")
    private String teacherNote;

    @Column(name = "elapsed")
    private float elapsed;

    private boolean status;

    public Result() {
    }

    public Result(int grade, Student student, String teacherNote, ExecutableExam exam, int elapsed){
        this.grade = grade;
        this.student = student;
        this.teacherNote = teacherNote;
        this.exam = exam;
        this.elapsed = elapsed;
        this.status = false;
    }

    public float getElapsed() {
        return elapsed;
    }

    public void setElapsed(float elapsed) {
        this.elapsed = elapsed;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public ExecutableExam getExam() {
        return exam;
    }

    public void setExam(ExecutableExam exam) {
        this.exam = exam;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getTeacherNote() {
        return teacherNote;
    }

    public void setTeacherNote(String teacherNote) {
        this.teacherNote = teacherNote;
    }
}
