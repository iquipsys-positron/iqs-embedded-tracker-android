package com.iquipsys.tracker.phone.service;

public interface ITrackerService {
    void startRunning();
    void stopRunning();
    void pressButton(boolean longPress);
}