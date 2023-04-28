package org.group7.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @Column(name = "id")
    private int questionId;

    @Column(name = "question")
    private String question;

    @ElementCollection
    @CollectionTable(name = "question_solutions", joinColumns = @JoinColumn(name = "question_id"))
    @MapKeyColumn(name = "solution_number")
    @Column(name = "solution")
    private Map<Integer, String> solutions;

    @Column(name = "correct_solution_number")
    private int correctSolutionNumber;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToMany(mappedBy = "questions")
    private List<Course> courseList;

    public Question(int questionId, String question, String[] solutions, int correctSolutionNumber, Subject subject) {
        this.questionId =  (subject.getSubjectId() * 1000) + questionId;
        this.question = question;
        this.solutions = new HashMap<>();

        int i = 1;
        for (String sol : solutions) {
            this.solutions.put(i, sol);
            i++;
        }

        this.correctSolutionNumber = correctSolutionNumber;
        this.subject = subject;
        this.courseList = new ArrayList<>();
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<Integer, String> getSolutions() {
        return solutions;
    }

    public void setSolutions(Map<Integer, String> solutions) {
        this.solutions = solutions;
    }

    public int getCorrectSolutionNumber() {
        return correctSolutionNumber;
    }

    public void setCorrectSolutionNumber(int correctSolutionNumber) {
        this.correctSolutionNumber = correctSolutionNumber;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }
}
