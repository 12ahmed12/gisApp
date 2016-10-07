package com.android.gis.huapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by spider on 10/06/2016.
 */
public class GCMInstanceIDListener extends InstanceIDListenerService {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    @Override
    public void onTokenRefresh() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = preferences.edit();
        prefEditor.putBoolean("token_sent", false).apply();
        startService(new Intent(this, RegistrationService.class));

    }
}
