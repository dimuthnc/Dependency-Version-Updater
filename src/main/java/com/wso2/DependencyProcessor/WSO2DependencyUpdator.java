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
import org.apache.maven.model.Model;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TODO:Class level comment
 */
public class WSO2DependencyUpdator extends DependencyUpdator{
    double sucess =0.0;
    double failure =0.0;
    public boolean canHandle(Dependency dependency) {
        return dependency.getGroupId().contains("org.wso2");
    }

    public Dependency handle(Dependency dependency) {


        String newVersion = MavenCentralConnector.getLatestVersion(dependency);
        if(dependency.getVersion()!=null){

            ArrayList<String> newVersions = MavenCentralConnector.getVersionList(dependency);

            //System.out.println("Dependency "+dependency.getGroupId()+" : "+dependency.getArtifactId()+" Current Version :"+dependency.getVersion()+" Updated to :"+newVersion);
            dependency.setVersion(newVersion);
        }

        if(newVersion.equals(Constants.EMPTY_STRING)){
            failure+=1.0;
        }
        else{
            //System.out.println("Current Version  "+dependency.getCurrentVersion()+"  Newest Version "+newVersion+ " and Current Version is the latest:"+dependency.getCurrentVersion().equals(newVersion)+"   "+MavenCentralConnector.getVersionList(dependency.getGroupId(),dependency.getArtifactId()));
            MavenCentralConnector.getVersionList(dependency).toString();
            sucess+=1.0;
        }
        return dependency;
    }

    public double getAccuracy(){
        return sucess/(sucess+failure);
    }

    public double getSucess() {
        return sucess;
    }

    public double getFailure() {
        return failure;
    }

    public Model updateModel(Model model,Properties properties){

        Properties localProperties = model.getProperties();

        List<Dependency> dependencies = model.getDependencies();
        List<Dependency> updatedDependencies = new ArrayList<Dependency>();
        List<Dependency> outdatedDependencies = new ArrayList<Dependency>();
        Model newModel = model.clone();
        for (Dependency dependency : dependencies) {
            String currentVersion = dependency.getVersion();



            if(currentVersion != null && (currentVersion.startsWith("${") && currentVersion.endsWith("}"))){
                /*
                String versionKey = currentVersion.substring(2,currentVersion.length()-1);
                String version = properties.getProperty(versionKey);

                if(version==null){
                    version=localProperties.getProperty(versionKey);
                }


                String latestVersion =MavenCentralConnector.getLatestVersion(dependency);


                if(latestVersion!=null && !latestVersion.equals(Constants.EMPTY_STRING) && !version.equals(latestVersion) ){

                    newModel.addProperty(versionKey,latestVersion);
                }
                */

            }

            else if(currentVersion !=null ){
                Dependency dependencyClone = dependency.clone();
                String LatestVersion =MavenCentralConnector.getLatestVersion(dependency);
                if(!LatestVersion.equals(currentVersion)){
                    outdatedDependencies.add(dependency);
                    dependencyClone.setVersion(LatestVersion);
                    updatedDependencies.add(dependencyClone);
                    System.out.println(dependency.getArtifactId()+"  : updated from version :"+currentVersion+" to latest Versoion : "+LatestVersion);

                }

            }


        }
        dependencies.removeAll(outdatedDependencies);
        dependencies.addAll(updatedDependencies);
        newModel.setDependencies(dependencies);



        return newModel;
    }
}
