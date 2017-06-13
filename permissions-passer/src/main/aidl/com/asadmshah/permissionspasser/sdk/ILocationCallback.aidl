package com.asadmshah.permissionspasser.sdk;

interface ILocationCallback {
    oneway void onLocationResult(in byte[] encoded);
}
