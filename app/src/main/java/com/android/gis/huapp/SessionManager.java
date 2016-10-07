package com.android.gis.huapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserSession";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User id (make variable public to access from outside)
    public static final String KEY_ID = "id";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // User username (make variable public to access from outside)
    public static final String KEY_USERNAME= "username";
    // User password (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";
    // User registerdate (make variable public to access from outside)
    public static final String KEY_REGSITERAT = "register_at";
    // User lastlogin (make variable public to access from outside)
    public static final String KEY_LASTLOGIN = "last_login";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id,String name, String username,String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing id in pref
        editor.putString(KEY_ID, id);
        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing name in pref
        editor.putString(KEY_USERNAME, username);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, com.android.gis.huapp.Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
            return false;
        }else
        {
            return true;
        }

    }



    /**
     * Get stored session data
     * */
    public User getUserDetails(){
        User user=new User();
        // user id
        user.setId(pref.getString(KEY_ID, null));
        //user name
        user.setName(pref.getString(KEY_NAME, null));
        //user username
        user.setUsername(pref.getString(KEY_USERNAME,null));
        // user email id
        user.setMail(pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences

        editor.remove(KEY_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_NAME);
        editor.remove(KEY_EMAIL);
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, com.android.gis.huapp.Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}