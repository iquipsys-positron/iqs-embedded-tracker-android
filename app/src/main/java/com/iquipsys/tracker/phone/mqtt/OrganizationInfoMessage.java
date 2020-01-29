package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class OrganizationInfoMessage extends Message {
    public byte data_version;
    public float center_lat;
    public float center_long;
    public byte radius;
    public short active_int;
    public short inactive_int;
    public short offsite_int;
    public byte data_rate;

    public OrganizationInfoMessage() {
        super((byte)2);

        active_int = 30;
        inactive_int = 300;
        offsite_int = 900;
    }

    public void stream(IStream stream) {
        super.stream(stream);

        data_version = stream.streamByte(data_version);
        center_lat = streamCoordinate(stream, center_lat);
        center_long = streamCoordinate(stream, center_long);
        radius = stream.streamByte(radius);
        active_int = stream.streamWord(active_int);
        inactive_int = stream.streamWord(inactive_int);
        offsite_int = stream.streamWord(offsite_int);
        data_rate = stream.streamByte(data_rate);
    }
    
}