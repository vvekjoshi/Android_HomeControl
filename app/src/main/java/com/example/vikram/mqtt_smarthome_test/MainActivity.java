package com.example.vikram.mqtt_smarthome_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

//import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity{

    //public String MQTTHOST ="";
    static String USERNAME= "";
    static String PASSWORD = "";
    public static String topicStr = "hello";
    public static MqttAndroidClient client;
    public static MqttConnectOptions options;

    TextView subText;
    Vibrator vibrator;
    Ringtone myRingtone;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    public String message="";
    private SharedPreferences sharedPreferences;
    private Switch light1;
    private Switch light2;
    private Switch fan1;
    private  Switch fan2;
    private Switch powerall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        subText = (TextView)findViewById(R.id.subText);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(getApplicationContext(),uri);
        light1 = (Switch) findViewById(R.id.switch1);
        light2 = (Switch) findViewById(R.id.switch2);
        fan1 = (Switch) findViewById(R.id.switch3);
        fan2 = (Switch) findViewById(R.id.switch4);
        powerall=(Switch) findViewById(R.id.powerall);



        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(),MainActivity.getbroker(this), clientId);
        options = new MqttConnectOptions();
        //options.setUserName(USERNAME);
        //options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this,"connected",Toast.LENGTH_SHORT).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"connection failed",Toast.LENGTH_SHORT).show();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msgRecieved = new String(message.getPayload());
                if (msgRecieved.equals("l1/1"))
                    light1.setChecked(true);
                else if (msgRecieved.equals("l1/0"))
                    light1.setChecked(false);
                else if (msgRecieved.equals("l2/1"))
                    light2.setChecked(true);
                else if (msgRecieved.equals("l2/0"))
                    light2.setChecked(false);
                else if (msgRecieved.equals("f1/1"))
                    fan1.setChecked(true);
                else if (msgRecieved.equals("f1/0"))
                    fan1.setChecked(false);
                else if (msgRecieved.equals("f2/1"))
                    fan2.setChecked(true);
                else if (msgRecieved.equals("f2/0"))
                    fan2.setChecked(false);

                subText.setText(msgRecieved);
                //myRingtone.play();

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        light1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    message="light1/1";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Light1 is On",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Failed to On",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else{
                    message="light1/0";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Light1 is Off",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Failed to Off",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

            }
        });
        light2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    message="light2/1";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Light2 is On",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Failed to On",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else{
                    message="light2/0";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Light2 is Off",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Failed to Off",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

            }
        });
        fan1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    message="fan1/1";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Fan1 is On",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Failed to On",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else{
                    message="fan1/0";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Fan1 is Off",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Failed to Off",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

            }
        });
        fan2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    message="fan2/1";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Fan2 is On",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Failed to On",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else{
                    message="fan2/0";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Fan2 is Off",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Failed to Off",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

            }
        });
        powerall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    message="fan2/1";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        message="fan1/1";
                        client.publish(topicStr, message.getBytes(), 0, false);
                        message="light1/1";
                        client.publish(topicStr, message.getBytes(), 0, false);
                        message="light1/1";
                        client.publish(topicStr, message.getBytes(), 0, false);
                        light1.setChecked(true);
                        light2.setChecked(true);
                        fan2.setChecked(true);
                        fan1.setChecked(true);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Power On Succeded",Toast.LENGTH_SHORT).show();

                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Power On Failed",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else{
                    message="fan2/0";
                    try {
                        client.publish(topicStr, message.getBytes(), 0, false);
                        message="fan1/0";
                        client.publish(topicStr, message.getBytes(), 0, false);
                        message="light1/0";
                        client.publish(topicStr, message.getBytes(), 0, false);
                        message="light1/0";
                        client.publish(topicStr, message.getBytes(), 0, false);
                        light1.setChecked(false);
                        light2.setChecked(false);
                        fan2.setChecked(false);
                        fan1.setChecked(false);
                        vibrator.vibrate(200);
                        Toast.makeText(MainActivity.this,"Power off Succeeded",Toast.LENGTH_SHORT).show();
                    }catch (MqttException e) {
                        Toast.makeText(MainActivity.this,"Power off failed",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    public static void connect(final Context context){
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(context,"connected",Toast.LENGTH_SHORT).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(context,"connection failed",Toast.LENGTH_SHORT).show();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public static void disconnect(View v,final Context context){
        try {
            IMqttToken token = client.disconnect();//connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(context,"disconnected",Toast.LENGTH_SHORT).show();
                    //setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(context,"disconnection failed",Toast.LENGTH_SHORT).show();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("test", Context.MODE_PRIVATE);
    }

    public static String getbroker(Context context) {
        return getPrefs(context).getString("broker", "tcp://broker.hivemq.com:1883");
    }

    public static void setbroker(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("broker", input);
        editor.commit();
    }
    public void setMQTT(String address){
        message=address;
    }
    public void pub(View v) {
        String topic = topicStr;
        //String payload = "the payload";

        message="hello world!";

        byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            //MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message.getBytes(),0,false);
            Toast.makeText(MainActivity.this,"published",Toast.LENGTH_SHORT).show();
        } //catch (UnsupportedEncodingException | MqttException e)
        catch (MqttException e) {
            Toast.makeText(MainActivity.this,"publish failed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
    private static void setSubscription(){
        try{
            client.subscribe(topicStr,0);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }
    public void conn(View v){
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this,"connected",Toast.LENGTH_SHORT).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"connection failed",Toast.LENGTH_SHORT).show();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void disconn(View v){
        try {
            IMqttToken token = client.disconnect();//connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this,"disconnected",Toast.LENGTH_SHORT).show();
                    //setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"disconnection failed",Toast.LENGTH_SHORT).show();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    private void addDrawerItems() {
        String[] osArray = { "Broker Address" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Broker_Details.class);

                //start the second Activitys
                startActivity(intent);
                //Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Settings");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
            try {
                IMqttToken token = client.disconnect();//connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Toast.makeText(MainActivity.this,"disconnected",Toast.LENGTH_SHORT).show();
                        //setSubscription();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Toast.makeText(MainActivity.this,"disconnection failed",Toast.LENGTH_SHORT).show();


                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

            finish();
            System.exit(0);
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
