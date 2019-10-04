package com.example.wintertempgranules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity {

    DatabaseReference dataRef;
    Spinner spinner;
    TextView textv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        textv = findViewById(R.id.textView);

        //startAlarm();
        Log.e("onCreate()","xxxxxxxxxxx");
    }



    public void startAlarm(View v){
        Intent intent = new Intent(this, MyBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10000),60000*60*3, pendingIntent);

        Toast.makeText(this, "Alarm Started", Toast.LENGTH_LONG).show();

    }

    public void getData(View v){
      String str = "Winter/" + spinner.getSelectedItem().toString();
      dataRef = FirebaseDatabase.getInstance().getReference(str);

      dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Log.e("DS", dataSnapshot.toString());

              StringBuilder sb = new StringBuilder("Data\n");
              sb.append("Day:\n");
              sb.append(dataSnapshot.child("SeqDay").getValue());
              sb.append("\nNight:\n");
              sb.append(dataSnapshot.child("SeqNight").getValue());

              Float d,n;
              sb.append("\n\nWeek1:\n");
              d = dataSnapshot.child("Week1").child("day").getValue(Float.class);
              n = dataSnapshot.child("Week1").child("night").getValue(Float.class);
              sb.append("day ").append(d).append("   |   night ").append(n);


              sb.append("\nWeek2:\n");
              d = dataSnapshot.child("Week2").child("day").getValue(Float.class);
              n = dataSnapshot.child("Week2").child("night").getValue(Float.class);
              sb.append("day ").append(d).append("   |   night ").append(n);

              sb.append("\nWeek3:\n");
              d = dataSnapshot.child("Week3").child("day").getValue(Float.class);
              n = dataSnapshot.child("Week3").child("night").getValue(Float.class);
              sb.append("day ").append(d).append("   |   night ").append(n);

              sb.append("\nWeek4:\n");
              d = dataSnapshot.child("Week4").child("day").getValue(Float.class);
              n = dataSnapshot.child("Week4").child("night").getValue(Float.class);
              sb.append("day ").append(d).append("   |   night ").append(n);

              textv.setText(sb.toString());
              Log.e("Builder",sb.toString());

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

    }


}
