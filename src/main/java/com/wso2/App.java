package com.wso2;

import com.wso2.DependencyProcessor.MavenCentralConnector;
import com.wso2.Model.Product;
import com.wso2.ProductBuilder.MavenInvoker;
import com.wso2.ProductRetrieve.GithubConnector;
import org.apache.maven.shared.invoker.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) {

        /*
        String repositoryName ="msf4j";
        ArrayList<String> pomLocationList =getPOMFilesList(Constants.ROOT_PATH+repositoryName);

        for(int pomFileLocation =0; pomFileLocation<pomLocationList.size();pomFileLocation++){
            getDependencyList(pomLocationList.get(pomFileLocation));
        }
        */


        Product product = new Product("Test Project","https://github.com/dimuthnc/test-dependency-manager.git",Constants.ROOT_PATH+"Test Product");

        GithubConnector githubConnector = new GithubConnector();
        //githubConnector.clone(product);
        //githubConnector.update(product);

        //MavenInvoker.mavenBuilder();
        //MavenCentralConnector.testServer();
        MavenCentralConnector.getLatestVersion();

    }






}
