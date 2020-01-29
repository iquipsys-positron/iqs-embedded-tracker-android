package com.iquipsys.tracker.phone.organizations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.iquipsys.tracker.phone.R;

public class OrganizationsAddActivity extends AppCompatActivity implements OrganizationsAddFragment.OnOrganizationsAddFragmentListener {

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, OrganizationsAddActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizations_add);

        // Enable back button on toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.addOrganizationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOrganizationAdded(Uri organizationUri) {
        super.onBackPressed();
    }
}
