package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class DevicePingReqMessage extends Message {
    public int timestamp;

    public DevicePingReqMessage() {
        super((byte)8);
    }

    public void stream(IStream stream) {
        super.stream(stream);

        timestamp = stream.streamDWord(timestamp);
    }
    
}