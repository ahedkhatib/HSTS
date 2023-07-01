package org.group7.client.Control;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.group7.client.App;
import org.group7.client.Client;

import java.awt.event.ActionEvent;
import java.util.Objects;

public class ServerConnectionController {

    @FXML
    private Button connectBtn;

    @FXML
    private TextField serverIp;

    @FXML
    public void connectToServer(){

        if(Objects.equals(serverIp.getText(), "")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Enter Valid IP Address!");
            alert.show();

            return;
        }

        Client.getClient().setHost(serverIp.getText());

        App.switchScreen("login");
    }

}
