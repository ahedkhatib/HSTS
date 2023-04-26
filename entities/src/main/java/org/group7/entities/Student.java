package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "students")
public class Student extends User {

    @ManyToMany
    private List<Course> courseList;


}
