package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class StateUpdateMessage2 extends Message {
    public byte data_version;
    public float lat;
    public float lng;
    public int alt;
    public short angle;
    public short speed;
    public DataValue[] params;
    public DataValue[] events;
    public String[] beacons;


    public StateUpdateMessage2() {
        super((byte)11);
    }

    public void stream(IStream stream) {
        super.stream(stream);
        
        data_version = stream.streamByte(data_version);

        lat = streamCoordinate(stream, lat);
        lng = streamCoordinate(stream, lng);
        alt = stream.streamInteger(alt);
        angle = stream.streamWord(angle);
        speed = stream.streamWord(speed);
                
        if (lat == 0 && lng == 0) {
            lat = lng = alt = angle = speed = 0;
        }

        params = streamDataValues(stream, params);
        events = streamDataValues(stream, events);

        beacons = streamStrings(stream, beacons);
    }
    
}