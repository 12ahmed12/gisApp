package com.android.gis.huapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class FollowMap extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback ,GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;

    ArrayList<String> ShopStuts = new ArrayList<String>();
    ArrayList<String> shopid = new ArrayList<String>();
    ArrayList<String> PlacesNames = new ArrayList<String>();

    ArrayList<String> PlacesLantute = new ArrayList<String>();
    ArrayList<String> PlacesLagtute = new ArrayList<String>();
    ArrayList<String> PlacesType = new ArrayList<String>();
    ArrayList<String> PlacesID = new ArrayList<String>();
    ArrayList<String> InterestedID = new ArrayList<String>();
    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    static double lan;
    static double lang;
    static int y;
    TextView Pname;
    TextView Bfolw;
    EditText comment;
    TextView submitcomment;
    TextView TTT;
    String U_ID;
    SessionManager sessionManager;
    private UiSettings mUiSettings=null;
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private boolean mUserSawDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_map);

        sessionManager =new SessionManager(getApplicationContext());
        boolean logd=sessionManager.checkLogin();
        Log.d("currentstate",""+logd);
        if(logd) {
            User user = sessionManager.getUserDetails();
            U_ID=user.getId();
            Log.d("user_name", user.getName());
            Log.d("user_id", U_ID);
            Log.d("user_username", user.getUsername());
            BackgorundRerrive backgorundRerrive = new BackgorundRerrive(getApplication());
            backgorundRerrive.execute(U_ID);
        }else
        {

        }

        mDrawer = (NavigationView) findViewById(R.id.map_navigate);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        BackGroundPlaces backGroundPlaces = new BackGroundPlaces();
        backGroundPlaces.execute();


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng helwanUNI = new LatLng(29.868728, 31.315409);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(helwanUNI));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(helwanUNI, 15));


        // Add a marker in Sydney and move the camera
    }

    public class BackGroundPlaces extends AsyncTask<String, Void, String> {
        public BackGroundPlaces() {
            super();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {

                String base_url = "http://hugis2.esy.es/GetAllShopAcount.php";
                Log.d("TTTTt", "RRRR");

                URL url = new URL(base_url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();


            } catch (IOException e) {

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            try {


                String y = datacuter(moviesJsonStr);
                Log.d("hhhh", moviesJsonStr);

                return moviesJsonStr;


            } catch (Exception E) {

                Log.v("exec", E.toString());


            }


            return moviesJsonStr;

        }


        public String datacuter(String x) throws JSONException


        {
            Log.d("YYYYY", x);

            JSONObject jsonObject = new JSONObject(x);
            JSONArray jsonArray = jsonObject.getJSONArray("shop");
            int array_length = jsonArray.length();
            Log.d("array", "" + array_length);

            for (int i = 0; i < array_length; i++) {

                shopid.add(jsonArray.getJSONObject(i).get("shop_id").toString());
                ShopStuts.add(jsonArray.getJSONObject(i).get("shop_status").toString());
                PlacesNames.add(jsonArray.getJSONObject(i).get("ShopName").toString());
                PlacesLantute.add(jsonArray.getJSONObject(i).get("shop_lat").toString());
                PlacesLagtute.add(jsonArray.getJSONObject(i).get("shop_long").toString());

            }
            Log.d("GGGGGGGGGGG", "" + PlacesLagtute);


            return null;
        }


        protected void onPostExecute(String path) {

            mMap.clear();

            final Marker[] M = new Marker[shopid.size()];


            for (int i = 0; i < PlacesNames.size(); i++) {
                lan = Double.parseDouble(PlacesLagtute.get(i).toString());
                lang = Double.parseDouble(PlacesLantute.get(i).toString());
                LatLng latLng = new LatLng(lang, lan);
                Log.d("TOOOOOO", "" + InterestedID);
                if (InterestedID.contains(shopid.get(i).toString())) {


                    M[i] = mMap.addMarker(new MarkerOptions().position(latLng).title(PlacesNames.get(i).toString()).draggable(true).snippet("" + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    Log.d("hii","inter");
                }
                else {
                    Log.d("hii"," Not inter");

                    M[i] = mMap.addMarker(new MarkerOptions().position(latLng).title(PlacesNames.get(i).toString()).draggable(true).snippet("" + i).draggable(true).snippet("" + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                }

                Log.d("MMMMMM", "" + M[i]);

//                final int finalY = y;
//                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(Marker marker) {
//                        M[finalY].showInfoWindow();
//                        Toast.makeText(getApplication(),PlacesNames.get(finalY)+"++++"+PlacesType.get(finalY),Toast.LENGTH_LONG).show();
//                        return false;
//
//                    }
//
//                });
//                Log.d("yyyyy",""+y);


            }


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    String x = marker.getSnippet();
                    y = Integer.parseInt(x);




                    showPopup(FollowMap.this);
                    if (InterestedID.contains(shopid.get(Integer.parseInt(marker.getSnippet().toString())).toString())) {
                        Bfolw.setText("U folw");
                        Bfolw.setBackgroundColor(Color.BLUE);
                    } else {
                        Bfolw.setText("Folw");
                        Bfolw.setBackgroundColor(Color.RED);


                    }


                    return false;


                }



            });
            Log.d("yyyyy", "" + y);


            y++;


        }


    }


    private void showPopup(final Activity context) {


        // Inflate the popup_layout.xml
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.popupmap);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popmap, viewGroup);
        submitcomment = (Button) layout.findViewById(R.id.submit);
        comment = (EditText) layout.findViewById(R.id.Comment);
        Pname = (TextView) layout.findViewById(R.id.name);
        TTT = (TextView) layout.findViewById(R.id.ttt);
        Bfolw = (Button) layout.findViewById(R.id.Flow);
        Pname.setText(PlacesNames.get(y).toString());
        Bfolw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String id = shopid.get(y).toString();

                if (InterestedID.contains(id)) {


                    Bfolw.setText("Flow");
                    Bfolw.setBackgroundColor(Color.RED);
                    BackgorundDelete backgorundDelete = new BackgorundDelete(getApplication());
                    backgorundDelete.execute(U_ID, id);
                    BackgorundRerrive backgorundRerrive = new BackgorundRerrive(getApplication());
                    backgorundRerrive.execute(U_ID);
                    BackGroundPlaces backGroundPlaces = new BackGroundPlaces();
                    backGroundPlaces.execute();

                } else {
                    Bfolw.setText("Un flow");
                    Bfolw.setBackgroundColor(Color.BLUE);
                    BackgorundInterest backgorundInterest = new BackgorundInterest(getApplication());
                    backgorundInterest.execute(U_ID, id);
                    BackgorundRerrive backgorundRerrive = new BackgorundRerrive(getApplication());
                    backgorundRerrive.execute(U_ID);
                    BackGroundPlaces backGroundPlaces = new BackGroundPlaces();
                    backGroundPlaces.execute();


                }


            }
        });
        submitcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coment = comment.getText().toString();
                Log.d("yeeees", coment);
                if(coment != null && !coment.isEmpty()) {
                    BackgorundAddcomment backgorundAddcomment = new BackgorundAddcomment(getApplication());
                    backgorundAddcomment.execute(U_ID, PlacesNames.get(y).toString(), coment);
                }
                else
                {
                    Toast.makeText(getApplication(),"Comment Can't Be Empty .....",Toast.LENGTH_LONG).show();

                }


            }
        });

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        popup.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);


        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);


    }


    public class BackgorundInterest extends AsyncTask<String, Void, String> {
        Context ctx;

        public BackgorundInterest(Context ctx) {
            this.ctx = ctx;
        }

        String user_id, Shop_id, Regs_Url;
        String result = "ckecking....";

        @Override
        protected void onPreExecute() {
            Regs_Url = "http://hugis2.esy.es/insertinteset.php";
        }


        @Override
        protected String doInBackground(String... params) {
            user_id = params[0];
            Shop_id = params[1];
            try {
                URL url = new URL(Regs_Url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                        URLEncoder.encode("shop_id", "UTF-8") + "=" + URLEncoder.encode(Shop_id, "UTF-8");
                bufferedWriter.write(data);
                Log.d("dataaa ", data);
                bufferedWriter.flush();

                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
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

                return "Done";
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

        }
    }
    public class BackgorundDelete extends AsyncTask<String, Void, String> {
        Context ctx;

        public BackgorundDelete(Context ctx) {
            this.ctx = ctx;
        }

        String user_id, Shop_id, Regs_Url;
        String result = "ckecking....";

        @Override
        protected void onPreExecute() {
            Regs_Url = "http://hugis2.esy.es/DeleteInterset.php";
        }


        @Override
        protected String doInBackground(String... params) {
            user_id = params[0];
            Shop_id = params[1];
            try {
                URL url = new URL(Regs_Url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                        URLEncoder.encode("shop_id", "UTF-8") + "=" + URLEncoder.encode(Shop_id, "UTF-8");
                bufferedWriter.write(data);
                Log.d("dataaa ", data);
                bufferedWriter.flush();

                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
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

                return "Done";
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

        }
    }

    public class BackgorundRerrive extends AsyncTask<String, Void, String> {
        Context ctx;

        public BackgorundRerrive(Context ctx) {
            this.ctx = ctx;
        }

        String user_id, Regs_Url;
        String result = "ckecking....";

        @Override
        protected void onPreExecute() {
            Regs_Url = "http://hugis2.esy.es/retriveinterset.php";
        }


        @Override
        protected String doInBackground(String... params) {
            user_id = params[0];
            try {
                URL url = new URL(Regs_Url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
                bufferedWriter.write(data);
                Log.d("dataaa ", data);
                bufferedWriter.flush();

                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
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

                try {
                    String x = Intersted(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        public String Intersted(String intrest) throws JSONException


        {
            Log.d("Soosoo", intrest);
            InterestedID.clear();
            JSONObject jsonObject = new JSONObject(intrest);
            JSONArray jsonArray = jsonObject.getJSONArray("Interset");
            int array_length = jsonArray.length();
            Log.d("array", "" + array_length);
            InterestedID.clear();


            for (int i = 0; i < array_length; i++) {

                InterestedID.add(jsonArray.getJSONObject(i).get("Shop_id").toString());

            }
            Log.d("Inter", "" + InterestedID);


            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String reslut) {
            Log.d("rereeeeee", reslut);

        }
    }

    public class BackgorundAddcomment extends AsyncTask<String, Void, String> {
        Context ctx;

        public BackgorundAddcomment(Context ctx) {
            this.ctx = ctx;
        }

        String user_id, Title, Comment, Regs_Url;
        String result = "ckecking....";

        @Override
        protected void onPreExecute() {
            Regs_Url = "http://hugis2.esy.es/Addfeedback.php";
        }


        @Override
        protected String doInBackground(String... params) {
            user_id = params[0];
            Title = params[1];
            Comment = params[2];
            try {
                URL url = new URL(Regs_Url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                        URLEncoder.encode("Title", "UTF-8") + "=" + URLEncoder.encode(Title, "UTF-8") + "&" +
                        URLEncoder.encode("Comment", "UTF-8") + "=" + URLEncoder.encode(Comment, "UTF-8");
                bufferedWriter.write(data);
                Log.d("dataaa ", data);
                bufferedWriter.flush();

                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
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

                return result;
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

            if (reslut.contains("New Row inserted")) {

                TTT.setText("your commnet Added");

            } else {
                TTT.setText("you comment is agnore");

            }

        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_optionals, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        if (id == R.id.map_op_navigation_item_1) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }
        if (id == R.id.map_op_navigation_item_2) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;
        }
        if (id == R.id.map_op_navigation_item_3) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        }
        if (id == R.id.map_op_navigation_item_4) {
            intent = new Intent(this, SearchMap.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(getApplicationContext(), "Market drag start ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(getApplicationContext(),"on draging  ",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(getApplicationContext(),"Market drag ended ",Toast.LENGTH_LONG).show();
    }

    private void navigate(int mSelectedId) {
        Intent intent = null;


        if (mSelectedId == R.id.map_navigation_item_1) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, News.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.map_navigation_item_2) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, SearchMap.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.map_navigation_item_3) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, NavigationMap.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.map_navigation_item_4) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, FollowMap.class);
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