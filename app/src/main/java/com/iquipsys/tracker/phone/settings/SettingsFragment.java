package com.iquipsys.tracker.phone.settings;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.iquipsys.tracker.phone.R;


public class SettingsFragment extends Fragment {
    private boolean _reading = false;

    private TextInputLayout _trackerUdiTextInputLayout;
    private TextInputLayout _serverUrlTextInputLayout;
    private TextInputLayout _mqttBrokerUrlTextInputLayout;
    private Switch _useMqttBrokerSwitch;
    private Switch _useBeaconsSwitch;
    private Switch _useAltPositioningSwitch;
    private Switch _useOrganizationParamsSwitch;
    private TextView _intervalTextView;
    private TextInputLayout _activeIntervalTextInputLayout;
    private TextInputLayout _inactiveIntervalTextInputLayout;
    private TextInputLayout _offsiteIntervalTextInputLayout;
    private Button _restoreDefaultsButton;
    private ImageView _enablePhoneEditTextButton;
    private ImageView _enableServerEditTextButton;
    private ImageView _enableBrockerEditTextButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        _trackerUdiTextInputLayout = (TextInputLayout) view.findViewById(R.id.settingsUdiTextInputLayout);

        _serverUrlTextInputLayout = (TextInputLayout) view.findViewById(R.id.settingsServerTextInputLayout);

        _mqttBrokerUrlTextInputLayout = (TextInputLayout) view.findViewById(R.id.settingsBrokerTextInputLayout);

        _useMqttBrokerSwitch = (Switch) view.findViewById(R.id.settingsUseMqttSwitch);

        _useBeaconsSwitch = (Switch) view.findViewById(R.id.settingsUseBeaconsSwitch);
        _useAltPositioningSwitch = (Switch) view.findViewById(R.id.settingsUseAltPositioningSwitch);

