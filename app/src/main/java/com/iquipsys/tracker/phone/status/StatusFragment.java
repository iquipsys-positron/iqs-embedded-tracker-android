package com.iquipsys.tracker.phone.status;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iquipsys.tracker.phone.R;
import com.iquipsys.tracker.phone.service.StatusWriter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StatusFragment extends Fragment {
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat GEO_FORMATTER = new DecimalFormat("###.#######");
    private static final DecimalFormat INTEGER_FORMATTER = new DecimalFormat("###");

    private GoogleMap _map;
    private TextView _statusLabelTextView;
    private TextView _statusValueTextView;
    private TextView _lastUpdateTextView;
    private TextView _longitudeTextView;
    private TextView _latitudeTextView;
    private TextView _altitudeTextView;
    private TextView _speedTextView;
    private TextView _angleTextView;
    private TextView _mobilityTextView;
    private TextView _beaconsTextView1;
    private TextView _beaconsTextView2;
    private TextView _beaconsTextView3;
    private TextView _beaconsTextView4;

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        _statusLabelTextView = (TextView) view.findViewById(R.id.statusLabelTextView);
        _statusValueTextView = (TextView) view.findViewById(R.id.statusValueTextView);
        _lastUpdateTextView = (TextView) view.findViewById(R.id.statusLastUpdateTextView);
        _longitudeTextView = (TextView) view.findViewById(R.id.statusLongTextView);
        _latitudeTextView = (TextView) view.findViewById(R.id.statusLatTextView);
        _altitudeTextView = (TextView) view.findViewById(R.id.statusAltTextView);
        _speedTextView = (TextView) view.findViewById(R.id.statusSpeedTextView);
        _angleTextView = (TextView) view.findViewById(R.id.statusAngleTextView);
        _mobilityTextView = (TextView) view.findViewById(R.id.statusMobilityTextView);
        _beaconsTextView1 = (TextView) view.findViewById(R.id.statusBeaconsTextView1);
        _beaconsTextView2 = (TextView) view.findViewById(R.id.statusBeaconsTextView2);
        _beaconsTextView3 = (TextView) view.findViewById(R.id.statusBeaconsTextView3);
        _beaconsTextView4 = (TextView) view.findViewById(R.id.statusBeaconsTextView4);

        readStatus();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        StatusWriter.subscribe(getActivity().getApplicationContext(), _refreshReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();

        StatusWriter.unsubscribe(getActivity().getApplicationContext(), _refreshReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Obtain map reference
        if (_map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.statusMap);
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        _map = googleMap;
                        if (_map != null) {
//                            _map.getUiSettings().setZoomControlsEnabled(true);
//                            _map.getUiSettings().setScrollGesturesEnabled(true);
//                            _map.getUiSettings().setCompassEnabled(true);
//                            _map.getUiSettings().setMyLocationButtonEnabled(true);
                            _map.getUiSettings().setAllGesturesEnabled(true);
                        }
                    }
                });
            }
        }
    }

    private BroadcastReceiver _refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readStatus();
        }
    };

    /*private String formatBeacons(List<String> beacons) {
        if (beacons == null || beacons.size() == 0) return "";

        StringBuilder builder = new StringBuilder();
        for (String beacon : beacons) {
            if (builder.length() > 0)
                builder.append(",\n");
            builder.append(beacon);
        }
        Log.d("StatusFragment", "Beacons: " + builder.toString());
        return builder.toString();
    }*/

    private String formatBeacon(String beacon) {
        if (beacon.length() <= 19)
            return beacon;
        return beacon.substring(0, 8) + "..." + beacon.substring(beacon.length() - 8);
    }

    private void readStatus() {
        Context context = getActivity().getApplicationContext();

        int status = StatusPreferences.getStatus(context);
        if (status == Status.ERROR) {
            _statusLabelTextView.setText(R.string.label_error);

            String error = StatusPreferences.getError(context);
            _statusValueTextView.setText(error);
        } else if (status == Status.CONNECTED_GPS) {
            _statusValueTextView.setText(R.string.label_organization);

            String organization = StatusPreferences.getOrganization(context);
            _statusValueTextView.setText(organization);
        } else {
            _statusLabelTextView.setText(R.string.label_organization);
            _statusValueTextView.setText(status);
        }

        long lastUpdateTicks = StatusPreferences.getLastUpdate(context);
        Date lastUpdate = new Date(lastUpdateTicks);
        _lastUpdateTextView.setText(lastUpdateTicks > 0 ? DATE_FORMATTER.format(lastUpdate) : "");

        float longitude = StatusPreferences.getLongitude(context);
        _longitudeTextView.setText(longitude != 0 ? GEO_FORMATTER.format(longitude) : "");

        float latitude = StatusPreferences.getLatitude(context);
        _latitudeTextView.setText(latitude != 0 ? GEO_FORMATTER.format(latitude) : "");

        float altitude = StatusPreferences.getAltitude(context);
        _altitudeTextView.setText(INTEGER_FORMATTER.format(altitude));

        float speed = StatusPreferences.getSpeed(context);
        _speedTextView.setText(INTEGER_FORMATTER.format(speed));

        float angle = StatusPreferences.getAngle(context);
        _angleTextView.setText(INTEGER_FORMATTER.format(angle));

        boolean mobility = StatusPreferences.getMobility(context);
        _mobilityTextView.setText(mobility ? R.string.mobility_mobile : R.string.mobility_freezed);

        List<String> beacons = Arrays.asList(StatusPreferences.getBeacons(context));

        beacons = beacons != null ? beacons : new ArrayList<String>();
        _beaconsTextView1.setText(beacons.size() >= 1 ? formatBeacon(beacons.get(0)) : "");
        _beaconsTextView2.setText(beacons.size() >= 2 ? formatBeacon(beacons.get(1)) : "");
        _beaconsTextView2.setVisibility(beacons.size() >= 2 ? View.VISIBLE : View.GONE);
        _beaconsTextView3.setText(beacons.size() >= 3 ? formatBeacon(beacons.get(2)) : "");
        _beaconsTextView3.setVisibility(beacons.size() >= 3 ? View.VISIBLE : View.GONE);
        _beaconsTextView4.setText(beacons.size() >= 4 ? formatBeacon(beacons.get(3)) : "");
        _beaconsTextView4.setVisibility(beacons.size() >= 4 ? View.VISIBLE : View.GONE);

        // Update map position
        if (_map != null) {
            _map.clear();

            if (latitude != 0 && longitude != 0) {
                LatLng tracker = new LatLng(latitude, longitude);
                _map.addMarker(new MarkerOptions().position(tracker).title(""));
                _map.moveCamera(CameraUpdateFactory.newLatLngZoom(tracker, 12.0f));
            }
        }
    }
}
