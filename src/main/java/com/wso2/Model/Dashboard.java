package com.wso2.Model;

import java.util.ArrayList;

public class Dashboard {

    private ArrayList<Report> reports;

    public Dashboard() {
        this.reports = new ArrayList<Report>();
    }

    public  boolean addReport(Report report){
        try{
            reports.add(report);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
