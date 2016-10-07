package com.android.gis.huapp;
/**
 * Author Vivz
 * Date 15/06/15
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;


public class Login extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private LinearLayout mRoot;
    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;
    private EditText mInputUsername;
    private EditText mInputPassword;
    private FloatingActionButton mFAB;
    private boolean mUserSawDrawer = false;
    SessionManager session;
    AlertDialogManager alertDialogManager;
    ProgressDialog progressDialog;
    String username, password;
    String Uusername, Upassword;

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    String f_id;
    String f_name;
    String f_email;
    String f_token;
    String Token;
    String tokee;
    private View.OnClickListener mSnackBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener mFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(mRoot, "FAB Clicked", Snackbar.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();
        tokee = FirebaseInstanceId.getInstance().getToken();
        Log.d("tokee", "" + tokee);
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = preferences.edit();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.e("onSuccess", "--------" + loginResult.getAccessToken());
                Log.e("Token", "--------" + loginResult.getAccessToken().getToken());
                Log.e("Permision", "--------" + loginResult.getRecentlyGrantedPermissions());
                f_token = loginResult.getAccessToken().getToken();
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



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("not found ", "mail  is not founded");
                                }
                                Log.d("facebook name", f_name);
                                Log.d("facebook id", f_id);
                                Log.d("facebook mail", f_email);
                                Log.d("facebook token", f_token);
                                Log.d("device token", tokee);
                                if(tokee!=null) {
                                    BackgorundLoginF task = new BackgorundLoginF(getApplicationContext());
                                    task.execute(f_id, f_name, f_email, tokee);
                                }else
                                {
                                    Toast.makeText(getApplicationContext(),"fail to find token",Toast.LENGTH_LONG).show();
                                }

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




      /*  prefEditor.remove("token");
        prefEditor.putBoolean("token_set",false);
        prefEditor.commit();*/

        alertDialogManager = new AlertDialogManager();


        mRoot = (LinearLayout) findViewById(R.id.root_activity_login);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.username_layout);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.password_layout);
        mInputUsername = (EditText) findViewById(R.id.input_username);
        mInputPassword = (EditText) findViewById(R.id.input_password);
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(mFabClickListener);


        mToolbar = (Toolbar) findViewById(R.id.app_bar_main);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Lo");
        //getSupportActionBar().setIcon(R.drawable.logo3);
        //getSupportActionBar().setLogo(R.drawable.logo3);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().setSubtitle("GIS");
        mDrawer = (NavigationView) findViewById(R.id.main_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }
        //mSelectedId = savedInstanceState == null ? R.id.navigation_item_1 : savedInstanceState.getInt(SELECTED_ITEM_ID);
        // navigate(mSelectedId);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        session = new SessionManager(getApplicationContext());
        boolean loged = session.isLoggedIn();
        Log.d("Condition is :", "" + loged);
        if (loged) {
            Intent intent = new Intent(getApplicationContext(), News.class);
            startActivity(intent);
        }
    }

    public void signup(View view) {
        Intent intent = new Intent(Login.this, SignUpActivity.class);
        startActivity(intent);
    }

    private boolean didUserSeeDrawer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }

    private void markDrawerSeen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mUserSawDrawer).apply();
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void navigate(int mSelectedId) {
        Intent intent = null;

        if (mSelectedId == R.id.navigation_item_1) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, SearchMap.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_2) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, AllBuilds.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_3) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, WeatherToday.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_4) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, News.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_5) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            //intent = new Intent(this, ThirdActivity.class);
            //startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_6) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, AboutUs.class);
            startActivity(intent);
        }
    }

    public void login(View view) {

        boolean isEmptyUserName = isEmptyUserName();
        boolean isEmptyPassword = isEmptyPassword();
        if (isEmptyUserName && isEmptyPassword) {
            Snackbar.make(mRoot, "One Or More Fields Are Blank", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_dismiss), mSnackBarClickListener)
                    .show();
        } else if (isEmptyUserName && !isEmptyPassword) {
            mUsernameLayout.setError("Username Cannot Be Empty");
            mPasswordLayout.setError(null);
        } else if (!isEmptyUserName && isEmptyPassword) {
            mPasswordLayout.setError("Password Cannot Be Empty");
            mUsernameLayout.setError(null);
        } else {
            //All Good Here
            if(isNetworkConnected()) {
                username = mInputUsername.getText().toString();
                password = mInputPassword.getText().toString();
                BackGroundLogin backgorundTask = new BackGroundLogin(this);
                backgorundTask.execute(username, password);
                Log.d("Hi","Conected");
            }
            else
            {
                new AlertDialog.Builder(Login.this)
                        .setTitle("No Network Connection ")
                        .setMessage("The InterNet Not Open Please Enable it")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)

                        .show();



            }


        }
    }


    private boolean isEmptyUserName() {
        return mInputUsername.getText() == null
                || mInputUsername.getText().toString() == null
                || mInputUsername.getText().toString().isEmpty();

    }

    private boolean isEmptyPassword() {
        return mInputPassword.getText() == null
                || mInputPassword.getText().toString() == null
                || mInputPassword.getText().toString().isEmpty();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        menuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();

        navigate(mSelectedId);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, mSelectedId);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public class BackGroundLogin extends AsyncTask<String, Void, String> {
        Context ctx;
        String result = "ckecking..";


        BackGroundLogin(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(Login.this, "Checking Data", "Loading...", false);
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://hugis2.esy.es/login.php";
            //Log.d("ahmed", "hi");
            String username = params[0];
            String password = params[1];
            Log.d("name", username);
            String JasonStr = null;

            try {
                URL url = new URL(login_url);
                Log.d("url", "" + url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                bufferedWriter.write(data);
                Log.d("dataaa ", data);
                bufferedWriter.flush();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));
                String L;
                String M = "";
                M = bufferedReader.readLine();
                Log.d("bbbbb", M);
                if (M.contains("not correct")) {
                    Log.d("failed login", "error in username or password");
                    result = M;
                } else {
                    JasonStr = M.toString();
                    GetNewsDataFromJson(JasonStr);
                    Log.d("success login", "correct data");
                    result = "Welcome";

                }


                os.close();
                bufferedWriter.close();
                IS.close();
                bufferedReader.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        private void GetNewsDataFromJson(String JsonStr)
                throws JSONException

        {
            try {
                // These are the names of the JSON objects that need to be extracted.
                final String LIST = "user";

                final String NAME = "userName";
                final String USERNAME = "userUsername";
                final String PASSWORD = "UserPassword";
                final String MAIL = "userMail";
                final String ID = "userID";
                final String REGISDATE = "registerdate";
                final String LASTLOGIN = "lastLogin";


                JSONObject JsonObj = new JSONObject(JsonStr);
                JSONArray MoviesArray = JsonObj.getJSONArray(LIST);
                User user;
                String ImgExtra;

                // data.clear();

                for (int i = 0; i < MoviesArray.length(); i++) {
                    user = new User();
                    JSONObject dataObj = MoviesArray.getJSONObject(i);

                    String name = dataObj.getString(NAME);
                    String username = dataObj.getString(USERNAME);
                    String password = dataObj.getString(PASSWORD);
                    String mail = dataObj.getString(MAIL);
                    String id = dataObj.getString(ID);
                    String regisDate = dataObj.getString(REGISDATE);
                    String lastLogin = dataObj.getString(LASTLOGIN);

                    user.setId(id);
                    user.setName(name);
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setMail(mail);
                    user.setRegist_date(regisDate);
                    user.setLastLogin(lastLogin);

                    Log.d("User ID:", user.getId());
                    Log.d("User username:", user.getUsername());
                    Log.d("User password:", user.getPassword());

                    session.createLoginSession(id, name, username, mail);
                    //data.add(item);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPostExecute(String res) {
             progressDialog.dismiss();
            Log.d("finalRes", res);
            Toast.makeText(ctx, res, Toast.LENGTH_LONG).show();
            Class<?> activityClass = null;
            if (res.contains("Welcome")) {
                SharedPreferences prefs = ctx.getSharedPreferences("X", 0);
                SharedPreferences.Editor editor = prefs.edit();
                try {
                    activityClass = Class.forName(
                            prefs.getString("lastActivity", News.class.getName()));
                    Log.d("activityNowwwww", "" + activityClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                //String activity=prefs.getString("lastActivity",News.class.getSimpleName());
                ctx.startActivity(new Intent(ctx, activityClass));
            } else {
                alertDialogManager.showAlertDialog(ctx, "Login failed..", "Please enter Vaild username ,password", false);
                Toast.makeText(ctx, "fail to login ", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class BackgorundLoginF extends AsyncTask<String, Void, String> {
        Context ctx;

        public BackgorundLoginF(Context ctx) {
            this.ctx = ctx;
        }

        String Id,Name, Mail, token, Regs_Url;
        String result = "ckecking....";

        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(Login.this, "Saving Data", "Wait...", false);
            Regs_Url = "http://hugis2.esy.es/addUserF.php";
        }


        @Override
        protected String doInBackground(String... params) {
            Id = params[0];
            Name = params[1];
            Mail = params[2];
            token = params[3];

            try {
                URL url = new URL(Regs_Url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8") + "&" +
                        URLEncoder.encode("mail", "UTF-8") + "=" + URLEncoder.encode(Mail, "UTF-8") + "&" +
                        URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
                bufferedWriter.write(data);
                Log.d("dataaa ", data);
                bufferedWriter.flush();

                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));
                String L;
                String R = "";
                R = bufferedReader.readLine();
                Log.d("resss", R);

                if (R != null) {
                    result = R;
                }
                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String reslut) {
            progressDialog.dismiss();
            if (reslut.contains("complete")) {
                session.createLoginSession(f_id, f_name, f_name, f_email);
                Class<?> activityClass = null;
                SharedPreferences prefs = ctx.getSharedPreferences("X", 0);
                SharedPreferences.Editor editor = prefs.edit();
                try {
                    activityClass = Class.forName(
                            prefs.getString("lastActivity", News.class.getName()));
                    Log.d("activityNowwwww", "" + activityClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                //String activity=prefs.getString("lastActivity",News.class.getSimpleName());
                Intent intent = new Intent(ctx, activityClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else
            {
                AlertDialogManager ma=new AlertDialogManager();
                ma.showAlertDialog(Login.this, "Login Fail...", "You Loged with this Account Before Mail Exist", false);
                Toast.makeText(ctx,"You Loged In with this account before",Toast.LENGTH_LONG).show();
            }

        }
    }
}

