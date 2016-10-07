package com.android.gis.huapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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

public class NavigationMap extends FragmentActivity implements  NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback ,GoogleMap.OnMarkerDragListener  {

    private GoogleMap mMap;
    static Location location;
    static Marker marker;
    String Location1;
    static String lat ="29.86314300";
    static String lang="31.31310900";
    static String Deslat;
    static String Deslang;

    static String SearchData1 = "";
    ArrayList<String> PlacesNames = new ArrayList<String>();
    ArrayList<String> PlacesNamesFrom = new ArrayList<String>();
    ArrayList<String> PlacesLantute = new ArrayList<String>();
    ArrayList<String> PlacesLagtute = new ArrayList<String>();
    public ArrayAdapter<String> Palces;
    public ArrayAdapter<String> PalcesFrom;

    Spinner From;
    Spinner To;
    String Too;
    Button Location;
    GPS_tracker tracker;

    private UiSettings mUiSettings=null;
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private boolean mUserSawDrawer = false;
    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_map);
        PlacesNamesFrom.add("My Location Now");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDrawer = (NavigationView) findViewById(R.id.map_navigate);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        From = (Spinner) findViewById(R.id.spinner);
        To = (Spinner) findViewById(R.id.spinner2);
        ImageButton Go = (ImageButton) findViewById(R.id.imageButton);



        Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkConnected()) {

                    Log.d("lonttttt",lat);
                    Log.d("longggee",lang);
                    Log.d("LLLLLLL", Deslat);
                    Log.d("GUUUU", Deslang);


                    BackGroundBath backGroundBath = new BackGroundBath();
                    backGroundBath.execute(lat,lang, Deslat, Deslang);
                    Log.d("Hi", "Conected");
                } else {
                    new android.support.v7.app.AlertDialog.Builder(NavigationMap.this)
                            .setTitle("InterNet Not Enable ")
                            .setMessage("The InterNet Not enable Please Enaple it")
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
        });



        BackGroundPlaces backGroundPlaces = new BackGroundPlaces();
        backGroundPlaces.execute();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_optionals, menu);
        return true;
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
        Toast.makeText(getApplicationContext(), "Market drag ended ", Toast.LENGTH_LONG).show();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
        LatLng helwanUNI = new LatLng(29.868728, 31.315409);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(helwanUNI));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(helwanUNI, 15));

    }


    public class BackGroundBath extends AsyncTask<String, Void, String> {


        public BackGroundBath() {
            super();
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String Bathurl = "http://hugis2.esy.es/graph2.php";

            try {

                lat = params[0];
                lang = params[1];
                Deslat=params[2];
                Deslang=params[3];


                URL url = new URL(Bathurl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("pos_lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8") + "&" +
                        URLEncoder.encode("pos_long", "UTF-8") + "=" + URLEncoder.encode(lang, "UTF-8") + "&" +
                        URLEncoder.encode("des_lat", "UTF-8") + "=" + URLEncoder.encode(Deslat, "UTF-8") + "&" +
                        URLEncoder.encode("des_long", "UTF-8") + "=" + URLEncoder.encode(Deslang, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                SearchData1 = "";
                while ((line = bufferedReader.readLine()) != null) {

                    SearchData1 += line;


                }
                String x = getpat(SearchData1);
                Log.d("GGGGGGg", "" + SearchData1);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return x;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        public String getpat(String x) {
            String path = "";
            try {
                JSONObject jsonObject = new JSONObject(x);
                path = jsonObject.getString("path");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Haemmmmm", path);

            return path;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            //path=s;
            mMap.clear();
            Log.d("ibrahem_ahmed", "" + s);
            String[] paths = s.split("_");
            String[] path_se;
            int counter=0;
            for (String p : paths) {

                path_se = p.split(",");
                Log.d("pahte", path_se[0]);
                LatLng startLat=new LatLng(Double.parseDouble(path_se[0]), Double.parseDouble(path_se[1]));
                LatLng endlat=new LatLng(Double.parseDouble(path_se[2]), Double.parseDouble(path_se[3]));
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(Double.parseDouble(path_se[0]), Double.parseDouble(path_se[1])),
                                new LatLng(Double.parseDouble(path_se[2]), Double.parseDouble(path_se[3])))
                        .width(5)
                        .color(Color.GREEN)) ;

                if(counter==0)
                {
                    mMap.addMarker(new MarkerOptions().position(endlat).title("Start").draggable(true).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));


                }
                if(counter==paths.length-1)
                {
                    mMap.addMarker(new MarkerOptions().position(endlat).title("End").draggable(true).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));



                }
                counter++;



            }


        }


    }


    public class BackGroundPlaces extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        public BackGroundPlaces() {
            super();
        }

        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(NavigationMap.this, "Loading Places","Loading...", false,true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {

                String base_url = "http://hugis2.esy.es/places.php";
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
            JSONArray jsonArray = jsonObject.getJSONArray("Places");
            int array_length = jsonArray.length();

            for (int i = 0; i < array_length; i++) {

                PlacesNames.add(jsonArray.getJSONObject(i).get("Place_name").toString());
                PlacesNamesFrom.add(jsonArray.getJSONObject(i).get("Place_name").toString());
                PlacesLantute.add(jsonArray.getJSONObject(i).get("Latitude").toString());
                PlacesLagtute.add(jsonArray.getJSONObject(i).get("Longitude").toString());


            }
            Log.d("GGGGGGGGGGG", "" + PlacesNames);
            Log.d("GGGGGGGGGGG", "" + PlacesNamesFrom);



            return null;
        }


        protected void onPostExecute(String path) {
            progressDialog.dismiss();
            Palces = new ArrayAdapter<String>(getApplication(),R.layout.spinner_item, PlacesNames);
            PalcesFrom = new ArrayAdapter<String>(getApplication(),R.layout.spinner_item, PlacesNamesFrom);

            From.setAdapter(PalcesFrom);
            To.setAdapter(Palces);
            From.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    if(position==0)
                    {
                        MapUP();

                    }
                    else {
                        int x=position-1;
                        lat = "";

                        lang = "";
                        lat = PlacesLantute.get(x).toString();
                        Log.d("lat", lat);
                        lang = PlacesLagtute.get(x).toString();
                        Log.d("lat", lang);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            To.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Deslat="";
                    Deslang="";
                    Deslat = PlacesLantute.get(position).toString();
                    Log.d("Ses Lat", Deslat);
                    Deslang = PlacesLagtute.get(position).toString();
                    Log.d("Ses LAng", Deslang);
                    //Toast.makeText(getApplication(), "" + PlacesNames.get(position), Toast.LENGTH_LONG).show();
                    // Too = PlacesNames.get(position).toString();
                    //  Toast.makeText(getApplication(), "name" + Too, Toast.LENGTH_LONG).show();
//               BackGroundBath backGroundBath=new BackGroundBath();
//                backGroundBath.execute(Too);

//               if(PlacesNames.get(position).equals("كليه الحاسبات"))
//               {
//                   BackGroundBath backGroundBath=new BackGroundBath();
//                   backGroundBath.execute(PlacesNames.get(position).toString());
//
//
//               }
//               else
//               {
//                   Toast.makeText(getApplication(),"Please salect Fcih right Now :( ",Toast.LENGTH_LONG).show();
//
//
//               }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }


    }
    public void MapUP()
    {
        UiSettings settings = mMap.getUiSettings();

        LocationManager service = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        location = service.getLastKnownLocation(provider);

        if(location!=null)
        {
            lat="29.86314300";
            lang="31.31310900";

            LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
            Log.d("Latitute",""+location.getLatitude());
            Log.d("Latnagggte",""+location.getLongitude());
            lat="29.86314300";
            lang="31.31310900";
            Log.d("lantttydydyd",lat);
            Log.d("lonagytyytyytyt",lang);




        }
        else
        {

            new AlertDialog.Builder(NavigationMap.this)
                    .setTitle("Google Location Service")
                    .setMessage("Google location Service Not enable Enaple it")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));



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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;
        }
        mMap.setMyLocationEnabled(true);

    }


//    class GPS_tracker extends Service implements LocationListener {
//
//        private final Context context;
//        boolean is_gps_enable = false;
//        boolean can_get_location = false;
//        boolean is_network_enable = false;
//        Location location;
//
//        double latitude;
//        double longitude;
//        private static final long min_update_distance = 100;
//        private static final long min_update_time = 1000 * 60 * 1;
//
//        protected LocationManager locationManager;
//
//
//        public GPS_tracker(Context context) {
//            this.context = context;
//            getlocation();
//        }
//
//        public Location getlocation() {
//            try {
//
//                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
//                is_gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//                is_network_enable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//                if (!is_network_enable && !is_gps_enable) {
//                } else {
//                    can_get_location = true;
//                    if (is_network_enable) {
//                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return null;
//                        }
//                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
//                                , min_update_time, min_update_distance, this);
//
//
//                        if (locationManager != null) {
//                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                            if (location != null) {
//                                latitude = location.getLatitude();
//                                longitude = location.getLongitude();
//
//
//                            }
//
//                        }
//
//                    }
//                    if (is_gps_enable) {
//                        if (location == null) {
//                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
//                                    , min_update_time, min_update_distance, this);
//                            if (locationManager != null) {
//
//                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                                if (location != null) {
//                                    latitude = location.getLatitude();
//                                    longitude = location.getLongitude();
//
//
//                                }
//
//
//                            }
//
//
//                        }
//                    }
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return location;
//
//
//        }
//
//        public void stop_using_gps() {
//            if (locationManager != null) {
//
//                // locationManager.removeUpdates(GPS_tracker.this);
//            }
//
//        }
//
//        public double get_latitude() {
//            if (location != null) {
//                latitude = location.getLatitude();
//            }
//            return latitude;
//        }
//
//        public double get_longitude() {
//            if (location != null) {
//                longitude = location.getLongitude();
//            }
//            return longitude;
//        }
//
//
//        public boolean Can_get_location() {
//
//            return can_get_location;
//        }
//
//        public void show_settings()
//
//        {
//
//            new AlertDialog.Builder(NavigationMap.this)
//                    .setTitle("Google Location Service")
//                    .setMessage("Google location Service Not enable Enaple it")
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//
//
//                        }
//                    })
//                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//
//
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
//
//
//
////        AlertDialog.Builder alert=new AlertDialog.Builder(context);
////
////        alert.setTitle("GPS settings");
////
////        alert.setMessage("GPS is not enabled");
////
////        alert.setPositiveButton("settings", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialogInterface, int i) {
////                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                getApplication().startActivity(intent);
////            }
////        });
////
////        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialogInterface, int i) {
////                dialogInterface.cancel();
////            }
////        });
////
////        alert.show();
//
//
//        }
//
//
//        @Override
//        public void onLocationChanged(Location location) {
//
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//
//        @Nullable
//        @Override
//        public IBinder onBind(Intent intent) {
//            return null;
//        }
//    }

}