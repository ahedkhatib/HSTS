package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.client.Control.CheckTimeRequestsController;
import org.group7.entities.ExtraTime;

public class CheckTimeRequestsBoundary extends Boundary{

    @FXML
    private ListView<ExtraTime> requestList;

    @FXML
    private AnchorPane listAP;

    @FXML
    private AnchorPane requestAP;

    @FXML
    private Button approveBtn;

    @FXML
    private Button backBtn;

    @FXML
    private TextArea commentText;

    @FXML
    private Button denyBtn;

    @FXML
    private Text titleText;

    private CheckTimeRequestsController controller;

    private ExtraTime activeReq;

    @Override
    public CheckTimeRequestsController getController(){
        return controller;
    }

    @FXML
    public void backToList(ActionEvent event) {

        controller.getAllRequests();

        requestAP.setDisable(true);
        requestAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setDisable(true);
    }

    @FXML
    public void approveReq(ActionEvent event) {
        controller.updateRequest("approve", activeReq);
        backToList(event);
    }

    @FXML
    public void denyReq(ActionEvent event) {
        controller.updateRequest("deny", activeReq);
        backToList(event);
    }

    public ListView<ExtraTime> getRequestList(){
        return requestList;
    }

    @FXML
    public void initialize() {
        controller = new CheckTimeRequestsController(this);

        requestAP.setDisable(true);
        requestAP.setVisible(false);

        requestList.setCellFactory(param -> new ListCell<ExtraTime>() {
            @Override
            public void updateItem(ExtraTime req, boolean empty) {
                super.updateItem(req, empty);

                if (empty || req == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    String teacherMsg = req.getTeacherMessage();

                    Text reqText = new Text(req.getRequestId() + ": " + teacherMsg.substring(0,
                            Math.min(teacherMsg.length(), 20)) + "...");
                    reqText.setFont(Font.font("Arial", 16));
                    Text arrow = new Text(">");
                    arrow.setFont(Font.font("Arial", 16));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    hbox.getChildren().addAll(reqText, spacer, arrow);

                    setGraphic(hbox);
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);

                if (selected && !isEmpty()) {
                    activeReq = getItem();

                    requestAP.setDisable(false);
                    requestAP.setVisible(true);

                    listAP.setDisable(true);
                    listAP.setDisable(false);

                    titleText.setText("Request for exam: " + activeReq.getExamId());

                    commentText.setText(activeReq.getTeacherMessage());
                }
            }
        });

        controller.getAllRequests();
    }
}

