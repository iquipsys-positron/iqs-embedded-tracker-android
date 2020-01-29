package com.iquipsys.tracker.phone.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.iquipsys.tracker.phone.settings.SettingsPreferences;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconDetector {
    private static final String TAG = "Beacon";
    private static final int BEACON_TIMEOUT = 5;
    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
//    private static final String ALTBEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
//    private static final String EDDYSTONE_UID_LAYOUT = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    private static final BeaconParser[] BEACON_PARSERS = new BeaconParser[] {
            new BeaconParser().setBeaconLayout(IBEACON_LAYOUT),
        new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT),
        new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT)
    };

    private Map<String, Long> _beacons = new HashMap<String, Long>();
    private Context _context;
    private BeaconDetectorListener _listener;

    public interface BeaconDetectorListener {
        void onBeaconsChanged(String[] beacons);
    }

    public BeaconDetector(BeaconDetectorListener listener) {
        _listener = listener;
    }

    public void subscribe(Context context) {
        _context = context;

        if (!SettingsPreferences.isUseBeacons(context))
            return;

        BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        try {
            BluetoothAdapter ba = bm.getAdapter();

            if (ba.getState() == BluetoothAdapter.STATE_OFF ) // scanner from bluetooth will be null
                return;

            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    BluetoothLeScanner scanner = ba.getBluetoothLeScanner();
//                    ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();
//                    ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
                    ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                    scanner.startScan(Collections.<ScanFilter> emptyList(), settings, _scanCallback1);
                } else {
                    ba.startLeScan(_scanCallback2);
                }
            } catch (SecurityException e) {
                Log.e(TAG, "Failed to scan BlueTooth beacons", e);
            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to get BlueTooth adapter on subscribe", e);
        }


    }

    public void unsubscribe(Context context) {
        BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        try {
            BluetoothAdapter ba = bm.getAdapter();

            if (ba.getState() == BluetoothAdapter.STATE_OFF ) // scanner from bluetooth will be null
                return;

            if (Build.VERSION.SDK_INT > 21) {
                BluetoothLeScanner scanner = ba.getBluetoothLeScanner();
                scanner.stopScan(_scanCallback1);
            } else {
                ba.stopLeScan(_scanCallback2);
            }
        }
        catch (NullPointerException e) {
            Log.e(TAG, "Failed to get BlueTooth adapter on unsubscribe", e);
        }
    }

    private String formatUdi(Identifier ...ids) {
        String udi = "";
        for (Identifier id : ids) {
            if (udi.length() > 0)
                udi += ":";
            udi += id.toHexString();
        }
        return udi.replace("0x", "").toLowerCase();
    }

    public String getBeaconUdi(BluetoothDevice device, int rssi, byte[] scanRecord) {
        for (BeaconParser parser : BEACON_PARSERS) {
            Beacon beacon = parser.fromScanData(scanRecord, rssi, device);

            if (beacon != null) {
                if (beacon.getServiceUuid() == 0xfeaa) {
                    // This is Eddystone, which uses a service Uuid of 0xfeaa
                    Identifier eddystoneNamespaceId = beacon.getId1();
                    Identifier eddystoneInstanceId = beacon.getId2();
                    return (eddystoneNamespaceId.toHexString() + ":" + eddystoneInstanceId.toHexString())
                        .replace("0x", "").toLowerCase();
                } else {
                    // This is another type of beacon like AltBeacon or iBeacon
                    Identifier uuid = beacon.getId1();
                    Identifier major = beacon.getId2();
                    Identifier minor = beacon.getId3();
                    return (uuid.toHexString() + ":" + major.toInt() + ":" + minor.toInt())
                            .replace("0x", "").toLowerCase();
                }
            }
        }

        return null;
    }

    private void appendDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String beacon = getBeaconUdi(device, rssi, scanRecord);

        if (beacon != null) {
            boolean newBeacon = !_beacons.containsKey(beacon);

            // Add scanned beacon
            long currentTime = SystemClock.elapsedRealtimeNanos();
            _beacons.put(beacon, currentTime);

            if (newBeacon) {
                Log.d(TAG, "Detected beacon " + beacon);

                String[] beacons = getLatestBeacons();

                if (_listener != null) {
                    _listener.onBeaconsChanged(beacons);
                }

                if (_context != null) {
                    StatusWriter.writeBeacons(_context, beacons);
                    StatusWriter.broadcastRefresh(_context);
                }
            }
        }
    }

    public String[] getLatestBeacons() {
        ArrayList<String> result = new ArrayList<>();
        long currentTime = SystemClock.elapsedRealtimeNanos();

        for (HashMap.Entry<String, Long> entry : _beacons.entrySet()) {
            long timeout = (currentTime - entry.getValue().longValue()) / 1000000000;
            if (timeout <= BEACON_TIMEOUT)
                result.add(entry.getKey());
        }

        return result.toArray(new String[result.size()]);
    }


    private ScanCallback _scanCallback1 = Build.VERSION.SDK_INT >= 21 ? new ScanCallback() {
        @TargetApi(21)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result != null && result.getDevice() != null && result.getScanRecord() != null) {
                appendDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
            }
        }

        @TargetApi(21)
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            if (results != null) {
                for (ScanResult result: results) {
                    if (result != null && result.getDevice() != null && result.getScanRecord() != null) {
                        appendDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                    }
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {}
    } : null;

    private BluetoothAdapter.LeScanCallback _scanCallback2 = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null) {
               appendDevice(device, rssi, scanRecord);
            }
        }
    };

}
