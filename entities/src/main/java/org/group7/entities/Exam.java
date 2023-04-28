package org.group7.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @Column(name = "id")
    private int examId;

    @Column(name = "duration")
    private int duration;

    @Column(name = "teacher_comments")
    private String teacherComments;

    @Column(name = "student_comments")
    private String studentComments;

    @ManyToMany(mappedBy = "exams")
    private List<Student> studentList;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public Exam() {

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

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
