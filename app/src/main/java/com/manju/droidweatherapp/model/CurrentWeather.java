package com.manju.droidweatherapp.model;

import com.manju.droidweatherapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by manju on 5/20/15.
 * Model object for the current weather
 */

public class CurrentWeather {
    private String icon;
    private long time;
    private double temperature;
    private double humidity;
    private double precipChance;
    private String summary;


    private String Timezone;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIconId() {
        int iconId = R.mipmap.clear_day;

        if (icon.equals("clear-day")) {
            iconId = R.mipmap.clear_day;
        }
        else if (icon.equals("clear-night")) {
            iconId = R.mipmap.clear_night;
        }
        else if (icon.equals("rain")) {
            iconId = R.mipmap.rain;
        }
        else if (icon.equals("snow")) {
            iconId = R.mipmap.snow;
        }
        else if (icon.equals("sleet")) {
            iconId = R.mipmap.sleet;
        }
        else if (icon.equals("wind")) {
            iconId = R.mipmap.wind;
        }
        else if (icon.equals("fog")) {
            iconId = R.mipmap.fog;
        }
        else if (icon.equals("cloudy")) {
            iconId = R.mipmap.cloudy;
        }
        else if (icon.equals("partly-cloudy-day")) {
            iconId = R.mipmap.partly_cloudy;
        }
        else if (icon.equals("partly-cloudy-night")) {
            iconId = R.mipmap.cloudy_night;
        }

        return iconId;

    }

    public long getTime() {
        return time;
    }

    /**
     * Convert the long time to the format "4:50 pm"
     * @return string formattedTime
     */
    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));
        String timeString = formatter.format(new Date(getTime() * 1000));
        return timeString;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public int getTemperature() {
        return (int) Math.round(temperature);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getPrecipChance() {
        return (int) Math.round(precipChance * 100); //Percentage value
    }

    public void setPrecipChance(double precipChance) {
        this.precipChance = precipChance;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public String getTimezone() {
        return Timezone;
    }

    public void setTimezone(String timezone) {
        Timezone = timezone;
    }
}
