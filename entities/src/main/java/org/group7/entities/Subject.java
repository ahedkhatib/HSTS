package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    private int subjectId;

//    @OneToMany
//    private List<Course> courseList;

//    @OneToMany
//    private List<Question> questionList;
}
