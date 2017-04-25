package com.asadmshah.permissionspasser.sdk;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationRequest;

public class PermissionsPasserRemoteServiceConnection implements ServiceConnection {

    private static final String TAG = IPermissionsPasserService.class.getSimpleName();

    @Nullable
    private StatusListener statusListener;

    @Nullable
    private IPermissionsPasserService service;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = IPermissionsPasserService.Stub.asInterface(service);

        if (statusListener != null) {
            statusListener.onConnected(this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;

        if (statusListener != null) {
            statusListener.onDisconnected(this);
        }
    }

    public void setStatusListener(@Nullable StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    public boolean isServiceConnected() {
        return service != null;
    }

    public boolean hasPermission(@NonNull String permission) throws RemoteException {
        return service.hasPermission(permission);
    }

    public void requestLastLocation(final LocationCallback callback) throws RemoteException {
        service.requestLastLocation(new ILocationCallback.Stub() {
            @Override
            public void onLocationResult(byte[] encoded) throws RemoteException {
                Location location = ParcelableUtil.decode(encoded, Location.CREATOR);
                callback.onLocation(location);
            }
        });
    }

    public void requestLocationUpdates(final LocationRequest request, final LocationCallback callback) throws RemoteException {
        service.requestLocationUpdates(ParcelableUtil.encode(request), new ILocationCallback.Stub() {
            @Override
            public void onLocationResult(byte[] encoded) throws RemoteException {
                Location location = ParcelableUtil.decode(encoded, Location.CREATOR);
                callback.onLocation(location);
            }
        });
    }

    public void requestSMSReader(String[] projection, String selection, String[] selectionArgs, String sortOrder, IRequestSMSReaderCallback.Stub callback) throws RemoteException {
        service.requestSMSReader(projection, selection, selectionArgs, sortOrder, callback);
    }

    public interface StatusListener {
        void onConnected(PermissionsPasserRemoteServiceConnection serviceConnection);
        void onDisconnected(PermissionsPasserRemoteServiceConnection serviceConnection);
    }

    public interface LocationCallback {
        void onLocation(Location location);
    }
}
