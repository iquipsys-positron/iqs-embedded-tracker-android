package com.iquipsys.tracker.phone.mqtt;

import android.util.Log;

import com.iquipsys.tracker.phone.streams.IStream;
import com.iquipsys.tracker.phone.streams.IStreamable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.iquipsys.tracker.phone.service.BaseTrackerService.TAG;

public class Message implements IStreamable {
    public byte type;
    public String org_id;
    public String gw_udi;
    public String device_udi;
    public Date time;

    protected Message(byte messageType) {
        type = messageType;
        time = new Date();
    }

    public void stream(IStream stream) {
        type = stream.streamByte(type);
        org_id = stream.streamString(org_id);
        gw_udi = stream.streamString(gw_udi);
        device_udi = stream.streamString(device_udi);
        time = stream.streamDateTime(time);
        Log.d("Message", "stream: org_id=" + org_id + ";time=" + time + ";device_udi" + device_udi + ";gw_udi=" + gw_udi);
    }

    protected float streamCoordinate(IStream stream, float value) {
        value = value * 10000000;
        value = stream.streamInteger((int) value);
        return value / 10000000;
    }

    protected String[] streamStrings(IStream stream, String[] values) {
        values = values != null ? values : new String[0];
        byte count = stream.streamByte((byte) values.length);
        String[] result = new String[count];
        for (int index = 0; index < count; index++) {
            String item = stream.streamString(values[index]);
            result[index] = item;
        }
        return result;
    }

    protected boolean isByteValue(DataValue value) {
        return value.val >= 0 && value.val <= 0xFF;
    }

    protected DataValue[] streamDataValues(IStream stream, DataValue[] values) {
        ArrayList<DataValue> dataValuesByte = new ArrayList<>();
        ArrayList<DataValue> dataValuesInt = new ArrayList<>();

        ArrayList<DataValue> newDataValues = new ArrayList<>();

        if (values != null) {
            for (DataValue value :
                    values) {
                if (isByteValue(value)) {
                    dataValuesByte.add(value);
                } else {
                    dataValuesInt.add(value);
                }
            }
        }

        byte dataValueByteCount = stream.streamByte((byte) dataValuesByte.size());

        for (int j = 0; j < dataValueByteCount; j++) {
            // Save byte values
            DataValue dataValue = null;
            if (dataValuesByte.size() > 0) {
                dataValue = dataValuesByte.get(j);
            }

            byte id = dataValue != null ? dataValue.id : 0;
            id = stream.streamByte(id);

            byte val = dataValue != null ? (byte) dataValue.val : 0;
            val = stream.streamByte(val);
            newDataValues.add(new DataValue(id, val));
        }

        byte dataValueIntCount = stream.streamByte((byte) dataValuesInt.size());

        for (int j = 0; j < dataValueIntCount; j++) {
            // Save byte values
            DataValue dataValue = null;
            if (dataValuesByte.size() > 0) {
                dataValue = dataValuesInt.get(j);
            }

            byte id = dataValue != null ? dataValue.id : 0;
            id = stream.streamByte(id);

            int val = dataValue != null ? dataValue.val : 0;
            val = stream.streamInteger(val);
            newDataValues.add(new DataValue(id, val));
        }

        DataValue[] newDataValuesArray = new DataValue[newDataValues.size()];
        newDataValuesArray = newDataValues.toArray(newDataValuesArray);

        return newDataValuesArray;
    }

}