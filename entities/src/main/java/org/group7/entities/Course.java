package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    private int courseId;

    private String courseName;

    @ManyToMany
    private List<Student> studentList;

    @ManyToMany
    private List<Teacher> teacherList;

//    @ManyToOne
//    private Subject subject;

}
