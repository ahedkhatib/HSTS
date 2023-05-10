package org.group7.client.Events;

import org.group7.entities.Message;
import org.group7.entities.Result;

import java.util.List;

public class ResultListEvent {

    List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public ResultListEvent(Message message){
        this.results = (List<Result>) message.getObject();
    }

}
