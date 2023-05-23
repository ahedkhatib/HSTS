package org.group7.client.Events;

import org.group7.entities.Message;
import org.group7.entities.User;

public class LoginEvent {

    private boolean success;

    private User user;

    private String message;

    public LoginEvent(Message message) {
        this.success = message.getObject() != null;
        this.user = (User) message.getObject();
        this.message = message.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
