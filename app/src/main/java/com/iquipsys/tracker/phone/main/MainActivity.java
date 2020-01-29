package com.iquipsys.tracker.phone.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;

import com.iquipsys.tracker.phone.R;
import com.iquipsys.tracker.phone.data.DatabaseDescription;
import com.iquipsys.tracker.phone.service.TrackerService;
import com.iquipsys.tracker.phone.settings.SettingsActivity;
import com.iquipsys.tracker.phone.organizations.OrganizationsAddActivity;
import com.iquipsys.tracker.phone.organizations.OrganizationsListActivity;
import com.iquipsys.tracker.phone.status.StatusActivity;
import com.iquipsys.tracker.phone.about.AboutActivity;
import com.iquipsys.tracker.phone.statistics.StatisticsActivity;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>, WelcomeFragment.OnWelcomeFragmentListener {

    private static final int ORGANIZATIONS_LOADER = 0;
    private static final int SHOW_MAIN_MESSAGE = 0;
    private static final int SHOW_WELCOME_MESSAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_app);

        if (savedInstanceState == null) {
            LoadingFragment loadingFragment = new LoadingFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.mainFrameLayout, loadingFragment);
            transaction.commit();
        }

        checkPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkPermissions() {
//        if (Build.VERSION.SDK_INT >= 23
//            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }

        ActivityCompat.requestPermissions(this, new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        }, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_status:
                Intent intent = StatusActivity.newIntent(this);
                startActivity(intent);
                return true;
            case R.id.action_organizations:
                intent = OrganizationsListActivity.newIntent(this);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = SettingsActivity.newIntent(this);
                startActivity(intent);
                return true;
//            case R.id.action_statistics:
//                intent = StatisticsActivity.newIntent(this);
//                startActivity(intent);
//                return true;
            case R.id.action_about:
                intent = AboutActivity.newIntent(this);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostResume() {
        super.onPostResume();

        // Read organizations and switch to appropriate fragment
        getSupportLoaderManager().initLoader(ORGANIZATIONS_LOADER, null, this);
    }

    @Override
    public void onAddOrganization() {
        Intent intent = OrganizationsAddActivity.newIntent(this);
        startActivity(intent);
    }

    private void showMainFragment() {
        MainActivityFragment mainFragment = new MainActivityFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrameLayout, mainFragment);
        transaction.commitAllowingStateLoss();
    }

    private void showWelcomeFragment() {
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrameLayout, welcomeFragment);
        transaction.commitAllowingStateLoss();
    }

    // Handler to switch fragments on UI thread inside loader
    private Handler showFragmentHandler = new Handler()  { // handler for commiting fragment after data is loaded
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_MAIN_MESSAGE) {
                showMainFragment();
            } else if (msg.what == SHOW_WELCOME_MESSAGE) {
                showWelcomeFragment();
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ORGANIZATIONS_LOADER:
                return new CursorLoader(this,
                    DatabaseDescription.Organization.CONTENT_URI,
                    null, null, null,
                    DatabaseDescription.Organization.COLUMN_NAME + " COLLATE NOCASE ASC"
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            showFragmentHandler.sendEmptyMessage(SHOW_MAIN_MESSAGE);
        } else {
            showFragmentHandler.sendEmptyMessage(SHOW_WELCOME_MESSAGE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //showFragmentHandler.sendEmptyMessage(SHOW_WELCOME_MESSAGE);
    }

}
