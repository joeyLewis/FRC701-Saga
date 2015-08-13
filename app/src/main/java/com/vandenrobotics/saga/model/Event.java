package com.vandenrobotics.saga.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Event.java
 * created by:      joeyLewis   on  8/12/15
 * last edited by:  joeyLewis   on  8/12/15
 * handles the information required by the system for each event
 * parses events in and out of JSON format, using TheBlueAlliance API JSON formatting to process
 * information and save only the necessary details
 * uses JSONObject formatting to save into files and recreate from files
 */
public class Event {

    private final String key;
    private final String name;
    private final String short_name;
    private final String location;

    public Event(String key, String name, String short_name, String location){
        this.key = key;
        this.name = name;
        this.short_name = short_name;
        this.location = location;
    }

    public Event(JSONObject details){
        String key, name, short_name, location;

        try {
            key = details.getString("key");
            name = details.getString("name");
            short_name = details.getString("short_name");
            location = details.getString("location");
        } catch (JSONException e){
            e.printStackTrace();
            key = null;
            name = null;
            short_name = null;
            location = null;
        }

        this.key = key;
        this.name = name;
        this.short_name = short_name;
        this.location = location;
    }

    public Event(String s){
        String key, name, short_name, location;

        try {
            JSONObject details = new JSONObject(s);
            key = details.getString("key");
            name = details.getString("name");
            short_name = details.getString("short_name");
            location = details.getString("location");
        } catch (JSONException e){
            e.printStackTrace();
            key = null;
            name = null;
            short_name = null;
            location = null;
        }

        this.key = key;
        this.name = name;
        this.short_name = short_name;
        this.location = location;
    }

    @Override
    public String toString(){
        JSONObject details = new JSONObject();
        try {
            details.put("key", key);
            details.put("name", name);
            details.put("short_name", short_name);
            details.put("location", location);
        } catch (JSONException e){
            e.printStackTrace();
        }

        return details.toString();
    }

    public String getKey(){
        return key;
    }

    public String getName(){
        return name;
    }

    public String getShortName(){
        return short_name;
    }

    public String getLocation(){
        return location;
    }

    public String getTitle(){
        return short_name.equals("null")? name : short_name;
    }

    /**
     * EventComparator class
     * handles sorting an Event by its name
     */
    public static class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event a, Event b){
            //sort events based on their name
            return a.name.compareTo(b.name);
        }
    }
}
