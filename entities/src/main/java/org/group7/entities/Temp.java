package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "temps")
public class Temp implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "temp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TempGrade> grades;

    public Temp(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.grades = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<TempGrade> getGrades() {
        return grades;
    }

    public void setGrades(List<TempGrade> grades) {
        this.grades = grades;
        for (TempGrade grade : grades) {
            grade.setTemp(this);
        }
    }

    public void addGrad(TempGrade grade) {
        this.grades.add(grade);
        grade.setTemp(this);
    }

    public void changeGrade(int gradeId, int grade) {
        for (TempGrade g : this.grades) {
            if (g.getId() == gradeId) {
                g.setGrade(grade);
                break;
            }
        }
    }
}
