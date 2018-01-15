package com.wso2.Model;


import org.json.JSONException;
import org.json.JSONObject;

public class LatestVersionJSON {







    public static JSONObject getLatestVersionRequestJSON(String groupID, String artifactID){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("groupID",groupID);
            jsonObject.put("artifactID",artifactID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}