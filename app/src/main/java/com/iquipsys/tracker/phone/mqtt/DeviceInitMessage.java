package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class DeviceInitMessage extends Message {
    public byte device_version;
    public byte data_version;

    public DeviceInitMessage() {
        super((byte)1);
    }

    public void stream(IStream stream) {
        super.stream(stream);

        device_version = stream.streamByte(device_version);
        data_version = stream.streamByte(data_version);
    }
    
}