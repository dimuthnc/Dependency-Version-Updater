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
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class POMReader {
    static DependencyUpdator dependencyUpdator;
    public POMReader(){
        dependencyUpdator = new WSO2DependencyUpdator();
    }
    public boolean getAllDependencies(String path, HashMap<String,String> propertyMap){
        Model model = getPomModel(path);
        Model updatedModel = model.clone();
        if(model.getVersion()!=null){
            propertyMap.put("project.version",model.getVersion());
        }
        if(model.getDependencyManagement() !=null){
            List<Dependency> dependencies = model.getDependencyManagement().getDependencies();
            DependencyManagement dependencyManagement = model.getDependencyManagement();
            dependencyManagement.setDependencies(updateDependencyList(propertyMap, dependencies));
            updatedModel.setDependencyManagement(dependencyManagement);
        }
        if(model.getDependencies()!= null){
            List<Dependency> dependencies = model.getDependencies();
            updatedModel.setDependencies(updateDependencyList(propertyMap, dependencies));
        }

        List<String> modules=model.getModules();
        for (String module : modules) {
            getAllDependencies(path+File.separator+module+File.separator,propertyMap);
        }

        return false;
    }

    private static List<Dependency> updateDependencyList(HashMap<String, String> propertyMap, List<Dependency> dependencies) {
        List<Dependency> updatedDependencyList = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {

            String version = dependency.getVersion();
            if(version!=null && version.startsWith("${") && version.endsWith("}")){
                String propertyKey = version.substring(2,version.length()-1);
                dependency.setVersion(propertyMap.get(propertyKey));
            }
            if(dependencyUpdator.canHandle(dependency)){
                Dependency updatedDependency =dependencyUpdator.handle(dependency);
                updatedDependencyList.add(updatedDependency);
            }
        }
        return updatedDependencyList;
    }


    private static HashMap<String,String> getProperties(String path){
        HashMap<String,String> propertyMap = new HashMap<String, String>();
        File pomfile = new File(path+Constants.POM_NAME);
        InputStreamReader reader;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();

        try {
            reader = new InputStreamReader(new FileInputStream(path+File.separator+Constants.POM_NAME), "UTF-8");
            Model model = mavenReader.read(reader);
            model.setPomFile(pomfile);
            Properties  properties=model.getProperties();
            for (Object propertyObject: properties.keySet()) {
                //System.out.println(propertyObject.toString()+"          "+properties.getProperty(propertyObject.toString()));

                propertyMap.put(propertyObject.toString(),properties.getProperty(propertyObject.toString()));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propertyMap;
    }


    public Model getPomModel(String path){
        File pomFile = new File(path+File.separator+Constants.POM_NAME);
        InputStreamReader reader;
        Model model = new Model();
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try {
            reader = new InputStreamReader(new FileInputStream(path+File.separator+Constants.POM_NAME), "UTF-8");
            model = mavenReader.read(reader);
            model.setPomFile(pomFile);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return model;
    }
}
