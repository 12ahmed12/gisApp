package com.android.gis.huapp;

        import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationService extends IntentService {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    public RegistrationService() {
        super("RegistrationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = preferences.edit();

        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken("456869641424", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d("Tokeeeen",token);
            if (!preferences.getBoolean("token_set", false))
            {
                prefEditor.putString("token", token);
                prefEditor.putBoolean("token_set",true);
                prefEditor.commit();
                //sendTokenToServer(token);
                String tok=preferences.getString("token",null);
                boolean b=preferences.getBoolean("token_set",false);

                Log.d("tokb",""+b);
                Log.d("tokS",tok);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Registration Service", "Error :get Token Failed !");

        }

    }



}
