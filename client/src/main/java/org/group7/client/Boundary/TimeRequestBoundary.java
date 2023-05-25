package org.group7.client.Boundary;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.group7.client.Control.TimeRequestController;

import java.awt.event.ActionEvent;

public class TimeRequestBoundary {

    @FXML
    private TextField examIdTF;

    @FXML
    private Button sendReqBtn;

    @FXML
    private TextArea teachersNotesTA;

    private TimeRequestController controller;

    @FXML
    public void sendRequest() {
        controller.sendRequest(examIdTF.getText(), teachersNotesTA.getText());
    }

    @FXML
    public void initialize(){

        controller = new TimeRequestController(this);

    }

}
