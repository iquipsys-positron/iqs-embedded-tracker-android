package com.iquipsys.tracker.phone.organizations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.iquipsys.tracker.phone.R;

public class OrganizationsListActivity extends AppCompatActivity implements OrganizationsListFragment.OnOrganizationsListFragmentListener {
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, OrganizationsListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizations_list);
    }

    @Override
    public void onAddOrganization() {
        Intent intent = OrganizationsAddActivity.newIntent(this);
        startActivity(intent);
    }

    @Override
    public void onSelectOrganization(Uri organizationUri) {
        Intent intent = OrganizationsDetailsActivity.newIntent(this, organizationUri);
        startActivity(intent);
    }

}
