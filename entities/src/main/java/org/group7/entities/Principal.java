package org.group7.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "principals")
public class Principal extends User implements Serializable{

    @Transient
    @Column(name = "report_list")
    private List<Report> reportList;

    public Principal(){
        super();
        super.setType(3);
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }
}
