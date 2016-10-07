package com.android.gis.huapp;

/**
 * Created by Ahmed on 5/30/2016.
 */


        import android.app.AlertDialog;
        import android.app.Service;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.provider.Settings;
        import android.support.annotation.Nullable;

/**
 * Created by Ibra on 4/28/2016.
 */
public class GPS_tracker extends Service implements LocationListener{

    private final Context context;
    boolean is_gps_enable=false;
    boolean can_get_location=false;
    boolean is_network_enable=false;
    Location location;

    double latitude;
    double longitude;
    private static final long min_update_distance=100;
    private static final long min_update_time=1000*60*1;

    protected LocationManager locationManager;


    public GPS_tracker(Context context) {
        this.context = context;
        getlocation();
    }

    public Location getlocation()
    {
        try {

            locationManager=(LocationManager)context.getSystemService(LOCATION_SERVICE);
            is_gps_enable=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            is_network_enable=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!is_network_enable && !is_gps_enable)
            {}
            else
            {
                can_get_location=true;
                if (is_network_enable) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                            , min_update_time, min_update_distance, this);


                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();


                        }

                    }

                }
                if(is_gps_enable)
                {
                    if (location==null)
                    {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                                , min_update_time, min_update_distance, this);
                        if(locationManager!=null)
                        {

                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();


                            }


                        }


                    }
                }

            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return location;



    }
    public void stop_using_gps()
    {
        if (locationManager!=null)
        {

            // locationManager.removeUpdates(GPS_tracker.this);
        }

    }

    public double get_latitude()
    {
        if(location!=null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double get_longitude()
    {
        if(location!=null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }


    public boolean Can_get_location()
    {

        return can_get_location;
    }

    public void show_settings()
    {
        AlertDialog.Builder alert=new AlertDialog.Builder(context);

        alert.setTitle("GPS settings");

        alert.setMessage("GPS is not enabled");

        alert.setPositiveButton("settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alert.show();









    }






    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
