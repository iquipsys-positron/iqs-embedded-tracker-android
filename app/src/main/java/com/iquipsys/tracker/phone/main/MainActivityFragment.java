package com.iquipsys.tracker.phone.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.nfc.Tag;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iquipsys.tracker.phone.R;
import com.iquipsys.tracker.phone.service.ITrackerService;
import com.iquipsys.tracker.phone.service.SignalSender;
import com.iquipsys.tracker.phone.service.TrackerService;
import com.iquipsys.tracker.phone.service.StatusWriter;
import com.iquipsys.tracker.phone.service.TrackerServiceBinder;
import com.iquipsys.tracker.phone.settings.SettingsActivity;
import com.iquipsys.tracker.phone.settings.SettingsPreferences;
import com.iquipsys.tracker.phone.status.Signal;
import com.iquipsys.tracker.phone.status.Status;
import com.iquipsys.tracker.phone.status.StatusPreferences;

public class MainActivityFragment extends Fragment {

    public static final String TAG = "MainActivityFragment";

    private static final long MIN_PRESS_TIMEOUT = 3000;
    private static final long SHORT_PRESS_INTERVAL = 850;
    private static final long LONG_PRESS_INTERVAL = 2000;

    private Drawable _powerButtonOffDrawable;
    private Drawable _powerButtonOnDrawable;

    private Drawable _redIndicatorDrawable;
    private Drawable _greyIndicatorDrawable;
    private Drawable _greenIndicatorDrawable;
    private Animation _indicatorAnimation;

    private ImageButton _powerButton;
    private ImageButton _pressButton;

    private View _mainView;
    private ImageView _powerImageView;
    private ImageView _locationImageView;
    private ImageView _mobilityImageView;
    private ImageView _networkImageView;
    private TextView _statusTextView;
    private TextView _pressTextView;

    public static final int PRESS_SHORT_SOUND_ID = 0;
    public static final int PRESS_LONG_SOUND_ID = 1;
    public static final int SIGNAL_ATTENTION_SOUND_ID = 2;
    public static final int SIGNAL_CONFIRM_SOUND_ID = 3;
    public static final int SIGNAL_WARNING_SOUND_ID = 4;
    public static final int SIGNAL_EMERGENCY_SOUND_ID = 5;
    private SoundPool _soundPool;
    private SparseIntArray _soundMap;

    private long _startPress = 0;
    private long _lastPress = 0;
    private int _emergencySoundId = 0;

    private ITrackerService _service;
    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            _service = ((TrackerServiceBinder)binder).getService();

            final Context context = getActivity().getApplicationContext();

            boolean running = StatusPreferences.getRunning(context);