        _useOrganizationParamsSwitch = (Switch) view.findViewById(R.id.settingsOrganizationParamsSwitch);
        _useOrganizationParamsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!_reading) {
                    toggleSettings();
                    //writeSettings();
                }
            }
        });

        _intervalTextView = (TextView) view.findViewById(R.id.intervalTextView);

        _activeIntervalTextInputLayout = (TextInputLayout) view.findViewById(R.id.settingsActiveIntervalTextInputLayout);

        _inactiveIntervalTextInputLayout = (TextInputLayout) view.findViewById(R.id.settingsInactiveIntervalTextInputLayout);

        _offsiteIntervalTextInputLayout = (TextInputLayout) view.findViewById(R.id.settingsOfforganizationIntervalTextInputLayout);

        _restoreDefaultsButton = (Button) view.findViewById(R.id.settingsRestoreButton);
        _restoreDefaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreDefaults();
            }
        });

        _enablePhoneEditTextButton = (ImageView) view.findViewById(R.id.enablePhoneEditTextButton);
        _enableServerEditTextButton = (ImageView) view.findViewById(R.id.enableServerEditTextButton);
        _enableBrockerEditTextButton = (ImageView) view.findViewById(R.id.enableBrockerEditTextButton);

        toggleSettings();
        readSettings();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!_reading) {
            writeSettings();
        }
    }

    private String intToStr(int value) {
        return Integer.toString(value);
    }

    private int strToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private void readSettings() {
        Context context = getActivity();

        _reading = true;
        try {
            _trackerUdiTextInputLayout.getEditText().setText(SettingsPreferences.getTrackerUdi(context));

            int position = _trackerUdiTextInputLayout.getEditText().length();
            _trackerUdiTextInputLayout.getEditText().setSelection(position);

            // phone number
            if (_trackerUdiTextInputLayout.getEditText().getText().toString().matches("\\+\\d{10,12}") ) {
                final TextInputLayout.LayoutParams params = new TextInputLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.2f //show button
                );
                _trackerUdiTextInputLayout.setLayoutParams(params);
                _trackerUdiTextInputLayout.getEditText().setEnabled(false);
                _enablePhoneEditTextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _trackerUdiTextInputLayout.getEditText().setEnabled(true);
                        final TextInputLayout.LayoutParams params = new TextInputLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                0f //hide button
                        );
                        _trackerUdiTextInputLayout.setLayoutParams(params);

                    }
                });
            }

            _serverUrlTextInputLayout.getEditText().setText(SettingsPreferences.getServerUrl(context));

            // server url
            if (_serverUrlTextInputLayout.getEditText().getText().toString().length() > 0) {
                final TextInputLayout.LayoutParams params = new TextInputLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.2f //show button
                );
                _serverUrlTextInputLayout.setLayoutParams(params);
                _serverUrlTextInputLayout.getEditText().setEnabled(false);
                _enableServerEditTextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _serverUrlTextInputLayout.getEditText().setEnabled(true);
                        final TextInputLayout.LayoutParams params = new TextInputLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                0f //hide button
                        );
                        _serverUrlTextInputLayout.setLayoutParams(params);

                    }
                });
            }

            _mqttBrokerUrlTextInputLayout.getEditText().setText(SettingsPreferences.getMqttBrokerUrl(context));

            // mqtt brocker
            if (_mqttBrokerUrlTextInputLayout.getEditText().getText().toString().length() > 0) {
                final TextInputLayout.LayoutParams params = new TextInputLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.2f //show button
                );
                _mqttBrokerUrlTextInputLayout.setLayoutParams(params);
                _mqttBrokerUrlTextInputLayout.getEditText().setEnabled(false);
                _enableBrockerEditTextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _mqttBrokerUrlTextInputLayout.getEditText().setEnabled(true);
                        final TextInputLayout.LayoutParams params = new TextInputLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                0f //hide button
                        );
                        _mqttBrokerUrlTextInputLayout.setLayoutParams(params);

                    }
                });
            }

            _useMqttBrokerSwitch.setChecked(SettingsPreferences.isUseMqttBroker(context));

            _useBeaconsSwitch.setChecked(SettingsPreferences.isUseBeacons(context));
            _useAltPositioningSwitch.setChecked(SettingsPreferences.isUseAltPositioning(context));

            _useOrganizationParamsSwitch.setChecked(SettingsPreferences.isUseOrganizationParams(context));

            _activeIntervalTextInputLayout.getEditText().setText(intToStr(SettingsPreferences.getActiveInterval(context)));
            _inactiveIntervalTextInputLayout.getEditText().setText(intToStr(SettingsPreferences.getInactiveInterval(context)));
            _offsiteIntervalTextInputLayout.getEditText().setText(intToStr(SettingsPreferences.getOfforganizationInterval(context)));
        } finally {
            _reading = false;
        }

        toggleSettings();
    }

    private void writeSettings() {
        Context context = getActivity();

        SettingsPreferences.setTrackerUdi(context, _trackerUdiTextInputLayout.getEditText().getText().toString());
        SettingsPreferences.setServerUrl(context, _serverUrlTextInputLayout.getEditText().getText().toString());
        SettingsPreferences.setMqttBrokerUrl(context, _mqttBrokerUrlTextInputLayout.getEditText().getText().toString());
        SettingsPreferences.setUseMqttBroker(context, _useMqttBrokerSwitch.isChecked());

        SettingsPreferences.setUseBeacons(context, _useBeaconsSwitch.isChecked());
        SettingsPreferences.setUseAltPositioning(context, _useAltPositioningSwitch.isChecked());

        SettingsPreferences.setUseOrganizationParams(context, _useOrganizationParamsSwitch.isChecked());

        SettingsPreferences.setActiveInterval(context, strToInt(_activeIntervalTextInputLayout.getEditText().getText().toString()));
        SettingsPreferences.setInactiveInterval(context, strToInt(_inactiveIntervalTextInputLayout.getEditText().getText().toString()));
        SettingsPreferences.setOfforganizationInterval(context, strToInt(_offsiteIntervalTextInputLayout.getEditText().getText().toString()));
    }

    private void toggleSettings() {
        int visibility = _useOrganizationParamsSwitch.isChecked() ? View.GONE : View.VISIBLE;
        _intervalTextView.setVisibility(visibility);
        _activeIntervalTextInputLayout.setVisibility(visibility);
        _inactiveIntervalTextInputLayout.setVisibility(visibility);
        _offsiteIntervalTextInputLayout.setVisibility(visibility);
    }

    private void restoreDefaults() {

        Context context = getActivity();
        SettingsPreferences.restoreDefaults(context);
        readSettings();
    }
}
