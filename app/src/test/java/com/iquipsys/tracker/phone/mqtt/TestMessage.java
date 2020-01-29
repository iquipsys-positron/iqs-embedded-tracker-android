package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

import java.util.Date;

public class TestMessage extends Message {

    public TestMessage() {
        super((byte)123);
    }

    public byte value1;
    public short value2;
    public int value3;
    public int value4;
    public boolean value5;
    public String value6;
    public Date value7;

    public void stream(IStream stream) {
        value1 = stream.streamByte(value1);
        value2 = stream.streamWord(value2);
        value3 = stream.streamDWord(value3);
        value4 = stream.streamDWord(value4);
        value5 = stream.streamBoolean(value5);
        value6 = stream.streamString(value6);
        value7 = stream.streamDateTime(value7);
    }

}
