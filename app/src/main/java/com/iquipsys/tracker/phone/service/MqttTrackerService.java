package com.iquipsys.tracker.phone.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.iquipsys.tracker.phone.R;
import com.iquipsys.tracker.phone.data.DatabaseDescription;
import com.iquipsys.tracker.phone.mqtt.DataValue;
import com.iquipsys.tracker.phone.mqtt.DeviceInitMessage;
import com.iquipsys.tracker.phone.mqtt.DevicePingMessage;
import com.iquipsys.tracker.phone.mqtt.DevicePingReqMessage;
import com.iquipsys.tracker.phone.mqtt.ErrorMessage;
import com.iquipsys.tracker.phone.mqtt.IncomingMessageDecoder;
import com.iquipsys.tracker.phone.mqtt.Message;
import com.iquipsys.tracker.phone.mqtt.CommandMessage;
import com.iquipsys.tracker.phone.mqtt.SignalMessage;
import com.iquipsys.tracker.phone.mqtt.OrganizationInfoMessage;
import com.iquipsys.tracker.phone.mqtt.StateUpdateMessage;
import com.iquipsys.tracker.phone.mqtt.StateUpdateMessage2;
import com.iquipsys.tracker.phone.settings.SettingsPreferences;
import com.iquipsys.tracker.phone.rest.OrganizationV1;
import com.iquipsys.tracker.phone.status.ButtonPress;
import com.iquipsys.tracker.phone.streams.WriteStream;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MqttTrackerService extends BaseTrackerService implements MqttCallbackExtended {
    private static final String TAG = "Mqtt";
    private MqttAndroidClient _client;
    private boolean _connected = false;
    private boolean _connecting = true;
    private OrganizationV1 _organization;

    final String serverUri = "tcp://m14.cloudmqtt.com:10756";
    final String username = "hgvtsylf";
    final String password = "hWzCz_3pDG5N";

    public MqttTrackerService() {}

    private String getClientId() {
        String trackerUdi = SettingsPreferences.getTrackerUdi(_context);
        trackerUdi = trackerUdi != null && !trackerUdi.isEmpty() ? trackerUdi : "misconfigured_phone";
        trackerUdi = trackerUdi.replace("+", "")
                .replace("(", "").replace(")", "")
                .replace(" ", "").replace("-", "");
        return trackerUdi;
    }

    private String getServerUri() {
        String serverUri = SettingsPreferences.getMqttBrokerUrl(_context);
        serverUri = serverUri.replace("mqtt://", "tcp://");
        //serverUri = this.serverUri;
        return serverUri;
    }

    @Override
    public void connectToService() {
        String serverUri = getServerUri();
        String clientId = getClientId();
        //_client = new MqttAndroidClient(_context, serverUri, clientId);
        _client = new MqttAndroidClient(_context, serverUri, clientId,
                new MemoryPersistence(), MqttAndroidClient.Ack.AUTO_ACK);
        _client.setCallback(this);
        _connected = false;
        _connecting = true;

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setKeepAliveInterval(900000);
        mqttConnectOptions.setConnectionTimeout(120);
        mqttConnectOptions.setCleanSession(false);
        //mqttConnectOptions.setUserName(username);
        //mqttConnectOptions.setPassword(password.toCharArray());

        try {

            _client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    _client.setBufferOpts(disconnectedBufferOptions);

                    _connected = true;
                    _connecting = false;

                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    _connecting = false;
                    Log.w(TAG, "Failed to connectToService to MQTT broker: ");
                    Log.d(TAG, exception.getMessage());
                    String fullStackTrace = "";
                    for (int i=0; i< exception.getStackTrace().length; i++) {
                        fullStackTrace += exception.getStackTrace()[i] + "\n";
                    }
                    Log.d(TAG, "stack trace: " + fullStackTrace);

                }
            });

        } catch (MqttException e){
            Log.e(TAG, "Failed to connectToService to MQTT broker", e);
        }
    }

    @Override
    public void disconnectFromServer() {
        if (_client != null) {
            try {
                _client.disconnect();
            } catch(Exception e) {
                Log.e(TAG, "Failed to disconnect MQTT", e);
            } finally {
                _client = null;
                _connected = false;
                _organization = null;
            }
        }
    }

    private void subscribeToTopic() {
        String topic = getClientId() + "/down";

        try {
            _client.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w(TAG,"Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w(TAG, "Subscribed fail!");
                }
            });

        } catch (MqttException e) {
            Log.e(TAG, "Failed to subscribe", e);
        }
    }

    private void publishToTopic(byte[] message) {
        String topic = getClientId() + "/up";

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            Log.d(TAG, dateFormat.format(date)+" Publishing to topic: "+topic);
            _client.publish(topic, message, 1, false);
        } catch (Exception e) {
            StatusWriter.writeError(_context, e.getMessage());
        }
    }

    @Override
    public void connectComplete(boolean b, String s) {
        _connected = true;
        Log.w(TAG, "Connected to MQTT");
    }

    @Override
    public void connectionLost(Throwable throwable) {
        _connected = false;
        Log.w(TAG, "Lost connection to MQTT");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        try {
            Message message = IncomingMessageDecoder.decode(mqttMessage.getPayload());

            if (message instanceof ErrorMessage) {
                StatusWriter.writeError(_context, ((ErrorMessage)message).message);
            }
            else if (message instanceof SignalMessage) {
                sendSignal((SignalMessage)message);
            }
            else if (message instanceof CommandMessage) {
                Log.d(TAG, "messageArrived: from topic " + topic);
                sendCommand((CommandMessage)message);
            }
            else if (message instanceof DevicePingReqMessage) {
                sendPingMessage((DevicePingReqMessage)message);
            }
            else if (message instanceof OrganizationInfoMessage) {
                updateOrganizationInfo((OrganizationInfoMessage)message);
            }
        } catch (Exception e) {
            StatusWriter.writeError(_context, e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken mqttDeliveryToken) {}

    @Override
    protected void sendStatusMessage(
            OrganizationV1 organization, Location location, String[] beacons, boolean freezed, int buttonPress) {

        // If not connected then skip
        if (!_connected) {
            Log.d(TAG, "Not connected. Skip sending");
            if (!_connecting) {
                String error = _context.getResources().getString(R.string.error_no_connection);
                StatusWriter.writeError(_context, error);
            }
            return;
        }

        // For new organization send init message
        if (organization != null && (_organization == null || !_organization.getId().equals(organization.getId())))
            sendInitMessage(organization);

        StateUpdateMessage2 message = new StateUpdateMessage2();

        message.org_id = organization != null ? organization.getId() : null;
        message.data_version = organization != null ? (byte)organization.getVersion() : 0;

        String deviceUdi = SettingsPreferences.getTrackerUdi(_context);
        message.device_udi = deviceUdi;

        Date time = new Date(System.currentTimeMillis());
        message.time = time;

        if (location != null) {
            message.lat = (float) location.getLatitude();
            message.lng = (float) location.getLongitude();
            message.alt = location.hasAltitude() ? (short)location.getAltitude() : 0;
            message.speed = location.hasSpeed() ? (byte)location.getSpeed() : 0;
            message.angle = location.hasBearing() ? (short)location.getBearing() : 0;
        }

        message.params = new DataValue[] {new DataValue((byte)1, 1), new DataValue((byte)2, freezed ? 1 : 0)};

        boolean pressed = buttonPress == ButtonPress.SHORT;
        boolean long_pressed = buttonPress == ButtonPress.LONG;

        message.events = new DataValue[] {new DataValue((byte)1, pressed ? 1 : 0), new DataValue((byte)2, long_pressed ? 1 : 0)};

        message.beacons = beacons;

        sendMessage(message);
    }

    private void sendInitMessage(OrganizationV1 organization) {
        if (organization == null) return;

        _organization = organization;

        DeviceInitMessage message = new DeviceInitMessage();

        message.org_id = organization.getId();
        message.data_version = (byte)organization.getVersion();

        String deviceUdi = SettingsPreferences.getTrackerUdi(_context);
        message.device_udi = deviceUdi;
        message.device_version = 1;

        sendMessage(message);
    }

    private void sendPingMessage(DevicePingReqMessage pingReqMessage) {
        DevicePingMessage message = new DevicePingMessage();

        message.org_id = pingReqMessage.org_id;
        message.gw_udi = pingReqMessage.gw_udi;

        String deviceUdi = SettingsPreferences.getTrackerUdi(_context);
        message.device_udi = deviceUdi;

        Date time = new Date(System.currentTimeMillis());
        message.time = time;

        sendMessage(message);
    }

    private void sendMessage(Message message) {
        WriteStream stream = new WriteStream();
        message.stream(stream);
        byte[] buffer = stream.toBuffer();
        publishToTopic(buffer);
    }

    private void sendSignal(SignalMessage message) {
        switch (message.signal) {
            case 1:
                SignalSender.broadcastSignal(_context, R.string.signal_attention);
                break;
            case 2:
                SignalSender.broadcastSignal(_context, R.string.signal_confirm);
                break;
            case 3:
                SignalSender.broadcastSignal(_context, R.string.signal_warning);
                break;
            case 4:
                SignalSender.broadcastSignal(_context, R.string.signal_emergency);
                break;
        }
    }

    private void sendCommand(CommandMessage message) {
        for (DataValue command :
                message.commands) {
            switch (command.val) {
                case 1:
                    SignalSender.broadcastSignal(_context, R.string.signal_attention);
                    break;
                case 2:
                    SignalSender.broadcastSignal(_context, R.string.signal_confirm);
                    break;
                case 3:
                    SignalSender.broadcastSignal(_context, R.string.signal_warning);
                    break;
                case 4:
                    SignalSender.broadcastSignal(_context, R.string.signal_emergency);
                    break;
            }
        }
    }

    private void updateOrganizationInfo(OrganizationInfoMessage message) {
        Cursor cursor = _context.getContentResolver().query(
                DatabaseDescription.Organization.CONTENT_URI,
                null,
                DatabaseDescription.Organization.COLUMN_ORG_ID + "=\""  + message.org_id + "\"",
                null,
                DatabaseDescription.Organization.COLUMN_NAME + " COLLATE NOCASE ASC"
        );
        if (cursor == null) return;

        try {
            Uri organizationUri = null;

            int idIndex = cursor.getColumnIndex(DatabaseDescription.Organization._ID);
            int orgIdIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_ORG_ID);
            int nameIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_NAME);
            int descriptionIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_DESCRIPTION);
            int offlineTimeoutIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_OFFLINE_TIMEOUT);

            // Find information about the organization
            while (cursor.moveToNext()) {
                if (cursor.getString(orgIdIndex).equals(message.org_id)) {
                    long id = cursor.getLong(idIndex);
                    organizationUri = DatabaseDescription.Organization.buildOrganizationUri(id);
                    break;
                }
            }

            // If organization wasn't fond then exit
            if (organizationUri == null) return;

            // Update organization info
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseDescription.Organization.COLUMN_ORG_ID, message.org_id);
            contentValues.put(DatabaseDescription.Organization.COLUMN_NAME, cursor.getString(nameIndex));
            contentValues.put(DatabaseDescription.Organization.COLUMN_DESCRIPTION, cursor.getString(descriptionIndex));
            contentValues.put(DatabaseDescription.Organization.COLUMN_VERSION, message.data_version);
            contentValues.put(DatabaseDescription.Organization.COLUMN_CENTER_LAT, message.center_lat);
            contentValues.put(DatabaseDescription.Organization.COLUMN_CENTER_LNG, message.center_long);
            contentValues.put(DatabaseDescription.Organization.COLUMN_RADIUS, message.radius != 0 ? message.radius : 5);
            contentValues.put(DatabaseDescription.Organization.COLUMN_ACTIVE_INT, message.active_int != 0 ? message.active_int : 60);
            contentValues.put(DatabaseDescription.Organization.COLUMN_INACTIVE_INT, message.inactive_int != 0 ? message.inactive_int : 300);
            contentValues.put(DatabaseDescription.Organization.COLUMN_OFFSITE_INT, message.offsite_int != 0 ? message.offsite_int : 900);
            contentValues.put(DatabaseDescription.Organization.COLUMN_OFFLINE_TIMEOUT, cursor.getInt(offlineTimeoutIndex));

            _context.getContentResolver().update(
                    organizationUri, contentValues, null, null
            );

        } finally {
            cursor.close();
        }
    }

}
