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

package org.wso2;

import org.wso2.DependencyProcessor.POMReader;
import org.wso2.DependencyProcessor.POMWriter;
import org.wso2.DependencyProcessor.WSO2DependencyUpdater;
import org.wso2.Model.Product;
import org.wso2.ProductBuilder.MavenInvoker;
import org.wso2.ProductRetrieve.GithubConnector;
import org.apache.maven.model.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wso2.ReportGenerator.UpdatedDependencyReporter;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public class App {
    static String MAVEN_HOME;
    public static void main( String[] args ) {
        readConfigFile();
        HashMap<String,String> productURLMap;
        HashMap<String,String > productRepoMap ;
        productURLMap = getAllProjectsFromDashboardDB();
        productRepoMap = getExistingRepos();
        HashMap<String,String> productMap = updateRepositories(productURLMap, productRepoMap);
        for (String productName : productMap.keySet()) {
            updateProductDependencies(productMap.get(productName));
            MavenInvoker.mavenBuilder(MAVEN_HOME,productName);
        }


    }

    private static boolean updateProductDependencies(String productName) {
        boolean status;


        String  projectPomPath = Constants.ROOT_PATH+productName;
        ArrayList<Model> modelList = new ArrayList<Model>();

        POMReader pomReader = new POMReader();
        POMWriter pomWriter = new POMWriter();
        WSO2DependencyUpdater wso2DependencyUpdater = new WSO2DependencyUpdater();


        Model model=pomReader.getPomModel(projectPomPath);
        Properties properties =model.getProperties();
        properties.setProperty("project.version",model.getVersion());
        List<String> modules =model.getModules();
        modelList.add(model);

        for (String module : modules) {
            model  = pomReader.getPomModel(projectPomPath+ File.separator+module);
            modelList.add(model);
        }
        ArrayList<Properties> propertiesList = new ArrayList<Properties>();
        Model updatedRootModel = new Model();
        for (Model childModel : modelList) {
            String pomLocation = childModel.getProjectDirectory().toString();

            Model updatedModel = wso2DependencyUpdater.updateModel(childModel,properties,pomLocation);

            if(!childModel.getProjectDirectory().toString().equals(projectPomPath)){

                //propertiesList.add(updatedModel.getProperties());
                //updatedModel.setProperties();
                pomWriter.writePom(updatedModel);

            }
            else{

                propertiesList.add(updatedModel.getProperties());
                updatedRootModel = updatedModel;

            }
        }

        for (Properties properties1 : propertiesList) {
            for (Object property : properties1.keySet()) {
                properties.setProperty(property.toString(), properties1.getProperty(property.toString()));

            }
        }
        updatedRootModel.setProperties(properties);

        //System.out.println(properties.getProperty("carbon.kernel.version"));
        status =pomWriter.writePom(updatedRootModel);
        return status;

    }

    private static HashMap<String,String> updateRepositories(HashMap<String, String> productURLMap, HashMap<String, String> productRepoMap) {
        HashMap<String,String> productMap = new HashMap<String, String>();
        GithubConnector githubConnector = new GithubConnector();
        for (String productName : productURLMap.keySet()) {
            if(productRepoMap.containsKey(productName)){
                Product product = new Product(productName,productURLMap.get(productName), Constants.ROOT_PATH+ File.separator+productRepoMap.get(productName));
                githubConnector.update(product);
                productMap.put(productName,productRepoMap.get(productName));
            }
            else{
                Product product = new Product(productName,productURLMap.get(productName),null);
                githubConnector.clone(product);
                product.setSubdirectory(Constants.ROOT_PATH+product.getName());
                productMap.put(productName,product.getName());
            }
        }
        return productMap;
    }

    private static HashMap<String,String> getExistingRepos() {
        HashMap<String,String> projectRepoMap = new HashMap<String, String>();
        //projectRepoMap.put("APIM","APIM");
        projectRepoMap.put("product-apim","product-apim");
        //projectRepoMap.put("DAS","DAS");
        //projectRepoMap.put("SP","SP");
        //projectRepoMap.put("EI","EI");
        //projectRepoMap.put("Message Broker","Message Broker");
        //projectRepoMap.put("Message Broker","Message Broker");
       // projectRepoMap.put("msf4j","msf4j");
       // projectRepoMap.put("API Manager","API Manager");
        return projectRepoMap;
    }

    private static HashMap<String,String> getAllProjectsFromDashboardDB() {
        HashMap<String,String> projectURLMap = new HashMap<String, String>();
        //projectURLMap.put("APIM","APIM");
        projectURLMap.put("product-apim","product-apim");
        //projectURLMap.put("APIM","APIM");
        //projectURLMap.put("DAS","DAS");
        //projectURLMap.put("SP","SP");
        //projectURLMap.put("EI","EI");
         // projectURLMap.put("Message Broker","https://github.com/wso2/message-broker.git");
        //projectURLMap.put("Message Broker","https://github.com/wso2/message-broker.git");
       // projectURLMap.put("Carbon API-Management","https://github.com/wso2/carbon-apimgt.git");
       // projectURLMap.put("msf4j","https://github.com/wso2/msf4j.git");
        //projectURLMap.put("API Manager","https://github.com/wso2/product-apim.git");

        return projectURLMap;
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
