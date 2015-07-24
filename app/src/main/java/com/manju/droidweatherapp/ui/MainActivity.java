package com.manju.droidweatherapp.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.manju.droidweatherapp.R;
import com.manju.droidweatherapp.model.CurrentWeather;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather currentWeather;

    @InjectView(R.id.temperatureLabel) TextView temperatureLabel;
    @InjectView(R.id.timeLabel) TextView timeLabel;
    @InjectView(R.id.humidityValue) TextView humidityValue;
    @InjectView(R.id.precipValue) TextView precipValue;
    @InjectView(R.id.summaryLabel) TextView summaryLabel;
    @InjectView(R.id.iconImageView) ImageView iconImageView;
    @InjectView(R.id.refreshImageView) ImageView refreshImageView;
    @InjectView(R.id.progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);   //Generate code

        progressBar.setVisibility(View.INVISIBLE);

        getForecast(37.8267, -122.423);

    }

    @OnClick(R.id.refreshImageView)
    void updateForecast() {
        getForecast(37.8267, -122.423);
    }

    /**
     * Fetch the forecast and update the UI
     */
    private void getForecast(double latitude, double longitude) {


        String apiKey = "****";

        String forecastUrl = new StringBuilder("https://api.forecast.io/forecast/")
                .append(apiKey)
                .append("/")
                .append(latitude)
                .append(",")
                .append(longitude).toString();

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String responseData = response.body().string();
                        Log.v(TAG, responseData);
                        if (response.isSuccessful()) {
                            currentWeather = getCurrentWeatherDetails(responseData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception occurred!", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception occurred!", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show/hide the refresh button or the progress bar (spinner)
     */
    private void toggleRefresh() {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
            refreshImageView.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            refreshImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Plug in the values to the view
     */
    private void updateDisplay() {
        temperatureLabel.setText(Integer.toString(currentWeather.getTemperature()));
        timeLabel.setText(new StringBuilder("At ")
                .append(currentWeather.getFormattedTime())
                .append(" it will be")
                .toString());
        humidityValue.setText(Double.toString(currentWeather.getHumidity()));
        precipValue.setText(Integer.toString(currentWeather.getPrecipChance()) + " %");
        summaryLabel.setText(currentWeather.getSummary());
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), currentWeather.getIconId(), null);
        iconImageView.setImageDrawable(drawable);
    }

    /**
     * Alert the user of errors by displaying a dialog
     */
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    /**
     * Returns whether the network is available and connected
     * @return boolean isNetworkAvailable
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (info != null && info.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    /**
     *
     * Parses the weather json into the model object & returns it
     * @param weatherJson json object as string
     * @return CurrentWeather model weather
     * @throws JSONException
     */
    public CurrentWeather getCurrentWeatherDetails(String weatherJson) throws JSONException {
        //Complete json response
        JSONObject json = new JSONObject(weatherJson);
        //Current weather json
        JSONObject currentWeatherJson = json.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currentWeatherJson.getDouble("humidity"));
        currentWeather.setPrecipChance(currentWeatherJson.getDouble("precipProbability"));
        currentWeather.setTemperature(currentWeatherJson.getDouble("temperature"));
        currentWeather.setTime(currentWeatherJson.getLong("time"));
        currentWeather.setIcon(currentWeatherJson.getString("icon"));
        currentWeather.setSummary(currentWeatherJson.getString("summary"));
        currentWeather.setTimezone(json.getString("timezone"));

        return currentWeather;
    }
}
