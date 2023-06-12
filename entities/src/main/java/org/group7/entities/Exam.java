package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "exams")
public class Exam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exam_id_sequence")
    @SequenceGenerator(name = "exam_id_sequence", sequenceName = "exam_id_sequence", allocationSize = 1, initialValue = 10)
    @Column(name = "exam_id")
    private int examId;

    private int duration;

    @Column(name = "exam_name")
    private String examName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private Teacher creator;

    @Column(name = "teacher_comments")
    private String teacherComments;

    @Column(name = "student_comments")
    private String studentComments;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(mappedBy = "examList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questionList;

    @Transient
    @Column(name = "question_points")
    private List<Integer> questionPoints;

    int type;

    public Exam() {

    }

    public Exam(String name, int type, int duration, Teacher creator, String teacherComments, String studentComments, Course course, List<Question> questionList, List<Integer> questionPoints) {
        this.examName = name;
        this.duration = duration;
        this.creator = creator;
        this.type = type;
        this.teacherComments = teacherComments;
        this.studentComments = studentComments;
        this.questionList = new ArrayList<>();
        this.questionList.addAll(questionList);
        this.questionPoints = questionPoints;
        this.course = course;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
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
