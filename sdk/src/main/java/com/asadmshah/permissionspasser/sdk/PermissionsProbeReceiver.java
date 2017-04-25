package com.asadmshah.permissionspasser.sdk;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PermissionsProbeReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.asadmshah.permissionspasser.sdk.PERMISSIONS_PROBE";

    private static final String KEY_RESPONSE_ACTION = "response_action";
    private static final String KEY_PERMISSIONS = "permissions";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("PermissionsProbeReq", "onReceive");

        String[] permissions = intent.getStringArrayExtra(KEY_PERMISSIONS);
        if (permissions == null || permissions.length == 0) {
            // TODO: Error.
            return;
        }

        boolean hasPermissions = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                hasPermissions = false;
                break;
            }
        }

        if (hasPermissions) {
            String action = intent.getStringExtra(KEY_RESPONSE_ACTION);
            Intent resIntent = new Intent(action);
            resIntent.putExtra("service", new ComponentName(context, PermissionsService.class));
            Log.d("PermissionsProbeReq", "Sending Broadcast");
            context.sendBroadcast(resIntent);
        }

       /*
        String packageName = context.getPackageName();
        Log.d("PERMISSIONS_PASSER", packageName);

        String packageCodePath = context.getPackageCodePath();
        Log.d("PERMISSIONS_PASSER", packageCodePath);

        String classSimpleName = PermissionsProbeReceiver.class.getSimpleName();
        Log.d("PERMISSIONS_PASSER", classSimpleName);

        String classCanonicalName = PermissionsProbeReceiver.class.getCanonicalName();
        Log.d("PERMISSIONS_PASSER", classCanonicalName);

        04-25 10:36:30.719 26479-26479/com.asadmshah.permissionspasser.sample.receiver D/PERMISSIONS_PASSER: com.asadmshah.permissionspasser.sample.receiver
        04-25 10:36:30.719 26479-26479/com.asadmshah.permissionspasser.sample.receiver D/PERMISSIONS_PASSER: /data/app/com.asadmshah.permissionspasser.sample.receiver-2/base.apk
        04-25 10:36:30.720 26479-26479/com.asadmshah.permissionspasser.sample.receiver D/PERMISSIONS_PASSER: PermissionsProbeReceiver
        04-25 10:36:30.720 26479-26479/com.asadmshah.permissionspasser.sample.receiver D/PERMISSIONS_PASSER: com.asadmshah.permissionspasser.sdk.PermissionsProbeReceiver
        */
    }

    public static Intent createIntent(String responseAction, String[] permissions) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY_RESPONSE_ACTION, responseAction);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        return intent;
    }

}
