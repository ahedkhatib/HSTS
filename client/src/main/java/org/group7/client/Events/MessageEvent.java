package org.group7.client.Events;

import org.group7.entities.Message;

public class MessageEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public MessageEvent(Message message) {
        this.message = message;
    }

}
