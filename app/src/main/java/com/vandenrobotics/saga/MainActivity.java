package com.vandenrobotics.saga;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.vandenrobotics.saga.adapters.EventListAdapter;
import com.vandenrobotics.saga.model.Event;
import com.vandenrobotics.saga.tools.FileTools;
import com.vandenrobotics.saga.tools.JSONTools;
import com.vandenrobotics.saga.tools.MenuTools;
import com.vandenrobotics.saga.tools.TheBlueAllianceRestClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * MainActivity.java
 * created by:       joeyLewis  on  8/12/15
 * last edited by:   joeyLewis  on  8/12/15
 * handles the main activity of the class, which allows the user to choose an event to collect
 * and view data on, as well as a menu to refresh events, view an about page, and visit the
 * Vanden Robotics website.
 */
public class MainActivity extends Activity {

    /**
     * ArrayList downloadedEvents
     * the list of events found on the device
     */
    private ArrayList<Event> downloadedEvents;

    /**
     * EventListAdapter mAdapter
     * class instance of EventListAdapter to handle the downloadedEvents in a ListView
     */
    private EventListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reset downloadedEvents, then set it to the current list of events from internal storage
        downloadedEvents = new ArrayList<>();
        downloadedEvents = FileTools.readEvents(getApplicationContext());

        // setup the adapter for the ListView with this activity as a context and the list of
        // events gathered from internal storage
        mAdapter = new EventListAdapter(this, downloadedEvents);

        // if the list of events from internal storage was empty (nothing has been downloaded or
        // the user has reset all local event data)
        if(downloadedEvents.isEmpty()){
            downloadEvents();
        }

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_settings:
                break;
            case R.id.action_refreshEvents:
                // refresh and download new events
                downloadEvents();
                break;
            case R.id.action_loadWebsite:
                // load the website, asking for permission to navigate away from the app
                MenuTools.loadWebsite(getFragmentManager());
                break;
            case R.id.action_about:
                MenuTools.about(this);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * initViews
     * create and handle input from the layout - includes filtering using the EditText and item
     * selection and opening an Event from the OpenEvent Button
     */
    private void initViews(){

        final EditText FilterEvents = (EditText)findViewById(R.id.editText_EventsFilter);
        final ListView EventList = (ListView)findViewById(R.id.listView_Events);
        final Button OpenEvent = (Button)findViewById(R.id.button_OpenEvent);

        EventList.setTextFilterEnabled(true);
        EventList.setAdapter(mAdapter);

        FilterEvents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        OpenEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    startActivity(loadEvent(mAdapter.getItem(EventList.getCheckedItemPosition())));
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * downloadEvents
     * gathers each available event from TheBlueAlliance and downloads the event to a list of
     * downloaded events if it doesn't currently have a record in the system
     */
    private void downloadEvents(){
        // create a new ArrayList to handle the events from TheBlueAlliance
        final ArrayList<Event> tbaEvents = new ArrayList<>();

        // create a ProgressDialog to show downloading progress of events
        final ProgressDialog eventProgressDialog = ProgressDialog.show(
                this, getResources().getString(R.string.dialog_EventProgressTitle),
                getResources().getString(R.string.dialog_EventProgressMessage));

        // check online status to see if we can load the Blue Alliance Data, otherwise load the
        // list without it
        if (TheBlueAllianceRestClient.isOnline(this)) {
            TheBlueAllianceRestClient.get(this, "events/", new JsonHttpResponseHandler() {
                // no need to pass a year to the API, as it will default to the current year, which
                // is always what we want
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray events) {
                    // handle the incoming JSONArray of events and populate the list view
                    try {
                        //parse the incoming JSONArray
                        ArrayList<JSONObject> eventList = JSONTools.parseJSONArray(events);
                        for(JSONObject jso : eventList){
                            // save each JSONObject from the incoming list to an Event object
                            tbaEvents.add(new Event(jso.getString("key"),jso.getString("name"),
                                    jso.getString("short_name"),jso.getString("location")));
                        }

                        boolean eventAlreadyDownloaded = false;

                        // check each event to see if it has already been downloaded
                        for(Event event : tbaEvents){
                            eventProgressDialog.setMessage("Checking Event: " + event.getKey());

                            // cross check against current local list
                            for(Event localEvent : downloadedEvents){
                                if(event.getKey().equals(localEvent.getKey())){
                                    eventAlreadyDownloaded=true;
                                    break;
                                }
                            }

                            // if the event is new, download its basic details and add it to our
                            // list of downloaded events
                            if(!eventAlreadyDownloaded){
                                eventProgressDialog.setMessage("Downloading New Event: " +
                                        event.getKey());
                                downloadedEvents.add(event);
                            }

                            eventAlreadyDownloaded = false;
                        }

                        // sort the final array and tell the adapter to update just in case
                        // anything new is present
                        Collections.sort(downloadedEvents, new Event.EventComparator());
                        FileTools.writeEvents(getApplicationContext(), downloadedEvents);
                        mAdapter.notifyDataSetChanged();

                        eventProgressDialog.setMessage("Successfully accessed remote host - " +
                                "www.thebluealliance.com");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    eventProgressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      Throwable t, JSONObject jo) {
                    eventProgressDialog.dismiss();
                }
            });

        } else {
            eventProgressDialog.setMessage("You aren't connected to the internet.  Proceeding " +
                    "with current local list of events...");
            eventProgressDialog.dismiss();
        }
    }

    /**
     * processes a JSONObject representing an event and returns an Intent setup to load that event
     * into the EventActivity
     * @param event: the event to load into the EventActivity
     * @return an Intent designed to send the Event object event as a string to be
     * retrieved later
     * by the EventActivity as a string
     */
    private Intent loadEvent(Event event){
        return new Intent(this, EventActivity.class).putExtra("event", event.toString());
    }
}
