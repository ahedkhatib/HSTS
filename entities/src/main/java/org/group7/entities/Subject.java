package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "subjects")
public class Subject implements Serializable {

    @Id
    @Column(name = "subject_id")
    private int subjectId;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courseList;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questionList;

    public Subject() {

    }

    public Subject(int subjectId) {
        this.subjectId = subjectId;
        this.courseList = new ArrayList<>();
        this.questionList = new ArrayList<>();
    }

    public Subject(int subjectId, List<Course> courseList, List<Question> questionList) {
        this.subjectId = subjectId;
        this.courseList = courseList;
        this.questionList = questionList;
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
