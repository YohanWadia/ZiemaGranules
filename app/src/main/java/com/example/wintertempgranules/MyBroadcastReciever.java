package com.example.wintertempgranules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("RECIEVER", intent.getAction() + "=========");

        if(intent.getAction() == null){
            Toast.makeText(context, "Alarm!!!", Toast.LENGTH_LONG).show();
            Log.e("RECIEVER","getAction = null... means this broacast is frm the ALARM");
        }
        else{
            Toast.makeText(context, "Self BroadCast Received!", Toast.LENGTH_LONG).show();
            Log.e("RECIEVER","info string: " + intent.getStringExtra("info") );
        }



    }
}
//from pc