package com.iquipsys.tracker.phone.rest;

import com.google.gson.annotations.SerializedName;

public class ErrorDescription {
    @SerializedName("code")
    private String _code;
    @SerializedName("name")
    private String _name;
    @SerializedName("message")
    private String _message;
    @SerializedName("category")
    private String _category;
    @SerializedName("stack_trace")
    private String _stackTrace;

    public String getCode() {
        return _code;
    }

    public void setCode(String value) {
        this._code = value;
    }

    public String getName() {
        return _name;
    }

    public void setName(String value) {
        this._name = value;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String value) {
        this._message = value;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String value) {
        this._category = value;
    }

    public String getStackTrace() {
        return _stackTrace;
    }

    public void setStackTrace(String value) {
        this._stackTrace = value;
    }
}
