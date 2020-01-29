package com.iquipsys.tracker.phone.mqtt;

import com.iquipsys.tracker.phone.streams.ReadStream;

public class IncomingMessageDecoder {
    
    public static Message decode(byte[] buffer) throws Exception {
        
        byte messageType = buffer != null && buffer.length > 0 ? buffer[0] : (byte)0xFF;
        Message message = null;

        switch (messageType) {
            case 2:
                message = new OrganizationInfoMessage();
                break;
            case 4:
                message = new SignalMessage();
                break;
            case 8:
                message = new DevicePingReqMessage();
                break;
            case 12:
                message = new CommandMessage();
                break;
        }

        if (message != null) {
            ReadStream stream = new ReadStream(buffer);
            // Deserialize the message
            message.stream(stream);
            return message;
        } else {
            throw new Exception("Received unknown or invalid message");
        }
    }
    
}