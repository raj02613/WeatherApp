package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationListenerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;


import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    MaterialTextView xAxis, yAxis, temp, city;
    LocationManager locationMgr;
    JSONObject jsonObj;
    JSONArray jsonArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xAxis = findViewById(R.id.xAxis);
        yAxis = findViewById(R.id.yAxis);
        temp = findViewById(R.id.temp);
        city = findViewById(R.id.city);

        locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);



    }

    boolean finallyExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if(finallyExit){
                        finish();
                    }
                    else{
                        finallyExit = true;
                        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finallyExit =false;
                            }
                        },2000);
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

        double lat = location.getLatitude();
        double longi = location.getLongitude();

        xAxis.setText(lat+"");
        yAxis.setText(longi+"");


        OkHttpClient client = new OkHttpClient();

        Request locationRequest = new Request.Builder()
                .url("http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=NqkXpLA0BKzggavvoFNlQmi91GbkwX4u&q="+lat+"%2C"+longi)
                .build();

        client.newCall(locationRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){

                    String LocationAPIresp = response.body().string();

                    Log.i("consoleLog",LocationAPIresp);
                    try {
                        jsonObj = new JSONObject(LocationAPIresp);

                        String locationID = jsonObj.getString("Key");

                        String location = jsonObj.getString("LocalizedName");

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                city.setText(location);
                                getWeatherFromlocationId(locationID);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

            }
        });


        //Weather API Request and Calls






    }

    private void getWeatherFromlocationId(String locationID) {
        OkHttpClient client = new OkHttpClient();

        Log.i("KeyAvailable",locationID);

        Request request = new Request.Builder()
                .url("http://dataservice.accuweather.com/currentconditions/v1/"+locationID+"?apikey=NqkXpLA0BKzggavvoFNlQmi91GbkwX4u")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.i("Failure","Failure");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful()){

                    Log.i("Response","Response Succesfull");
                    String apiResponse1 = response.body().string();
                    Log.i("KeyAvailable",apiResponse1);
                    try {

                        jsonArray = new JSONArray(apiResponse1);

                        Double tempC = jsonArray.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value");

                        Log.i("TempC", String.valueOf(tempC));

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                temp.setText(String.valueOf(tempC));

                                Log.i("TAG",apiResponse1);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            }

        });
    }
}