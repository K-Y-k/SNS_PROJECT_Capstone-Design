package com.example.sns_project.arduino;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ArduinoSensor {
    @SuppressLint("StaticFieldLeak")
    private TextView arduino_temp;
    @SuppressLint("StaticFieldLeak")
    private TextView arduino_airHumidity;
    @SuppressLint("StaticFieldLeak")
    private TextView arduino_soilHumidity;
    @SuppressLint("StaticFieldLeak")
    private TextView arduino_bright;

    public ArduinoSensor() {
    }
    
    public void setArduinoSesor(TextView arduino_temp, TextView arduino_airHumidity, TextView arduino_soilHumidity, TextView arduino_bright) {
        this.arduino_temp = arduino_temp;
        this.arduino_airHumidity = arduino_airHumidity;
        this.arduino_soilHumidity = arduino_soilHumidity;
        this.arduino_bright = arduino_bright;
    }


    /**
     * 아두이노 센서 값 접근 및 적용 메서드
     */
    public void AccessSensor() {
        DatabaseReference arduino_info = FirebaseDatabase.getInstance().getReference();  // 리얼 타임 데이터베이스 객체 생성

        Log.i("arduinoSensor_info", "arduino_temp: " + arduino_temp);
        Log.i("arduinoSensor_info", "arduino_airHumidity: " + arduino_airHumidity);
        Log.i("arduinoSensor_info", "arduino_soilHumidity: " + arduino_soilHumidity);
        Log.i("arduinoSensor_info", "arduino_bright: " + arduino_bright);
        
        arduino_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                String temp = datasnapshot.child("temp").getValue().toString();
                String airHumidity = datasnapshot.child("airHumiValue").getValue().toString();
                String soilHumidity = datasnapshot.child("soilHumiValue").getValue().toString();
                String bright = datasnapshot.child("cdsValue").getValue().toString();

                arduino_temp.setText(temp);
                arduino_airHumidity.setText(airHumidity);
                arduino_soilHumidity.setText(soilHumidity);
                arduino_bright.setText(bright);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
