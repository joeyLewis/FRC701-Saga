package com.vandenrobotics.saga.tools;

import android.content.Context;

import com.vandenrobotics.saga.model.Event;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * FileTools.java
 * created by:      joeyLewis   on  8/12/15.
 * last edited by:  joeyLewis   on  8/12/15.
 * handles reading and writing key system data files for long-term application storage using the
 * internal storage directories provided in the application framework
 */
public final class FileTools {

    // private constructor prevents implementation of the class by user
    private FileTools(){}

    /**
     * readEvents looks at the internal storage directory and grabs the necessary events file and
     * processes it into an ArrayList of events
     * @param context the context used to generate the internal storage directory
     * @return the ArrayList containing the events
     */
    public static ArrayList<Event> readEvents(Context context){
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<JSONObject> eventsArray = new ArrayList<>();
        try{
            String line;
            FileInputStream fileInputStream = new FileInputStream(createFile(context, "",
                    "events.json"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            while((line = br.readLine()) != null)
                eventsArray.add(new JSONObject(line));
            br.close();
            fileInputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        for(JSONObject jsoEvent : eventsArray){
            Event event = new Event(jsoEvent);
            events.add(event);
        }

        return events;
    }

    /**
     * writeEvents takes application data for events and saves it to the internal storage directory
     * @param context the context used to generate the internal storage directory
     * @param events the ArrayList to be saved to the directory
     * @return the result of the FileWriter attempt, true if success, false if failed
     */
    public static boolean writeEvents(Context context, ArrayList<Event> events){

        try {
            FileWriter fileWriter = new FileWriter(createFile(context, "" , "events.json"));
            for(Event event : events){
                fileWriter.write(event.toString() + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * deleteEvents deletes the current local event file, called before we re-download the events
     * from TheBlueAlliance to provide a clean slate to download events
     * @param context the context used to generate the internal storage directory
     * @return the result of the delete attempt, true if success, false if failed
     */
    public static boolean deleteEvents(Context context){
        return deleteDirectory(context, "events.json");
    }

    private static File createDirectory(Context context, String dir){
        File f = new File(context.getFilesDir() + "/" + dir);
        if(!f.exists())
            f.mkdirs();
        return f;
    }

    private static File createFile(Context context, String dir, String filename){
        File path = createDirectory(context, dir);
        File f = new File(path, filename);
        if(!f.exists())
            try {
                f.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        return f;
    }

    private static boolean deleteDirectory(Context context, String dir){
        return deleteFiles(context, new File(context.getFilesDir() + "/" + dir));
    }

    private static boolean deleteFiles(Context context, File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteFiles(context, files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return path.delete();
    }


}
