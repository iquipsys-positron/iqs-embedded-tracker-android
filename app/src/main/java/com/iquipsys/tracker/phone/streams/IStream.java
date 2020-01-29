package com.iquipsys.tracker.phone.streams;

import java.util.Date;

public interface IStream {
    byte streamByte(byte value);
    short streamWord(short value);
    int streamDWord(int value);
    int streamInteger(int value);
    String streamString(String value);
    boolean streamBoolean(boolean value);
    Date streamDateTime(Date value);

    Byte streamNullableByte(Byte value);
    Short streamNullableWord(Short value);
    Integer streamNullableDWord(Integer value);
    Integer streamNullableInteger(Integer value);
    String streamNullableString(String value);
    Boolean streamNullableBoolean(Boolean value);
    Date streamNullableDateTime(Date value);
}