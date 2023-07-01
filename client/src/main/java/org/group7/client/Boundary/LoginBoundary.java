package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.group7.client.Client;
import org.group7.client.Control.LoginController;

public class LoginBoundary extends Boundary {

    private LoginController controller;

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private TextField usernameTF;

    @FXML
    void login(ActionEvent event) {
        controller.login(usernameTF.getText(), passwordTF.getText());
    }

    @Override
    public LoginController getController(){
        return controller;
    }

    @FXML
    public void initialize(){

        controller = new LoginController(this);
        super.setController(controller);

    }

}
