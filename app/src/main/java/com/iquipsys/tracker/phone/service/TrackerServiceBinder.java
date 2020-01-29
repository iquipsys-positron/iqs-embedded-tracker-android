package com.iquipsys.tracker.phone.service;

import android.os.Binder;

public class TrackerServiceBinder extends Binder {
    private ITrackerService _service;

    public TrackerServiceBinder(ITrackerService service) {
        _service = service;
    }

    public ITrackerService getService() {
        return _service;
    }
}
