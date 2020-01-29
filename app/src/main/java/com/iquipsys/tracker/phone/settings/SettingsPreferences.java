package com.iquipsys.tracker.phone.settings;

import android.content.Context;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class SettingsPreferences {
    public static final String SERVER_URL_DEF = "http://api.positron.iquipsys.net:30001";
    public static final String MQTT_BROKER_URL_DEF = "mqtt://api.positron.iquipsys.net:31883";
    public static final int ACTIVE_INTERVAL_DEF = 30;
    public static final int INACTIVE_INTERVAL_DEF = 300;
    public static final int OFFSITE_INTERVAL_DEF = 900;

    public static final String SERVER_URL = "serverUrl";
    public static final String MQTT_BROKER_URL = "mqttBrokerUrl";
    public static final String TRACKER_UDI = "trackerUdi";
    public static final String USE_MQTT_BROKER = "useMqttBroker";
    public static final String USE_BEACONS = "useBeacons";
    public static final String USE_ALT_POSITIONING = "useAltPositioning";
    public static final String USE_ORGANIZATION_PARAMS = "useOrganizationParams";
    public static final String ACTIVE_INTERVAL = "activeInterval";
    public static final String INACTIVE_INTERVAL = "inactiveInterval";
    public static final String OFFSITE_INTERVAL = "offsiteInterval";


    public static String getServerUrl(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SERVER_URL, SERVER_URL_DEF);
        value = value != null && !value.isEmpty() ? value : SERVER_URL_DEF;
        return value;
    }

    public static void setServerUrl(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(SERVER_URL, value)
            .apply();
    }

    public static String getMqttBrokerUrl(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(MQTT_BROKER_URL, MQTT_BROKER_URL_DEF);
        value = value != null && !value.isEmpty() ? value : MQTT_BROKER_URL_DEF;
        return value;
    }

    public static void setMqttBrokerUrl(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(MQTT_BROKER_URL, value)
                .apply();
    }

    public static String getTrackerUdi(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(TRACKER_UDI, null);
        value = value != null && !value.isEmpty() ? value : getDefaultTrackerUdi(context);
        return value;
    }

    public static void setTrackerUdi(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(TRACKER_UDI, value)
                .apply();
    }

    public static boolean isUseMqttBroker(Context context) {
        boolean value = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(USE_MQTT_BROKER, true);
        return value;
    }

    public static void setUseMqttBroker(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(USE_MQTT_BROKER, value)
                .apply();
    }

    public static boolean isUseBeacons(Context context) {
        boolean value = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(USE_BEACONS, true);
        return value;
    }

    public static void setUseBeacons(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(USE_BEACONS, value)
                .apply();
    }

    public static boolean isUseAltPositioning(Context context) {
        boolean value = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(USE_ALT_POSITIONING, false);
        return value;
    }

    public static void setUseAltPositioning(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(USE_ALT_POSITIONING, value)
                .apply();
    }

    public static boolean isUseOrganizationParams(Context context) {
        boolean value = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(USE_ORGANIZATION_PARAMS, true);
        return value;
    }

    public static void setUseOrganizationParams(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(USE_ORGANIZATION_PARAMS, value)
                .apply();
    }

    public static int getActiveInterval(Context context) {
        int value = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(ACTIVE_INTERVAL, ACTIVE_INTERVAL_DEF);
        value = value >= 0 ? value : ACTIVE_INTERVAL_DEF;
        return value;
    }

    public static void setActiveInterval(Context context, int value) {
        value = value >= 0 ? value : ACTIVE_INTERVAL_DEF;

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(ACTIVE_INTERVAL, value)
                .apply();
    }

    public static int getInactiveInterval(Context context) {
        int value = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(INACTIVE_INTERVAL, INACTIVE_INTERVAL_DEF);
        value = value >= 0 ? value : INACTIVE_INTERVAL_DEF;
        return value;
    }

    public static void setInactiveInterval(Context context, int value) {
        value = value >= 0 ? value : INACTIVE_INTERVAL_DEF;

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(INACTIVE_INTERVAL, value)
                .apply();
    }

    public static int getOfforganizationInterval(Context context) {
        int value = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(OFFSITE_INTERVAL, OFFSITE_INTERVAL_DEF);
        value = value >= 0 ? value : OFFSITE_INTERVAL_DEF;
        return value;
    }

    public static void setOfforganizationInterval(Context context, int value) {
        value = value >= 0 ? value : OFFSITE_INTERVAL_DEF;

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(OFFSITE_INTERVAL, value)
                .apply();
    }

    public static String getDefaultTrackerUdi(Context context) {
        // Define current phone number
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            String value = manager.getLine1Number();
            if (value == null || value.isEmpty())
                value = "+";
            return value;
        } catch (SecurityException e) {
            return null;
        }
    }    
    
    public static void restoreDefaults(Context context) {
        setServerUrl(context, SERVER_URL_DEF);
        setMqttBrokerUrl(context, MQTT_BROKER_URL_DEF);
        setTrackerUdi(context, getDefaultTrackerUdi(context));
        setUseOrganizationParams(context, true);
        setActiveInterval(context, ACTIVE_INTERVAL_DEF);
        setInactiveInterval(context, INACTIVE_INTERVAL_DEF);
        setOfforganizationInterval(context, OFFSITE_INTERVAL_DEF);
    }
    
}
