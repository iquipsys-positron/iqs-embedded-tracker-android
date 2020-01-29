package com.iquipsys.tracker.phone.rest;

import com.google.gson.annotations.SerializedName;

public class GeoJsonPoint {
    @SerializedName("type")
    private String _type;
    @SerializedName("coordinates")
    private float[] _coordinates;

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public float[] getCoordinates() {
        return _coordinates;
    }

    public void setCoordinates(float[] _coordinates) {
        this._coordinates = _coordinates;
    }
}
