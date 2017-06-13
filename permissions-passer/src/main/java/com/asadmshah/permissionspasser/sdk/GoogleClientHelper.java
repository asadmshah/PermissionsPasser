package com.asadmshah.permissionspasser.sdk;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GoogleClientHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final GoogleApiClient client;
    @Nullable
    private Callback callback;

    public GoogleClientHelper(Context context) {
        client = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void connect(Callback callback) {
        this.callback = callback;
        client.connect();
    }

    public void disconnect() {
        callback = null;
        client.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (callback != null) callback.onConnected(client);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (callback != null) callback.onConnectionSuspended(client, i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (callback != null) callback.onConnectionFailed(client, connectionResult);
    }

    public interface Callback {
        void onConnected(GoogleApiClient client);
        void onConnectionSuspended(GoogleApiClient client, int errorCode);
        void onConnectionFailed(GoogleApiClient client, ConnectionResult connectionResult);
    }
}
