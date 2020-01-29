package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class StateUpdateMessage extends Message {
    public byte data_version;
    public boolean freezed;
    public boolean pressed;
    public boolean long_pressed;
    public float lat;
    public float lng;
    public int alt;
    public short angle;
    public short speed;
    public byte quality;
    public String[] beacons;

    public StateUpdateMessage() {
        super((byte)3);
    }

    public void stream(IStream stream) {
        super.stream(stream);
        
        data_version = stream.streamByte(data_version);

        byte state = freezed ? (byte)1 : 0;
        state |= pressed ? 2 : 0;
        state |= long_pressed ? 4 : 0;
        state = stream.streamByte(state);
        freezed = (state & 1) != 0;
        pressed = (state & 2) != 0;
        long_pressed = (state & 4) != 0;

        lat = streamCoordinate(stream, lat);
        lng = streamCoordinate(stream, lng);
        alt = stream.streamInteger(alt);
        angle = stream.streamWord(angle);
        speed = stream.streamWord(speed);
        quality = stream.streamByte(quality);
                
        if (lat == 0 && lng == 0) {
            lat = lng = alt = angle = speed = quality = 0;
        }

        beacons = streamStrings(stream, beacons);
    }
    
}