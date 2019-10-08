package com.example.wintertempgranules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String TAG = "Main";

    DatabaseReference dataRef;
    Spinner spinner;
    TextView textv;
    ImageView img1,img2,img3,img4,img11,img12,img13,img14;
    List<Boolean> imgBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        textv = findViewById(R.id.textView);
        img1 = findViewById(R.id.imageView1);
        img2 = findViewById(R.id.imageView2);
        img3 = findViewById(R.id.imageView3);
        img4 = findViewById(R.id.imageView4);
        img11 = findViewById(R.id.imageView11);
        img12 = findViewById(R.id.imageView12);
        img13 = findViewById(R.id.imageView13);
        img14 = findViewById(R.id.imageView14);



        checkTimes();



        //startAlarm();
        Log.e("onCreate()","xxxxxxxxxxx");
    }

    private void checkTimes() {
        DatabaseReference zz = FirebaseDatabase.getInstance().getReference("Winter/zz");
        zz.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=1;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    int x  = ds.getValue(Integer.class);
                    if(x==1){
                      if(i==1){
                         img1.setImageResource(R.drawable.btn_check_buttonless_off);
                      }
                      else if(i==2){
                          img2.setImageResource(R.drawable.btn_check_buttonless_off);
                      }
                      else if(i==3){
                          img3.setImageResource(R.drawable.btn_check_buttonless_off);
                      }
                      else if(i==4){
                          img4.setImageResource(R.drawable.btn_check_buttonless_off);
                      }
                      else if(i==5){
                          img11.setImageResource(R.drawable.btn_check_buttonless_off);
                      }
                      else if(i==6){
                          img12.setImageResource(R.drawable.btn_check_buttonless_off);
                      }
                      else if(i==7){
                          img13.setImageResource(R.drawable.btn_check_buttonless_off);
                      }
                      else if(i==8){
                          img14.setImageResource(R.drawable.btn_check_buttonless_off);
                      }
                    }
                    else if(x==2){
                        if(i==1){
                            img1.setImageResource(R.drawable.btn_check_buttonless_on);
                        }
                        else if(i==2){
                            img2.setImageResource(R.drawable.btn_check_buttonless_on);
                        }
                        else if(i==3){
                            img3.setImageResource(R.drawable.btn_check_buttonless_on);
                        }
                        else if(i==4){
                            img4.setImageResource(R.drawable.btn_check_buttonless_on);
                        }
                        else if(i==5){
                            img11.setImageResource(R.drawable.btn_check_buttonless_on);
                        }
                        else if(i==6){
                            img12.setImageResource(R.drawable.btn_check_buttonless_on);
                        }
                        else if(i==7){
                            img13.setImageResource(R.drawable.btn_check_buttonless_on);
                        }
                        else if(i==8){
                            img14.setImageResource(R.drawable.btn_check_buttonless_on);
                        }
                    }
                    i++;
                    Log.e(TAG, ds.getKey() + ": " + x +" put in Img" + i);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void startAlarm(View v){

        Intent intent = new Intent(this, MyBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10000),60000*60*3, pendingIntent);
        //documents say go with below if its interval repeating and not particular time waking up
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 10000,
                60000*60*3, pendingIntent);

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
//blaaa