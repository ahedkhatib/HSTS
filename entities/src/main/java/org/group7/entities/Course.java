package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @Column(name = "id")
    private int courseId;

    private String courseName;

    @ManyToMany
    @JoinTable(
            name = "students_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students;

    @ManyToMany
    @JoinTable(
            name = "teachers_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private List<Teacher> teachers;

    @ManyToMany
    @JoinTable(
            name = "questions_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Teacher> questions;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public Course(int id, String name, Subject subject){
        this.courseId = id;
        this.courseName = name;
        this.students = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.subject = subject;
    }

    public List<Teacher> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Teacher> questions) {
        this.questions = questions;
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

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> studentList) {
        this.students = studentList;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teacherList) {
        this.teachers = teacherList;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
