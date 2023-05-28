package org.group7.client.Boundary;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.group7.client.Control.StartExamController;

public class StartExamBoundary extends Boundary {

    private StartExamController controller;

    @FXML
    private Button startButton;

    @FXML
    private AnchorPane introAp;

    @FXML
    private AnchorPane autoAp;

    @FXML
    private AnchorPane manualAp;

    @FXML
    private TextField examNumber;

    @FXML
    void startExam(ActionEvent event) {
        controller.getExam(examNumber.getText());
    }

    public AnchorPane openAutoExam() {
        introAp.setDisable(true);
        introAp.setVisible(false);

        autoAp.setDisable(false);
        autoAp.setVisible(true);

        return autoAp;
    }

    public AnchorPane openManualExam() {
        introAp.setDisable(true);
        introAp.setVisible(false);

        manualAp.setDisable(false);
        manualAp.setVisible(true);

        return manualAp;
    }

    @FXML
    void initialize() {
        controller = new StartExamController(this);
        super.setController(controller);
    }

}
