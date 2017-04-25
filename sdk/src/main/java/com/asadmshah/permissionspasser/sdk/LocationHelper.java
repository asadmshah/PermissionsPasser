package com.asadmshah.permissionspasser.sdk;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {

    private final GoogleApiClient client;

    public LocationHelper(GoogleApiClient client) {
        this.client = client;
    }

    @SuppressWarnings({"MissingPermission"})
    public Location getLastLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(client);
    }

    @SuppressWarnings({"MissingPermission"})
    public void requestLocationUpdates(LocationRequest request, LocationListener listener) {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, listener);
    }
}
