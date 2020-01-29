package com.iquipsys.tracker.phone.organizations;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.iquipsys.tracker.phone.data.DatabaseDescription.Organization;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class OrganizationsDetailsFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ORGANIZATIONS_LOADER = 0;
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat GEO_FORMATTER = new DecimalFormat("###.#######");
    private static final DecimalFormat INTEGER_FORMATTER = new DecimalFormat("###");
    private static final DecimalFormat FLOAT_FORMATTER = new DecimalFormat("###.##");


    private GoogleMap _map;
    private Uri _organizationUri;
    private float _latitude = 0;
    private float _longitude = 0;

    private TextView _organizationTextView;
    private TextView _latitudeTextView;
    private TextView _longitudeTextView;
    private TextView _radiusTextView;
    private TextView _activeIntervalTextView;
    private TextView _inactiveIntervalTextView;
    private TextView _offsiteIntervalTextView;

    public interface OnOrganizationsDetailsFragmentListener {
        void onOrganizationDeleted(Uri organizationUri);
    }

    private OnOrganizationsDetailsFragmentListener _listener;

    public OrganizationsDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizations_details, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            _organizationUri = arguments.getParcelable(OrganizationsDetailsActivity.EXTRA_ORGANIZATION_URI);
            getLoaderManager().initLoader(ORGANIZATIONS_LOADER, null, this);
        }

        _organizationTextView = (TextView) view.findViewById(R.id.detailsOrganizationTextView);
        _latitudeTextView = (TextView) view.findViewById(R.id.detailsLatTextView);
        _longitudeTextView = (TextView) view.findViewById(R.id.detailsLngTextView);
        _radiusTextView = (TextView) view.findViewById(R.id.detailsRadiusTextView);
        _activeIntervalTextView = (TextView) view.findViewById(R.id.detailsActiveIntervalTextView);
        _inactiveIntervalTextView = (TextView) view.findViewById(R.id.detailsInactiveIntervalTextView);
        _offsiteIntervalTextView = (TextView) view.findViewById(R.id.detailsOfforganizationIntervalTextView);

        return view;
    }

    @Override
    public void setArguments(Bundle arguments) {
        super.setArguments(arguments);

        if (arguments != null) {
            _organizationUri = arguments.getParcelable(OrganizationsDetailsActivity.EXTRA_ORGANIZATION_URI);
            getLoaderManager().initLoader(ORGANIZATIONS_LOADER, null, this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrganizationsDetailsFragmentListener) {
            _listener = (OnOrganizationsDetailsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOrganizationsDetailsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Obtain map reference
        if (_map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.detailsMap);
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

                            _map.clear();

                            if (_latitude != 0 && _longitude != 0) {
                                LatLng tracker = new LatLng(_latitude, _longitude);
                                _map.addMarker(new MarkerOptions().position(tracker).title(""));
                                _map.moveCamera(CameraUpdateFactory.newLatLngZoom(tracker, 5.0f));
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_organizations_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.delete:
                deleteOrganization();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteOrganization() {
        RemoveOrganizationConfirmationDialog confirmDialog = new RemoveOrganizationConfirmationDialog();
        confirmDialog.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().getContentResolver().delete(_organizationUri, null, null);
                if (_listener != null)
                    _listener.onOrganizationDeleted(_organizationUri);
            }
        });
        confirmDialog.show(getFragmentManager(), "confirm delete");

//        getActivity().getContentResolver().delete(_organizationUri, null, null);
//        if (_listener != null)
//            _listener.onOrganizationDeleted(_organizationUri);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ORGANIZATIONS_LOADER:
                return new CursorLoader(getActivity(), _organizationUri, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int nameIndex = data.getColumnIndex(Organization.COLUMN_NAME);
            int latitudeIndex = data.getColumnIndex(Organization.COLUMN_CENTER_LAT);
            int longitudeIndex = data.getColumnIndex(Organization.COLUMN_CENTER_LNG);
            int radiusIndex = data.getColumnIndex(Organization.COLUMN_RADIUS);
            int activeIntIndex = data.getColumnIndex(Organization.COLUMN_ACTIVE_INT);
            int inactiveIntIndex = data.getColumnIndex(Organization.COLUMN_INACTIVE_INT);
            int offsiteIntIndex = data.getColumnIndex(Organization.COLUMN_OFFSITE_INT);

            _organizationTextView.setText(data.getString(nameIndex));
            _latitude = data.getFloat(latitudeIndex);
            _latitudeTextView.setText(GEO_FORMATTER.format(_latitude));
            _longitude = data.getFloat(longitudeIndex);
            _longitudeTextView.setText(GEO_FORMATTER.format(_longitude));
            _radiusTextView.setText(FLOAT_FORMATTER.format(data.getFloat(radiusIndex)));
            _activeIntervalTextView.setText(INTEGER_FORMATTER.format(data.getInt(activeIntIndex)));
            _inactiveIntervalTextView.setText(INTEGER_FORMATTER.format(data.getInt(inactiveIntIndex)));
            _offsiteIntervalTextView.setText(INTEGER_FORMATTER.format(data.getInt(offsiteIntIndex)));

            // Update map position
            if (_map != null) {
                _map.clear();

                if (_latitude != 0 && _longitude != 0) {
                    LatLng tracker = new LatLng(_latitude, _longitude);
                    _map.addMarker(new MarkerOptions().position(tracker).title(""));
                    _map.moveCamera(CameraUpdateFactory.newLatLngZoom(tracker, 5.0f));
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
