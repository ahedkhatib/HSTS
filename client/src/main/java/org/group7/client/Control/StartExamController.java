package org.group7.client.Control;

import com.sun.scenario.animation.shared.TimerReceiver;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.StartExamBoundary;
import org.group7.client.Client;
import org.group7.client.Events.StartExamEvent;
import org.group7.entities.ExecutableExam;
import org.group7.entities.ExtraTime;
import org.group7.entities.Message;

import java.util.Objects;

public class StartExamController extends Controller {

    private StartExamBoundary boundary;

    private ExecutableExam exam;

    private Thread timer;

    private volatile boolean isTimerRunning = false;

    private int durationSeconds;

    public StartExamController(StartExamBoundary boundary) {
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    public void getExam(String examId) {
        try{
            Client.getClient().sendToServer(new Message(examId, "#StartExam"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startTimer(){

        timer = new Thread(()->{

            isTimerRunning = true;

            try {
                for (int i = durationSeconds; i >= 0; i--) {
                    Thread.sleep(1000);
                    if(!isTimerRunning){
                        return;
                    }
                    durationSeconds--;
                    boundary.setTimeSeconds(i);
                    System.out.println(boundary.getTimeSeconds());
                }
            } catch (InterruptedException e) {
                System.out.println("Time Extended!");
            }
        });

        timer.setDaemon(true);
        timer.start();
    }

    @Subscribe
    public void updateTimer(ExtraTime request) {
        if(Objects.equals(request.getExamId(), exam.getExamId())){
            durationSeconds += (request.getExtra() * 60);

            if(isTimerRunning){
                isTimerRunning = false;
                try {
                    timer.join();
                } catch (InterruptedException e){
                    System.out.println("Time Extended");
                }
            }

            startTimer();
        }
    }

    public boolean checkId(String id){
        if(id.length() != 9 || !id.matches("\\d+")){

            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Id is 9 digits, all numbers!"
            );
            alert.setTitle("Error!");
            alert.setHeaderText("Error: Incorrect Input");
            alert.show();

            return false;
        }

        durationSeconds = exam.getTime() * 60 ;
        startTimer();

        return true;
    }

    @Subscribe
    public void startExam(StartExamEvent event){

        AnchorPane pane = null;

        if(!event.isExists()){
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Exam Doesn't Exist!"
            );
            alert.setTitle("Error!");
            alert.setHeaderText("Error: Incorrect Input");
            alert.show();
        } else {
            if(event.getType() == 1){
                pane = boundary.openAutoExam();
                exam = event.getExam();
                boundary.setQuestions(event.getExam());
            } else {
                pane = boundary.openManualExam();
                exam = event.getExam();
            }
        }
    }

}
