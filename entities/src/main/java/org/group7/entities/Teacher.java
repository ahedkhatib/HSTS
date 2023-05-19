package org.group7.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "teachers")
public class Teacher extends User implements Serializable{

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "teacher_course",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courseList;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "teacher_exam",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "exam_id"))
    private List<Exam> examList;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exam> createdExams;

    public Teacher() {
        super();
        super.setType(2);
    }

    public Teacher(String username, String password, String firstname, String lastname) {
        super(username, password, firstname, lastname, 2);
        this.examList = new ArrayList<>();
        this.courseList = new ArrayList<>();
        this.createdExams = new ArrayList<>();
    }

    public Teacher(String username, String password, String firstname, String lastname, List<Course> courseList, List<Exam> examList, List<Exam> createdExams) {
        super(username, password, firstname, lastname, 2);
        this.courseList = courseList;
        this.examList = examList;
        this.createdExams = createdExams;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public List<Exam> getExamList() {
        return examList;
    }

    public void setExamList(List<Exam> examList) {
        this.examList = examList;
    }

    public List<Exam> getCreatedExams() {
        return createdExams;
    }

    public void setCreatedExams(List<Exam> createdExams) {
        this.createdExams = createdExams;
    }
}
