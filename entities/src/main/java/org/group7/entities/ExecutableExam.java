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
    private List<Teacher> studentList;

    @Transient
    private Exam exam;

    private int time;

    public ExecutableExam(){

    }

    public ExecutableExam(String examId, Exam exam, Teacher teacher) {
        this.examId = examId;
        this.exam = exam;
        this.time = exam.getDuration();
        this.teacher = teacher;
        this.studentList  = new ArrayList<>();
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

    public Teacher getTeacherList() {
        return teacher;
    }

    public void setTeacherList(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Teacher> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Teacher> studentList) {
        this.studentList = studentList;
    }
}
