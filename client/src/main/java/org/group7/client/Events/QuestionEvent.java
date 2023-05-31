package org.group7.client.Events;

import org.group7.entities.Message;
import org.group7.entities.Question;
import org.group7.entities.User;

public class QuestionEvent {
    private boolean success;

    private Question quest;

    private String message;

    public QuestionEvent(Message message) {
        this.success = message.getObject() != null;
        this.quest = (Question) message.getObject();
        this.message = message.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Question getUser() {
        return quest;
    }

    public void setQuesttion(Question quest) {
        this.quest = quest;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
