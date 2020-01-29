package com.iquipsys.tracker.phone.mqtt;

public class DataValue {
    public byte id;
    public int val;

    public DataValue(byte id, int val) {
        this.id = id;
        this.val = val;
    }

    public DataValue(DataValue obj) {
        this.id = obj.id;
        this.val = obj.val;
    }
}
