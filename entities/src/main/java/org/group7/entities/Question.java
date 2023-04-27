package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    private int questionId;

//    @ManyToOne
//    private List<Subject> subjectList;
}
