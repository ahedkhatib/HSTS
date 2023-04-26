package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "teachers")
public class Teacher extends User{

    @ManyToMany
    private List<Course> courseList;

}
