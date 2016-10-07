

package com.android.gis.huapp;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

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

public class SignUpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    EditText u_name,u_username ,u_password,u_mail;
    String name,username,password,mail,confirm;

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private LinearLayout mRoot;
    private TextInputLayout mNameLayout;
    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mMailLayout;
    private TextInputLayout mConfirmLayout;
    private EditText mInputName;
    private EditText mInputUsername;
    private EditText mInputPassword;
    private EditText mInputMail;
    private EditText mInputConfirmpass;
    private FloatingActionButton mFAB;
    private boolean mUserSawDrawer = false;
    AlertDialogManager alertDialogManager;
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
        setContentView(R.layout.activity_sign_up);

         alertDialogManager=new AlertDialogManager();



      /*  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = preferences.edit();

      *//*  prefEditor.remove("token");
        prefEditor.putBoolean("token_set",false);
        prefEditor.commit();*//*
        boolean b=preferences.getBoolean("token_set", false);
        Log.d("tokenboolen", "" + b);
        if(b)
        {
            Token=preferences.getString("token", null);
            Log.d("tokenString", Token);

        }*/


        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();
         tokee = FirebaseInstanceId.getInstance().getToken();
        Log.d("tokee", "" + tokee);


        mRoot = (LinearLayout) findViewById(R.id.main_content);
        mNameLayout= (TextInputLayout) findViewById(R.id.Name_layout);
        mUsernameLayout= (TextInputLayout) findViewById(R.id.username_layout);
        mPasswordLayout= (TextInputLayout) findViewById(R.id.password_layout);
        mMailLayout= (TextInputLayout) findViewById(R.id.email_layout);
        mConfirmLayout= (TextInputLayout) findViewById(R.id.confirm_layout);

        mInputName= (EditText) findViewById(R.id.name);
        mInputUsername= (EditText) findViewById(R.id.username);
        mInputPassword= (EditText) findViewById(R.id.password);
        mInputConfirmpass= (EditText) findViewById(R.id.confirmpass);
        mInputMail= (EditText) findViewById(R.id.email);
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(mFabClickListener);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
            intent = new Intent(this, com.android.gis.huapp.News.class);
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

    public  void signup(View view) {


        boolean isEmptyName = isEmptyName();
        boolean isEmptyUserName = isEmptyUserName();
        boolean isEmptyPassword = isEmptyPassword();
        boolean isEmptyMail = isEmptyMail();
        boolean isEmptyConfirm = isEmptyConfirm();

        name = mInputName.getText().toString();
        username = mInputUsername.getText().toString();
        password = mInputPassword.getText().toString();
        password = mInputPassword.getText().toString();
        mail = mInputMail.getText().toString();
        confirm = mInputConfirmpass.getText().toString();
        boolean isValid = isValidEmail(mail);
        boolean confirmed = isConfirmed(password, confirm);

        if (isEmptyName || isEmptyUserName || isEmptyPassword || isEmptyMail || isEmptyConfirm) {
            Snackbar.make(mRoot, "One Or More Fields Are Empty", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_dismiss), mSnackBarClickListener)
                    .show();
        } else if (!isEmptyName && name.length() < 5 || name.length() > 20) {
            mNameLayout.setError("Name must be large  than 5 character and less than 20");
            mNameLayout.animate().rotationX(DEFAULT_KEYS_SEARCH_GLOBAL);
            mUsernameLayout.setError(null);
            mMailLayout.setError(null);
            mPasswordLayout.setError(null);
            mConfirmLayout.setError(null);
        } else if (!isEmptyMail && !isValid) {
            mMailLayout.setError("Email not valid");
            mNameLayout.setError(null);
            mUsernameLayout.setError(null);
            mPasswordLayout.setError(null);
            mConfirmLayout.setError(null);
        } else if (!isEmptyUserName && username.length() < 5 || username.length() > 20) {
            mUsernameLayout.setError("Username Must be less than 20 character and more than 5");
            mMailLayout.setError(null);
            mPasswordLayout.setError(null);
            mMailLayout.setError(null);
            mConfirmLayout.setError(null);
        } else if (!isEmptyPassword && password.length() < 5 || username.length() > 20) {
            mPasswordLayout.setError("password Must be less than 20 character and more than 5");
            mMailLayout.setError(null);
            mUsernameLayout.setError(null);
            mMailLayout.setError(null);
            mConfirmLayout.setError(null);
        } else if (!confirmed) {
            mConfirmLayout.setError("Not Identical");
            mPasswordLayout.setError("Not Identical");
            mMailLayout.setError(null);
            mUsernameLayout.setError(null);
            mMailLayout.setError(null);
        } else {
            //All Good Here

            String method = "register";
            //BackgorundTask backgorundTask=new BackgorundTask(this);
            if (tokee != null) {
                //All Good Here-
                if (isNetworkConnected()) {
                    BackgorundTask backgorundTask = new BackgorundTask(this);
                    backgorundTask.execute(name, username, password, mail, tokee);
                    Log.d("Hi", "Conected");
                } else {
                    new AlertDialog.Builder(SignUpActivity.this)
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
            } else {
                alertDialogManager.showAlertDialog(SignUpActivity.this, "Token Empty", "wrong data", false);
            }
        }
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isConfirmed(String password,String confirm) {
            if(password.equals(confirm))
            {
                return true;
            }else
            {
                return  false;
            }
    }

    private boolean isEmptyName() {
        return mInputName.getText() == null
                || mInputName.getText().toString() == null
                || mInputName.getText().toString().isEmpty();
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

    private boolean isEmptyMail() {
        return mInputMail.getText() == null
                || mInputMail.getText().toString() == null
                || mInputMail.getText().toString().isEmpty();
    }
    private boolean isEmptyConfirm() {
        return mInputConfirmpass.getText() == null
                || mInputConfirmpass.getText().toString() == null
                || mInputConfirmpass.getText().toString().isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public class  BackgorundTask extends AsyncTask<String ,Void,String>
    {
        Context ctx;
        ProgressDialog progressDialog;
        public BackgorundTask(Context ctx){
          this.ctx=ctx;
        }

        String Name,Username,Password,Mail,token,Regs_Url;
        String result="ckecking....";

        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(SignUpActivity.this, "Saving Data","Please wait..", false);
            Regs_Url="http://hugis2.esy.es/add_info.php";
        }



        @Override
        protected String doInBackground(String... params) {
            Name=params[0];
            Username=params[1];
            Password=params[2];
            Mail=params[3];
            token=params[4];

            try {
                URL url=new URL(Regs_Url);

                HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8") + "&" +
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(Username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8") + "&" +
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
                R= bufferedReader.readLine();
                Log.d("resss", R);

                if (R!=null)
                {
                    result=R;
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
            if(reslut.contains("complete"))
            {
                Toast.makeText(getApplication(), reslut, Toast.LENGTH_LONG).show();
                ctx.startActivity(new Intent(ctx, com.android.gis.huapp.Login.class));
            }else
            {
                alertDialogManager.showAlertDialog(ctx, "Registration Fail...", reslut, false);

            }

        }
    }


}
