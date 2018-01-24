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

package com.wso2.DependencyProcessor;

import com.wso2.Constants;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TODO:Class level comment
 */
public class WSO2DependencyUpdater extends DependencyUpdater {

    public Model updateModel(Model model,Properties properties){

        Model newModel = model.clone();
        Properties localProperties = model.getProperties();
        Model dependencyModel =updateToLatest(model.getDependencies(), properties, localProperties);
        newModel.setDependencies(dependencyModel.getDependencies());
        newModel.setProperties(dependencyModel.getProperties());

        if(model.getDependencyManagement()!=null){

            Model  dependencyManagementModel = updateToLatest(model.getDependencyManagement().getDependencies(),properties,localProperties);

            DependencyManagement dependencyManagement =newModel.getDependencyManagement();
            dependencyManagement.setDependencies(dependencyManagementModel.getDependencies());
            newModel.setDependencyManagement(dependencyManagement);
            Properties managementProperties = dependencyManagementModel.getProperties();

            for (Object property : managementProperties.keySet()) {

                newModel.addProperty(property.toString(),managementProperties.getProperty(property.toString()));

            }
        }
        return newModel;
    }

    private Model updateToLatest(List<Dependency> dependencies, Properties globalProperties, Properties localProperties) {

        Model model = new Model();
        List<Dependency> updatedDependencies = new ArrayList<Dependency>();
        List<Dependency> outdatedDependencies = new ArrayList<Dependency>();

        for (Dependency dependency : dependencies) {

            String currentVersion = dependency.getVersion();
            String groupId = dependency.getGroupId();

            if(groupId.contains("org.wso2")){

                if(currentVersion != null && (currentVersion.startsWith("${") && currentVersion.endsWith("}"))){

                    String versionKey = currentVersion.substring(2,currentVersion.length()-1);
                    String version = globalProperties.getProperty(versionKey);

                    if(version==null){

                        version=localProperties.getProperty(versionKey);
                    }

                    String latestVersion = MavenCentralConnector.getLatestVersion(dependency);

                    if(version!= null && latestVersion!=null && !latestVersion.equals(Constants.EMPTY_STRING) && !version.equals(latestVersion) ){
                        System.out.println(dependency.getGroupId()+" : "+dependency.getArtifactId() +" updated from the version :"+ version+" to latest version :"+latestVersion);
                        model.addProperty(versionKey,latestVersion);

                    }
                }
                else if(currentVersion !=null ){
                    Dependency dependencyClone = dependency.clone();
                    String LatestVersion =MavenCentralConnector.getLatestVersion(dependency);
                    if(!LatestVersion.equals(currentVersion)){
                        System.out.println(dependency.getGroupId()+" : "+dependency.getArtifactId() +" updated from the version :"+ currentVersion+" to latest version :"+LatestVersion);
                        outdatedDependencies.add(dependency);
                        dependencyClone.setVersion(LatestVersion);
                        updatedDependencies.add(dependencyClone);

                    }
                }
            }
        }
        dependencies.removeAll(outdatedDependencies);
        dependencies.addAll(updatedDependencies);
        model.setDependencies(dependencies);
        return model;
    }
}
