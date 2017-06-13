package com.asadmshah.permissionspasser.sdk;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class PermissionsService extends Service {

    private static final String LOG_TAG = PermissionsService.class.getSimpleName();

    @Nullable
    private GoogleClientHelper googleClientHelper = null;

    private final IPermissionsPasserService.Stub binder = new IPermissionsPasserService.Stub() {
        @Override
        public boolean hasPermission(String permission) throws RemoteException {
            return ContextCompat.checkSelfPermission(PermissionsService.this, permission) == PackageManager.PERMISSION_GRANTED;
        }

        @Override
        public void requestLastLocation(final ILocationCallback callback) throws RemoteException {
            googleClientHelper = new GoogleClientHelper(PermissionsService.this);
            googleClientHelper.connect(new GoogleClientHelper.Callback() {
                @Override
                public void onConnected(GoogleApiClient client) {
                    LocationHelper locationHelper = new LocationHelper(client);
                    Location location = locationHelper.getLastLocation();
                    if (location != null) {
                        try {
                            callback.onLocationResult(ParcelableUtil.encode(location));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "Unable to get location");
                    }
                    googleClientHelper.disconnect();
                }

                @Override
                public void onConnectionSuspended(GoogleApiClient client, int errorCode) {
                    Log.d(LOG_TAG, "onConnectionSuspended " + errorCode);
                    googleClientHelper.disconnect();
                }

                @Override
                public void onConnectionFailed(GoogleApiClient client, ConnectionResult connectionResult) {
                    Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
                    googleClientHelper.disconnect();
                }
            });
        }

        @Override
        public void requestLocationUpdates(final byte[] locationRequest, final ILocationCallback callback) throws RemoteException {
            googleClientHelper = new GoogleClientHelper(PermissionsService.this);
            googleClientHelper.connect(new GoogleClientHelper.Callback() {
                @Override
                public void onConnected(GoogleApiClient client) {
                    Log.d(LOG_TAG, "requestLocationUpdates#onConnected");
                    LocationRequest request = ParcelableUtil.decode(locationRequest, LocationRequest.CREATOR);
                    LocationHelper locationHelper = new LocationHelper(client);
                    locationHelper.requestLocationUpdates(request, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            try {
                                callback.onLocationResult(ParcelableUtil.encode(location));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onConnectionSuspended(GoogleApiClient client, int errorCode) {
                    Log.d(LOG_TAG, "requestLocationUpdates#onConnectionSuspended " + errorCode);
                    googleClientHelper.disconnect();
                }

                @Override
                public void onConnectionFailed(GoogleApiClient client, ConnectionResult connectionResult) {
                    Log.d(LOG_TAG, "requestLocationUpdates#onConnectionFailed " + connectionResult.getErrorMessage());
                    googleClientHelper.disconnect();
                }
            });
        }

        @Override
        public void requestSMSReader(String[] projection, String selection, String[] selectionArgs, String sortOrder, IRequestSMSReaderCallback callback) throws RemoteException {
            Uri uri = Uri.parse("content://sms/inbox");

            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
                if (cursor != null && cursor.moveToFirst()) {
                    while (cursor.moveToNext()) {
                        String[] values = new String[projection.length];
                        for (int i = 0; i < projection.length; i++) {
                            values[i] = cursor.getString(i);
                        }
                        callback.onItem(values);
                    }
                } else {
                    callback.onFinished();
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PermissionsService", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (googleClientHelper != null) {
            googleClientHelper.disconnect();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
//        return null;
    }

}
