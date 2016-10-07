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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import java.util.ArrayList;

public class Notifications extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener,NotificationAdapter.ClickListener{
    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    // Need this to link with the Snackbar
    private CoordinatorLayout mCoordinator;
    //Need this to set the title of the app bar
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private boolean mUserSawDrawer = false;
    ArrayList<String> InterestedShops;
    ArrayList<notification> mNotifications;
    SessionManager sessionManager;
    String userid=null;
    String values[];
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    int counter=0;
    ProgressDialog progressDialog;
    NotificationAdapter notificationAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        InterestedShops=new ArrayList<>();
        mNotifications=new ArrayList<>();
        sessionManager =new SessionManager(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.RecycleNotify);




        SharedPreferences prefs =getApplicationContext().getSharedPreferences("X", 0);
        SharedPreferences.Editor editor = prefs.edit();
        Log.d("currentActivity",prefs.getString("lastActivity","empty"));
        editor.clear();
        editor.commit();
        boolean bb=sessionManager.isLoggedIn();
        if(bb)
        {
            User user=sessionManager.getUserDetails();
            editor.putString("lastActivity", "com.android.gis.huapp.Notifications");
            editor.putString("ID",user.getId());
            editor.commit();
            Log.d("currentActivity",prefs.getString("lastActivity","empty"));
        }

        getMyInterestedShops interests=new getMyInterestedShops(getApplicationContext());

        boolean b=sessionManager.checkLogin();
        if (!isNetworkConnected()) {
            new AlertDialog.Builder(Notifications.this)
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
        }else {
            if (b) {
                String uid = prefs.getString("ID", null);
                if (uid != null) {
                    interests.execute(uid);
                } else {
                    User u = sessionManager.getUserDetails();
                    userid = u.getId();
                    Log.d("userid", userid);
                    interests.execute(userid);
                }
            }
        }


