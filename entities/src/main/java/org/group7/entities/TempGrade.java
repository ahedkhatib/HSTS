package org.group7.entities;

import javax.persistence.*;

@Entity
@Table(name = "temp_grade")
public class TempGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int grade;

    @ManyToOne(fetch = FetchType.LAZY)
    private Temp temp;


    public TempGrade(int grade){
        this.grade = grade;
    }
    public TempGrade(int grade, Temp temp){
        this.grade = grade;
        this.temp = temp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }
}
