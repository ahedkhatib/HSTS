package org.group7.client.Control;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.PrinExamReportsBoundary;
import org.group7.client.Client;
import org.group7.client.Events.ExamsListEvent;
import org.group7.client.Events.StudentListEvent;
import org.group7.client.Events.SubjectsListEvent;
import org.group7.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PrinExamReportsController extends Controller {

    PrinExamReportsBoundary boundary;

    public PrinExamReportsController(PrinExamReportsBoundary boundary) {
        this.boundary = boundary;

        EventBus.getDefault().register(this);

        try {
            Client.getClient().sendToServer(new Message(null, "#GetExams"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Client.getClient().sendToServer(new Message(null, "#GetSubjects"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Message message = new Message(1, "#GetStudents");
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void GotSubjects(SubjectsListEvent event){

        List<Subject> subjects = event.getSubjects();
        List<String> names = new ArrayList<>();
        for(Subject s : subjects){
            names.add(s.getSubjectName());
        }
        boundary.subjects = subjects;
        boundary.subjectsNames = names;
        boundary.updateSubjectsComboBox();
    }

    @Subscribe
    public void gotExams(ExamsListEvent event) {

        List<Exam> exams = event.getExams();
        List<String> examNames = new ArrayList<>();
        for (Exam e : exams) {
            examNames.add(e.getExamName());
        }
        boundary.exams = exams;
        boundary.examNames = examNames;
        boundary.updateExamsComboBox();
    }

    @Subscribe
    public void GotStudents(StudentListEvent event){

        List<Student> students = event.getStudents();
        List<String> names = new ArrayList<>();
        for(Student s : students){
            names.add(s.getFirstName() + " "+ s.getLastName());
        }
        boundary.students = students;
        boundary.studentsNames = names;
        boundary.updateStudentsCB();
    }


    public String getStringExam(Exam selectedExam) {
        StringBuilder questionDetails = new StringBuilder(" ");
        for (Question q : selectedExam.getQuestionList()) {
            questionDetails.append(getStringQues(q));

        }
        String examDetails = "Exam Id: " + selectedExam.getExamId() + "\n" +
                "Exam name: " + selectedExam.getExamName() + "\n" +
                "This exam builded by: " + selectedExam.getCreator().getFirstName() + " " + selectedExam.getCreator().getLastName() + "\n" +
                "It is belong to the course: " + selectedExam.getCourse().getCourseName() + "\n" +
                "Duration: " + selectedExam.getDuration() + " h" + "\n" +
                "Questions: " + "\n" + questionDetails + "\n";
        return examDetails;
    }
    public String getStringQues(Question ques) {
        StringBuilder questionDetails = new StringBuilder(" ");
        questionDetails.append(ques.getInstructions() + " ");
        for (String s : ques.getAnswerList()) {
            questionDetails.append("\n" + "    " + s);
        }
        questionDetails.append("\n");
        questionDetails.append("The correct answer is: " + ques.getCorrectAnswer() + "\n");
        return questionDetails.toString();
    }
    public String getStringResult(Result res){
        String s="Result id: "+res.getResultId()+"\n"
                +"Exam: "+res.getExam().getExam().getExamName()+"\n"+
                "Grade: "+res.getGrade() +"\n"+
                "Teacher's notes: "+res.getTeacherNote();
        return s;

    }

}
