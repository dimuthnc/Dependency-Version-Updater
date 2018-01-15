package com.wso2.Model;

import java.util.ArrayList;

public class Report {
    private ArrayList<String> outdatedDependencies;

    public ArrayList<String> getOutdatedDependencies() {
        return outdatedDependencies;
    }

    public void setOutdatedDependencies(ArrayList<String> outdatedDependencies) {
        this.outdatedDependencies = outdatedDependencies;
    }

    public Report(ArrayList<String> outdatedDependencies) {

        this.outdatedDependencies = outdatedDependencies;
    }
}
