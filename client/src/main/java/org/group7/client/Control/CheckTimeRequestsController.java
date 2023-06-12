package org.group7.client.Control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.CheckTimeRequestsBoundary;
import org.group7.client.Client;
import org.group7.entities.ExecutableExam;
import org.group7.entities.ExtraTime;
import org.group7.entities.Message;

import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.List;

public class CheckTimeRequestsController extends Controller {

    private CheckTimeRequestsBoundary boundary;

    public CheckTimeRequestsController(CheckTimeRequestsBoundary boundary) {
        EventBus.getDefault().register(this);
        this.boundary = boundary;
    }

    @Override
    public void unregisterController() {EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void getAllRequests(String req) {
        if(req.equals("#GetAllTimeRequests")) {
            try {
                Client.getClient().sendToServer(new Message(Client.getClient().getUser(), "#GetTimeRequests"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateRequest(String update, ExtraTime request) {

        if (update.equals("approve")) {
            try {
                Client.getClient().sendToServer(new Message(request, "#ApproveTimeRequest"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Client.getClient().sendToServer(new Message(request, "#DenyTimeRequest"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void setRequestListItems(List<ExtraTime> reqList) {

        if(reqList.size() == 0){
            boundary.getEmptyAP().setVisible(true);
            boundary.getListAP().setVisible(false);
            boundary.getListAP().setDisable(true);
        } else {
            boundary.getEmptyAP().setVisible(false);

            if(boundary.getActiveExam() == null && boundary.getActiveReq() == null){
                boundary.getListAP().setVisible(true);
                boundary.getListAP().setDisable(false);
            }

            boundary.getRequestList().setItems(FXCollections.observableList(reqList));
            boundary.getRequestList().refresh();
            boundary.getRequestList().getItems().sort(Comparator.comparing(ExtraTime::isStatus));
        }
    }

}
