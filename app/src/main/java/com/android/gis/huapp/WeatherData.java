package com.android.gis.huapp;

/**
 * Created by spider on 01/07/2016.
 */
public class WeatherData {

    private String dateTime;
    private  int humidity;
    private  double pressure;
    private String  highandLow;
    private String description;
    private int weatherId;

    public String getHighandLow() {
        return highandLow;
    }

    public void setHighandLow(String highandLow) {
        this.highandLow = highandLow;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }
}
