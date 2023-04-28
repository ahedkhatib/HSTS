package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @Column(name = "id")
    private int subjectId;

    @OneToMany(mappedBy = "subject")
    private List<Course> courseList;

    @OneToMany(mappedBy = "subject")
    private List<Question> questionList;

    public Subject() {
        this.courseList = new ArrayList<>();
        this.questionList = new ArrayList<>();
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
