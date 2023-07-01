package org.group7.client.Control;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.PrinShowDataBoundary;
import org.group7.client.Client;
import org.group7.client.Events.ExamsListEvent;
import org.group7.client.Events.StudentListEvent;
import org.group7.client.Events.SubjectsListEvent;
import org.group7.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PrinShowDataController extends Controller {

    PrinShowDataBoundary boundary;

    public PrinShowDataController(PrinShowDataBoundary boundary) {
        this.boundary = boundary;

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void getData(String req) {

        if (!req.equals("#GetData"))
            return;

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
    public void GotSubjects(SubjectsListEvent event) {

        List<Subject> subjects = event.getSubjects();
        List<String> names = new ArrayList<>();
        for (Subject s : subjects) {
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
    public void GotStudents(StudentListEvent event) {

        List<Student> students = event.getStudents();
        List<String> names = new ArrayList<>();

        for (Student s : students) {
            names.add(s.getFirstName() + " " + s.getLastName());
        }

        boundary.students = students;
        boundary.studentsNames = names;
        boundary.updateStudentsCB();
    }


    public String getStringExam(Exam selectedExam) {
        StringBuilder questionDetails = new StringBuilder(" ");

        int count = 0;

        for (Question q : selectedExam.getQuestionList()) {
            questionDetails.append(getStringQues(q, selectedExam, count));
            count++;
        }

        String examDetails = "Exam Id: " + (selectedExam.getCourse().getSubject().getSubjectId() * 10000 +
                selectedExam.getCourse().getCourseId() * 100 + selectedExam.getExamId()) + "\n\n" +
                "Exam name: " + selectedExam.getExamName() + "\n\n" +
                "This exam builded by: " + selectedExam.getCreator().getFirstName() + " " + selectedExam.getCreator().getLastName() + "\n\n" +
                "It is belong to the course: " + selectedExam.getCourse().getCourseName() + "\n\n" +
                "Duration: " + selectedExam.getDuration() + " m" + "\n\n" +
                "Questions: " + "\n" + questionDetails + "\n";

        return examDetails;
    }

    public String getStringQues(Question ques, Exam exam, int quesitonIndex) {

        StringBuilder questionDetails = new StringBuilder();
        questionDetails.append("Question id: ").append(ques.getSubject().getSubjectId() * 1000 + ques.getQuestionId()).append("\n");
        questionDetails.append(ques.getInstructions()).append(" ");

        for (String s : ques.getAnswerList()) {
            questionDetails.append("\n" + "  -  ").append(s).append("\n");
        }

        if(exam != null) {
            questionDetails.append("Question points = ").append(exam.getQuestionPoints().get(quesitonIndex)).append("\n");
        }

        questionDetails.append("The correct answer is: ").append(ques.getCorrectAnswer()).append("\n______________________________________\n");

        return questionDetails.toString();
    }

    public String getStringResult(Result res) {
        String s = "Result id: " + res.getResultId() + "\n"
                + "Exam: " + res.getExam().getExam().getExamName() + "\n" +
                "Grade: " + res.getGrade() + "\n" +
                "Teacher's notes: " + res.getTeacherNote();
        return s;

    }

}
