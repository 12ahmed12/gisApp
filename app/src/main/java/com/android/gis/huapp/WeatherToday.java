package com.android.gis.huapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WeatherToday extends AppCompatActivity implements NotificationAdapter.ClickListener,NavigationView.OnNavigationItemSelectedListener {
    private final String LOG_TAG = WeatherToday.class.getSimpleName();
    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    // Need this to link with the Snackbar
    private CoordinatorLayout mCoordinator;
    //Need this to set the title of the app bar
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private LinearLayout mRoot;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mUserSawDrawer = false;
    private int mSelectedId;
    private ArrayAdapter<String> mForecastAdapter;
    private final Context mContext=WeatherToday.this;


    WeatherAdapter mWeatherAdapter;
    ArrayList<WeatherData> mWeatherData;
    RecyclerView mWeatherRecycle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_today);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Univeristy Weather");

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


        mWeatherData=new ArrayList<>();
        mWeatherRecycle = (RecyclerView) findViewById(R.id.listWeather);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if (!isNetworkConnected()) {
            new AlertDialog.Builder(WeatherToday.this)
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
            // WeatherAdapter.setClickListener(this);
            getWeatherTask task=new getWeatherTask(getApplicationContext());
            task.execute();
        }



        mWeatherAdapter=new WeatherAdapter(getApplicationContext(),mWeatherData);
       // mWeatherAdapter.setClickListener((WeatherAdapter.ClickListener) this);
    }

    @Override
    public void itemClicked(View view, int position) {
        Toast.makeText(getApplicationContext(),"clicking",Toast.LENGTH_LONG).show();
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }



    public class getWeatherTask extends AsyncTask<String ,Void,String>
    {
        Context ctx;
        ProgressDialog progressDialog;
        getWeatherTask(Context ctx)
        {
            this.ctx=ctx;
        }
        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(WeatherToday.this, "Downloading News","Loading", false,true);

            super.onPreExecute();
            mWeatherData.clear();
        }



        @Override
        protected String doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
         /*   if (params.length == 0) {
                return null;
            }
            String locationQuery = params[0];*/

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;
            String appid="3dd8b5d5c9561eb47b7c34893b556f02";
            String result=null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, "helwan")
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM,appid)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d("url",""+url);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    result="empty";
                    //return result;
                }else {
                    forecastJsonStr = buffer.toString();

                    result = "done";
                    Log.d("resultdata", forecastJsonStr);
                    getWeatherDataFromJson(forecastJsonStr);
                   // return "sucess";
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return result;
        }
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        private void getWeatherDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_PRESSURE = "pressure";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_DESCRIPTION = "description";
            WeatherData item;
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
            // now we work exclusively in UTC
            dayTime = new Time();

            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;
                double pressure;
                int humidity;
                item=new WeatherData();
                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);
                Log.d("DAY",day);
                pressure = dayForecast.getDouble(OWM_PRESSURE);
                humidity = dayForecast.getInt(OWM_HUMIDITY);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                Log.d("HighAndLow", highAndLow);
                item.setDateTime(day);
                item.setDescription(description);
                item.setHighandLow(highAndLow);
                item.setHumidity(humidity);
                item.setPressure(pressure);
                mWeatherData.add(item);

            }

            for (WeatherData s : mWeatherData) {
                Log.v(LOG_TAG, "Forecast low and high: "+s.getHighandLow());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result!=null)
            {


            Log.d("resultt",result);
            progressDialog.dismiss();
                   for (WeatherData d:mWeatherData) {
                       Log.d("tempHighlow",""+d.getHighandLow());
                   }


            mWeatherAdapter=new WeatherAdapter(ctx,mWeatherData);
            mWeatherRecycle.setAdapter(mWeatherAdapter);

             LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ctx);
                    linearLayoutManager.setReverseLayout(false);
                    linearLayoutManager.setStackFromEnd(false);
                    mWeatherRecycle.setLayoutManager(linearLayoutManager);
            mWeatherAdapter.notifyDataSetChanged();

             }else
            {
                Toast.makeText(ctx,"Something Wrong",Toast.LENGTH_LONG).show();
            }
        }
       }





}
