package com.github.pocmo.sensordashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.pocmo.sensordashboard.data.Sensor;
import com.github.pocmo.sensordashboard.database.DataEntry;
import com.github.pocmo.sensordashboard.events.BusProvider;
import com.github.pocmo.sensordashboard.events.NewSensorEvent;
import com.github.pocmo.sensordashboard.events.SensorUpdatedEvent;
import com.github.pocmo.sensordashboard.ui.ExportActivity;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RemoteSensorManager remoteSensorManager;

    Toolbar mToolbar;

    private ViewPager pager;
    private View emptyState;
    private NavigationView mNavigationView;
    private Menu mNavigationViewMenu;
    private List<Node> mNodes;
    private TextView testSHowingVIew;

    private int postCount =0;



    //wenjing
    RequestQueue thisQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testSHowingVIew = (TextView)findViewById(R.id.testResult);
        //get view
//        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
//        emptyState = findViewById(R.id.empty_state);
//
//        mNavigationView = (NavigationView) findViewById(R.id.navView);
//        mNavigationView.setNavigationItemSelectedListener(this);
//        mNavigationViewMenu = mNavigationView.getMenu();

//        initToolbar();
//        initViewPager();

        remoteSensorManager = RemoteSensorManager.getInstance(this);


        thisQueue = Volley.newRequestQueue(this);

        //edittext 毫无意义
//        final EditText tagname = (EditText) findViewById(R.id.tagname);
//
//        findViewById(R.id.tag_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String tagnameText = "EMPTY";
//                if (!tagname.getText().toString().isEmpty()) {
//                    tagnameText = tagname.getText().toString();
//                }
//
//                RemoteSensorManager.getInstance(MainActivity.this).addTag(tagnameText);
//            }
//        });
//
//        tagname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId,
//                                          KeyEvent event) {
//                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                    in.hideSoftInputFromWindow(tagname
//                                    .getApplicationWindowToken(),
//                            InputMethodManager.HIDE_NOT_ALWAYS);
//
//
//                    return true;
//
//                }
//                return false;
//            }
//        });

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //初始化initial bar
//    private void initToolbar() {
//        setSupportActionBar(mToolbar);
//
//        final ActionBar ab = getSupportActionBar();
//        if (ab != null) {
//            ab.setDisplayHomeAsUpEnabled(false);
//            ab.setTitle(R.string.app_name);
//            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.action_about:
//                            startActivity(new Intent(MainActivity.this, AboutActivity.class));
//                            return true;
//                        case R.id.action_export:
//                            startActivity(new Intent(MainActivity.this, ExportActivity.class));
//                            return true;
//                    }
//
//                    return true;
//                }
//            });
//        }
//    }

    //初始化 viewpager 内容
//    private void initViewPager() {
//        pager = (ViewPager) findViewById(R.id.pager);
//
//        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i2) {
//
//            }
//
//            //切换画面筛选sensor
//            @Override
//            public void onPageSelected(int id) {
//                ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) pager.getAdapter();
//                if (adapter != null) {
//                    Sensor sensor = adapter.getItemObject(id);
//                    if (sensor != null) {
//                        remoteSensorManager.filterBySensorId((int) sensor.getId());
//                    }
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //注册busevent
    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        List<Sensor> sensors = RemoteSensorManager.getInstance(this).getSensors();

    for(int i = 0; i< sensors.size();i++){
        Sensor testSensor = sensors.get(i);
        Log.i("wenjingInfoOnResume",""+testSensor.getName()+testSensor.getMaxValue());
    }


        //设置adapter
//        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), sensors));

//        if (sensors.size() > 0) {
//            emptyState.setVisibility(View.GONE);
//        } else {
//            emptyState.setVisibility(View.VISIBLE);
//        }


        remoteSensorManager.startMeasurement();

        for(int i = 0; i< sensors.size();i++){
            Sensor testSensor = sensors.get(i);
            Log.i("wenjingInfoAfterStart",""+testSensor.getName()+testSensor.getMaxValue());
        }


//        mNavigationViewMenu.clear();


        //getnodes 然后回调？但是app中没有显示
//        remoteSensorManager.getNodes(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
//            @Override
//            public void onResult(final NodeApi.GetConnectedNodesResult pGetConnectedNodesResult) {
//                mNodes = pGetConnectedNodesResult.getNodes();
//                for (Node node : mNodes) {
//                    SubMenu menu = mNavigationViewMenu.addSubMenu(node.getDisplayName());
//
//                    MenuItem item = menu.add("15 sensors");
//                    if (node.getDisplayName().startsWith("G")) {
//                        item.setChecked(true);
//                        item.setCheckable(true);
//                    } else {
//                        item.setChecked(false);
//                        item.setCheckable(false);
//                    }
//                }
//            }
//        });
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

    @Override
    public boolean onNavigationItemSelected(final MenuItem pMenuItem) {
        Toast.makeText(this, "Device: " + pMenuItem.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }

//    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
//        private List<Sensor> sensors;
//
//        public ScreenSlidePagerAdapter(FragmentManager fm, List<Sensor> symbols) {
//            super(fm);
//            this.sensors = symbols;
//        }
//
//
//        public void addNewSensor(Sensor sensor) {
//            this.sensors.add(sensor);
//        }
//
//
//        private Sensor getItemObject(int position) {
//            return sensors.get(position);
//        }
//
//        @Override
//        public android.support.v4.app.Fragment getItem(int position) {
//            return SensorFragment.newInstance(sensors.get(position).getId());
//        }
//
//        @Override
//        public int getCount() {
//            return sensors.size();
//        }
//
//    }



    private void TestPost(final SensorUpdatedEvent event) {

        String postUrl = "http://129.63.16.123:8080/yytest/insert";



        StringRequest stringRequest = new StringRequest(Request.Method.POST, postUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("wenjingyang", s);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("wenjingyang", volleyError.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", "wenjingYangID");
                map.put("coll", "wenjingTest");

                JSONObject abc = new JSONObject();

                try {
                    abc.put("sensor name", event.getSensor().getName());
                    abc.put("thisPointTimeStamp", event.getDataPoint().getTimestamp());

                    float[] thisValue = event.getDataPoint().getValues();
                    JSONArray mJSONArray = new JSONArray();

                    for(int i = 0; i< thisValue.length;i++){
                        mJSONArray.put(thisValue[i]);
                    }
                    //JSONArray mJSONArray = new JSONArray(Arrays.asList(event.getDataPoint().getValues()));


                    Log.i("wenjingTestOutPutmJSONArray", "" + mJSONArray.toString());
                    abc.put("thisPointValue",mJSONArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                map.put("val",abc.toString());

                Log.i("wenjingTestOutPut", "" + abc.toString());
//                Log.i("wenjingTestOutPut", "" + map.toString());

                return map;
            }
        };
        thisQueue.add(stringRequest);


    }



    private void notifyUSerForNewSensor(Sensor sensor) {
        Toast.makeText(this, "New Sensor!\n" + sensor.getName(), Toast.LENGTH_SHORT).show();
    }


    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        Log.i("wenjingInfoGetEvent", "" + event.getSensor().getName()+event.getSensor().getMaxValue());

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
//        if(postCount == 6){
            TestPost(event);
//            postCount = 0 ;
//        }
//        postCount++;

        testSHowingVIew.setText(event.getSensor().getName());
    }




}
