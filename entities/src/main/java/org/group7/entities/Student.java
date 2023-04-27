package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "students")
public class Student extends User {

    @ManyToMany
    private List<Course> courseList;

//    @ManyToMany(mappedBy = "grades")
//    private Map<Exam, Integer> examGrades;

    public Student() {
        this.courseList = new ArrayList<>();
//        this.examGrades = new HashMap<>();
    }

    public Student(String username, String password, String firstName, String lastName) {
        super(username, password, firstName, lastName);
        this.courseList = new ArrayList<>();
//        this.examGrades = new HashMap<>();
    }
}
