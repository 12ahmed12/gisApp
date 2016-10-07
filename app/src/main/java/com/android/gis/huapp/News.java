package com.android.gis.huapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class News extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    RecyclerView recyclerView;
    private ArrayList<RecycleData> data;
    private boolean mUserSawDrawer = false;
    SessionManager sessionManager;
    int Position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        /*sessionManager =new SessionManager(getApplicationContext());

        boolean loged=sessionManager.checkLogin();

        if(loged) {
            User user = sessionManager.getUserDetails();
            Log.d("user_name", user.getName());
            Log.d("user_username", user.getUsername());
        }*/

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("University News");
        getSupportActionBar().setSubtitle("H-U-App");
        mDrawer = (NavigationView) findViewById(R.id.main_navigate);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
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
        //navigate(mSelectedId);


        data = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        if(isNetworkConnected()) {
            GetNewsTask task=new GetNewsTask(getApplication());
            task.execute();
            Log.d("Hi","Conected");
        }
        else
        {
            new AlertDialog.Builder(News.this)
                    .setTitle("No Network Connection ")
                    .setMessage("The InterNet Not Open you Must Open it or App may Crash")
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





        recyclerView.addOnItemTouchListener(
                new Recycler_View_Adapter(data, getApplicationContext(), new Recycler_View_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Position=position;
                        Log.d("@@@@@", "" + position);
                        RecycleData item = data.get(position);
                        Log.d("itemData", item.getMessage() + ",," + item.getStartDate());

                       // BottomSheetDialogFragment bottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
                        com.android.gis.huapp.CustomBottomSheetDialogFragment bottomSheetDialogFragment = com.android.gis.huapp.CustomBottomSheetDialogFragment.newInstance(item);
                        //show it
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                            //showPopup(News.this,item);

                    }
                })
        );

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void showPopup(final Activity context,RecycleData item) {
        int popupWidth = 600;
        int popupHeight = 560;
        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);
        TextView title= (TextView) layout.findViewById(R.id.poptitle);
        TextView date= (TextView) layout.findViewById(R.id.popdate);
        ImageView img= (ImageView) layout.findViewById(R.id.popimg);
        TextView messge= (TextView) layout.findViewById(R.id.popmessage);
        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        popup.setOutsideTouchable(false);

        title.setText(item.getTitle());
        date.setText(item.getStartDate());
        messge.setText(item.getMessage());
        String im=item.getImg();
        byte[] decodedString = Base64.decode(String.valueOf(im), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        img.setImageBitmap(decodedByte);
        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }






    public class GetNewsTask extends AsyncTask<Void, Void, String> {
        Context ctx;
        String result = "ckecking..";
        String jsonUrl;
        ProgressDialog progressDialog;
        GetNewsTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(News.this, "Downloading News","Loading", false,true);
            jsonUrl="http://hugis2.esy.es/get_News.php";
        }
        @Override
        protected String doInBackground(Void... params) {
            String JasonStr=null;
            try {
                URL url = new URL(jsonUrl);
                Log.d("url", "" + url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));

                StringBuilder stringBuilder=new StringBuilder();

                String line;
                StringBuffer buffer=new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                {
                    buffer.append(line+"\n");
                }
                Log.d("dataaaa", ""+buffer);

                JasonStr=buffer.toString();
                GetNewsDataFromJson(JasonStr);
                IS.close();
                bufferedReader.close();
                httpURLConnection.disconnect();
                if (buffer != null) {
                    result = ""+buffer;
                }


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
                final String LIST = "news";
                final String TITLE = "title";
                final String MESSAGE = "message";
                final String STARTDATE = "startat";
                final String ENDDATE="endat";
                final String ID="notification_id";
                final String IMG="img";


                JSONObject JsonObj = new JSONObject(JsonStr);
                JSONArray MoviesArray = JsonObj.getJSONArray(LIST);
                RecycleData item;
                String ImgExtra ;

                data.clear();

                for (int i = 0; i < MoviesArray.length(); i++) {
                    item = new RecycleData();
                    JSONObject dataObj = MoviesArray.getJSONObject(i);

                    String title = dataObj.getString(TITLE);
                    String message = dataObj.getString(MESSAGE);
                    String startdate = dataObj.getString(STARTDATE);
                    String enddate=dataObj.getString(ENDDATE);
                    String id=dataObj.getString(ID);
                    String img=dataObj.get(IMG).toString();

                    item.setTitle(title);
                    item.setMessage(message);
                    item.setStartDate(startdate);
                    item.setEndDate(enddate);
                    item.setId(id);
                    item.setImg(img);

                    data.add(item);

                }
            }catch (JSONException  e)
            {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String res) {
            progressDialog.dismiss();
            Log.d("finalRes", res);

            for (RecycleData d:data) {
                Log.d("newsTitle",d.getTitle());
                Log.d("siize",""+d.getImg().length());
            }
            Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getApplication(),new Recycler_View_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // TODO Handle item click
                    // Log.e("@@@@@", "" + position);
                }
            });

            recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager=new LinearLayoutManager(ctx);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
            adapter.notifyDataSetChanged();
            //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL));
          /*  RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1000);
            itemAnimator.setRemoveDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);*/

        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.Logout) {
            sessionManager.logoutUser();
            return true;
        }*/

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
}
