package com.iquipsys.tracker.phone.streams;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ReadStream implements IStream {
    private byte[] _data;
    private int _pos = 0;

    public ReadStream(byte[] data) {
        _data = data;
    }

    public byte streamByte(byte value) {
        value = _data[_pos++];
        return value;
    }

    public short streamWord(short value) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        for (int index = 0; index < 2; index++)
            buffer.put(index, _data[_pos++]);
        value = buffer.getShort(0);
        return value;
    }

    public int streamDWord(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        for (int index = 0; index < 4; index++)
            buffer.put(index, _data[_pos++]);
        value = buffer.getInt(0);
        return value;
    }

    public int streamInteger(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        for (int index = 0; index < 4; index++)
            buffer.put(index, _data[_pos++]);
        value = buffer.getInt(0);
        return value;
    }

    public String streamString(String value) {
        byte length = _data[_pos++];
        if (length == 0)
            value = "";
        else {
            byte[] buffer = new byte[length];
            System.arraycopy(_data, _pos, buffer, 0, length);
            //value = new String(buffer, StandardCharsets.UTF_8);
            value = new String(buffer, Charset.forName("UTF-8"));
            _pos += length;
        }
        return value;
    }

    public boolean streamBoolean(boolean value) {
        return streamByte((byte)0) != 0;
    }

    public Date streamDateTime(Date value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        for (int index = 0; index < 4; index++)
            buffer.put(index, (byte)0);
        for (int index = 0; index < 4; index++)
            buffer.put(index + 4, _data[_pos++]);
        long utcTimestamp = buffer.getLong(0);

        long ticks = (utcTimestamp - new Date().getTimezoneOffset() * 60) * 1000;
        value = new Date(ticks);
        return value;
    }

    public Byte streamNullableByte(Byte value) {
        boolean notNull = streamBoolean(value != null);
        return notNull ? streamByte(value) : null;
    }

    public Short streamNullableWord(Short value) {
        boolean notNull = streamBoolean(value != null);
        return notNull ? streamWord(value) : null;
    }

    public Integer streamNullableDWord(Integer value) {
        boolean notNull = streamBoolean(value != null);
        return notNull ? streamDWord(value) : null;
    }

    public Integer streamNullableInteger(Integer value) {
        boolean notNull = streamBoolean(value != null);
        return notNull ? streamInteger(value) : null;
    }

    public String streamNullableString(String value) {
        boolean notNull = streamBoolean(value != null);
        return notNull ? streamString(value) : null;
    }

    public Boolean streamNullableBoolean(Boolean value) {
        boolean notNull = streamBoolean(value != null);
        return notNull ? streamBoolean(value) : null;
    }

    public Date streamNullableDateTime(Date value) {
        boolean notNull = streamBoolean(value != null);
        return notNull ? streamDateTime(value) : null;
    }
    
    public void reset() {
        _pos = 0;
    }
}