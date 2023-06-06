package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "courses")
public class Course implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_id_sequence")
    @SequenceGenerator(name = "course_id_sequence", sequenceName = "course_id_sequence", allocationSize = 1, initialValue = 10)
    @Column(name = "course_id")
    private int courseId;

    @Column(name = "course_name")
    private String courseName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToMany(mappedBy = "courseList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> studentList;

    @ManyToMany(mappedBy = "courseList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Teacher> teacherList;

    @ManyToMany(mappedBy = "courseList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questionList;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exam> examList;

    public Course(){
    }

    public Course(String courseName, Subject subject) {
        this.courseName = courseName;
        this.subject = subject;

        this.studentList = new ArrayList<>();
        this.teacherList = new ArrayList<>();
        this.questionList = new ArrayList<>();
        this.examList = new ArrayList<>();
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public List<Exam> getExamList() {
        return examList;
    }

    public void setExamList(List<Exam> examList) {
        this.examList = examList;
    }
}
