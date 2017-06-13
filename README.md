# Permissions Passer

Don't have a specific permission? Don't worry, ask another application to request
something for you.

# How's It Work?

Say you have two applications: Alice and Bob. Alice wants Location data but doesn't have the 
necessary permissions. Bob on the other hand does.

Alice sends out a Broadcast with a list of the permission she requires. The broadcast will be 
received by every application that has the `PermissionsProbeReceiver` listening. Each of those
applications runs through the list of requested permissions, whichever application happens to 
have them all will then send back a broadcast with the specific `Service` to bind to. In our case
Bob.

Alice then binds to a `PermissionsService` registered and exported in Bob's application. Once 
connected, she can send requests for Bob to complete.

# What's Left?

- The code is absolute, horrendous shit. 

- It's not as developer friendly as I'd like it to be.

- I'm sure the test applications leak all over the place.

- Currently only Last Location, Request Location Updates, and Read SMS Inbox functionality. Need to
  add more.

# Usage

## Bob Application (Has Required Permissions)

**AndroidManifest.xml**

```xml
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <service android:name="com.asadmshah.permissionspasser.sdk.PermissionsService"
             android:exported="true"
             />

    <receiver android:name="com.asadmshah.permissionspasser.sdk.PermissionsProbeReceiver">
        <intent-filter>
            <action android:name="com.asadmshah.permissionspasser.sdk.PERMISSIONS_PROBE"/>
        </intent-filter>
    </receiver>
```

## Alice Application (Needs Content without Permission)

**MainActivity.java**

```java
    ...
    String randomString = UUID.randomUUID().toString();

    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(randomString);
    registerReceiver(receiver, intentFilter);

    Intent intent = PermissionsProbeReceiver.createIntent(randomString, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
    sendBroadcast(intent);
    ...
```

**YourBroadcastReceiver.java**: This is the `BroadcastReceiver` where Bob will send the
specific `Service` to bind to.

```java
class YourBroadcastReceiver extends BroadcastReceiver {

    private PermissionsPasserRemoteServiceConnection serviceConnection = new PermissionsPasserRemoteServiceConnection();

    private final PermissionsPasserRemoteServiceConnection.StatusListener statusListener = new PermissionsPasserRemoteServiceConnection.StatusListener() {
        @Override
        public void onConnected(PermissionsPasserRemoteServiceConnection serviceConnection) {
            LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(1000)
                .setInterval(1000)
                .setNumUpdates(1);

            serviceConnection.requestLocationUpdates(locationRequest, new PermissionsPasserRemoteServiceConnection.LocationCallback() {
                @Override
                public void onLocation(Location location) {
                
                }
            });
        }

        @Override
        public void onDisconnected(PermissionsPasserRemoteServiceConnection serviceConnection) {
        
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        serviceConnection.setStatusListener(statusListener);
    
        ComponentName componentName = intent.getParcelableExtra("service");

        Intent newIntent = new Intent();
        newIntent.setComponent(componentName);
        bindService(newIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
```

# License
Copyright (C) 2017 Asad Shah

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.