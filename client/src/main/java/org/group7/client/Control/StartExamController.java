package org.group7.client.Control;

import com.sun.scenario.animation.shared.TimerReceiver;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.StartExamBoundary;
import org.group7.client.Client;
import org.group7.client.Events.StartExamEvent;
import org.group7.entities.*;
import com.sun.javafx.binding.StringFormatter;

import java.io.IOException;
import java.util.*;

import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;


import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.crypto.Cipher;

import static java.lang.System.out;

public class StartExamController extends Controller {

    private StartExamBoundary boundary;

    private ExecutableExam exam;

    private Thread timer;

    private volatile boolean isTimerRunning = false;

    private int durationSeconds;

    private int elapsedSeconds = 0;

    public StartExamController(StartExamBoundary boundary) {
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    @Override
    public void unregisterController(){
        EventBus.getDefault().unregister(this);
    }

    public void getExam(String examId) {
        try {
            Client.getClient().sendToServer(new Message(examId, "#StartExam"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startTimer() {

        timer = new Thread(() -> {

            isTimerRunning = true;

            try {
                for (int i = durationSeconds; i >= 0; i--) {
                    Thread.sleep(1000);
                    if (!isTimerRunning) {
                        return;
                    }
                    durationSeconds--;
                    elapsedSeconds++;
                    boundary.setTimeSeconds(i);
                }

                isTimerRunning = false;
                finishExam(true);

            } catch (InterruptedException e) {
                System.out.println("Timer Interrupted!");
            }
        });

        timer.setDaemon(true);
        timer.start();
    }

    @Subscribe
    public void updateTimer(ExtraTime request) {
        if (Objects.equals(request.getExam().getExamId(), exam.getExamId())) {
            durationSeconds += (request.getExtra() * 60);

            if (isTimerRunning) {
                isTimerRunning = false;
                try {
                    timer.join();
                } catch (InterruptedException e) {
                    System.out.println("Time Extended");
                }
            }

            startTimer();
        }
    }

    public boolean checkId(String id) {
        if (id.length() != 9 || !id.matches("\\d+")) {

            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Id is 9 digits, all numbers!"
            );
            alert.setTitle("Error!");
            alert.setHeaderText("Error: Incorrect Input");
            alert.show();

            return false;
        }
        if(!Objects.equals(((Student) Client.getClient().getUser()).getStudentIdNum(), id)){
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Incorrect Id!"
            );
            alert.setTitle("Error!");
            alert.setHeaderText("Error: Id doesn't match");
            alert.show();

            return false;
        }

        durationSeconds = exam.getTime() * 60;
        startTimer();

        return true;
    }

    @Subscribe
    public void startExam(StartExamEvent event) {

        AnchorPane pane = null;

        out.println(event.isExists());

        if (!event.isExists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Exam Doesn't Exist!"
            );
            alert.setTitle("Error!");
            alert.setHeaderText("Error: Incorrect Input");
            alert.show();
        } else {
            if (event.getType() == 1) {
                pane = boundary.openAutoExam();
                exam = event.getExam();
                boundary.setQuestions(event.getExam());
            } else {
                pane = boundary.openManualExam();
                exam = event.getExam();
                durationSeconds = exam.getTime() * 60;
            }
        }
    }

    public void createWord() {
        Exam ex = exam.getExam();
        List<Question> questionList = ex.getQuestionList();
        XWPFDocument document = new XWPFDocument();

        String fileName = ex.getExamName() + "-" + exam.getExamId() + ".docx";
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();

            titleRun.setText(ex.getExamName());
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.addBreak();
            titleRun.setFontSize(24);

            int questionNumber = 1;
            for (Question question : questionList) {
                XWPFParagraph questionParagraph = document.createParagraph();
                questionParagraph.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun questionRun = questionParagraph.createRun();

                questionRun.setText("Question " + questionNumber + ": " + question.getInstructions());
                questionRun.addBreak();
                String[] answers = question.getAnswerList();
                int answerCount = 1;
                for (String a : answers) {
                    questionRun.setText(answerCount + ". " + a);
                    questionRun.addBreak();
                    answerCount++;
                }
                questionRun.setText("Answer: ________________________");
                questionRun.addBreak();
                questionRun.addBreak();
                questionNumber++;
            }
            document.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startTimer();
    }

    public void finishExam(boolean flag) {
        if (isTimerRunning && timer != null && timer.isAlive()) {
            timer.interrupt();
        }

        double elapsed = elapsedSeconds / 60.0;

        if(exam.getExam().getType() == 1){
            List<ToggleGroup> answersToggle = boundary.getToggleGroups();

            List<Integer> points = exam.getExam().getQuestionPoints();
            List<Question> questions = exam.getExam().getQuestionList();

            HashMap<Question, Integer> answers = new HashMap<>();

            int grade = 0;

            for (int i = 0; i < questions.size(); i++) {
                if (answersToggle.get(i).getSelectedToggle() != null && questions.get(i).getCorrectAnswer() == answersToggle.get(i).getToggles().indexOf(answersToggle.get(i).getSelectedToggle())) {
                    grade += points.get(i);
                } else {
                    answers.put(questions.get(i), -1);
                }
                answers.put(questions.get(i), answersToggle.get(i).getToggles().indexOf(answersToggle.get(i).getSelectedToggle()));
            }

            //update average
            int n = exam.getStudentList().size();
            double avg = exam.getAverage();

            avg = (avg * n + grade) / (n + 1);

            Result result = new Result(grade, (Student) Client.getClient().getUser(), "", exam, elapsed, flag, answers);

            //update median
            List<Student> students = exam.getStudentList();
            List<Result> results = new ArrayList<>();

            for (Student student : students) {
                results.addAll(student.getResultList());
            }

            results.add(result);

            List<Integer> grades = new ArrayList<>();
            for (Result r : results) {
                grades.add(r.getGrade());
            }

            Collections.sort(grades);
            double median = 0;

            n++;

            if (n % 2 == 0) {
                int midIndex = n / 2;
                median = (grades.get(midIndex - 1) + grades.get(midIndex)) / 2.0;
            } else {
                int midIndex = n / 2;
                median = (grades.get(midIndex));
            }

            //update distribution
            int[] updatedDistribution = exam.getDistribution().clone();
            int[] gradeRanges = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

            for (int i = 0; i < gradeRanges.length - 1; i++) {
                if (grade >= gradeRanges[i] && grade < gradeRanges[i + 1]) {
                    updatedDistribution[i]++;
                    break;
                }
            }

            //Send to server
            double[] arr = {avg, median};
            Object[] objects = {result, Client.getClient().getUser(), exam, arr, updatedDistribution};

            try {
                Client.getClient().sendToServer(new Message(objects, "#FinishExam"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            boundary.getManualAp().setDisable(true);
            boundary.getManualAp().setDisable(true);

            ManualResult result = new ManualResult(boundary.getManualSolution(), exam, (Student) Client.getClient().getUser());

            try {
                Client.getClient().sendToServer(new Message(result, "#FinishManExam"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void examEndedMessage(String message) {

        if(!message.startsWith("Exam"))
            return;

        boundary.getAutoAp().setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                message
        );

        alert.show();
    }

}
