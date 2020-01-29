package com.iquipsys.tracker.phone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;

import com.iquipsys.tracker.phone.data.DatabaseDescription;
import com.iquipsys.tracker.phone.data.OrganizationsContentProvider;
import com.iquipsys.tracker.phone.rest.GeoJsonPoint;
import com.iquipsys.tracker.phone.rest.OrganizationV1;

import java.util.ArrayList;
import java.util.List;

public class OrganizationsReader {
    private Object _lock = new Object();
    private List<OrganizationV1> _organizations;

    public OrganizationsReader() {}

    public void subscribe(Context context) {
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(_reloadReceiver, new IntentFilter(OrganizationsContentProvider.MESSAGE_ORGANIZATIONS_RELOAD));
    }

    public void unsubscribe(Context context) {
        LocalBroadcastManager.getInstance(context)
            .unregisterReceiver(_reloadReceiver);
    }

    private BroadcastReceiver _reloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (_lock) {
                _organizations = null;
            }
        }
    };

    private List<OrganizationV1> getAll(Context context) {
        synchronized (_lock) {
            if (_organizations != null) return _organizations;

            Cursor cursor = context.getContentResolver().query(
                    DatabaseDescription.Organization.CONTENT_URI,
                    null, null, null,
                    DatabaseDescription.Organization.COLUMN_NAME + " COLLATE NOCASE ASC"
            );
            if (cursor == null) return null;

            try {
                int orgIdIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_ORG_ID);
                int nameIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_NAME);
                int descriptionIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_DESCRIPTION);
                int versionIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_VERSION);
                int latitudeIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_CENTER_LAT);
                int longitudeIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_CENTER_LNG);
                int radiusIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_RADIUS);
                int activeIntervalIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_ACTIVE_INT);
                int inactiveIntervalIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_INACTIVE_INT);
                int offsiteIntervalIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_OFFSITE_INT);
                int offlineTimeoutIndex = cursor.getColumnIndex(DatabaseDescription.Organization.COLUMN_OFFLINE_TIMEOUT);

                List<OrganizationV1> organizations = new ArrayList<OrganizationV1>();

                while (cursor.moveToNext()) {
                    OrganizationV1 organization = new OrganizationV1();
                    organization.setId(cursor.getString(orgIdIndex));
                    organization.setName(cursor.getString(nameIndex));
                    organization.setDescription(cursor.getString(descriptionIndex));
                    organization.setVersion(cursor.getInt(versionIndex));

                    float latitude = cursor.getFloat(latitudeIndex);
                    float longitude = cursor.getFloat(longitudeIndex);
                    GeoJsonPoint center = new GeoJsonPoint();
                    center.setType("point");
                    center.setCoordinates(new float[]{longitude, latitude});
                    organization.setCenter(center);

                    organization.setRadius(cursor.getFloat(radiusIndex));
                    organization.setActiveInt(cursor.getInt(activeIntervalIndex));
                    organization.setInactiveInt(cursor.getInt(inactiveIntervalIndex));
                    organization.setOfforganizationInt(cursor.getInt(offsiteIntervalIndex));
                    organization.setOfflineTimeout(cursor.getInt(offlineTimeoutIndex));

                    organizations.add(organization);
                }

                _organizations = organizations;
                return organizations;
            } finally {
                cursor.close();
            }
        }
    }

    private float calculateDistance(Location location, OrganizationV1 organization) {
        if (location == null || organization == null) return Float.MAX_VALUE;

        float organizationLongitude = organization.getCenter().getCoordinates()[0];
        float organizationLatitude = organization.getCenter().getCoordinates()[1];
        Location organizationLocation = new Location("");
        organizationLocation.setLatitude(organizationLatitude);
        organizationLocation.setLongitude(organizationLongitude);
        return location.distanceTo(organizationLocation);
    }

    public OrganizationV1 findCurrent(Context context, Location location) {
        List<OrganizationV1> organizations = getAll(context);

        for (OrganizationV1 organization : organizations) {
            float distance = calculateDistance(location, organization);
            float organizationRadius = organization.getRadius() * 1000;

            if (distance <= organizationRadius)
                return organization;
        }

        return null;
    }

    public OrganizationV1 findClosest(Context context, Location location) {
        List<OrganizationV1> organizations = getAll(context);

        OrganizationV1 closestOrganization = null;
        float closestDistance = Float.MAX_VALUE;

        for (OrganizationV1 organization : organizations) {
            float distance = calculateDistance(location, organization);
            if (distance <= closestDistance) {
                closestDistance = distance;
                closestOrganization = organization;
            }
        }

        return closestOrganization;
    }

}