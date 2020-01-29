package com.iquipsys.tracker.phone.rest;

import com.google.gson.annotations.SerializedName;
import com.iquipsys.tracker.phone.mqtt.DataValue;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class StatusMessageV1 {
    private static final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @SerializedName("device_udi")
    private String _device_udi;
    @SerializedName("org_id")
    private String _org_id;
    @SerializedName("time")
    private String _time;

    @SerializedName("params")
    private DataValue[] _params;
    @SerializedName("params")
    private DataValue[] _events;

    @SerializedName("lat")
    private double _lat;
    @SerializedName("long")
    private double _long;
    @SerializedName("alt")
    private Double _alt;
    @SerializedName("angle")
    private Double _angle;
    @SerializedName("speed")
    private Double _speed;
    @SerializedName("quality")
    private Integer _quality;
    @SerializedName("beacons")
    private String[] _beacons;


    public String getDeviceUdi() {
        return _device_udi;
    }

    public void setDeviceUdi(String value) {
        this._device_udi = value;
    }

    public String getOrganizationId() {
        return _org_id;
    }

    public void setOrganizationId(String value) {
        this._org_id = value;
    }

    public String getTime() {
        return _time;
    }

    public void setTime(String value) {
        this._time = value;
    }

    public void setTime(Date value) {
        value = value != null ? value : new Date(System.currentTimeMillis());
        String strValue = isoFormat.format(value);
        setTime(strValue);
    }

    public DataValue[] getParams() {
        return _params;
    }

    public void setParams(DataValue[] params) {
//        this._params = params;

        this._params = new DataValue[params.length];
        for (int i = this._params.length - 1; i >= 0; --i) {
            DataValue p = params[i];
            if (p != null) {
                this._params[i] = new DataValue(p);
            }
        }
    }

    public DataValue[] getEvents() {
        return _events;
    }

    public void setEvents(DataValue[] events) {
//        this._events = events;

        this._events = new DataValue[events.length];
        for (int i = this._events.length - 1; i >= 0; --i) {
            DataValue e = events[i];
            if (e != null) {
                this._events[i] = new DataValue(e);
            }
        }
    }

    public double getLatitude() {
        return _lat;
    }

    public void setLatitude(double lat) {
        this._lat = lat;
    }

    public double getLongitude() {
        return _long;
    }

    public void setLongitude(double value) {
        this._long = value;
    }

    public Double getAltitude() {
        return _alt;
    }

    public void setAltitude(Double value) {
        this._alt = value;
    }

    public Double getAngle() {
        return _angle;
    }

    public void setAngle(Double value) {
        this._angle = value;
    }

    public Double getSpeed() {
        return _speed;
    }

    public void setSpeed(Double value) {
        this._speed = value;
    }

    public Integer getQuality() {
        return _quality;
    }

    public void setQuality(Integer value) {
        this._quality = value;
    }

    public String[] getBeacons() {
        return _beacons;
    }

    public void setBeacons(String[] value) {
        this._beacons = value;
    }
}