            if (running)
                _service.startRunning();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            _service = null;
        }
    };

    public MainActivityFragment() {}

    public void setService(TrackerService service) {
        _service = service;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Retrieve image resources
        _powerButtonOffDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.power_button_off);
        _powerButtonOnDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.power_button_on);
        _redIndicatorDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_indicator_red_24dp);
        _greyIndicatorDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_indicator_grey_24dp);
        _greenIndicatorDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_indicator_green_24dp);
        _indicatorAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.indicator);

        // Retrieve sound resources
        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setUsage(AudioAttributes.USAGE_ALARM);

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            builder.setAudioAttributes(attrBuilder.build());
            _soundPool = builder.build();

            _soundMap = new SparseIntArray(6);
            _soundMap.put(PRESS_SHORT_SOUND_ID, _soundPool.load(getActivity(), R.raw.press_short, 1));
            _soundMap.put(PRESS_LONG_SOUND_ID, _soundPool.load(getActivity(), R.raw.press_long, 1));
            _soundMap.put(SIGNAL_ATTENTION_SOUND_ID, _soundPool.load(getActivity(), R.raw.signal_attention, 1));
            _soundMap.put(SIGNAL_CONFIRM_SOUND_ID, _soundPool.load(getActivity(), R.raw.signal_confirmation, 1));
            _soundMap.put(SIGNAL_WARNING_SOUND_ID, _soundPool.load(getActivity(), R.raw.signal_warning, 1));
            _soundMap.put(SIGNAL_EMERGENCY_SOUND_ID, _soundPool.load(getActivity(), R.raw.signal_emergency, 1));
        }

        // Start service
        Intent intent = new Intent(getActivity(), TrackerService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, _serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        // Stop service
        getActivity().unbindService(_serviceConnection);

        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setVolumeControlStream(AudioManager.STREAM_ALARM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        _mainView = view;

        _powerButton = (ImageButton) view.findViewById(R.id.powerButton);
        _powerButton.setBackground(_powerButtonOffDrawable);
        _powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRunning();
            }
        });

        _pressButton = (ImageButton) view.findViewById(R.id.pressButton);
        _pressButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        _startPress = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (_emergencySoundId != 0) {
                            stopEmergencySound();
                        } else {
                            long now = System.currentTimeMillis();
                            long pressTimeout = now - _lastPress;
                            long pressInterval = now - _startPress;
                            if (pressTimeout >= MIN_PRESS_TIMEOUT
                                    && pressInterval >= SHORT_PRESS_INTERVAL) {
                                _lastPress = now;
                                sendButtonPress(pressInterval >= LONG_PRESS_INTERVAL);
                            }
                        }
                        break;

                }
                return false;
            }
        });

        _powerImageView = (ImageView) view.findViewById(R.id.mainPowerImageView);
        _locationImageView = (ImageView) view.findViewById(R.id.mainLocationImageView);
        _mobilityImageView = (ImageView) view.findViewById(R.id.mainMobilityImageView);
        _networkImageView = (ImageView) view.findViewById(R.id.mainNetworkImageView);
        _statusTextView = (TextView) view.findViewById(R.id.mainStatusTextView);
        _pressTextView = (TextView) view.findViewById(R.id.mainPressTextView);

        readStatus();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        StatusWriter.subscribe(getActivity().getApplicationContext(), _refreshReceiver);
        SignalSender.subscribe(getActivity().getApplicationContext(), _signalReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();

        StatusWriter.unsubscribe(getActivity().getApplicationContext(), _refreshReceiver);
        SignalSender.unsubscribe(getActivity().getApplicationContext(), _signalReceiver);
    }

    private BroadcastReceiver _refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readStatus();
        }
    };
    private BroadcastReceiver _signalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int signal = intent.getIntExtra(SignalSender.EXTRA_SIGNAL, Signal.NONE);
            int message = 0;

            switch (signal) {
                case Signal.ATTENTION:
                    message = R.string.received_signal_attention;
                    playSound(SIGNAL_ATTENTION_SOUND_ID);
                    break;
                case Signal.CONFIRM:
                    message = R.string.received_signal_confirm;
                    playSound(SIGNAL_CONFIRM_SOUND_ID);
                    break;
                case Signal.WARNING:
                    message = R.string.received_signal_warning;
                    playSound(SIGNAL_WARNING_SOUND_ID);
                    break;
                case Signal.EMERGENCY:
                    message = R.string.received_signal_emergency;
                    playEmergencySound(SIGNAL_EMERGENCY_SOUND_ID);
                    break;
            }

            if (message > 0) {
                Toast toast = Toast.makeText(
                    getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT
                );
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        }
    };

    private void playSound(int soundId) {
        if (_soundPool != null && _soundMap != null) {
            _soundPool.play(_soundMap.get(soundId), 1, 1, 1, 0, 1.f);
        }
    }

    private void playEmergencySound(int soundId) {
        if (_soundPool != null && _soundMap != null) {
            _emergencySoundId = _soundPool.play(_soundMap.get(SIGNAL_EMERGENCY_SOUND_ID), 1, 1, 1, 100, 1.f);
        }

        int color = ContextCompat.getColor(getActivity(), R.color.backgroundEmergency);
        _mainView.setBackgroundColor(color);
        _pressTextView.setText(R.string.hint_emergency);
    }

    private void stopEmergencySound() {
        if (_soundPool != null && _emergencySoundId != 0) {
            _soundPool.stop(_emergencySoundId);
            _emergencySoundId = 0;
        }

        int color = ContextCompat.getColor(getActivity(), android.R.color.background_light);
        _mainView.setBackgroundColor(color);
        _pressTextView.setText(R.string.hint_press);
    }

    private void toggleRunning() {
        final Context context = getActivity().getApplicationContext();

        if (_service != null) {
            boolean running = StatusPreferences.getRunning(context);

            if (running)
                _service.stopRunning();
            else {

                final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                // check is gps enabled
                try {
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.geolocation_disabled_title)
                                .setMessage(R.string.geolocation_disabled_text)
                                .setPositiveButton(R.string.geolocation_disabled_button, new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                                .create()
                                .show();
                        return;
                    }
                } catch (NullPointerException e) {
                    Log.e("MainActivityFragment", "Can't get gps provider ", e);
                }

                // bluetooth turned off but use beacons = true
                BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

                try {
                    final BluetoothAdapter ba = bm.getAdapter();

                    if (SettingsPreferences.isUseBeacons(getActivity()) && ba.getState() == BluetoothAdapter.STATE_OFF) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.bluetooth_disabled_title)
                                .setMessage(R.string.bluetooth_disabled_text)
                                .setPositiveButton(R.string.bluetooth_disabled_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ba.enable();
                                        try {
                                            Log.d(TAG, "start wait");
                                            Thread.sleep(300);
                                            Log.d(TAG, "end wait");
                                        } catch (InterruptedException e) {
                                            Log.e(TAG, "Failed to wait after BlueTooth enabled", e);
                                        }
                                        toggleRunning();
                                    }
                                })
                                .create()
                                .show();
                        return;
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "Failed to get BlueTooth adapter", e);
                }

                if (!SettingsPreferences.getTrackerUdi(context).matches("\\+\\d{10,12}")) { //tracker udi doesn't match phone regex

                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.tracker_id_missing_title)
                            .setMessage(R.string.tracker_id_missing_text)
                            .setPositiveButton(R.string.tracker_id_missing_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(SettingsActivity.newIntent(context));
                                }
                            })
                            .create()
                            .show();
                } else
                    _service.startRunning();
            }
        }
    }

    private void sendButtonPress(boolean longPress) {
        // Check if tracker already running
        boolean running = StatusPreferences.getRunning(getActivity().getApplicationContext());
        if (!running) {
            Toast toast = Toast.makeText(
                    getActivity().getApplicationContext(),
                    longPress ? R.string.sent_long_press : R.string.turn_on_tracker, Toast.LENGTH_SHORT
            );
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return;
        }

        // Sent button press
        if (_service != null) {
            // Call the service
            _service.pressButton(longPress);

            // Play sound
            playSound(longPress ? PRESS_LONG_SOUND_ID : PRESS_SHORT_SOUND_ID);

            // Show toast
            Toast toast = Toast.makeText(
                getActivity().getApplicationContext(),
                longPress ? R.string.sent_long_press : R.string.sent_short_press, Toast.LENGTH_SHORT
            );
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    private void readStatus() {
        try {
            Context context = getActivity().getApplicationContext();

            // Set default values
            _powerImageView.setImageDrawable(_greyIndicatorDrawable);
            _powerImageView.setAnimation(null);
            _locationImageView.setImageDrawable(_greyIndicatorDrawable);
            _locationImageView.setAnimation(null);
            _mobilityImageView.setImageDrawable(_greyIndicatorDrawable);
            _mobilityImageView.setAnimation(null);
            _networkImageView.setImageDrawable(_greyIndicatorDrawable);
            _networkImageView.setAnimation(null);

            int status = StatusPreferences.getStatus(context);
            _statusTextView.setText(status);

            // Exit if tracker is turned off
            if (status == Status.DISABLED) return;

            // Update power light
            boolean running = StatusPreferences.getRunning(context);
            if (running) {
                _powerImageView.setImageDrawable(_greenIndicatorDrawable);
                _powerImageView.startAnimation(_indicatorAnimation);
            } else {
                _statusTextView.setText(Status.DISABLED);
                return;
            }

            // Update location light
            boolean location = StatusPreferences.getLocation(context);
            if (location) {
                _locationImageView.setImageDrawable(_greenIndicatorDrawable);
                _locationImageView.startAnimation(_indicatorAnimation);
            }

            // Update mobility light
            boolean mobility = StatusPreferences.getMobility(context);
            if (mobility) {
                _mobilityImageView.setImageDrawable(_greenIndicatorDrawable);
                _mobilityImageView.startAnimation(_indicatorAnimation);
            }

            // Update network light
            boolean network = StatusPreferences.getNetwork(context);
            if (network) {
                _networkImageView.setImageDrawable(_greenIndicatorDrawable);
                _networkImageView.startAnimation(_indicatorAnimation);
            }

            // Set connected status
            if (status == Status.CONNECTED_GPS) {
                String organization = StatusPreferences.getOrganization(context);
                String organizationText = organization != null
                    ? getResources().getString(R.string.status_connected_gps, organization)
                    : getResources().getString(R.string.status_connected_beacon);
                _statusTextView.setText(organizationText);
            }

            // Set error status
            if (status == Status.ERROR) {
                String error = StatusPreferences.getError(context);
                _powerImageView.setImageDrawable(_redIndicatorDrawable);
                _powerImageView.startAnimation(_indicatorAnimation);
                _statusTextView.setText(error);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*private void buildAlertMessageNoGps() {
        final
        final AlertDialog alert = builder.create();
        alert.show();
    }*/
}
