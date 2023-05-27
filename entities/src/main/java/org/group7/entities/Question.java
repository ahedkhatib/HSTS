package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "questions")
public class Question implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_id_sequence")
    @SequenceGenerator(name = "question_id_sequence", sequenceName = "question_id_sequence", allocationSize = 1, initialValue = 10)
    @Column(name = "question_id")
    private int questionId;

    @Column(name = "instructions")
    private String instructions;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "question_course",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courseList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "correct_answer")
    private int correctAnswer;

    @Column(name = "answer_list")
    private String[] answerList;

    public Question() {

    }

    public Question(String instructions, List<Course> courseList, Subject subject, int correctAnswer, String[] answerList) {
        this.instructions = instructions;
        this.courseList = courseList;
        this.subject = subject;
        this.correctAnswer = correctAnswer;
        this.answerList = answerList;

        this.questionId = subject.getSubjectId() * 1000 + this.questionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String[] getAnswerList() {
        return answerList;
    }

    public void setAnswerList(String[] answerList) {
        this.answerList = answerList;
    }
}
