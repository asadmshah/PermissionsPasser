package com.asadmshah.permissionspasser.sdk;

import com.asadmshah.permissionspasser.sdk.ILocationCallback;
import com.asadmshah.permissionspasser.sdk.IRequestSMSReaderCallback;

interface IPermissionsPasserService {
    boolean hasPermission(String permission);

    oneway void requestLastLocation(ILocationCallback callback);
    oneway void requestLocationUpdates(in byte[] locationRequest, ILocationCallback callback);

    oneway void requestSMSReader(in String[] projection, in String selection, in String[] selectionArgs, in String sortOrder, IRequestSMSReaderCallback callback);
}
