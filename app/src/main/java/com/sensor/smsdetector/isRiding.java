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
    boolean mulaiSensor = false;

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        File sdCard = Environment.getExternalStorageDirectory();
        //File dir = new File (sdCard.getAbsolutePath() + "");
        //dir.mkdirs();
        //File file = new File(dir, "filename");

        //FileOutputStream f = new FileOutputStream(file);

        CSVWriter writer = null;

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light = event.values[0];

            String txt = "Light : " + light;

            lightT.setText(Html.fromHtml(txt));

            if (light < 22.93) {
                lightStatus = true;
            } else {
                lightStatus = false;
            }

        }

        if(mulaiSensor == true) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                float xVal = event.values[0];
                float yVal = event.values[1];
                float zVal = event.values[2];

                sx = "X Value : <font color = '#800080'> " + xVal + "</font>";
                sy = "Y Value : <font color = '#800080'> " + yVal + "</font>";
                sz = "Z Value : <font color = '#800080'> " + zVal + "</font>";

                x.setText(Html.fromHtml(sx));
                y.setText(Html.fromHtml(sy));
                z.setText(Html.fromHtml(sz));
                String coba = xVal+"#"+yVal+"#"+zVal+"#"+light;

                if((Math.abs(xVal-tmpX)>0.5 || Math.abs(yVal-tmpY)>0.5 || Math.abs(zVal-tmpZ)>0.5) && light < 25) {
                    try {
                        writer = new CSVWriter(new FileWriter(sdCard.getAbsolutePath() + "/" + fileName + ".csv", true), ',');
                        String[] entries = coba.split("#"); // array of your values
                        writer.writeNext(entries);
                        writer.close();
                        tmpX = xVal;
                        tmpY = yVal;
                        tmpZ = zVal;
                    } catch (IOException e) {
                        //error
                    }
                }
            }
        }

    }
}
