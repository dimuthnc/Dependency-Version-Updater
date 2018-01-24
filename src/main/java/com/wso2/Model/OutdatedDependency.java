/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

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

    public OutdatedDependency() {
        super();

    }


}
