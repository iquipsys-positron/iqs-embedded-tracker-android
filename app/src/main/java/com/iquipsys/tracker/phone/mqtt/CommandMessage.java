package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class CommandMessage extends Message {
    public int timestamp;
    public DataValue[] commands;

    public CommandMessage() {
        super((byte)12);
    }

    public void stream(IStream stream) {
        super.stream(stream);

        this.timestamp = stream.streamDWord(this.timestamp);
        this.commands = streamDataValues(stream, this.commands);
    }
    
}