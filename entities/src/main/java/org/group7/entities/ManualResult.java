package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "manual_results")
public class ManualResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manual_result_id")
    private int id;

    @Column(name = "exam_solution")
    private String examSol;

    @Transient
    private ExecutableExam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    public ManualResult() {

    }

    public ManualResult(String examSol, ExecutableExam exam, Student student) {
        this.examSol = examSol;
        this.exam = exam;
        this.student = student;
    }

    public ExecutableExam getExam() {
        return exam;
    }

    public void setExam(ExecutableExam exam) {
        this.exam = exam;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExamSol() {
        return examSol;
    }

    public void setExamSol(String examSol) {
        this.examSol = examSol;
    }
}