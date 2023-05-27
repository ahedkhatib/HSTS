package org.group7.client.Control;

import org.group7.client.Boundary.HomepageBoundary;
import org.group7.client.Client;
import org.group7.entities.Message;
import org.group7.entities.User;

public class HomepageController extends Controller{

    private HomepageBoundary boundary;

    public HomepageController(HomepageBoundary boundary){
        this.boundary = boundary;
    }

    public User getUser(){
        return Client.getClient().getUser();
    }

    public void logout() {
        try {
            Client.getClient().sendToServer(new Message(getUser(), "#Logout"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
