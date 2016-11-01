package com.sensor.smsdetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class isRiding extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;

    private boolean lightStatus;
    private String fileName;
    public boolean mulaiSensor = false;

    private float tmpX;
    private float tmpY;
    private float tmpZ;

    float light = 100000;

    TextView x;
    TextView y;
    TextView z;

    TextView lightT;

    String sx, sy, sz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor
                (Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor
                (Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light = event.values[0];

            String txt = "Light : " + light;

            if (light < 25) {
                mulaiSensor = true;
            } else {
                mulaiSensor = false;
            }
        }
    }
}
