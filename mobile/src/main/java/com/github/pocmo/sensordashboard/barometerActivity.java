package com.github.pocmo.sensordashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pocmo.sensordashboard.events.BusProvider;
import com.github.pocmo.sensordashboard.events.NewSensorEvent;
import com.github.pocmo.sensordashboard.events.SensorUpdatedEvent;
import com.github.pocmo.sensordashboard.wenjinghelp.PressureSildingWindow;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

import io.realm.internal.Util;

public class barometerActivity extends AppCompatActivity {

    private RemoteSensorManager remoteSensorManager;


    private String logTag ="wenjingyang";


    private Intent pressureIntent;
    public static final double SEALEVEL_PRESSURE_DEFAULT = 1013.25;
    private static final boolean up = true;
    private static final boolean down = false;
//    private double altitude;
    private double currentPressure = 0.0;
    private boolean ifrecordStep =false;
    private double targetPressure = 0;

    private double heightPerPressure = 8.3;
    private int stepHeight = 20;
    private int predictStep = 0;
    private double thredhold =0.12;
    private boolean upOrDown = up;

    private TextView currentPressureView;
//    private TextView targetPressureView;
    private TextView predictUpOrDown;
    private TextView predictCount;

//    private Button beginRecordStep;

    private PressureSildingWindow thisWindows;
    private double largestPressure;
    private double smallestPressure;
    private double averagePressure;

    private boolean ifFirstRecord = true;
    private double firstPressure = 0;
    private double topPressure = 1050;

    private void initView() {
        currentPressureView = (TextView)findViewById(R.id.tv_currentPressure);
//        targetPressureView = (TextView)findViewById(R.id.tv_recordPressure);
        predictUpOrDown= (TextView)findViewById(R.id.tv_predictUPorDown);
        predictCount = (TextView)findViewById(R.id.tv_predictStepCounter);
//        beginRecordStep = (Button)findViewById(R.id.button_stepRecord);



    }


//    public void recordCurrentPressure(View view){
//
//        targetPressure = currentPressure;
//        targetPressureView.setText("记录气压:"+String.format("%.2f", targetPressure));
//    }


//    public void beginRecordStep(View view){
//
//        if(targetPressure == 0){
//            Toast.makeText(this,"请设置气压",Toast.LENGTH_SHORT).show();
//        }else {
//
//            if (!ifrecordStep) {
//                ifrecordStep = true;
//                beginRecordStep.setText("停止记步");
//
//            } else {
//                ifrecordStep = false;
//                beginRecordStep.setText("开始记步");
//
//            }
//        }
//    }


    private BroadcastReceiver pressureBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentPressure = intent.getDoubleExtra(PressureService.BROADCAST_EXTRA_PRESSURE, 0.0);


            if(ifFirstRecord){
                firstPressure = currentPressure;
                ifFirstRecord = false;
            }

            if(currentPressure< topPressure){
                topPressure = currentPressure;
            }


            Log.i(logTag, "currentPressure" + currentPressure);
//            altitude = Util.getCalculatedAltitude(currentPressure, sealevelPressure);
//
//            if (altitudeFragment != null)
//                altitudeFragment.onCurrentPressure(currentPressure);
//            if (floorFragment != null)
//                floorFragment.onCurrentPressure(currentPressure);


//            currentPressureView.setText("当前气压:" + "记录气压:" + String.format("%.2f", currentPressure));


            maintainWindows(currentPressure);


        }
    };
    private void maintainWindows(double comingPressureData) {

        thisWindows.addNumber(comingPressureData);
        largestPressure = thisWindows.getLargestOne();
        averagePressure = thisWindows.getArrayAverage();
        smallestPressure = thisWindows.getSmallestOne();

//        Log.i(logTag,"largetst "+ largestPressure +"averagePre"+averagePressure+"small"+smallestPressure);

    }


    @Override
    protected void onResume() {
        super.onResume();

        startService(pressureIntent);
        registerReceiver(pressureBroadcastReceiver, new IntentFilter(PressureService.BROADCAST_PRESSURE_ACTION));

        BusProvider.getInstance().register(this);
        remoteSensorManager.startMeasurement();


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(pressureIntent);
        unregisterReceiver(pressureBroadcastReceiver);
        BusProvider.getInstance().unregister(this);

        remoteSensorManager.stopMeasurement();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barometer);

        pressureIntent = new Intent(PressureService.SERVICE_PRESSURE_ACTION);
        pressureIntent.setClass(this, PressureService.class);

        remoteSensorManager = RemoteSensorManager.getInstance(this);

        thisWindows = new PressureSildingWindow();

        initView();

    }



    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        Log.i("wenjingInfoGetEvent", "" + event.getSensor().getName() + event.getSensor().getMaxValue());

//        ((ScreenSlidePagerAdapter) pager.getAdapter()).addNewSensor(event.getSensor());
//        pager.getAdapter().notifyDataSetChanged();
//        emptyState.setVisibility(View.GONE);
//        notifyUSerForNewSensor(event.getSensor());
    }

    @Subscribe
    public void onSensorUpdatedEvent(SensorUpdatedEvent event) {
        Log.i("wenjingInfoonSensorUpdatedEvent", "sensor name" + event.getSensor().getName() + "sensor maxVaule" + event.getSensor().getMaxValue()
                        + "sensor minValue" + event.getSensor().getMinValue() + "thisPointTimeStamp" + event.getDataPoint().getTimestamp() + "thisPointAcuracy" +
                        event.getDataPoint().getAccuracy() + "thisPointValue" + Arrays.toString(event.getDataPoint().getValues())
        );
//        if(event.getSensor().getName().contains("Step Detector")&&ifrecordStep){
        if(event.getSensor().getName().contains("Step")){

                Log.i(logTag, "i am counting and refresh the step number");

            doPredict();

        }
    }





    private void doPredict() {

        if(fullfilledJuge()) {
            predictStep = (int) ((averagePressure - currentPressure) / heightPerPressure * 10000 / stepHeight);
            if (predictStep > 0) {
                upOrDown = up;
                predictUpOrDown.setText("上楼ing");


                predictStep = (int) ((averagePressure - firstPressure) / heightPerPressure * 10000 / stepHeight)*(-1);


                predictCount.setText("" + predictStep);


            } else if (predictStep < 0) {
                upOrDown = down;
                predictUpOrDown.setText("下楼ing");


                predictStep = (int) ((averagePressure - topPressure) / heightPerPressure * 10000 / stepHeight);


                predictCount.setText("" + predictStep);
            }
        }else{
            predictUpOrDown.setText("上下楼:");
            predictCount.setText("0");

        }
    }

    private boolean fullfilledJuge() {

        Log.i(logTag,"largetst "+ Math.abs(currentPressure-averagePressure) +"averagePre"+Math.abs(currentPressure-averagePressure)+"small"+Math.abs(currentPressure-smallestPressure));

        if((Math.abs(currentPressure-averagePressure)>thredhold&&Math.abs(currentPressure-largestPressure)>thredhold)
                ||(Math.abs(currentPressure-smallestPressure)>thredhold&&Math.abs(currentPressure-averagePressure)>thredhold)){
            Log.i(logTag,"that will be true");
            return true;
        }
        Log.i(logTag,"that will be false");

        return false;
    }

}
