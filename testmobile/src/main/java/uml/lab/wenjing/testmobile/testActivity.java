package uml.lab.wenjing.testmobile;

import android.app.Activity;

import android.os.Bundle;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.List;

import uml.lab.wenjing.testmobile.data.Sensor;
import uml.lab.wenjing.testmobile.events.BusProvider;
import uml.lab.wenjing.testmobile.events.NewSensorEvent;
import uml.lab.wenjing.testmobile.events.SensorUpdatedEvent;


public class testActivity extends Activity {


    private RemoteSensorManager remoteSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        remoteSensorManager = RemoteSensorManager.getInstance(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        List<Sensor> sensors = RemoteSensorManager.getInstance(this).getSensors();

        remoteSensorManager.startMeasurement();

        for(int i = 0; i< sensors.size();i++){
            Sensor testSensor = sensors.get(i);
            Log.i("wenjingInfoAfterGetNode",""+testSensor.getName()+testSensor.getMaxValue());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);

        remoteSensorManager.stopMeasurement();
    }

    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        Log.i("wenjingInfoGetEvent", "" + event.getSensor().getName() + event.getSensor().getMaxValue());


    }

    @Subscribe
    public void onSensorUpdatedEvent(SensorUpdatedEvent event) {
        Log.i("wenjingInfoonSensorUpdatedEvent", "sensor name" + event.getSensor().getName() + "sensor maxVaule" + event.getSensor().getMaxValue()
                        + "sensor minValue" + event.getSensor().getMinValue() + "thisPointTimeStamp" + event.getDataPoint().getTimestamp() + "thisPointAcuracy" +
                        event.getDataPoint().getAccuracy() + "thisPointValue" + Arrays.toString(event.getDataPoint().getValues())
        );


    }

}
