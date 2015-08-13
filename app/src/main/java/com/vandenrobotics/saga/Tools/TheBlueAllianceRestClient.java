package com.vandenrobotics.saga.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * TheBlueAllianceRestClient.java
 * created by:      joeyLewis   on  8/12/15
 * last edited by:  joeyLewis   on  8/12/15
 * handles accessing TheBlueAlliance api to grab information needed to run the application
 */
public final class TheBlueAllianceRestClient {

    // private constructor prevents implementation of the class by user
    private TheBlueAllianceRestClient(){}

    public static Header[] GET_HEADER = {new BasicHeader("X-TBA-App-Id", "frc701:Sága:v0")};
    private static final String BASE_URL = "http://www.thebluealliance.com/api/v2/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, AsyncHttpResponseHandler responseHandler){
        client.get(context, getAbsoluteUrl(url), GET_HEADER, null, responseHandler);
    }

    public static boolean isOnline(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static String getAbsoluteUrl(String relativeUrl){
        return BASE_URL + relativeUrl;
    }

}
