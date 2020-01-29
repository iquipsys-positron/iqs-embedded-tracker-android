package com.iquipsys.tracker.phone.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iquipsys.tracker.phone.status.StatusPreferences;

public class NetworkDetector {
    private static final String TAG = "Network";

    public NetworkDetector() {}

    public void subscribe(Context context) {
    }

    public void unsubscribe(Context context) {
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

}
