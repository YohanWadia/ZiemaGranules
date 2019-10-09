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

    String TAG = "BroadCast";

    Context ctx;
    String monthName;
    int day,hr;

    String[] arrMonth = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    int monthIndex;

    String[] arrWeek = {"Week1","Week2","Week3","Week4"};
    int weekIndex;

    String work;//write... read,write... update

    DatabaseReference seqRef,weekRef, startEndRef;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("RECIEVER", "Alarm =========");
        ctx = context;
        work="";//to avoid null pointer on update
        startEndRef =  FirebaseDatabase.getInstance().getReference("Winter/zz");

        doDateStuff();
        makeCorrectRef();
        callWebService();
        //doWork(1.5f);
        Log.e("RECIEVER", "xxxxxxxxx");
    }


    private void doDateStuff() {
        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        monthIndex = cal.get(Calendar.MONTH);

        day = cal.get(Calendar.DAY_OF_MONTH);
        weekIndex = (day/7>3)? 3 : (day/7) ;

        hr = cal.get(Calendar.HOUR_OF_DAY);
        startEndRef.child(String.valueOf(hr)).setValue(1);


        Log.e("******","Month: " + monthName + "| Index: " + monthIndex  + "| arrMonth: " + arrMonth[monthIndex]);
        Log.e("******","Day: " + day + " | Week: " + day/7 + " | arrWeek: " + arrWeek[weekIndex]);
        Log.e("******","Hour: " + hr);

    }

    private void makeCorrectRef() {
        Log.e(TAG, "makeCorrectRef: ..................");
        String seqStr = null, weekStr=null;

        if ((hr>7) && (hr<18)){
            seqStr = "Winter/" + monthName + "/SeqDay";
            seqRef = FirebaseDatabase.getInstance().getReference(seqStr);

            if(hr==8){
                work="write";
            }
            else if((hr==11)|| (hr==14)){
                work="readwrite";
            }
            else if(hr==17){
                weekStr = "Winter/" + monthName + "/" + arrWeek[weekIndex]  + "/day" ;
                weekRef = FirebaseDatabase.getInstance().getReference(weekStr);
            }
        }//=====day ends

        else{
            seqStr = "Winter/" + monthName + "/SeqNight";
            seqRef = FirebaseDatabase.getInstance().getReference(seqStr);

            if(hr==20){
                work="write";
            }
            else if(hr==23){
                //read...put , and write after concat
                work="readwrite";
            }
            else if(hr==2){
                work="readwrite";
                if(day==1){
                    //this is a new month..& new week.. but night should be of prev this happens at 2am
                    seqStr = "Winter/" + arrMonth[monthIndex-1] + "/SeqNight";
                    seqRef = FirebaseDatabase.getInstance().getReference(seqStr);
                }
            }
            else if(hr==5){
                if(day==1){
                    //this is a new month..& new week.. but night should be of prev this happens at 2am
                    seqStr = "Winter/" + arrMonth[monthIndex-1] + "/SeqNight";//put month 1 back
                    seqRef = FirebaseDatabase.getInstance().getReference(seqStr);

                    weekStr = "Winter/" + arrMonth[monthIndex-1] + "/Week4/night" ;//we can force this to "Week4"
                    weekRef = FirebaseDatabase.getInstance().getReference(weekStr);
                }
                else if((day%7==0) && (day!=28)){
                    weekStr = "Winter/" + monthName + "/" + arrWeek[weekIndex-1] + "/night" ;//put week 1 back.. but on day 28 dont make it WEEK3!
                    weekRef = FirebaseDatabase.getInstance().getReference(weekStr);         //cz week4 continues
                }
                else{
                    weekStr = "Winter/" + monthName + "/" + arrWeek[weekIndex]  + "/night" ;
                    weekRef = FirebaseDatabase.getInstance().getReference(weekStr);
                }
            }
        }

        Log.e(TAG, "SeqRef: " + seqStr);
        Log.e(TAG, "WeekRef: " + weekStr);
    }



    private void doWork(final float temp) {
        if(work.equals("write")){
            seqRef.setValue(String.valueOf(temp));
            startEndRef.child(String.valueOf(hr)).setValue(2);//tick completion for only 8&20, since "write" ends here
        }
        else{
            seqRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String str = dataSnapshot.getValue(String.class);
                    str = str + "," + temp;
                    seqRef.setValue(str);

                    if((hr==17) || (hr==5)) {//this is when you need to do weekstuff after readwrite
                        String[] splitz = str.split(",");
                        float sum=0.0f;
                        for(String t : splitz){
                            sum += Float.valueOf(t);
                        }
                        float avg = sum/4;
                        weekStuff(avg);
                    }
                    else {
                        startEndRef.child(String.valueOf(hr)).setValue(2);//all others completion tick comes here 17&5 is in weekstuff()
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



    }

    private void weekStuff(final Float avg) {
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
            startEndRef.child(String.valueOf(hr)).setValue(2);//put the completion tick
            clearTicks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void clearTicks() {
        if(hr==5){
            startEndRef.child("8").setValue(0);
            startEndRef.child("11").setValue(0);
            startEndRef.child("14").setValue(0);
            startEndRef.child("17").setValue(0);
        }
        else if(hr==17){
            startEndRef.child("20").setValue(0);
            startEndRef.child("23").setValue(0);
            startEndRef.child("2").setValue(0);
            startEndRef.child("5").setValue(0);
        }
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
                    doWork(Float.valueOf(temp));
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