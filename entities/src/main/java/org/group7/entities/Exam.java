package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "exams")
public class Exam implements Serializable {

    @Id
    @Column(name = "exam_id")
    private int examId;

    private int duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private Teacher creator;

    @ManyToMany(mappedBy = "examList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Teacher> teacherList;

    @ManyToMany(mappedBy = "examList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Student> studentList;

    @Column(name = "teacher_comments")
    private String teacherComments;

    @Column(name = "student_comments")
    private String studentComments;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Transient
    @Column(name = "question_list")
    private List<Question> questionList;

    @Transient
    @Column(name = "question_points")
    private List<Integer> questionPoints;

    public Exam() {

    }

    public Exam(int examid, int duration, Teacher creator, String teacherComments, String studentComments, Course course, List<Question> questionList, List<Integer> questionPoints) {
        this.duration = duration;
        this.creator = creator;
        this.teacherComments = teacherComments;
        this.studentComments = studentComments;
        this.questionList = questionList;
        this.questionPoints = questionPoints;

        this.teacherList = new ArrayList<>();
        this.studentList = new ArrayList<>();

        this.examId = course.getSubject().getSubjectId() * 1000 + course.getCourseId() * 100 + examid;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Teacher getCreator() {
        return creator;
    }

    public void setCreator(Teacher creator) {
        this.creator = creator;
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teachers) {
        this.teacherList = teachers;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public String getTeacherComments() {
        return teacherComments;
    }

    public void setTeacherComments(String teacherComments) {
        this.teacherComments = teacherComments;
    }

    public String getStudentComments() {
        return studentComments;
    }

    public void setStudentComments(String studentComments) {
        this.studentComments = studentComments;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public List<Integer> getQuestionPoints() {
        return questionPoints;
    }

    public void setQuestionPoints(List<Integer> questionPoints) {
        this.questionPoints = questionPoints;
    }
}
