package com.iquipsys.tracker.phone.status;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StatusPreferences {
    public static final String RUNNING = "running";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String ORGANIZATION = "organization";
    public static final String LAST_UPDATE = "lastUpdate";
    public static final String LOCATION = "location";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String ALTITUDE = "altitude";
    public static final String ANGLE = "angle";
    public static final String SPEED = "speed";
    public static final String BEACONS = "beacons";
    public static final String MOBILITY = "mobility";
    public static final String NETWORK = "network";

    public static boolean getRunning(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(RUNNING, false);
    }

    public static void setRunning(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(RUNNING, value)
                .apply();
    }

    public static int getStatus(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(STATUS, Status.DISABLED);
    }

    public static void setStatus(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(STATUS, value)
                .apply();
    }

    public static String getError(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(ERROR, null);
    }

    public static void setError(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(ERROR, value)
                .apply();
    }

    public static String getOrganization(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(ORGANIZATION, null);
    }

    public static void setOrganization(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(ORGANIZATION, value)
                .apply();
    }

    public static long getLastUpdate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(LAST_UPDATE, 0);
    }

    public static void setLastUpdate(Context context, long value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(LAST_UPDATE, value)
                .apply();
    }

    public static boolean getLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(LOCATION, true);
    }

    public static void setLocation(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(LOCATION, value)
                .apply();
    }

    public static float getLongitude(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(LONGITUDE, 0);
    }

    public static void setLongitide(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(LONGITUDE, value)
                .apply();
    }

    public static float getLatitude(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(LATITUDE, 0);
    }

    public static void setLatitude(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(LATITUDE, value)
                .apply();
    }

    public static float getAltitude(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(ALTITUDE, 0);
    }

    public static void setAltitude(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(ALTITUDE, value)
                .apply();
    }

    public static float getSpeed(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(SPEED, 0);
    }

    public static void setSpeed(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(SPEED, value)
                .apply();
    }

    public static float getAngle(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(ANGLE, 0);
    }

    public static void setAngle(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(ANGLE, value)
                .apply();
    }

    public static String[] getBeacons(Context context) {
        Set<String> result = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(BEACONS, null);
        return result != null ? result.toArray(new String[result.size()]) : new String[0];
    }

    public static void setBeacons(Context context, String[] value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(BEACONS, new HashSet<String>(Arrays.asList(value)))
                .apply();
    }

    public static boolean getMobility(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(MOBILITY, true);
    }

    public static void setMobility(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(MOBILITY, value)
                .apply();
    }

    public static boolean getNetwork(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(NETWORK, false);
    }

    public static void setNetwork(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(NETWORK, value)
                .apply();
    }

}
