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

package org.wso2.DependencyProcessor;

import org.wso2.Constants;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.wso2.Model.OutdatedDependency;
import org.wso2.ReportGenerator.UpdatedDependencyReporter;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TODO:Class level comment
 */
public class WSO2DependencyUpdater extends DependencyUpdater {

    public Model updateModel(Model model,Properties properties,String pomLocation){


        Model newModel = model.clone();
        Properties localProperties = model.getProperties();
        //Model dependencyModel =updateToLatest(model.getDependencies(), properties, localProperties);
        Model dependencyModel =updateToLatestInLocation(model.getProjectDirectory().toString(),model.getDependencies(), properties, localProperties);
        newModel.setDependencies(dependencyModel.getDependencies());
        newModel.setProperties(dependencyModel.getProperties());

        if(model.getDependencyManagement()!=null){

            //Model  dependencyManagementModel = updateToLatest(model.getDependencyManagement().getDependencies(),properties,localProperties);
            Model  dependencyManagementModel = updateToLatestInLocation(pomLocation,model.getDependencyManagement().getDependencies(),properties,localProperties);

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
    private Model updateToLatestInLocation(String pomLocation,List<Dependency> dependencies, Properties globalProperties, Properties localProperties) {
        Model model = new Model();
        List<Dependency> updatedDependencies = new ArrayList<Dependency>();
        List<Dependency> outdatedDependencies = new ArrayList<Dependency>();
        List<OutdatedDependency> outdatedDependencyList = new ArrayList<OutdatedDependency>();
        List<Dependency> dependenciesNotFound = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {

            String currentVersion = dependency.getVersion();
            String groupId = dependency.getGroupId();
            String artifactId = dependency.getArtifactId();

            if (groupId.contains("org.wso2") && !artifactId.contains("logging")) {

                String latestVersion = MavenCentralConnector.getLatestVersion(dependency);

                if(latestVersion.equals(Constants.EMPTY_STRING)){
                    dependenciesNotFound.add(dependency);
                }
                else{
                    if(currentVersion != null && (currentVersion.startsWith("${") && currentVersion.endsWith("}"))){

                        String versionKey = currentVersion.substring(2,currentVersion.length()-1);
                        String version = localProperties.getProperty(versionKey);

                        if(version==null){

                            version=globalProperties.getProperty(versionKey);
                        }


                        Dependency dependencyClone = dependency.clone();
                        if(version!=null && !latestVersion.equals(version)){
                            outdatedDependencies.add(dependency);
                            dependencyClone.setVersion(latestVersion);
                            updatedDependencies.add(dependencyClone);
                            dependency.setVersion(version);

                            OutdatedDependency outdatedDependency = new OutdatedDependency();
                            outdatedDependency.setLatestVersion(latestVersion);
                            outdatedDependency.setVersion(version);
                            outdatedDependency.setGroupId(dependency.getGroupId());
                            outdatedDependency.setArtifactId(dependency.getArtifactId());
                            outdatedDependency.setNewVersions(MavenCentralConnector.getVersionList(dependency));
                            outdatedDependencyList.add(outdatedDependency);


                        }

                    }
                    else if(currentVersion !=null ){
                        Dependency dependencyClone = dependency.clone();
                        if(!latestVersion.equals(currentVersion)){
                            outdatedDependencies.add(dependency);
                            dependencyClone.setVersion(latestVersion);
                            updatedDependencies.add(dependencyClone);


                            OutdatedDependency outdatedDependency = new OutdatedDependency();
                            outdatedDependency.setLatestVersion(latestVersion);
                            outdatedDependency.setVersion(currentVersion);
                            outdatedDependency.setGroupId(dependency.getGroupId());
                            outdatedDependency.setArtifactId(dependency.getArtifactId());
                            outdatedDependency.setNewVersions(MavenCentralConnector.getVersionList(dependency));
                            outdatedDependencyList.add(outdatedDependency);


                        }
                    }

                }


            }
        }


        dependencies.removeAll(outdatedDependencies);
        dependencies.addAll(updatedDependencies);
        model.setDependencies(dependencies);
        model.setProperties(localProperties);
        UpdatedDependencyReporter.generateDependencyUpdateReport(pomLocation,outdatedDependencyList);
        UpdatedDependencyReporter.generateDependencyNotFoundReport(pomLocation+"-notfound",  dependenciesNotFound);
        return model;
    }

    private String getProperty(String key,Properties localProperties, Properties globalProperties){
        String value = localProperties.getProperty(key);
        if(value==null){
            value = globalProperties.getProperty(key);
        }
        return value;
    }

    private String getVersionKey(String propertyKey){
        return propertyKey.substring(2,propertyKey.length()-1);
    }






}
