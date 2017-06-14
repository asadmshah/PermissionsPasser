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
    }

    public static Intent createIntent(String responseAction, String[] permissions) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY_RESPONSE_ACTION, responseAction);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        return intent;
    }

}
