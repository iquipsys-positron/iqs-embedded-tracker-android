package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.IStream;

public class ErrorMessage extends Message {
    public short code;
    public String message;

    public ErrorMessage() {
        super((byte)1);
    }

    public void stream(IStream stream) {
        super.stream(stream);

        code = stream.streamWord(code);
        message = stream.streamString(message);
    }

}