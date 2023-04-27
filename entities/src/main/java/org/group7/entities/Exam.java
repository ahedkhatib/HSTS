package org.group7.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @Column(name = "examId")
    private int examId;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "exams")
    private List<Student> studentList;
    public Exam() {

    }
}
