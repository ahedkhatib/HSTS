package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "students")
public class Student extends User implements Serializable {

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "student_exam",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "exam_id"))
    private List<ExecutableExam> examList;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courseList;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> resultList;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManualResult> manualResultList;

    @Column(name = "student_id_number")
    private String studentIdNum;

    public Student() {
        super();
        super.setType(1);
    }

    public Student(String username, String password, String firstname, String lastname, String studentIdNum) {
        super(username, password, firstname, lastname, 1);
        this.studentIdNum = studentIdNum;
        this.resultList = new ArrayList<>();
        this.examList = new ArrayList<>();
        this.courseList = new ArrayList<>();
        this.manualResultList = new ArrayList<>();
    }

    public List<ManualResult> getManualResultList() {
        return manualResultList;
    }

    public void setManualResultList(List<ManualResult> manualResultList) {
        this.manualResultList = manualResultList;
    }

    public String getStudentIdNum() {
        return studentIdNum;
    }

    public void setStudentIdNum(String studentIdNum) {
        this.studentIdNum = studentIdNum;
    }

    public List<Result> getResultList() {
        return resultList;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }

    public List<ExecutableExam> getExamList() {
        return examList;
    }

    public void setExamList(List<ExecutableExam> examList) {
        this.examList = examList;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

}
