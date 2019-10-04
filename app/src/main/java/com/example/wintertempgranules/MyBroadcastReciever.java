package com.example.wintertempgranules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyBroadcastReciever extends BroadcastReceiver {

    Context ctx;

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
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        Log.e("******","Ans: " + monthName);
        Log.e("******","Day: " + day + " | Week: " + day/7);
        Log.e("******","Hour: " + hr);

        if ((hr>6) && (hr<19)){
            if(hr==7){

                //write DaySequence directly cz it is a new day
            }
            else if(hr==10){

                //read DaySequence and concatenate new temp with ,

            }
            else if(hr==13){

                //read DaySequence and concatenate new temp with ,
            }
            else if(hr==16){
                //read DaySequence and concatenate new temp ... but no ,
                //split & divide by 4
                if((day/7)<=1){ weekStuff("Week1","day");}
                else if((day/7)<=2){ weekStuff("Week2","day");}
                else if((day/7)<=3){ weekStuff("Week3","day");}
                else { weekStuff("Week4","day");}
            }

        }
        else{
            if(hr==19){
                //make temp DaySequence= null
            }
        }
    }

    private void weekStuff(String week, String when) {
        //make ref with week/when and read value
        //keep if val exists.. add to present temp & div by 2 to get avg.
        //then save back to that path

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
                    temp = df.format(tempVal);
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