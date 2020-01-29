package com.iquipsys.tracker.phone.rest;

import com.google.gson.annotations.SerializedName;

public class OrganizationV1 {
    @SerializedName("id")
    private String _id;
    @SerializedName("code")
    private String _code;
    @SerializedName("create_time")
    private String _create_time;
    @SerializedName("creator_id")
    private String _creator_id;
    @SerializedName("deleted")
    private Boolean _deleted;
    @SerializedName("active")
    private boolean _active;
    @SerializedName("version")
    private int _version;

    @SerializedName("name")
    private String _name;
    @SerializedName("description")
    private String _description;
    @SerializedName("address")
    private String _address;

    @SerializedName("center")
    private GeoJsonPoint _center; // GeoJSON
    @SerializedName("radius")
    private float _radius; // In km
    @SerializedName("geometry")
    private GeoJsonPolygon _geometry; //GeoJSON
    @SerializedName("boundaries")
    private GeoJsonMultiPoint _boundaries; //GeoJSON

    @SerializedName("language")
    private String _language;
    @SerializedName("timezone")
    private String _timezone;
    @SerializedName("industry")
    private String _industry;
    @SerializedName("org_size")
    private int _org_size;
    @SerializedName("total_organizations")
    private int _total_organizations;
    @SerializedName("purpose")
    private String _purpose;

    @SerializedName("active_int")
    private int _active_int; // In seconds
    @SerializedName("inactive_int")
    private int _inactive_int; // In seconds
    @SerializedName("offsite_int")
    private int _offsite_int; // In seconds
    @SerializedName("offline_timeout")
    private int _offline_timeout; // In seconds
    @SerializedName("data_date")
    private int _data_rate;

    @SerializedName("params")
    private Object _params;

    @SerializedName("map_id")
    private String _map_id; // Blob id with map background
    @SerializedName("map_north")
    private double _map_north;
    @SerializedName("map_south")
    private double _map_south;
    @SerializedName("map_west")
    private double _map_west;
    @SerializedName("map_east")
    private double _map_east;

    public String getId() {
        return _id;
    }

    public void setId(String value) {
        this._id = value;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String value) {
        this._code = value;
    }

    public String getCreateTime() {
        return _create_time;
    }

    public void setCreateTime(String value) {
        this._create_time = value;
    }

    public String getCreatorId() {
        return _creator_id;
    }

    public void setCreatorId(String value) {
        this._creator_id = value;
    }

    public Boolean isDeleted() {
        return _deleted;
    }

    public void setDeleted(Boolean value) {
        this._deleted = value;
    }

    public boolean isActive() {
        return _active;
    }

    public void setActive(boolean value) {
        this._active = value;
    }

    public int getVersion() {
        return _version;
    }

    public void setVersion(int value) {
        this._version = value;
    }

    public String getName() {
        return _name;
    }

    public void setName(String value) {
        this._name = value;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String value) {
        this._description = value;
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String value) {
        this._address = value;
    }

    public GeoJsonPoint getCenter() {
        return _center;
    }

    public void setCenter(GeoJsonPoint value) {
        this._center = value;
    }

    public float getRadius() {
        return _radius;
    }

    public void setRadius(float value) {
        this._radius = value;
    }

    public GeoJsonPolygon getGeometry() {
        return _geometry;
    }

    public void setGeometry(GeoJsonPolygon value) {
        this._geometry = value;
    }

    public GeoJsonMultiPoint getBoundaries() {
        return _boundaries;
    }

    public void setBoundaries(GeoJsonMultiPoint value) {
        this._boundaries = value;
    }

    public String getLanguage() {
        return _language;
    }

    public void setLanguage(String value) {
        this._language = value;
    }

    public String getTimezone() {
        return _timezone;
    }

    public void setTimezone(String value) {
        this._timezone = value;
    }

    public String getIndustry() {
        return _industry;
    }

    public void setIndustry(String value) {
        this._industry = value;
    }

    public int getOrgSize() {
        return _org_size;
    }

    public void setOrgSize(Integer value) {
        this._org_size = value;
    }

    public int getTotalOrganizations() {
        return _total_organizations;
    }

    public void setTotalOrganizations(Integer value) {
        this._total_organizations = value;
    }

    public String getPurpose() {
        return _purpose;
    }

    public void setPurpose(String value) {
        this._purpose = value;
    }

    public int getActiveInt() {
        return _active_int;
    }

    public void setActiveInt(int value) {
        this._active_int = value;
    }

    public int getInactiveInt() {
        return _inactive_int;
    }

    public void setInactiveInt(int value) {
        this._inactive_int = value;
    }

    public int getOfforganizationInt() {
        return _offsite_int;
    }

    public void setOfforganizationInt(int value) {
        this._offsite_int = value;
    }

    public int getOfflineTimeout() {
        return _offline_timeout;
    }

    public void setOfflineTimeout(int value) {
        this._offline_timeout = value;
    }

    public int getDataRate() {
        return _data_rate;
    }

    public void setDataRate(int value) {
        this._data_rate = value;
    }

    public Object getParams() {
        return _params;
    }

    public void setParams(Object value) {
        this._params = value;
    }

    public String getMapId() {
        return _map_id;
    }

    public void setMapId(String value) {
        this._map_id = value;
    }

    public double getMapNorth() {
        return _map_north;
    }

    public void setMapNorth(int value) {
        this._map_north = value;
    }

    public double getMapSouth() {
        return _map_south;
    }

    public void setMapSouth(int value) {
        this._map_south = value;
    }

    public double getMapWest() {
        return _map_west;
    }

    public void setMapWest(int value) {
        this._map_west = value;
    }

    public double getMapEast() {
        return _map_east;
    }

    public void setMapEast(int value) {
        this._map_east = value;
    }
}