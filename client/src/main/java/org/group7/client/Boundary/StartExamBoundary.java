package org.group7.client.Boundary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.group7.client.Control.StartExamController;
import javafx.stage.FileChooser.ExtensionFilter;


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
    private TextField file;

    @FXML
    private TextField statusLabel;

    private String temp;

    @FXML
    void startExam(ActionEvent event) {
        controller.getExam(examNumber.getText());
        temp = examNumber.getText();
    }

    @FXML
    void uploadExam(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Word Files", "*.docx")
        );

        Stage stage = new Stage();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null && !files.isEmpty()) {
            File uploadedFile = files.get(0);
            try {
                String uploadedAnswer = new String(Files.readAllBytes(Paths.get(uploadedFile.toURI())),
                        StandardCharsets.UTF_8);
                file.setText(uploadedFile.getPath());
                statusLabel.setText("Upload successful: " + uploadedFile.getName());
               //Perform further logic with uploadedAnswer if needed
            } catch (IOException e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        } else {
            statusLabel.setText("Upload canceled.");
        }

    }

    @FXML
    void downloadExam(ActionEvent event)
    {
        controller.saveToWord()
    }

//    public void createWord() throws IOException {
//
//        XWPFDocument document = new XWPFDocument();
//
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Save Test File");
//        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Word Files", "*.docx"));
//        Stage stage = new Stage();
//        File selectedFile = fileChooser.showSaveDialog(stage);
//
//        if (selectedFile != null) {
//            try (FileOutputStream out = new FileOutputStream(selectedFile)) {
//                XWPFParagraph titleParagraph = document.createParagraph();
//                titleParagraph.setAlignment(ParagraphAlignment.LEFT);
//                //XWPFRun titleRun = titleParagraph.createRun();
//                //titleRun.setText(finishedTest.getTestName());
//
//                List<Question> questions =
//                int questionNumber = 1;
//                for ( question : currQuestions) {
//                    XWPFParagraph questionParagraph = document.createParagraph();
//                    questionParagraph.setAlignment(ParagraphAlignment.LEFT);
//                    XWPFRun questionRun = questionParagraph.createRun();
//
//                    questionRun.setText("Question " + questionNumber + ": " + question.getQuestion().getQuestionText());
//                    questionRun.addBreak();
//                    questionRun.setText("a. " + question.getQuestion().getAnswer_1());
//                    questionRun.addBreak();
//                    questionRun.setText("b. " + question.getQuestion().getAnswer_2());
//                    questionRun.addBreak();
//                    questionRun.setText("c. " + question.getQuestion().getAnswer_3());
//                    questionRun.addBreak();
//                    questionRun.setText("d. " + question.getQuestion().getAnswer_4());
//                    questionRun.addBreak();
//                    questionRun.setText("Answer: _____________________");
//                    questionRun.addBreak();
//                    questionRun.addBreak();
//
//                    questionNumber++;
//                }
//
//                document.write(out);
//            }
//        }
//    }

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
