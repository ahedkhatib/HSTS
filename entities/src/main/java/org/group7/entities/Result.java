package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;

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

    @Transient
    @Column(name = "exam")
    private ExecutableExam exam;

    @Column(name = "teacher_note")
    private String teacherNote;

    @Column(name = "elapsed")
    private double elapsed;

    private boolean status;

    @Column(name = "time_up")
    private boolean timeUp;

    @Transient
    private HashMap<Question, Integer> answers;

    public Result() {
    }

    public Result(int grade, Student student, String teacherNote, ExecutableExam exam, double elapsed, boolean timeUp, HashMap<Question, Integer> answers){
        this.grade = grade;
        this.student = student;
        this.teacherNote = teacherNote;
        this.exam = exam;
        this.elapsed = elapsed;
        this.timeUp = timeUp;
        this.answers = answers;
        this.status = false;
    }

    public HashMap<Question, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<Question, Integer> answers) {
        this.answers = answers;
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public void setTimeUp(boolean timeUp) {
        this.timeUp = timeUp;
    }

    public double getElapsed() {
        return elapsed;
    }

    public void setElapsed(double elapsed) {
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

    public String getFirstName() {
        return student != null ? student.getFirstName() : null;
    }

    public String getLastName() {
        return student != null ? student.getLastName() : null;
    }
}
