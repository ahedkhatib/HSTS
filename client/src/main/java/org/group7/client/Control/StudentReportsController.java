package org.group7.client.Control;

import javafx.collections.FXCollections;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.StudentReportsBoundary;
import org.group7.client.Client;
import org.group7.client.Events.ResultListEvent;
import org.group7.entities.*;

import java.util.ArrayList;
import java.util.List;

public class StudentReportsController extends Controller {

    private StudentReportsBoundary boundary;

    public StudentReportsController(StudentReportsBoundary boundary) {
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    @Override
    public void unregisterController() {EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void getResults(String req) {

        if(!req.equals("#GetStudentResults"))
            return;

        Message message = new Message(Client.getClient().getUser(), "#GetStudentResults");
        try {
            Client.getClient().sendToServer(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void setResults(ResultListEvent event){

        List<Result> allResults = event.getResults();

        List<Result> results = new ArrayList<>();

        for(Result result : allResults){
            if(result.isStatus()){
                results.add(result);
            }
        }

        if(results.size() == 0){
            boundary.getEmptyAP().setVisible(true);
            boundary.getListAP().setVisible(false);
            boundary.getListAP().setDisable(true);
        } else {

            boundary.setResultList(results);

            boundary.getEmptyAP().setVisible(false);

            if(boundary.getActiveResult() == null){
                boundary.getListAP().setVisible(true);
                boundary.getListAP().setDisable(false);
            }

            boundary.getResultListView().setItems(FXCollections.observableList(results));
            boundary.getResultListView().refresh();
        }
    }


}
