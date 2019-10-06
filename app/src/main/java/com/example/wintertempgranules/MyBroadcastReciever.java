package com.example.wintertempgranules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyBroadcastReciever extends BroadcastReceiver {

    Context ctx;
    String monthName;
    int day,hr;
    DatabaseReference seqRef,weekRef;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("RECIEVER", "Alarm =========");
        ctx = context;
        //doDateStuff();
        //callWebService();
        Log.e("RECIEVER", "xxxxxxxxx");
    }

    private void doDateStuff() {
        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        day = cal.get(Calendar.DAY_OF_MONTH);
        hr = cal.get(Calendar.HOUR_OF_DAY);
        Log.e("******","Ans: " + monthName);
        Log.e("******","Day: " + day + " | Week: " + day/7);
        Log.e("******","Hour: " + hr);
    }

    private void tempSequence(Float temp){
        Log.e("MAIN", "temp reveived " + temp + "°C");

        if ((hr>7) && (hr<18)){
            String SeqStr = "Winter/" + monthName + "/SeqDay";
            seqRef = FirebaseDatabase.getInstance().getReference(SeqStr);
            if(hr==8){
                seqRef.setValue(String.valueOf(temp));
            }
            else if((hr==11)|| (hr==14)){
                //actionNeeded="readwrite";//read...put , and write after concat
                readWriteSeq(seqRef,false,temp,null,null);
            }
            else if(hr==17){
                if((day/7)<1){readWriteSeq(seqRef,true,temp,"Week1","day");}
                else if((day/7)<2){ readWriteSeq(seqRef,true,temp,"Week2","day");}
                else if((day/7)<3){ readWriteSeq(seqRef,true,temp,"Week3","day");}
                else { readWriteSeq(seqRef,true,temp,"Week4","day");}

            }

        }
        else{
            String SeqStr = "Winter/" + monthName + "/SeqNight";
            seqRef = FirebaseDatabase.getInstance().getReference(SeqStr);
            if(hr==20){
                seqRef.setValue(String.valueOf(temp));
            }
            else if((hr==23)|| (hr==2)){
                //actionNeeded="readwrite";//read...put , and write after concat
                readWriteSeq(seqRef,false,temp,null,null);
            }
            else if(hr==5){
                if((day/7)<1){readWriteSeq(seqRef,true,temp,"Week1","night");}
                else if((day/7)<2){ readWriteSeq(seqRef,true,temp,"Week2","night");}
                else if((day/7)<3){ readWriteSeq(seqRef,true,temp,"Week3","night");}
                else { readWriteSeq(seqRef,true,temp,"Week4","night");}

            }
        }
    }

    private void readWriteSeq(final DatabaseReference seqRef, final boolean lastTemp, final Float temp, final String week, final String time) {

            seqRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String str = dataSnapshot.getValue(String.class);
                    seqRef.setValue(str + "," + temp);
                    if(lastTemp) {
                        String[] splitz = str.split(",");
                        float sum=0.0f;
                        for(String t : splitz){
                            sum += Float.valueOf(t);
                        }
                        sum = sum + temp;
                        float avg = sum/4;
                        weekStuff(week,time,avg);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    private void weekStuff(String week, String time, final Float avg) {
        String str = "Winter/" + monthName + "/" + week  + "/" + time ;
        weekRef = FirebaseDatabase.getInstance().getReference(str);
        weekRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Float f = dataSnapshot.getValue(Float.class);
                    Float ans = (f+avg)/2;

                    DecimalFormat df = new DecimalFormat("#.#");
                    String ansStr = df.format(ans);//decimalFormatter always returns a String

                    weekRef.setValue(Float.valueOf(ansStr));
                }
                else {
                    DecimalFormat df = new DecimalFormat("#.#");
                    String ansStr = df.format(avg);//decimalFormatter always returns a String

                    weekRef.setValue(Float.valueOf(ansStr));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


// we"ll make HTTP request to this URL to retrieve weather conditions
    //"http://api.openweathermap.org/data/2.5/weather?q=vilnius,lt&appid=xxxxxxxxxYourAPI_Keyxxxxxxxxxxxx&units=metric";

    String weatherWebserviceURL = "http://api.openweathermap.org/data/2.5/weather?";
    String param1= "q=";
    String param2= "&appid=2156e2dd5b92590ab69c0ae1b2d24586";
    String param3= "&units=metric";

    JSONObject jsonObj;
    public void callWebService() {
        // make HTTP request to retrieve the weather
        weatherWebserviceURL = weatherWebserviceURL + param1 + "Vilnius" + param2 + param3;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                weatherWebserviceURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    //temperature that is a part of another "MAIN" object also present in the Response(not the main in weather)
                    JSONObject jsonObjectMain = response.getJSONObject("main");
                    String temp = jsonObjectMain.getString("temp");
                    Log.e("MAIN", "temp: " + temp + "°C");

                    //temp = "-24.58";//= -24.6... anything with even .5 is rounded down to the lower value, but in -ve that is the higher val
                    DecimalFormat df = new DecimalFormat("#.#");
                    Float tempVal = Float.valueOf(temp);
                    temp = df.format(tempVal);//decimalFormatter always returns a String
                    tempSequence(Float.valueOf(temp));
                    Log.e("MAIN", "temp: " + temp + "°C");

                    Log.e("XXXXX", "xxxxxxxxxxxxxxxxxx");

                } catch (JSONException e) {
                    Log.e("ErrorJSON: ", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                Log.e("Error: ", error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance(ctx).addToRequestQueue(jsonObjReq);
    }





}
//from pc