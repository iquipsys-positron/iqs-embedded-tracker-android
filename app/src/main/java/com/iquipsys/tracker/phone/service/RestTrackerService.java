package com.iquipsys.tracker.phone.service;

import android.content.Context;
import android.location.Location;

import com.iquipsys.tracker.phone.mqtt.DataValue;
import com.iquipsys.tracker.phone.rest.RestGatewayClientV1;
import com.iquipsys.tracker.phone.rest.OrganizationV1;
import com.iquipsys.tracker.phone.rest.StatusMessageV1;
import com.iquipsys.tracker.phone.settings.SettingsPreferences;
import com.iquipsys.tracker.phone.status.ButtonPress;

import java.util.Date;

public class RestTrackerService extends BaseTrackerService {

    public RestTrackerService() {}

    private StatusMessageV1 createStatusMessage(
            OrganizationV1 organization, Location location, String[] beacons, boolean freezed, int buttonPress) {

        StatusMessageV1 message = new StatusMessageV1();

        String deviceUdi = SettingsPreferences.getTrackerUdi(_context);
        message.setDeviceUdi(deviceUdi);

        String orgId = organization != null ? organization.getId() : null;
        message.setOrganizationId(orgId);

        Date time = new Date(System.currentTimeMillis());
        message.setTime(time);

        if (location != null) {
            double latitude = location.getLatitude();
            message.setLatitude(latitude);

            double longitude = location.getLongitude();
            message.setLongitude(longitude);

            Double altitude = location.hasAltitude() ? location.getAltitude() : null;
            message.setAltitude(altitude);

            Double speed = location.hasSpeed() ? location.getSpeed() * 3.6 : null;
            message.setSpeed(speed);

            Double angle = location.hasBearing() ? (double) location.getBearing() : null;
            message.setAngle(angle);
        }

        message.setParams(new DataValue[] {new DataValue((byte)1, 1), new DataValue((byte)2, freezed ? 1 : 0)});

        boolean pressed = buttonPress == ButtonPress.SHORT;
        boolean long_pressed = buttonPress == ButtonPress.LONG;

        message.setEvents(new DataValue[] {new DataValue((byte)1, pressed ? 1 : 0), new DataValue((byte)2, long_pressed ? 1 : 0)});

        message.setBeacons(beacons);

        return message;
    }

    @Override
    protected void sendStatusMessage(
        OrganizationV1 organization, Location location, String[] beacons, boolean freezed, int buttonPress) {

        String baseRoute = SettingsPreferences.getServerUrl(_context);
        RestGatewayClientV1 client = new RestGatewayClientV1(baseRoute);
        StatusMessageV1 message = createStatusMessage(organization, location, beacons, freezed, buttonPress);
        try {
            client.updateStatus(message);
        } catch (Exception e) {
            StatusWriter.writeError(_context, e.getMessage());
        }
    }

}
