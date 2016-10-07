package com.android.gis.huapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    ProfileTracker mProfileTracker;
    String f_id;
    String f_name;
    String f_email;
    String f_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                // Fetch user details from New Profile

                if (newProfile!=null) {
                    Log.e("NFName", "--" + newProfile.getFirstName());
                    Log.e("NLName", "--" + newProfile.getLastName());
                    Log.e("Nid", "--" + newProfile.getId());

                }





            }
        };

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.e("onSuccess", "--------" + loginResult.getAccessToken());
                Log.e("Token", "--------" + loginResult.getAccessToken().getToken());
                Log.e("Permision", "--------" + loginResult.getRecentlyGrantedPermissions());
                f_token=loginResult.getAccessToken().getToken();
                Log.e("OnGraph", "------------------------");
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    final JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                try {
                                    f_token = loginResult.getAccessToken().getToken();
                                    f_name = object.getString("name");
                                    f_id = object.getString("id");
                                    f_email = object.getString("email");
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("not found ","mail  is not founded");
                                }
                                Log.d("facebook name", f_name);
                                Log.d("facebook id", f_id);
                                Log.d("facebook mail", f_email);
                                Log.d("facebook token", f_token);



                                Log.e("GraphResponse", "-------------" + response.toString());

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException error) {
                info.setText("Login attempt failed.");
            }
        });







    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
}
}
