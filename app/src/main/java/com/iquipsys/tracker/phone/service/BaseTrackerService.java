package com.iquipsys.tracker.phone.service;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.iquipsys.tracker.phone.R;
import com.iquipsys.tracker.phone.rest.OrganizationV1;
import com.iquipsys.tracker.phone.settings.SettingsPreferences;
import com.iquipsys.tracker.phone.status.Status;
import com.iquipsys.tracker.phone.status.StatusPreferences;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class BaseTrackerService extends android.app.Service
    implements ITrackerService, MotionDetector.MotionDetectorListener,
        LocationDetector.LocationDetectorListener, BeaconDetector.BeaconDetectorListener {

    public static final String TAG = "Service";

    private static final int RETRY_INTERVAL = 10;
    private static final int OFFSITE_INTERVAL = 900;

    protected IBinder _binder = new TrackerServiceBinder(this);
    protected Context _context;

    protected LocationDetector _locationDetector = new LocationDetector(this);
    protected BeaconDetector _beaconDetector = new BeaconDetector(this);
    protected NetworkDetector _networkDetector = new NetworkDetector();
    protected MotionDetector _motionDetector = new MotionDetector(this);
    protected ButtonDetector _buttonDetector = new ButtonDetector(null);
    protected OrganizationsReader _organizationsReader = new OrganizationsReader();
    protected boolean _subscribed = false;

    protected Semaphore _processLock = new Semaphore(0);
    protected Thread _processThread = null;

    public BaseTrackerService() {}

    public void setContext(Context context) {
        _context = context;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Assign context
        _context = getApplicationContext();

        // Start process if it was started
        boolean running = StatusPreferences.getRunning(_context);
        if (running)
            activate();

        return _binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        deactivate();
        return true;
    }

    public void startRunning() {
        StatusWriter.writeRunning(_context, true);
        activate();
        Log.i(TAG, "Started tracker");
    }

    public void stopRunning() {
        StatusWriter.writeRunning(_context, false);
        deactivate();
        Log.i(TAG, "Stopped tracker");
    }

    public void pressButton(boolean longPress) {
        _buttonDetector.pressButton(longPress);
        _processLock.release();
    }

    public void activate() {
        subscribeListeners();
        connectToService();
        startProcess();
    }

    public void deactivate() {
        unsubscribeListeners();
        disconnectFromServer();
        stopProcess();
    }

    public void connectToService() {
        // Override in child classes
    }

    public void disconnectFromServer() {
        // Override in child classes
    }

    private void subscribeListeners() {
        if (!_subscribed) {
            _locationDetector.subscribe(_context);
            _beaconDetector.subscribe(_context);
            _networkDetector.subscribe(_context);
            _motionDetector.subscribe(_context);
            _buttonDetector.subscribe(_context);
            _organizationsReader.subscribe(_context);

            _subscribed = true;
        }
    }

    private void unsubscribeListeners() {
        if (_subscribed) {
            _locationDetector.unsubscribe(_context);
            _beaconDetector.unsubscribe(_context);
            _networkDetector.unsubscribe(_context);
            _motionDetector.unsubscribe(_context);
            _buttonDetector.unsubscribe(_context);
            _organizationsReader.unsubscribe(_context);

            _subscribed = false;
        }
    }

    private void startProcess() {
        if (_processThread == null) {
            _processThread = new Thread(_processHandler);
            _processThread.setName("Tracker service");
            _processThread.start();
        }
    }

    private void stopProcess() {
        if (_processThread != null) {
            Thread thread = _processThread;
            _processThread = null;
            thread.interrupt();
        }
    }

    private Runnable _processHandler = new Runnable() {
        @Override
        public void run() {
            while (_processThread != null) {
                try {
                    int interval = process();
                    Log.d(TAG, "Sleeping for " + interval + " ms");
                    _processLock.tryAcquire(interval, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // Ignore exceptions
                } catch (Exception e) {
                    Log.e(TAG, "Failed to process status update", e);
                }
            }
        }
    };

    @Override
    public void onMotionChanged(boolean freezed) {
        _processLock.release();
    }

    @Override
    public void onBeaconsChanged(String[] beacons) {
        _processLock.release();
    }

    @Override
    public void onLocationChanged(Location location) {
        _processLock.release();
    }

    private int validateSettings() {
        String deviceUdi = SettingsPreferences.getTrackerUdi(_context);
        if (deviceUdi == null && deviceUdi.isEmpty())
            return R.string.error_no_udi;

        String serverUri = SettingsPreferences.getServerUrl(_context);
        if (serverUri == null && serverUri.isEmpty())
            return R.string.error_no_rest_uri;

        String mqttUri = SettingsPreferences.getServerUrl(_context);
        if (mqttUri == null && mqttUri.isEmpty())
            return R.string.error_no_mqtt_uri;

        return 0;
    }

    private int process() {
        // Check for errors
        int errorId = validateSettings();
        if (errorId != 0) {
            String error = getResources().getString(errorId);
            StatusWriter.writeError(_context, error);
            return RETRY_INTERVAL;
        }

        // Read sensors
        boolean connected = _networkDetector.isConnected(_context);
        Location location = _locationDetector.getLatestLocation();
        String[] beacons = _beaconDetector.getLatestBeacons();
        boolean freezed = _motionDetector.isfreezed();
        int buttonPress = _buttonDetector.readButtonPress();

        // Write sensors status
        StatusWriter.writeLocation(_context, location);
        StatusWriter.writeBeacons(_context, beacons);
        StatusWriter.writeMobility(_context, !freezed);
        StatusWriter.writeNetwork(_context, connected);

        // Check the network status
        if (!connected) {
            StatusWriter.writeStatus(_context, Status.NO_NETWORK);
            Log.i(TAG, "Waiting for network...");
            return RETRY_INTERVAL;
        }

        // Check the last known location
        if (location == null && beacons.length == 0) {
            StatusWriter.writeStatus(_context, Status.NO_LOCATION);
            Log.i(TAG, "Waiting for location...");
            return RETRY_INTERVAL;
        }

        // Retrieve current organization
        OrganizationV1 organization = _organizationsReader.findCurrent(_context, location);
        if (organization == null && beacons.length == 0) {
            StatusWriter.writeStatus(_context, Status.OFFSITE);
            Log.i(TAG, "Outside of organizations");

            // Use offsite interval from settings
            if (!SettingsPreferences.isUseOrganizationParams(_context))
                return SettingsPreferences.getOfforganizationInterval(_context);

            // Determine offsite interval
            OrganizationV1 closestOrganization = _organizationsReader.findClosest(_context, location);
            int offlineInterval = closestOrganization != null ? closestOrganization.getOfforganizationInt() : 0;
            offlineInterval = offlineInterval > 0 ? offlineInterval : OFFSITE_INTERVAL;
            return offlineInterval;
        }

        // Process and send data
        StatusWriter.writeUpdate(_context, organization);
        sendStatusMessage(organization, location, beacons, freezed, buttonPress);
        if (location != null)
            Log.i(TAG, "Sent status update at " + location.getLatitude() + " : " + location.getLongitude());
        else if (beacons != null && beacons.length > 0)
            Log.i(TAG, "Sent status update at " + beacons[0]);

        // Determine freezed interval
        if (freezed) {
            int inactiveInterval = SettingsPreferences.isUseOrganizationParams(_context) && organization != null
                ? organization.getInactiveInt() : SettingsPreferences.getInactiveInterval(_context);
            return inactiveInterval;
        }

        // Determine active interval
        int activeInterval = SettingsPreferences.isUseOrganizationParams(_context) && organization != null
            ? organization.getActiveInt() : SettingsPreferences.getActiveInterval(_context);
        return activeInterval;
    }

    protected void sendStatusMessage(OrganizationV1 organization, Location location, String[] beacons, boolean freezed, int buttonPress) {
        // Override in child classes
    }

}
