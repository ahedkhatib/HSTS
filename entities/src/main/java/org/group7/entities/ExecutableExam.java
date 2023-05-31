package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "executable_exams")
public class ExecutableExam implements Serializable {

    @Id
    @Column(name = "exam_id")
    private String examId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private Teacher teacher;

    @ManyToMany(mappedBy = "examList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> studentList;

    @Transient
    private Exam exam;

    private int time;

    // Statistics
    double average;

    double median;

    int[] distribution;

    public ExecutableExam(){

    }

    public ExecutableExam(String examId, Exam exam, Teacher teacher) {
        this.examId = examId;
        this.exam = exam;
        this.time = exam.getDuration();
        this.teacher = teacher;
        this.studentList  = new ArrayList<>();

        this.average = 0;
        this.median = 0;
        this.distribution = new int[10];
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public int[] getDistribution() {
        return distribution;
    }

    public void setDistribution(int[] distribution) {
        this.distribution = distribution;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }
}
