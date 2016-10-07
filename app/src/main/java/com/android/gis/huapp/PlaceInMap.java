package com.android.gis.huapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class PlaceInMap extends FragmentActivity implements
        OnMapReadyCallback   {

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    static Marker marker;
    private UiSettings mUiSettings = null;
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private boolean mUserSawDrawer = false;
    private GoogleMap mMap;
    String SearchData = "";
    String Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_in_map);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String name =getIntent().getStringExtra("name").toString();
        BackGroundShowMap backGroundShowMap=new BackGroundShowMap();
        backGroundShowMap.execute(name);

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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng helwanUNI = new LatLng(29.868728, 31.315409);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(helwanUNI));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(helwanUNI, 15));


    }

    public class BackGroundShowMap extends AsyncTask<String, Void, String> {
        public BackGroundShowMap() {
            super();
        }

        @Override
        protected String doInBackground(String... params) {
            String ShowMap_url = "http://hugis2.esy.es/ShowMapPlace.php";

            try {
                Location = params[0];

                URL url = new URL(ShowMap_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("locationName", "UTF-8") + "=" + URLEncoder.encode(Location, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                SearchData = "";
                while ((line = bufferedReader.readLine()) != null) {

                    SearchData += line;


                }
                Log.d("GGGGGGg", "" + SearchData);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return SearchData;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Result", result);
            if (result.contains("something")) {
                Toast.makeText(getApplication(), "place not inDB", Toast.LENGTH_LONG).show();


            } else

            {

                mMap.clear();
                double lantitue;
                double langtute;
                String[] LatLgTute = result.split("_");
                lantitue = Double.parseDouble(LatLgTute[0]);
                langtute = Double.parseDouble(LatLgTute[1]);
                LatLng latLng = new LatLng(lantitue, langtute);
                mMap.addMarker(new MarkerOptions().position(latLng).title(Location).draggable(true).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));


            }

        }


    }



}
