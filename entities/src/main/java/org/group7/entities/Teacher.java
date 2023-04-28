package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "teachers")
public class Teacher extends User{

    @ManyToMany(mappedBy = "teachers", cascade = CascadeType.ALL)
    private List<Course> courses;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exam> exams;

    public Teacher(){
        this.courses = new ArrayList<>();
        this.exams = new ArrayList<>();
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    public void addCourse(Course course){
        this.courses.add(course);
        course.getTeachers().add(this);
    }

    public void addExam(Exam exam){
        this.exams.add(exam);
    }
}
