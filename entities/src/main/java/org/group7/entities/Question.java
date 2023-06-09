package org.group7.entities;

import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

@Entity
@Table(name = "questions")
public class Question implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_id_sequence")
    @SequenceGenerator(name = "question_id_sequence", sequenceName = "question_id_sequence", allocationSize = 1, initialValue = 100)
    @Column(name = "question_id")
    private int questionId;

    @Column(name = "instructions")
    private String instructions;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "question_course",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courseList;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "question_exam",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "exam_id"))
    private List<Exam> examList;

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
        this.courseList = new ArrayList<>();
        this.courseList.addAll(courseList);
        this.examList = new ArrayList<>();
        this.subject = subject;
        this.correctAnswer = correctAnswer;
        this.answerList = answerList;
    }

    public List<Exam> getExamList() {
        return examList;
    }

    public void setExamList(List<Exam> examList) {
        this.examList = examList;
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
