package com.example.vikram.mqtt_smarthome_test;

/**
 * Created by vikram on 10/6/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Broker_Details extends MainActivity{
    Button mButton;
    EditText mEdit;
    Context mContext;
    Button connect;
    Button Disconnect;
    Vibrator vibrator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_broker);
        mButton = (Button)findViewById(R.id.save);
        connect = (Button)findViewById(R.id.connect);
        Disconnect =(Button)findViewById(R.id.disconnect);
        mEdit   = (EditText)findViewById(R.id.editText2);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        mContext = this;
        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String recived=mEdit.getText().toString();
                        MainActivity.setbroker(mContext,recived);
                        vibrator.vibrate(100);
                        Toast.makeText(Broker_Details.this,"Address Saved",Toast.LENGTH_SHORT).show();
                        Toast.makeText(Broker_Details.this,"Please Restart App Now",Toast.LENGTH_LONG).show();
                        //subText.setText(new String(topicStr));
                        //Log.v("EditText", mEdit.getText().toString());
                    }
                });
        connect.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {

                        MainActivity.connect(mContext);
                        //subText.setText(new String(topicStr));
                        //Log.v("EditText", mEdit.getText().toString());
                    }
                });
        Disconnect.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {

                        MainActivity.disconnect(view,mContext);
                        //subText.setText(new String(topicStr));
                        //Log.v("EditText", mEdit.getText().toString());
                    }
                });
    }

}
