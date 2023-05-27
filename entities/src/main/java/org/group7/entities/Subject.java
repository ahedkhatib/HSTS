package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "subjects")
public class Subject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_id_sequence")
    @SequenceGenerator(name = "subject_id_sequence", sequenceName = "subject_id_sequence", allocationSize = 1, initialValue = 10)
    @Column(name = "subject_id")
    private int subjectId;

    @Column(name = "subject_name")
    private String subjectName;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courseList;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questionList;

    public Subject() {

    }

    public Subject(String subjectName) {
        this.subjectName = subjectName;
        this.courseList = new ArrayList<>();
        this.questionList = new ArrayList<>();
    }

    public Subject(String subjectName, List<Course> courseList, List<Question> questionList) {
        this.subjectName = subjectName;
        this.courseList = courseList;
        this.questionList = questionList;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
