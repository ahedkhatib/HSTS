package org.group7.entities;

import javax.persistence.*;

@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int examId;

    public Exam() {

    }
}
