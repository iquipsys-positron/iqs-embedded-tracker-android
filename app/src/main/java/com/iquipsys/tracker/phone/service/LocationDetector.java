package com.iquipsys.tracker.phone.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.iquipsys.tracker.phone.settings.SettingsPreferences;
import com.iquipsys.tracker.phone.status.StatusPreferences;

public class LocationDetector implements android.location.LocationListener {
    private static final String TAG = "Location";
    private static final int LOCATION_INTERVAL = 5;
    private static final int LOCATION_TIMEOUT = 10;

    private Location _location;
    private Context _context;
    private LocationDetectorListener _listener;

    public interface LocationDetectorListener {
        void onLocationChanged(Location location);
    }

    public LocationDetector(LocationDetectorListener listener) {
        _listener = listener;
    }

    public void subscribe(Context context) {
        _context = context;

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL * 1000, 0, this);
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to request GPS provider", e);
        }

        if (SettingsPreferences.isUseAltPositioning(context)) {
            try {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL * 1000, 0, this);
            } catch (SecurityException e) {
                Log.e(TAG, "Failed to request Network provider", e);
            }
        }
    }

    public void unsubscribe(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);
    }

    public Location getLatestLocation() {
        // Clear up obsolete locations
        if (_location != null) {
            // Compare as UTC dates
            long gpsTime = _location.getElapsedRealtimeNanos();
            long currentTime = SystemClock.elapsedRealtimeNanos();
            long timeout = (currentTime - gpsTime) / 1000000000;
            if (timeout > LOCATION_TIMEOUT)
                return null;
        }

        return _location;
    }

    @Override
    public void onLocationChanged(Location location) {
        boolean updated = false;

        Location oldLocation = getLatestLocation();

        if (location.getProvider() == LocationManager.GPS_PROVIDER) {
            _location = location;
            updated = true;
        } else {
            if (oldLocation == null || oldLocation.getProvider() != LocationManager.GPS_PROVIDER) {
                _location = location;
                updated = true;
            }
        }

        if (updated) {
            Log.d(TAG, "Detected location");

            if (oldLocation == null && _listener != null) {
                _listener.onLocationChanged(location);
            }

            if (_context != null) {
                StatusWriter.writeLocation(_context, location);
                StatusWriter.broadcastRefresh(_context);
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
