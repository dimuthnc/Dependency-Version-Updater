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
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.model.Dependency;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;


public class MavenCentralConnector {
    public static String getLatestVersion(Dependency dependency){

        try {
            String data ="{"+"groupID:"+dependency.getGroupId()+","+"artifactID:"+dependency.getArtifactId()+"}";
            StringEntity entity = new StringEntity(data,
                    ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(Constants.GET_LATEST_VERSION_URL);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode()==404){
                //System.out.println("Dependency Not Found for "+groupID+"  "+artifactID);
            }
            else{

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                rd.close();
                return jsonObject.getString("NewestVersion");
            }



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Constants.EMPTY_STRING;
    }

    public static ArrayList<String> getVersionList(Dependency dependency){
        try {

            String data ="{"+"groupID:"+dependency.getGroupId()+","+"artifactID:"+dependency.getArtifactId()+"}";
            StringEntity entity = new StringEntity(data,
                    ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(Constants.GET_VERSION_LIST);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode()==404){
                //System.out.println("Dependency Not Found for "+groupID+"  "+artifactID);
            }
            else{

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                rd.close();
                ArrayList<String> versions = new ArrayList<String>();
                JSONArray versionList = jsonObject.getJSONArray("AvailableVersions");


                boolean newerVersionFound = false;
                for (int index = 0; index < versionList.length(); index++) {
                    String version = versionList.optString(index);
                    if(version.equals(dependency.getVersion())){
                        newerVersionFound =true;
                    }
                    if(newerVersionFound){
                        versions.add(version);
                    }

                }
                return versions;
            }



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();

    }

}
