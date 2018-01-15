package com.wso2.DependencyProcessor;

import com.wso2.App;
import com.wso2.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class POMProcessor {


    public static ArrayList<String> getAllDependencies(String path){
        try {

            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            Node rootElement = doc.getDocumentElement();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            //NodeList nodes = doc.getElementsByTagName("movies");

            NodeList dependencies = doc.getElementsByTagName("dependencies");

            NodeList nList = dependencies.item(0).getChildNodes();





            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {




                Node nNode = nList.item(temp);



                if (nNode.getNodeType() == Node.ELEMENT_NODE ) {

                    Element eElement = (Element) nNode;

                    if(!eElement.getElementsByTagName("version").equals(null)){

                        String artifactId =eElement.getElementsByTagName("artifactId").item(0).getTextContent();
                        String groupId =eElement.getElementsByTagName("groupId").item(0).getTextContent();
                        String version = eElement.getElementsByTagName("version").item(0).getTextContent();



                        if(version.startsWith("${")&& version.endsWith("}")){
                            String propertyName = version.substring(2,version.length()-1);

                            NodeList versionProperty = doc.getElementsByTagName(propertyName);
                            version = versionProperty.item(0).getTextContent();
                        }
                        if(groupId.contains(Constants.GROUP_ID)){
                            System.out.println("\nGroup ID : " + groupId);
                            System.out.println("Artifact ID : " + artifactId);
                            System.out.println("Version : " +version);
                        }

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public static ArrayList<String> getAllPOMFiles(String rootPath) {
        File folder = new File(rootPath);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> pomFiles = new ArrayList<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getName().equals(Constants.POM_NAME)) {

                    pomFiles.add(listOfFiles[i].getAbsolutePath());
                }
            } else if (listOfFiles[i].isDirectory()) {

                pomFiles.addAll(POMProcessor.getAllPOMFiles(rootPath + File.separator + listOfFiles[i].getName()));
            }
        }

        return  pomFiles;
    }



}
