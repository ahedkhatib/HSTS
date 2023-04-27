package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "students")
public class Student extends User {

    @ManyToMany
    private List<Course> courseList;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> examGrades;

    @ManyToMany(cascade = { CascadeType.ALL})
    @JoinTable(
        name = "student_exam",
        joinColumns = { @JoinColumn(name = "student_username")},
        inverseJoinColumns = { @JoinColumn(name = "exam_examId")}
    )
    private List<Exam> exams;
//    @ManyToMany(mappedBy = "grades")
//    private Map<Exam, Integer> examGrades;

    public Student() {
        this.courseList = new ArrayList<>();
        this.examGrades = new ArrayList<>();
    }

    public Student(String username, String password, String firstName, String lastName) {
        super(username, password, firstName, lastName);
        this.courseList = new ArrayList<>();
        this.examGrades = new ArrayList<>();
    }
}
