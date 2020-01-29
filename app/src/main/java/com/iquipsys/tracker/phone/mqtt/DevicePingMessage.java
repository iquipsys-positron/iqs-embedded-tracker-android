package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class DevicePingMessage extends Message {
    
    public DevicePingMessage() {
        super((byte)7);
    }

    public void stream(IStream stream) {
        super.stream(stream);
    }
    
}