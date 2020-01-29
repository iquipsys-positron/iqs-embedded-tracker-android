package com.iquipsys.tracker.phone.organizations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.iquipsys.tracker.phone.R;

public class OrganizationsDetailsActivity extends AppCompatActivity
        implements OrganizationsDetailsFragment.OnOrganizationsDetailsFragmentListener {

    public static final String EXTRA_ORGANIZATION_URI = "organizationUri";
    private OrganizationsDetailsFragment _fragment;


    public static Intent newIntent(Context packageContext, Uri organizationUri) {
        Intent intent = new Intent(packageContext, OrganizationsDetailsActivity.class);
        intent.putExtra(EXTRA_ORGANIZATION_URI, organizationUri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizations_details);

        Intent intent = getIntent();
        Uri organizationUri = (Uri) intent.getParcelableExtra(EXTRA_ORGANIZATION_URI);

        _fragment = (OrganizationsDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.organizationsDetailsFragment);
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_ORGANIZATION_URI, organizationUri);
        _fragment.setArguments(arguments);
    }

    @Override
    public void onOrganizationDeleted(Uri organizationUri) {
        onBackPressed();
    }
}
