package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class SignalMessage extends Message {
    public byte signal;
    public int timestamp;

    public SignalMessage() {
        super((byte)4);
    }

    public void stream(IStream stream) {
        super.stream(stream);
        
        signal = stream.streamByte(signal);
        timestamp = stream.streamDWord(timestamp);
    }
    
}