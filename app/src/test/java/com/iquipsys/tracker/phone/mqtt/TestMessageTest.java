package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.ReadStream;
import com.iquipsys.tracker.phone.streams.WriteStream;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;


public class TestMessageTest {

    @Test
    public void testReadingAndWritingMessage() {
        WriteStream writeStream = new WriteStream();

        TestMessage message = new TestMessage();
        message.value1 = 1;
        message.value2 = 2;
        message.value3 = 3;
        message.value4 = -4;
        message.value5 = true;
        message.value6 = "ABC";
        message.value7 = new Date();

        message.stream(writeStream);

        byte[] buffer = writeStream.toBuffer();
        assertEquals(20, buffer.length);

        ReadStream readStream = new ReadStream(buffer);
        TestMessage message2 = new TestMessage();
        message2.stream(readStream);

        assertEquals(message.value1, message2.value1);
        assertEquals(message.value2, message2.value2);
        assertEquals(message.value3, message2.value3);
        assertEquals(message.value4, message2.value4);
        assertEquals(message.value5, message2.value5);
        assertEquals(message.value6, message2.value6);
        assertEquals(message.value7.getTime() / 1000, message2.value7.getTime() / 1000);
    }

}
