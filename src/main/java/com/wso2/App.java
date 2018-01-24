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

package com.wso2;

import com.wso2.DependencyProcessor.POMReader;
import com.wso2.DependencyProcessor.POMWriter;
import com.wso2.DependencyProcessor.WSO2DependencyUpdator;
import com.wso2.ProductBuilder.MavenInvoker;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class App {
    static String MAVEN_HOME;
    public static void main( String[] args ) {
        readConfigFile();
        String repositoryName ="product-apim";
        String  projectPomPath = Constants.ROOT_PATH+repositoryName;
        System.out.println(projectPomPath);
        ArrayList<Model> modelList = new ArrayList<Model>();

        POMReader pomReader = new POMReader();
        POMWriter pomWriter = new POMWriter();
        WSO2DependencyUpdator wso2DependencyUpdator = new WSO2DependencyUpdator();




        Model model=pomReader.getPomModel(projectPomPath);
        Properties properties =model.getProperties();
        List<String> modules =model.getModules();
        modelList.add(model);

        for (String module : modules) {
            model  = pomReader.getPomModel(projectPomPath+File.separator+module);
            modelList.add(model);
        }
        ArrayList<Properties> propertiesList = new ArrayList<Properties>();
        Model updatedRootModel = new Model();
        for (Model childModel : modelList) {

            Model updatedModel = wso2DependencyUpdator.updateModel(childModel,properties);

            if(!childModel.getProjectDirectory().toString().equals(projectPomPath)){
                System.out.println(childModel.getProjectDirectory().toString());
                propertiesList.add(updatedModel.getProperties());
                updatedModel.setProperties(new Properties());
                pomWriter.writePom(updatedModel);
            }
            else{
                System.out.println("model updated");
                updatedRootModel = childModel;
            }
        }

        for (Properties properties1 : propertiesList) {
            for (Object property : properties1.keySet()) {
                //System.out.println("OLD VALUE "+properties.getProperty(property.toString())+" NEW VALUE  "+ properties1.getProperty(property.toString()));
                properties.setProperty(property.toString(), properties1.getProperty(property.toString()));

            }
        }
        updatedRootModel.setProperties(properties);
        System.out.println(model==updatedRootModel);
        pomWriter.writePom(updatedRootModel);






        //ArrayList<String> pomLocationList =POMReader.getAllPOMFiles(Constants.ROOT_PATH+repositoryName);

//        for(int pomFileLocation =0; pomFileLocation<pomLocationList.size();pomFileLocation++){
//            dependencies.addAll(POMReader.getAllDependencies(pomLocationList.get(pomFileLocation)));
//
//        }

        //System.out.println(propertyMap);




        //Product product = new Product("Test Project","https://github.com/dimuthnc/test-dependency-manager.git",Constants.ROOT_PATH+"Test Product");

        //GithubConnector githubConnector = new GithubConnector();
        //githubConnector.clone(product);
        //githubConnector.update(product);
        MavenInvoker.mavenBuilder(MAVEN_HOME);
        //MavenInvoker.mavenNewBuilder();
        //MavenCentralConnector.testServer();
        //MavenCentralConnector.getLatestVersion("org.wso2.andes.wso2","andes-client");

    }

    private static void readConfigFile() {
        File configFile = new File(Constants.CONFIG_FILE_NAME);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(configFile);
            doc.getDocumentElement().normalize();
            Node node =doc.getElementsByTagName("M2_HOME").item(0);
            MAVEN_HOME =node.getTextContent();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
