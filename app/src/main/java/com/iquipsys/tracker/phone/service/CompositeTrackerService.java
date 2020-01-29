package com.iquipsys.tracker.phone.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.iquipsys.tracker.phone.settings.SettingsPreferences;
import com.iquipsys.tracker.phone.status.StatusPreferences;

public class CompoorganizationTrackerService extends android.app.Service implements ITrackerService {
    private IBinder _binder = new TrackerServiceBinder(this);
    private BaseTrackerService _selectedService;
    private BaseTrackerService _unselectedService;
    private MqttTrackerService _mqttService = new MqttTrackerService();
    private RestTrackerService _restService = new RestTrackerService();

    public CompoorganizationTrackerService() {}

    @Override
    public IBinder onBind(Intent intent) {
        Context context = getApplicationContext();

        _mqttService.setContext(context);
        _restService.setContext(context);

        if (SettingsPreferences.isUseMqttBroker(context)) {
            _selectedService = _mqttService;
            _unselectedService = _restService;
        } else {
            _selectedService = _restService;
            _unselectedService = _mqttService;
        }

        if (StatusPreferences.getRunning(getApplicationContext())) {
            _selectedService.activate();
            _unselectedService.connectToService();
        }

        return _binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        _mqttService.deactivate();
        _restService.deactivate();
        return true;
    }

    public void startRunning() {
        if (_selectedService != null) {
            _selectedService.startRunning();
            _unselectedService.connectToService();
        }
    }

    public void stopRunning() {
        if (_selectedService != null) {
            _selectedService.stopRunning();
            _unselectedService.disconnectFromServer();
        }
    }

    public void pressButton(boolean longPress) {
        if (_selectedService != null)
            _selectedService.pressButton(longPress);
    }
}
