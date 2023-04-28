package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "students")
public class Student extends User {

    @ManyToMany(mappedBy = "students")
    private List<Course> courseList;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades;

    @ManyToMany(cascade = { CascadeType.ALL})
    @JoinTable(
        name = "student_exam",
        joinColumns = { @JoinColumn(name = "student_id")},
        inverseJoinColumns = { @JoinColumn(name = "exam_id")}
    )
    private List<Exam> exams;

    public Student() {
        this.courseList = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    public Student(String username, String password, String firstName, String lastName) {
        super(username, password, firstName, lastName);
        this.courseList = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    public void addGrade(Grade grade){
        this.grades.add(grade);
    }

    public void addCourse(Course course){
        this.courseList.add(course);
        course.getStudents().add(this);
    }

    public void addExam(Exam exam){
        this.exams.add(exam);
        exam.getStudentList().add(this);
    }
}