        mCoordinator = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawer = (NavigationView) findViewById(R.id.navigation_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notice how the Coordinator Layout object is used here
                Snackbar.make(mCoordinator, "FAB Clicked", Snackbar.LENGTH_SHORT).setAction("DISMISS", null).show();
            }
        });

        //Notice how the title is set on the Collapsing Toolbar Layout instead of the Toolbar
        mCollapsingToolbarLayout.setTitle("Notifications");

        notificationAdapter=new NotificationAdapter(getApplicationContext(),mNotifications);

        notificationAdapter.setClickListener(this);

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void itemClicked(View view, int position) {
     Log.d("ItemClicked","ClickItem"+position);

        notification item=mNotifications.get(position);
        notificationDialogFragment nt=notificationDialogFragment.newInstance(item);
        nt.show(getSupportFragmentManager(),nt.getTag());

    }


    public class getMyInterestedShops extends AsyncTask<String, Void, String> {
        Context ctx;
        String result = "ckecking..";

        getMyInterestedShops(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(Notifications.this, "Downloading Data", "Wait Please...",false,true);
            InterestedShops.clear();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String Shop_Account_url = "http://hugis2.esy.es/getInterestedShops.php";
            //Log.d("ahmed", "hi");
            String id = params[0];

            Log.d("id", id);
            String JasonStr=null;

            try {
                URL url = new URL(Shop_Account_url);
                Log.d("url", "" + url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

                bufferedWriter.write(data);
                Log.d("id ", data);
                bufferedWriter.flush();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String M=null;
                String line;
                StringBuffer buffer=new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                {
                    buffer.append(line+"\n");
                }
                M=""+buffer;
                Log.d("bbbbb", M);
                if(M.contains("error"))
                {
                    Log.d("error","something wrong");
                    result = M;
                }else
                {
                    JasonStr=M.toString();
                    GetNewsDataFromJson(JasonStr);
                    Log.d("found shop account", "correct data");
                    result="success";

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
                final String LIST = "interestedShop";

                final String SHOP_ID="shop_id";



                JSONObject JsonObj = new JSONObject(JsonStr);
                JSONArray MoviesArray = JsonObj.getJSONArray(LIST);

                for (int i = 0; i < MoviesArray.length(); i++) {
                    JSONObject dataObj = MoviesArray.getJSONObject(i);
                    String id=dataObj.getString(SHOP_ID);
                    InterestedShops.add(id);
                    Log.d("shop ID:", id);
                    //data.add(item);

                }

            }catch (JSONException  e)
            {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String res) {

            Log.d("finalRes", res);
            mNotifications.clear();
            int c=InterestedShops.size();
            values= new String[InterestedShops.size()];
            values=InterestedShops.toArray(values);
            int sizeList=InterestedShops.size();
            int size=values.length;
            Log.d("shops number List",""+sizeList);
            Log.d("shops  number Array",""+size);

            for (String  d:values) {
                Log.d("ShopID",d);
               new TaskNotifications(ctx).execute(d);
            }

        }
    }


    public class TaskNotifications extends AsyncTask<String, Void, String> {
        Context ctx;
        String result = "ckecking..";

        TaskNotifications(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            InterestedShops.clear();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String Shop_Account_url = "http://hugis2.esy.es/getNotifications.php";
            //Log.d("ahmed", "hi");
            String shop_id = params[0];

            Log.d("id", shop_id);
            String JasonStr=null;

            try {
                URL url = new URL(Shop_Account_url);
                Log.d("url", "" + url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = URLEncoder.encode("shop_id", "UTF-8") + "=" + URLEncoder.encode(shop_id, "UTF-8");

                bufferedWriter.write(data);
                Log.d("shop_id ", data);
                bufferedWriter.flush();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));
                String M;
                String line;
                StringBuffer buffer=new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                {
                    buffer.append(line+"\n");
                }

                M=""+buffer;
                Log.d("jsondataaaa ",""+M);
                if(M.contains("error"))
                {
                    Log.d("error","something wrong");
                    result = M;
                }else
                {
                    JasonStr=M.toString();
                    GetNewsDataFromJson(JasonStr);
                    Log.d("found shop Notification", "correct data");
                    Log.d("jsonString", M);
                    result="success";

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
                final String LIST = "notifications";
                final String NOTIFICATION_ID="notify_id";
                final String SHOP_ID="shop_id";
                final String NOTIFICATION_MESSAGE="notify_messg";
                final String NOTIFICATION_START="notify_start";
                final String NOTIFICATION_END="notify_end";
                final String SHOP_NAME="shop_name";

                JSONObject JsonObj = new JSONObject(JsonStr);
                JSONArray NotifcationArray = JsonObj.getJSONArray(LIST);
                notification note ;
                for (int i = 0; i < NotifcationArray.length(); i++) {
                    note=new notification();
                    JSONObject dataObj = NotifcationArray.getJSONObject(i);
                    String notify_id=dataObj.getString(NOTIFICATION_ID);
                    String shop_id=dataObj.getString(SHOP_ID);
                    String notify_message=dataObj.getString(NOTIFICATION_MESSAGE);
                    String notify_start=dataObj.getString(NOTIFICATION_START);
                    String notify_end=dataObj.getString(NOTIFICATION_END);
                    String shop_name=dataObj.getString(SHOP_NAME);
                     Log.d("message",notify_message);


                    note.setId(notify_id);
                    note.setShopId(shop_id);
                    note.setStartDate(notify_start);
                    note.setEndDate(notify_end);
                    note.setMessge(notify_message);
                    note.setShop_name(shop_name);
                    mNotifications.add(note);

                    //data.add(item);

                }

            }catch (JSONException  e)
            {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String res) {
             counter++;
            String cnow=""+counter;
            Log.d("current  counter", cnow);
            Log.d("VAlues number",""+values.length);
            if (counter==values.length)
            {
                progressDialog.dismiss();
                for (notification  d:mNotifications) {
                    Log.d("notification ID:", d.getId());
                    Log.d(" ID of shop :", d.getShopId());
                    Log.d(" ID of shop :", d.getMessge());

                    notificationAdapter=new NotificationAdapter(ctx,mNotifications);

                    recyclerView.setAdapter(notificationAdapter);

                  /*  gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(gaggeredGridLayoutManager);*/

                    GridLayoutManager mLayoutManager = new GridLayoutManager(ctx, 3); // (Context context, int spanCount)
                    recyclerView.setLayoutManager(mLayoutManager);


                    /*LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ctx);
                    linearLayoutManager.setReverseLayout(true);
                    recyclerView.setLayoutManager(linearLayoutManager);*/
                    notificationAdapter.notifyDataSetChanged();

                }
            }

        }
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
       if (id == R.id.Logout) {
            sessionManager.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean didUserSeeDrawer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }
    protected void onPostCreate(Bundle savedInstanceState) {
        // TODO Display the navigation drawer icon on action bar when there state has changed
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
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
            intent = new Intent(this, Notifications.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_6) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, AboutUs.class);
            startActivity(intent);
        }
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
}
