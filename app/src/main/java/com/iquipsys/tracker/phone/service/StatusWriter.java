package com.iquipsys.tracker.phone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.iquipsys.tracker.phone.rest.OrganizationV1;
import com.iquipsys.tracker.phone.status.Status;
import com.iquipsys.tracker.phone.status.StatusPreferences;

public class StatusWriter {
    public static final String MESSAGE_REFRESH = "com.iquipsys.tracker.phone.service.action.REFRESH";

    public static void subscribe(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(receiver, new IntentFilter(MESSAGE_REFRESH));
    }

    public static void unsubscribe(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(receiver);
    }

    public static void broadcastRefresh(Context context) {
        Intent intent = new Intent(MESSAGE_REFRESH);
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
    }

    public static void writeRunning(Context context, boolean running) {
        StatusPreferences.setRunning(context, running);

        StatusPreferences.setStatus(context, running ? Status.ENABLED : Status.DISABLED);

        // Broadcast message to update status
        broadcastRefresh(context);
    }

    public static void writeLocation(Context context, Location location) {
        if (location != null) {
            StatusPreferences.setLocation(context, true);

            double latitude = location.getLatitude();
            StatusPreferences.setLatitude(context, (float) latitude);

            double longitude = location.getLongitude();
            StatusPreferences.setLongitide(context, (float) longitude);

            double altitude = location.getAltitude();
            StatusPreferences.setAltitude(context, (float)altitude);

            float speed = location.getSpeed();
            StatusPreferences.setSpeed(context, speed);

            float angle = location.getBearing();
            StatusPreferences.setAngle(context, angle);
        } else {
            StatusPreferences.setLocation(context, false);
            StatusPreferences.setLatitude(context, 0);
            StatusPreferences.setLongitide(context, 0);
            StatusPreferences.setAltitude(context, 0);
            StatusPreferences.setSpeed(context, 0);
            StatusPreferences.setAngle(context, 0);
        }
    }

    public static void writeBeacons(Context context, String[] beacons) {
        StatusPreferences.setBeacons(context, beacons);
        if (beacons != null && beacons.length > 0)
            StatusPreferences.setLocation(context, true);
    }

    public static void writeNetwork(Context context, boolean connected) {
        StatusPreferences.setNetwork(context, connected);
    }

    public static void writeMobility(Context context, boolean mobile) {
        StatusPreferences.setMobility(context, mobile);
    }

    public static void writeError(Context context, String error) {
        if (StatusPreferences.getRunning(context)) {
            StatusPreferences.setStatus(context, Status.ERROR);
            StatusPreferences.setError(context, error);

            // Broadcast message to update status
            broadcastRefresh(context);
        }
    }

    public static void writeStatus(Context context, int status) {
        if (StatusPreferences.getRunning(context)) {
            StatusPreferences.setLastUpdate(context, System.currentTimeMillis());
            StatusPreferences.setStatus(context, status);
            StatusPreferences.setOrganization(context, null);
            StatusPreferences.setError(context, null);

            // Broadcast message to update status
            broadcastRefresh(context);
        }
    }

    public static void writeUpdate(Context context, OrganizationV1 organization) {
        if (StatusPreferences.getRunning(context)) {
            StatusPreferences.setLastUpdate(context, System.currentTimeMillis());

            StatusPreferences.setStatus(context, organization != null ? Status.CONNECTED_GPS : Status.CONNECTED_BEACON);
            StatusPreferences.setOrganization(context, organization != null ? organization.getName() : null);
            StatusPreferences.setError(context, null);

            // Broadcast message to update status
            broadcastRefresh(context);
        }
    }

}
