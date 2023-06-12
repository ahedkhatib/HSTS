package org.group7.client.Control;

import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.App;
import org.group7.client.Boundary.LoginBoundary;
import org.group7.client.Client;
import org.group7.client.Events.LoginEvent;
import org.group7.entities.Message;
import org.group7.entities.User;

import java.util.Objects;

public class LoginController extends Controller{

    private LoginBoundary boundary;

    public void login(String username, String password) {
        Object[] obj = {username, password};

        Message message = new Message(obj, "#Login");
        try {
            Client.getClient().sendToServer(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public LoginController(LoginBoundary boundary) {
        EventBus.getDefault().register(this);
        this.boundary = boundary;
    }

    @Subscribe
    public void checkLogin(LoginEvent event) {
        if (event.isSuccess()) {
            User user = event.getUser();

            if (Objects.equals(event.getMessage(), "Success")) {
                try {
                    Client.getClient().sendToServer(new Message(null, "#NewClient"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                unregisterController();
                App.switchScreen("homepage");
            } else {
                Alert alert;
                alert = new Alert(Alert.AlertType.ERROR,
                        String.format("User " + user.getFirstName() + " " + user.getLastName() + "\nIs already connected!")
                );
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Username or Password are incorrect!"
            );
            alert.setTitle("Error!");
            alert.setHeaderText("Error: Incorrect Input");
            alert.show();
        }
    }

    @Override
    public void unregisterController(){
        EventBus.getDefault().unregister(this);
    }

}
