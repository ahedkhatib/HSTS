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
    private Exam exam;

    @Column(name = "teacher_note")
    private String teacherNote;

    public Result() {
    }

    public Result(int grade, Student student, String teacherNote, Exam exam){
        this.grade = grade;
        this.student = student;
        this.teacherNote = teacherNote;
        this.exam = exam;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
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
