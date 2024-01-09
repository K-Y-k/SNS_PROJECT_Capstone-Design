package com.example.sns_project;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sns_project.arduino.ArduinoSensor;
import com.example.sns_project.cctv.CCTVActivity;
import com.example.sns_project.community.CommunityActivity;
import com.example.sns_project.member.LoginActivity;
import com.example.sns_project.member.ProfileActivity;
import com.example.sns_project.weather.Weather;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.sns_project.dictionary.dict_main;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView temperature_result, description_result, wind_humidity_result, area;
    ImageView weatherIcon;
    ImageButton refreshWeather, refreshSensor;

    TextView arduino_temp, arduino_airHumidity, arduino_soilHumidity, arduino_bright;

    private final Weather weather = new Weather(this);
    private final ArduinoSensor arduinoSensor = new ArduinoSensor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버튼을 고유 id로 설정
        findViewById(R.id.btn_cctv).setOnClickListener(onClickListener);
        findViewById(R.id.btn_dictionary).setOnClickListener(onClickListener);
        findViewById(R.id.btn_profile).setOnClickListener(onClickListener);
        findViewById(R.id.btn_community).setOnClickListener(onClickListener);
        findViewById(R.id.btn_watering).setOnClickListener(onClickListener);

        temperature_result = findViewById(R.id.temperature_result);                    // 화면에 보여지는 현재 기온
        description_result = findViewById(R.id.description_result);                    // 화면에 보여지는 날씨 상태 설명
        wind_humidity_result = findViewById(R.id.wind_humidity_result);                // 화면에 보여지는 풍속, 습도
        weatherIcon = findViewById(R.id.weatherIcon);                                  // 화면에 보여지는 날씨 아이콘
        area = findViewById(R.id.area);                                                // 화면에 보여지는 지역 이름
        refreshWeather = findViewById(R.id.btn_refreshWeather);                        // 날짜 새로고침

        // 날씨 클래스의 필드 값 지정
        weather.setWeatherTextView(temperature_result, description_result, wind_humidity_result, area, weatherIcon); 
        
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();                          // 현재 인증된 user 객체 생성

        if (user == null) {                                                                       // 유저 인증이 안되었으면 로그인 화면으로 이동한다.
            IntentActivity(LoginActivity.class);
        } else{                                                                                   // 유저 인증이 잘 되었으면
            FirebaseFirestore db = FirebaseFirestore.getInstance();                               // 파이어스토어에 접근하기 위한 db생성
            DocumentReference docRef = db.collection("users").document(user.getUid());// db와 연결한 문서 내용을 확인한다.
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {                                       // 성공시
                        DocumentSnapshot document = task.getResult();                // 결과를 document에 넣는다.
                        String cityWeather = document.getString("userAddress"); //document에 사용자가 입력했던 농장주소 내용을 저장하여
                        weather.searchWeather(cityWeather);                          // 날씨 메서드에 사용자가 입력했던 농장주소를 넣어 결과를 나타내준다.

                        if (document.exists()) {                                     // 결과 내용이 존재하면 메인 화면으로 정상 이동한다.
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {                                                     // 결과 내용이 비어있으면 프로필 화면으로 이동한다.
                            Log.d(TAG, "No such document");
                            IntentActivity(ProfileActivity.class);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        
        arduino_temp = findViewById(R.id.aduino_temp);                // 아두이노 온도 센서 값
        arduino_airHumidity = findViewById(R.id.aduino_airhumidity);   // 아두이노 온습도 센서 값
        arduino_soilHumidity = findViewById(R.id.aduino_soilhumidity); // 아두이노 토양습도 센서 값
        arduino_bright = findViewById(R.id.aduino_bright);             // 아두이노 조도 센서 값

        arduinoSensor.setArduinoSesor(arduino_temp, arduino_airHumidity, arduino_soilHumidity, arduino_bright);
        arduinoSensor.AccessSensor();
    }

    /**
     * 각 버튼 클릭시 발생하는 이벤트
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) { 
            switch (v.getId()) {
                case R.id.btn_cctv:        // CCTV 버튼 클릭 시 프로필 액티비티로 이동한다.
                    IntentActivity(CCTVActivity.class);
                    break;

                case R.id.btn_dictionary:  // 작물사전 버튼 클릭 시 프로필 액티비티로 이동한다.
                    Intent intent = new Intent(getApplicationContext(), dict_main.class);
                    startActivity(intent);
                    break;

                case R.id.btn_profile:     // 프로필 버튼 클릭 시 프로필 액티비티로 이동한다.
                    IntentActivity(ProfileActivity.class);
                    break;

                case R.id.btn_community:   // SNS 버튼 클릭 시 프로필 액티비티로 이동한다.
                    IntentActivity(CommunityActivity.class);
                    break;

                case R.id.btn_watering:   // 물주기 버튼 클릭 시
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);    // Builder을 먼저 생성하여 옵션을 설정
                    builder.setTitle("물 주기 버튼");         // 타이틀을 지정
                    builder.setMessage("물을 주시겠습니까?");  // 메시지를 지정

                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {        // 확인 버튼을 생성하고 클릭시 동작을 구현
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            //"YES" 버튼 클릭시
                            Toast.makeText(MainActivity.this, "물 주기가 실행되고 있습니다", Toast.LENGTH_SHORT).show();
                            DatabaseReference aduino_info = FirebaseDatabase.getInstance().getReference();
                            aduino_info.child("buttonvalue").setValue("ON");
                        }
                    });

                    builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {    // 취소 버튼을 생성하고 클릭시 동작을 구현
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //"NO" 버튼 클릭시
                        }
                    });

                    AlertDialog alert = builder.create();    // 빌더를 이용하여 AlertDialog객체를 생성
                    alert.show();  // 알림을 보여준다.

                    break;
            }
        }
    };
    
    /**
     * 센서 값 새로고침 메서드
     */
    public void refreshSensor(View view) {
        refreshSensor = findViewById(R.id.btn_refreshSensor);   // 센서 값 새로고침 버튼
        arduinoSensor.AccessSensor();
        Toast.makeText(MainActivity.this, "센서 값 새로고침 완료", Toast.LENGTH_SHORT).show();
    }

    /**
     * 날씨 새로고침 메서드
     */
    public void refreshWeather(View view) {
        refreshWeather = findViewById(R.id.btn_refreshWeather);   // 날씨 새로고침 버튼

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();                        // 현재 user 객체 생성
        FirebaseFirestore db = FirebaseFirestore.getInstance();                                 // 파이어베이스에 접근할 db생성
        DocumentReference docRef = db.collection("users").document(user.getUid());  // db와 연결한 문서내용을 확인한다.
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {                                // 성공시
                    DocumentSnapshot document = task.getResult();         // 결과를 document에 넣는다.
                    String cityWeather = document.getString("userAddress"); //document에 사용자가 입력했던 농장주소 내용을 저장하여

                    weather.searchWeather(cityWeather);                   // 날씨 메서드에 사용자가 입력했던 농장주소를 넣어 결과를 나타내준다.
                    Toast.makeText(MainActivity.this, "날씨 새로고침 완료", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 뒤로가기 클릭시
     * 다른 화면으로 이동되지 않고 바로 종료되는 메서드
     */
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);    // Builder을 먼저 생성하여 옵션을 설정
        builder.setTitle("어플 종료");                 // 타이틀을 지정
        builder.setMessage("어플을 종료하시겠습니까?");  // 메시지를 지정합

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {        // 확인 버튼을 생성하고 클릭시 동작을 구현
            @Override

            public void onClick(DialogInterface dialog, int which) {
                //"YES" 버튼 클릭시
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {   // 취소 버튼을 생성하고 클릭시 동작을 구현
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //"NO" 버튼 클릭시
            }
        });

        AlertDialog alert = builder.create();    // 빌더를 이용하여 AlertDialog 객체를 생성
        alert.show();                            // 알림을 보여준다.
    }


    private void IntentActivity(Class c){  // 인텐트 함수
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    // 이동하고자 하는 화면을 스택 상단에 남기고 해당 화면의 나머지인 위의 화면들은 모두 삭제해준다.
        startActivity(intent);
    }
}