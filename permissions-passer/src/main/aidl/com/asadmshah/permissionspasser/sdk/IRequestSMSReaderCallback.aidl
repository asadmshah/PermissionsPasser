package com.asadmshah.permissionspasser.sdk;

interface IRequestSMSReaderCallback {
    oneway void onItem(in String[] message);
    oneway void onFinished();
}
