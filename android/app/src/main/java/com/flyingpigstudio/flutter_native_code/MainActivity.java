package com.flyingpigstudio.flutter_native_code;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

    // define the CHANNEL with the same name as the one in Flutter
    private static final String CHANNEL = "flyingpigstudio.co.uk/native";
    private static final String EVENT_CHANNEL = "eventChannel";


//    private Handler handler;

//    private int count = 0;


    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private String sensorString;


    private EventChannel.EventSink eventSink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager)
                .registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

    }

    @Override
    protected void onPause() {
        // stops update sensor changes
        mSensorManager.unregisterListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        super.onPause();
    }

    @Override
    protected void onResume() {
        // starts update sensor changes
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // stops update sensor changes
        mSensorManager.unregisterListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        super.onDestroy();
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 1) {
                sensorString = String.valueOf(mAccel);
//                Log.d("Android", "sensorString changed");
                if (eventSink != null) {
                    eventSink.success(sensorString);
                }
            }


        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };



    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        // define the MethodChannel and set a MethodCallHandler
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {


                    if (call.method.equals("powerManage")) {
                        boolean deviceStatus = getDeviceStatus();

                        // conversion of boolean to string to avoid type errors on the Flutter side
                        String myMessage = Boolean.toString(deviceStatus);
                        result.success(myMessage);

                    }

                    if (call.method.equals("takePhoto")) {
                        readyCamera();
                        result.success("Camera Opened");
                    }

                    if (call.method.equals("callNumber")) {
                        callNumber(call.argument("phoneNumber"));
                        result.success("Calling Number");
                    }


                    if (call.method.equals("getBatteryLevel")) {
                        int batteryLevel = getBatteryLevel();

                        if (batteryLevel != -1) {
                            result.success(String.valueOf(batteryLevel));
//                            result.success("Battery Level Passed");
                        } else {
                            result.error("UNAVAILABLE", "Battery level not available.", null);
                        }
                    } else {
                        result.notImplemented();
                    }







                });


        new EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), EVENT_CHANNEL)
                .setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {


                        eventSink = events;


//                        // her 1 saniyede flutter a sensorString gonderiyor
//                        handler = new Handler(message -> {
//                            events.success(sensorString);
//                            handler.sendEmptyMessageDelayed(0, 1000);
//                            return false;
//                        });
//                        handler.sendEmptyMessage(0);





//                        handler = new Handler(message -> {
////                             Then send the number to Flutter
//                            events.success(sensorString);
//                            handler.sendEmptyMessageDelayed(0, 1000);
//                            return false;
//                        });
//                        handler.sendEmptyMessage(0);


//                        // Numbers every second+1
//                        handler = new Handler(message -> {
//                        Log.d("Android", "EventChannel onListen called");
//                            // Then send the number to Flutter
//                            events.success(String.valueOf(++count));
//                            handler.sendEmptyMessageDelayed(0, 1000);
//                            return false;
//                        });
//                        handler.sendEmptyMessage(0);





//                        Log.d("Android", "EventChannel onListen called");
//
//                        final Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //Do something after 100ms
//                                Toast.makeText(MainActivity.this, "Event Stream", Toast.LENGTH_SHORT).show();
//                            }
//                        }, 500);



                    }

                    @Override
                    public void onCancel(Object arguments) {

                        eventSink = null;
//                        handler.removeMessages(0);
//                        handler = null;
//                        count = 0;
                    }
                });
    }


    private boolean getDeviceStatus() {
        boolean deviceStatus = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                deviceStatus = powerManager.isDeviceIdleMode();
            }
        }

        return deviceStatus;

    }

    public void readyCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(intent);
    }

    public void callNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);

    }

    private int getBatteryLevel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            assert batteryManager != null;
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent = new ContextWrapper(getApplicationContext()).
                    registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            assert intent != null;
            return (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
                    intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
    }

}
