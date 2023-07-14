package org.group7.client;

import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.group7.client.Events.*;
import org.group7.entities.*;
import org.group7.client.ocsf.AbstractClient;
import org.group7.client.Events.ExecutableExamEvent;


import java.io.IOException;
import java.util.List;

public class Client extends AbstractClient {

    private static Client client = null;

    private static User user = null;

    private Client(String host, int port) {
        super(host, port);
    }

    public User getUser() {
        return user;
    }


    @Override
    protected void handleMessageFromServer(Object msg) {
        Message message = (Message) msg;
        String post = message.getMessage();

        if (post.startsWith("#Login_")) {
            LoginEvent event = new LoginEvent(new Message(message.getObject(), post.substring(7)));

            if (event.isSuccess()) {
                user = (User) message.getObject();
            }

            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        } else if (post.startsWith("#Logout")) {
            user = null;
        } else if (post.startsWith("#ExtraTime_")) {

            String temp = post.substring(11);

            if (temp.equals("Fail")) {
                String m = ("Exam: " + message.getObject() + " could not be found!");
                Platform.runLater(() -> {
                    EventBus.getDefault().post(m);
                });
            } else {
                Platform.runLater(() -> {
                    EventBus.getDefault().post("Request Sent");
                });
            }
        } else if (post.startsWith("#GotRequestsList")) {
            List<ExtraTime> reqs = (List<ExtraTime>) message.getObject();
            Platform.runLater(() -> {
                EventBus.getDefault().post(reqs);
            });
        } else if (post.startsWith("#GotTeacherExams")) {
            CreateExecutableEvent event = new CreateExecutableEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        } else if (post.startsWith("#StartExam_")) {
            StartExamEvent event = new StartExamEvent(message.getObject(), post);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        } else if (post.startsWith("#TimeRequestApproved")) {
            ExtraTime extraTime = (ExtraTime) message.getObject();
            Platform.runLater(() -> {
                EventBus.getDefault().post(extraTime);
            });
        } else if (post.startsWith("#ExamFinished")) {
            String s = "Exam Finished!";
            Platform.runLater(() -> {
                EventBus.getDefault().post(s);
            });
        } else if (post.startsWith("#GotSubjects")) {
            SubjectsListEvent event = new SubjectsListEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        }else if (post.startsWith("#getCourses")) {
            CoursesListEvent event = new CoursesListEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        }else if (post.startsWith("#GotStudents")) {
            StudentListEvent event = new StudentListEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        }else if (post.startsWith("#gotExams")) {
            ExamsListEvent event = new ExamsListEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        }else if (post.startsWith("#getTeachers")) {
            TeachersListEvent event = new TeachersListEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        }else if (post.startsWith("#PrepareQuestion_")) {
            QuestionEvent event = new QuestionEvent(new Message(message.getObject(), post.substring(12)));
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        } else if (post.startsWith("#ExamSaved")) {
            MessageEvent event = new MessageEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        } else if(post.startsWith("#GotTeacherCourses")){
            CoursesEvent event = new CoursesEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        } else if(post.startsWith("#GotStudentResults")){
            ResultListEvent event = new ResultListEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        }else if (post.startsWith("#GotExecutableExam")) {
            ExecutableExamEvent event = new ExecutableExamEvent(message);
            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        } else {
            Platform.runLater(() -> {
                EventBus.getDefault().post(message.getMessage());
            });
        }
    }

    public static Client getClient() {
        return client;
    }

    public static boolean newClient(String host) throws IOException {
        client = new Client(host, 3000);
        try {
            client.openConnection();

            return true;
        }catch (IOException e){
            return false;
        }
    }

}
