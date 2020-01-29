package com.iquipsys.tracker.phone.streams;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class WriteStream implements IStream {
    private static final int MAX_BUFFER_SIZE = 512;
    private byte[] _data;
    private int _pos = 0;

    public WriteStream(int size) {
        _data =  new byte[size];
    }

    public WriteStream() {
        this(MAX_BUFFER_SIZE);
    }

    public byte streamByte(byte value) {
        _data[_pos++] = value;
        return value;
    }

    public short streamWord(short value) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort(0, value);
        for (int index = 0; index < 2; index++)
            _data[_pos++] = buffer.get(index);
        return value;
    }

    public int streamDWord(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(0, value);
        for (int index = 0; index < 4; index++)
            _data[_pos++] = buffer.get(index);
        return value;
    }

    public int streamInteger(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(0, value);
        for (int index = 0; index < 4; index++)
            _data[_pos++] = buffer.get(index);
        return value;
    }

    public String streamString(String value) {
        value = value != null && !value.isEmpty() ? value : "";
        //byte[] buffer = value.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = value.getBytes(Charset.forName("UTF-8"));
        _data[_pos++] = (byte) buffer.length;
        for (int index = 0; index < buffer.length; index++)
            _data[_pos++] = buffer[index];
        return value;
    }

    public boolean streamBoolean(boolean value) {
        streamByte(value ? (byte)1 : 0);
        return value;
    }

    public Date streamDateTime(Date value) {
        long utcTimestamp = (value.getTime() / 1000) + (value.getTimezoneOffset() * 60);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(0, utcTimestamp);
        for (int index = 0; index < 4; index++)
            _data[_pos++] = buffer.get(index + 4);

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

    public byte[] toBuffer() {
        byte[] result = new byte[_pos];
        System.arraycopy(_data, 0, result, 0, _pos);
        return result;
    }

}