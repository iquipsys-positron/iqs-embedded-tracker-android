package com.iquipsys.tracker.phone.organizations;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iquipsys.tracker.phone.R;
import com.iquipsys.tracker.phone.rest.RestClient;
import com.iquipsys.tracker.phone.rest.OrganizationV1;
import com.iquipsys.tracker.phone.rest.OrganizationsClientV1;
import com.iquipsys.tracker.phone.settings.SettingsPreferences;

import com.iquipsys.tracker.phone.data.DatabaseDescription.Organization;

public class OrganizationsAddFragment extends Fragment {
    public interface OnOrganizationsAddFragmentListener {
        void onOrganizationAdded(Uri organizationUri);
    }

    private OnOrganizationsAddFragmentListener _listener;
    private EditText _organizationCodeEditText;
    private TextView _errorTextView;
    private ProgressBar _progressBar;

    public OrganizationsAddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizations_add, container, false);

        _errorTextView = (TextView) view.findViewById(R.id.addOrganizationsErrorTextView);
        _progressBar = (ProgressBar) view.findViewById(R.id.addOrganizationsProgressBar);

        TextInputLayout organizationCodeTextInputLayout = (TextInputLayout) view.findViewById(R.id.addOrganizationCodeTextInputLayout);
        _organizationCodeEditText = organizationCodeTextInputLayout.getEditText();

        Button addOrganizationButton = (Button) view.findViewById(R.id.addOrganizationButton);
        addOrganizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String organizationCode = _organizationCodeEditText.getText().toString().trim();
                if (!organizationCode.isEmpty())
                    addOrganization(organizationCode);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrganizationsAddFragmentListener) {
            _listener = (OnOrganizationsAddFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOrganizationsAddFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    private void addOrganization(String organizationCode) {
        _organizationCodeEditText.setEnabled(false);
        _errorTextView.setVisibility(View.GONE);

        _progressBar.setIndeterminate(true);
        _progressBar.setVisibility(View.VISIBLE);

        RetrieveAndStoreOrganizationTask task = new RetrieveAndStoreOrganizationTask();
        task.execute(organizationCode);
    }

    private void saveOrganization(OrganizationV1 organization) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Organization.COLUMN_ORG_ID, organization.getId());
        contentValues.put(Organization.COLUMN_NAME, organization.getName());
        contentValues.put(Organization.COLUMN_DESCRIPTION, organization.getDescription());
        contentValues.put(Organization.COLUMN_VERSION, organization.getVersion());
        contentValues.put(Organization.COLUMN_CENTER_LAT,
            organization.getCenter() != null && organization.getCenter().getCoordinates() != null
            ? organization.getCenter().getCoordinates()[1] : 0
        );
        contentValues.put(Organization.COLUMN_CENTER_LNG,
                organization.getCenter() != null && organization.getCenter().getCoordinates() != null
                        ? organization.getCenter().getCoordinates()[0] : 0
        );
        contentValues.put(Organization.COLUMN_RADIUS, organization.getRadius() != 0 ? organization.getRadius() : 5);
        contentValues.put(Organization.COLUMN_ACTIVE_INT, organization.getActiveInt() != 0 ? organization.getActiveInt() : 60);
        contentValues.put(Organization.COLUMN_INACTIVE_INT, organization.getInactiveInt() != 0 ? organization.getInactiveInt() : 300);
        contentValues.put(Organization.COLUMN_OFFSITE_INT, organization.getOfforganizationInt() != 0 ? organization.getOfforganizationInt() : 900);
        contentValues.put(Organization.COLUMN_OFFLINE_TIMEOUT, organization.getOfflineTimeout() != 0 ? organization.getOfflineTimeout() : 900);

        Uri newOrganizationUri = getActivity().getContentResolver().insert(
                Organization.CONTENT_URI, contentValues
        );

        if (newOrganizationUri != null) {
            if (_listener != null)
                _listener.onOrganizationAdded(newOrganizationUri);
        } else {
            _errorTextView.setVisibility(View.VISIBLE);
            _errorTextView.setText(R.string.error_organization_save_failure);
        }
    }


    private class RetrieveAndStoreOrganizationTask extends AsyncTask<String, Void, OrganizationV1> {
        private int error = 0;

        @Override
        protected OrganizationV1 doInBackground(String... strings) {
            String organizationCode = strings[0];

            // Check network connectivity
            if (!RestClient.hasNetworkAccess(getActivity())) {
                error = R.string.error_no_network;
                return null;
            }

            try {
                Context context = getActivity().getApplicationContext();
                String serverUrl = SettingsPreferences.getServerUrl(context);
                OrganizationsClientV1 client = new OrganizationsClientV1(serverUrl);
                OrganizationV1 organization = client.findOrganizationByCode(organizationCode);

                if (organization == null) {
                    error = R.string.error_organization_not_found;
                }

                return organization;
            } catch (Exception e) {
                error = R.string.error_unexpected;
            }

            return null;
        }

        @Override
        protected void onPostExecute(OrganizationV1 organization) {
            _organizationCodeEditText.setEnabled(true);
            _progressBar.setVisibility(View.GONE);

            if (error != 0) {
                _errorTextView.setVisibility(View.VISIBLE);
                _errorTextView.setText(error);
            } else {
                saveOrganization(organization);
            }
        }
    }

}
