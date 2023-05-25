package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "exams")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Exam implements Serializable {

    @Id
    @Column(name = "exam_id")
    private int examId;

    @Column(name = "exam_num")
    private int examNum;

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

    int type;

    public Exam() {

    }

    public Exam(int examId){
        this.examId = examId;
    }

    public Exam(int examid, int examNum, int type, int duration, Teacher creator, String teacherComments, String studentComments, Course course, List<Question> questionList, List<Integer> questionPoints) {
        this.duration = duration;
        this.creator = creator;
        this.type = type;
        this.teacherComments = teacherComments;
        this.studentComments = studentComments;
        this.questionList = questionList;
        this.questionPoints = questionPoints;

        this.teacherList = new ArrayList<>();
        this.studentList = new ArrayList<>();

        this.examId = course.getSubject().getSubjectId() * 1000 + course.getCourseId() * 100 + examNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getExamNum() {
        return examNum;
    }

    public void setExamNum(int examNum) {
        this.examNum = examNum;
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
