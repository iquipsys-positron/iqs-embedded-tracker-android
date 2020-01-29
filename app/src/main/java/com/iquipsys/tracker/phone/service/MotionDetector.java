package com.iquipsys.tracker.phone.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.iquipsys.tracker.phone.status.StatusPreferences;

public class MotionDetector implements SensorEventListener {
    private static final String TAG = "Motion";
    private static final int MOBILITY_INTERVAL = 5000;
    private static final int IMMOBILITY_TIMEOUT = 300000;

    private Context _context;
    private long _lastMotionTime = System.currentTimeMillis();
    private MotionDetectorListener _listener;

    public interface MotionDetectorListener {
        void onMotionChanged(boolean freezed);
    }

    public MotionDetector(MotionDetectorListener listener) {
        _listener = listener;
    }

    public void subscribe(Context context) {
        _context = context;

        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(accelerometer == null) {
            Log.d(TAG, "Linear accelerometer is null!");
            accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if(accelerometer == null) {
            Log.d(TAG, "Accelerometer is also null!");
            //TODO
        }

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unsubscribe(Context context) {
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(this);
    }

    public boolean isfreezed() {
        return System.currentTimeMillis() - _lastMotionTime >= IMMOBILITY_TIMEOUT;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION || event.sensor.getType() == Sensor.TYPE_ACCELEROMETER))
            return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        boolean updated = false;
        float diff = (float) Math.sqrt(x * x + y * y + z * z);
        //Log.d(TAG,"Diff: " + diff);
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            diff = (float) (diff - 9.3); // movement threadhold
            if(diff<0)
                diff = (float) (0 - diff);
        }

        long oldMotionTime = _lastMotionTime;
        long now = System.currentTimeMillis();
        long delay = now - oldMotionTime;

        // 0.5 is a threshold, you can test it and change it
        if (diff > 0.5) {
            _lastMotionTime = now;
            if (delay >= IMMOBILITY_TIMEOUT) {
                updated = true;
                Log.d(TAG, "Detected movement");
            }
        } else {
            if (delay >= IMMOBILITY_TIMEOUT && delay < IMMOBILITY_TIMEOUT + 200) {
                updated = true;
                Log.d(TAG, "Detected immobility");
            }
        }

        if (updated) {
            boolean freezed = isfreezed();

            if (_context != null) {
                StatusWriter.writeMobility(_context, !freezed);
                StatusWriter.broadcastRefresh(_context);
            }

            if (_listener != null) {
                _listener.onMotionChanged(freezed);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
