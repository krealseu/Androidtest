package org.kreal.senser;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;
    MediaRecorder mediaRecorde;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView s=(TextView) findViewById(R.id.wode);
         sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
         Sensor sensor=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        s.setText("dfg" + sensor.getName() + sensor.getVendor());
         sensorEventListener=new SensorEventListener() {
            Long lastTime= Long.valueOf(0);
            @Override
            public void onSensorChanged(SensorEvent event) {
                Long t=System.currentTimeMillis();
                if((t-lastTime)>100) {
                    Log.i("asd", "s" + event.values[SensorManager.AXIS_X]);
                    lastTime=t;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        mediaRecorde=new MediaRecorder();
//        String file= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"sdf.mp3";
//        File file1=new File(file);
//        mediaRecorde.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        mediaRecorde.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        mediaRecorde.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        mediaRecorde.setOutputFile(file);
//        Log.i("saa", file);
//
//        try {
//            file1.createNewFile();
//            mediaRecorde.prepare();
//            Log.i("sa","sdasdsad");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaRecorde.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
//        mediaRecorde.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
