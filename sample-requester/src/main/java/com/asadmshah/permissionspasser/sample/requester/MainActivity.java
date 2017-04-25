package com.asadmshah.permissionspasser.sample.requester;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.asadmshah.permissionspasser.sdk.IRequestSMSReaderCallback;
import com.asadmshah.permissionspasser.sdk.PermissionsPasserRemoteServiceConnection;
import com.asadmshah.permissionspasser.sdk.PermissionsProbeReceiver;
import com.google.android.gms.location.LocationRequest;

import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private PermissionsPasserRemoteServiceConnection serviceConnection = new PermissionsPasserRemoteServiceConnection();

    private final PermissionsPasserRemoteServiceConnection.StatusListener statusListener = new PermissionsPasserRemoteServiceConnection.StatusListener() {
        @Override
        public void onConnected(PermissionsPasserRemoteServiceConnection serviceConnection) {
            Log.d(LOG_TAG, "onConnected");

            boolean hasPermission;
            try {
                hasPermission = serviceConnection.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                Log.d(LOG_TAG, "Has Location Permission: " + hasPermission);

                LocationRequest locationRequest = new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setFastestInterval(1000)
                        .setInterval(1000)
                        .setNumUpdates(1);

                serviceConnection.requestLocationUpdates(locationRequest, new PermissionsPasserRemoteServiceConnection.LocationCallback() {
                    @Override
                    public void onLocation(Location location) {
                        Log.d(LOG_TAG, "Found Location: " + location);
                    }
                });

                String[] projection = new String[]{"address", "person", "date", "body"};
                String selection = null;
                String[] selectionArgs = null;
                String order = "date DESC LIMIT 10";
                serviceConnection.requestSMSReader(projection, selection, selectionArgs, order, new IRequestSMSReaderCallback.Stub() {
                    @Override
                    public void onItem(String[] message) throws RemoteException {
                        Log.d(LOG_TAG, "Received SMS: " + Arrays.toString(message));
                    }

                    @Override
                    public void onFinished() throws RemoteException {
                        Log.d(LOG_TAG, "Finished Reading SMS");
                    }
                });
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Unable to check permission", e);
            }
        }

        @Override
        public void onDisconnected(PermissionsPasserRemoteServiceConnection serviceConnection) {
            Log.d(LOG_TAG, "onDisconnected");
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(MainActivity.class.getSimpleName(), "onReceive");

            ComponentName componentName = intent.getParcelableExtra("service");

            Intent newIntent = new Intent();
            newIntent.setComponent(componentName);
            bindService(newIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceConnection.setStatusListener(statusListener);

        String randomString = UUID.randomUUID().toString();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(randomString);
        registerReceiver(receiver, intentFilter);
        Log.d("MainActivity", "Registered Receiver");

        Intent intent = PermissionsProbeReceiver.createIntent(randomString, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS});
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);

        serviceConnection.setStatusListener(null);

        super.onDestroy();
    }
}
