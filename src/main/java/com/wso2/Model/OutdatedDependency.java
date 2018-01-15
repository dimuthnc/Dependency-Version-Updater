package com.wso2.Model;

import java.util.ArrayList;

public class OutdatedDependency extends Dependency {
    private String latestVersion;
    private ArrayList<String> newVersions;

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public ArrayList<String> getNewVersions() {
        return newVersions;
    }

    public void setNewVersions(ArrayList<String> newVersions) {
        this.newVersions = newVersions;
    }



    public OutdatedDependency(String groupId, String artifactId, String currentVersion) {
        super(groupId, artifactId, currentVersion);

    }


}
