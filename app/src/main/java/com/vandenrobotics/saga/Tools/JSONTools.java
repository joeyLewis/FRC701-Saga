package com.vandenrobotics.saga.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * JSONTools.java
 * created by:      joeyLewis   on  8/12/15
 * last edited by:  joeyLewis   on  8/12/15
 * handles the interactions necessary with JSONObjects and JSONArrays to make their storage and
 * recall easy and readily accessible
 */
public final class JSONTools {

    // private constructor prevents implementation of the class by user
    private JSONTools(){}

    public static ArrayList<JSONObject> parseJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            if(jsonArray.get(i) instanceof JSONObject)
                jsonObjects.add(jsonArray.getJSONObject(i));
        }
        return jsonObjects;
    }
}
