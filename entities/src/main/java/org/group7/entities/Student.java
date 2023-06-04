package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "students")
public class Student extends User implements Serializable {

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "student_exam",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "exam_id"))
    private List<ExecutableExam> examList;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courseList;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> resultList;

    public Student() {
        super();
        super.setType(1);
    }

    public Student(String username, String password, String firstname, String lastname) {
        super(username, password, firstname, lastname, 1);
        this.resultList = new ArrayList<>();
        this.examList = new ArrayList<>();
        this.courseList = new ArrayList<>();
    }

    public List<Result> getResultList() {
        return resultList;
    }

    public void setResultList(List<Result> resultList) {
        this.resultList = resultList;
    }

    public List<ExecutableExam> getExamList() {
        return examList;
    }

    public void setExamList(List<ExecutableExam> examList) {
        this.examList = examList;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }
    public double getAvg(){
        int examNum=0,gradesSum=0;
        if(this.resultList!=null)
        {
            for(Result r:this.resultList){
                gradesSum+=r.getGrade();
                examNum++;
            } }else{return 0;}
        return gradesSum/examNum;
    }
    public double getMedian(){
        int examNum = getResultList().size();
        List<Integer> grades = new ArrayList<>();
        for(Result r : getResultList()){
            grades.add(r.getGrade());
        }
        Collections.sort(grades);
        double median = 0;

        if(examNum % 2 == 0){
            int midIndex = examNum / 2;
            median = (grades.get(midIndex - 1) + grades.get(midIndex)) / 2;
        } else {
            int midIndex = examNum / 2;
            median = (grades.get(midIndex ));
        }
        return median;
    }
    public int[] getDistribution(){
        int[] updatedDistribution = new int[10];
        List<Result> newList = getResultList();
        int[] gradeRanges = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        for( int j = 0; j< getResultList().size(); j++) {
            int grade = newList.get(j).getGrade();
            for (int i = 0; i < gradeRanges.length - 1; i++) {
                if (grade >= gradeRanges[i] && grade < gradeRanges[i + 1]) {
                    updatedDistribution[i]++;
                    break;
                }
            }
        }
        return updatedDistribution;
    }
}
