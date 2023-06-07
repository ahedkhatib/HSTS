package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "principals")
public class Principal extends User implements Serializable{

    @Transient
    @Column(name = "request_list")
    private List<ExtraTime> requestList;

    public Principal(){
        super();
        super.setType(3);
    }

    public Principal(String username, String password, String firstname, String lastname) {
        super(username, password, firstname, lastname, 3);
        this.requestList = new ArrayList<>();
    }

    public List<ExtraTime> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<ExtraTime> requestList) {
        this.requestList = requestList;
    }
}
