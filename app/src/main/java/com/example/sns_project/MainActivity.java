package com.example.sns_project;

import android.content.DialogInterface;
import android.os.AsyncTask;
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

import com.example.sns_project.community.CommunityActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.sns_project.dictionary.dict_main;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView temperature_result, description_result, wind_humidity_result, area;
    ImageView weatherIcon;
    ImageButton refreshWeather, refreshSensor;


    class Weather extends AsyncTask<String, Void, String> {      // 날씨 정보를 저장할 클래스 첫번째 String은 URL, Void는 아무 의미없음, 세번째 String은 리턴 받아올 타입

        @Override
        protected String doInBackground(String... address) {     // array 형식으로 주소의 내용을 보낸다.
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();   // 주소와 연결설정으로 URL을 연결한다.
                connection.connect();

                InputStream is = connection.getInputStream();    // url에서 데이터를 검색한다.
                InputStreamReader isr = new InputStreamReader(is);

                int data = isr.read();                           // 데이터를 하나씩 읽어와 content에 저장하고 문자열로 반환한다.
                String content = "";
                while (data != -1) {
                    char ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public void searchWeather(String cityWeather){  // 현재 입력한 농장의 주소의 날씨 메서드
        temperature_result = findViewById(R.id.temperature_result);                    // 화면에 보여지는 현재 기온
        description_result = findViewById(R.id.description_result);                    // 화면에 보여지는 날씨 상태 설명
        wind_humidity_result = findViewById(R.id.wind_humidity_result);                // 화면에 보여지는 풍속, 습도
        weatherIcon = findViewById(R.id.weatherIcon);                                  // 화면에 보여지는 날씨 아이콘
        area = findViewById(R.id.area);                                                // 화면에 보여지는 지역 이름

        String content;                                    // api를 저장할 변수
        MainActivity.Weather weather = new MainActivity.Weather();
        try {
            content = weather.execute("https://api.openweathermap.org/data/2.5/weather?q=" +                // q=와 &appid 사이는 도시의 이름이 들어가므로 사이에 cityWeather로 지정해서 넣는다.
                    cityWeather + "&appid=b4caa9a0696608b54527be88d348f19b&units=metric").get();    // 날씨 api 정보가 content에 저장된다.
            Log.i("contentData", content);

            //JSON
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");   // weather 항목의 배열들이 저장위치 선언
            String main = jsonObject.getString("main");             // 기온, 습도는 main 항목안에 있기에 따로 분리한 것이다.
            String wind = jsonObject.getString("wind");             // 풍속은 wind 항목안에 있기에 따로 분리한 것이다.

            Log.i("weatherData", weatherData);

            JSONArray array = new JSONArray(weatherData);

            String icon;          // 날씨 아이콘
            String state = "";    // 날씨 상태 설명
            int temperature;      // 현재 온도
            int humidity;         // 습도
            int windSpeed;        // 풍속

            for(int i=0; i<array.length(); i++) {                     // weather의 모든 항목을 조사하여 저장한다.
                JSONObject weatherPart = array.getJSONObject(i);

                icon = weatherPart.getString("icon");          // weather 항목 안의 icon을 가져옴
                loadIcon(icon);                                      // 가져온 icon으로 아이콘 연결 함수 실행

                state = weatherPart.getString("description");  // weather 항목 안의 description을 가져옴

            }

            switch (state) {  // 날씨 상태 설명이 한글로 번역해와서 단어가 이상하여 재번역 실행
                case "clear sky":
                    state = state.replace("clear sky", "맑음");
                    break;

                case "few clouds":
                    state = state.replace("few clouds", "구름낌");
                    break;
                case "broken clouds":
                    state = state.replace("broken clouds", "흐림");
                    break;
                case "overcast clouds":
                    state = state.replace("overcast clouds", "흐림");
                    break;
                case "scattered clouds":
                    state = state.replace("scattered clouds", "흐림");
                    break;

                case "shower rain":
                    state = state.replace("shower rain", "폭우");
                    break;
                case "rain":
                    state = state.replace("rain", "비");
                    break;
                case "light rain":
                    state = state.replace("light rain", "소나기");
                    break;
                case "moderate rain":
                    state = state.replace("moderate rain", "비");
                    break;
                case "heavy intensity rain":
                    state = state.replace("heavy intensity rain", "거센 비");
                    break;
                case "very heavy rain":
                    state = state.replace("very heavy rain", "거센 비");
                    break;
                case "extreme rain":
                    state = state.replace("extreme rain", "폭우");
                    break;
                case "freezing rain":
                    state = state.replace("freezing rain", "거센 비");
                    break;
                case "light intensity shower rain":
                    state = state.replace("light intensity shower rain", "소나기");
                    break;
                case "heavy intensity shower rain":
                    state = state.replace("heavy intensity shower rain", "소나기");
                    break;
                case "ragged shower rain":
                    state = state.replace("ragged shower rain", "소나기");
                    break;

                case "mist":
                    state = state.replace("mist", "안개낌");
                    break;
                case "smoke":
                    state = state.replace("smoke", "안개낌");
                    break;
                case "haze":
                    state = state.replace("haze", "안개낌");
                    break;
                case "sand/ dust whirls":
                    state = state.replace("sand/ dust whirls", "황사");
                    break;
                case "fog":
                    state = state.replace("fog", "안개낌");
                    break;
                case "sand":
                    state = state.replace("sand", "황사");
                    break;
                case "dust":
                    state = state.replace("dust", "먼지");
                    break;
                case "volcanic ash":
                    state = state.replace("volcanic ash", "화산재");
                    break;
                case "squalls":
                    state = state.replace("squalls", "돌풍");
                    break;
                case "tornado":
                    state = state.replace("tornado", "태풍");
                    break;

                case "light snow":
                    state = state.replace("light snow", "가벼운 눈");
                    break;
                case "snow":
                    state = state.replace("snow", "눈내림");
                    break;
                case "heavy snow":
                    state = state.replace("heavy snow", "거센 눈");
                    break;
                case "sleet":
                    state = state.replace("sleet", "진눈깨비");
                    break;
                case "light shower sleet":
                    state = state.replace("light shower sleet", "진눈깨비");
                    break;
                case "shower sleet":
                    state = state.replace("shower sleet", "진눈깨비");
                    break;
                case "light rain and snow":
                    state = state.replace("light rain and snow", "가벼운 비와 눈");
                    break;
                case "rain and snow":
                    state = state.replace("rain and snow", "비와 눈");
                    break;
                case "light shower snow":
                    state = state.replace("light shower snow", "소나기 눈");
                    break;
                case "heavy shower snow":
                    state = state.replace("heavy shower snow", "거센 소나기 눈");
                    break;

                case "light intensity drizzle":
                    state = state.replace("light intensity drizzle", "이슬비");
                    break;
                case "drizzle":
                    state = state.replace("drizzle", "이슬비");
                    break;
                case "heavy intensity drizzle":
                    state = state.replace("heavy intensity drizzle", "강랑비");
                    break;
                case "light intensity drizzle rain":
                    state = state.replace("light intensity drizzle rain", "이슬비");
                    break;
                case "drizzle rain":
                    state = state.replace("drizzle rain", "이슬비");
                    break;
                case "heavy intensity drizzle rain":
                    state = state.replace("heavy intensity drizzle rain", "강랑비");
                    break;
                case "shower rain and drizzle":
                    state = state.replace("shower rain and drizzle", "이슬비");
                    break;
                case "heavy shower rain and drizzle":
                    state = state.replace("heavy shower rain and drizzle", "거센 이슬비");
                    break;
                case "shower drizzle":
                    state = state.replace("shower drizzle", "가랑비");
                    break;

                case "thunderstorm with light rain":
                    state = state.replace("thunderstorm with light rain", "뇌우");
                    break;
                case "thunderstorm with rain":
                    state = state.replace("thunderstorm with rain", "뇌우");
                    break;
                case "thunderstorm with heavy rain":
                    state = state.replace("thunderstorm with heavy rain", "거센 뇌우");
                    break;
                case "light thunderstorm":
                    state = state.replace("light thunderstorm", "가벼운 뇌우");
                    break;
                case "thunderstorm":
                    state = state.replace("thunderstorm", "뇌우");
                    break;
                case "heavy thunderstorm":
                    state = state.replace("heavy thunderstorm", "거센 뇌우");
                    break;
                case "ragged thunderstorm":
                    state = state.replace("ragged thunderstorm", "거센 뇌우");
                    break;
                case "thunderstorm with light drizzle":
                    state = state.replace("thunderstorm with light drizzle", "가벼운 뇌우");
                    break;
                case "thunderstorm with drizzle":
                    state = state.replace("thunderstorm with drizzle", "뇌우");
                    break;
                case "thunderstorm with heavy drizzle":
                    state = state.replace("thunderstorm with heavy drizzle", "거센 뇌우");
                    break;

            }

            JSONObject mainPart = new JSONObject(main);          // 각 배열의 항목이 다르기에 각자 분리해준다. -> main 항목 생성
            temperature = mainPart.getInt("temp");         // main 항목 안의 temp를 가져옴
            humidity = mainPart.getInt("humidity");        // main 항목 안의 huminity를 가져옴

            JSONObject windPart = new JSONObject(wind);          // wind 항목 생성
            windSpeed = windPart.getInt("speed");          // wind 항목 안의 speed를 가져옴


            String result1 = " "     + temperature + "℃";          // 각 내용들을 reult1~3변수에 저장한다.
            String result2 = "    "  + state;
            String result3 = "     " + windSpeed   + "m/s              "   + humidity + "%";

            temperature_result.setText(result1);                // 현재 기온       결과를 보여줌
            description_result.setText(result2);                // 날씨 상태 설명   결과를 보여줌
            wind_humidity_result.setText(result3);              // 풍속, 습도      결과를 보여줌
            area.setText(cityWeather);                          // 지역           결과를 보여줌

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void refreshWeather(View view){  // 날씨 새로고침 메서드
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
                    searchWeather(cityWeather);                                  // 날씨 메서드에 사용자가 입력했던 농장주소를 넣어 결과를 나타내준다.
                    Toast.makeText(MainActivity.this, "날씨 새로고침 완료", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        });
    }

    private void loadIcon(String icon) {  // 아이콘 연결 함수
        Ion.with(this)            // gradle에 설치해야 Ion 함수를 이용할 수 있다!! (중요)
                .load("http://openweathermap.org/img/wn/" + icon + ".png") // wn/와 .png 사이의 icon 고유 값에 따라 능동적으로 이미지뷰의 아이콘이 바뀐다.
                .intoImageView(weatherIcon);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  // 현재 인증된 user 객체 생성
        DatabaseReference aduino_info = FirebaseDatabase.getInstance().getReference();  // 리얼 타임 데이터베이스 객체 생성

        if(user == null) {                         // 유저 인증이 안되었으면 로그인 화면으로 이동한다.
            IntentActivity(LoginActivity.class);
        }else{                                     // 유저 인증이 잘 되었으면
            FirebaseFirestore db = FirebaseFirestore.getInstance();      // 파이어스토어에 접근하기 위한 db생성
            DocumentReference docRef = db.collection("users").document(user.getUid());  // db와 연결한 문서 내용을 확인한다.
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {                                // 성공시
                        DocumentSnapshot document = task.getResult();         // 결과를 document에 넣는다.
                        String cityWeather = document.getString("userAddress"); //document에 사용자가 입력했던 농장주소 내용을 저장하여
                        searchWeather(cityWeather);                                  // 날씨 메서드에 사용자가 입력했던 농장주소를 넣어 결과를 나타내준다.

                        if(document != null){
                            if (document.exists()) {                          // 결과 내용이 존재하면 메인 화면으로 정상 이동한다.
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {                                          // 결과 내용이 비어있으면 프로필 화면으로 이동한다.
                                Log.d(TAG, "No such document");
                                IntentActivity(ProfileActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        TextView aduino_tmep = findViewById(R.id.aduino_temp);                 // 아두이노 온도 센서 값
        TextView aduino_airhumidity = findViewById(R.id.aduino_airhumidity);   // 아두이노 온습도 센서 값
        TextView aduino_soilhumidity = findViewById(R.id.aduino_soilhumidity); // 아두이노 토양습도 센서 값
        TextView aduino_bright = findViewById(R.id.aduino_bright);             // 아두이노 조도 센서 값



        aduino_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                String temp = datasnapshot.child("temp").getValue().toString();
                String airhumidity = datasnapshot.child("airHumiValue").getValue().toString();
                String soilhumidity = datasnapshot.child("soilHumiValue").getValue().toString();
                String bright = datasnapshot.child("cdsValue").getValue().toString();

                aduino_tmep.setText(temp);
                aduino_airhumidity.setText(airhumidity);
                aduino_soilhumidity.setText(soilhumidity);
                aduino_bright.setText(bright);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        findViewById(R.id.btn_cctv).setOnClickListener(onClickListener);
        findViewById(R.id.btn_dictionary).setOnClickListener(onClickListener);
        findViewById(R.id.btn_profile).setOnClickListener(onClickListener);
        findViewById(R.id.btn_community).setOnClickListener(onClickListener);
        findViewById(R.id.btn_watering).setOnClickListener(onClickListener);
    }


    public void refreshSensor(View view){  // 센서 값 새로고침 메서드
        refreshSensor = findViewById(R.id.btn_refreshSensor);   // 센서 값 새로고침 버튼
        TextView aduino_tmep = findViewById(R.id.aduino_temp);
        TextView aduino_airhumidity = findViewById(R.id.aduino_airhumidity);
        TextView aduino_soilhumidity = findViewById(R.id.aduino_soilhumidity);
        TextView aduino_bright = findViewById(R.id.aduino_bright);

        DatabaseReference aduino_info = FirebaseDatabase.getInstance().getReference();  // 리얼 타임 데이터베이스 객체 생성

        aduino_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                String temp = datasnapshot.child("temp").getValue().toString();
                String airhumidity = datasnapshot.child("airHumiValue").getValue().toString();
                String soilhumidity = datasnapshot.child("soilHumiValue").getValue().toString();
                String bright = datasnapshot.child("cdsValue").getValue().toString();

                aduino_tmep.setText(temp);
                aduino_airhumidity.setText(airhumidity);
                aduino_soilhumidity.setText(soilhumidity);
                aduino_bright.setText(bright);

                Toast.makeText(MainActivity.this, "센서 값 새로고침 완료", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {    // 각 버튼 클릭 시 이벤트 발생
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
                            //"YES" Button Click
                            Toast.makeText(MainActivity.this, "물 주기가 실행되고 있습니다", Toast.LENGTH_SHORT).show();
                            DatabaseReference aduino_info = FirebaseDatabase.getInstance().getReference();
                            aduino_info.child("buttonvalue").setValue("ON");
                        }
                    });

                    builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {   // 취소 버튼을 생성하고 클릭시 동작을 구현
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //"NO" Button Click

                        }
                    });
                    AlertDialog alert = builder.create();    // 빌더를 이용하여 AlertDialog객체를 생성
                    alert.show();  // 알림을 보여준다.
                    break;
            }
        }
    };

    public void onBackPressed() {   // 다른 화면으로 이동되지 않고 바로 종료되는 메서드
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);    // Builder을 먼저 생성하여 옵션을 설정
        builder.setTitle("어플 종료");                 // 타이틀을 지정
        builder.setMessage("어플을 종료하시겠습니까?");  // 메시지를 지정합

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {        // 확인 버튼을 생성하고 클릭시 동작을 구현
            @Override

            public void onClick(DialogInterface dialog, int which) {
                //"YES" Button Click
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {   // 취소 버튼을 생성하고 클릭시 동작을 구현
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //"NO" Button Click

            }
        });
        AlertDialog alert = builder.create();    // 빌더를 이용하여 AlertDialog객체를 생성
        alert.show();  // 알림을 보여준다.

    }

    private void IntentActivity(Class c){  // 인텐트 함수
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    // 이동하고자하는 화면을 스택 상단에 남기고 해당 화면의 나머지인 위의 화면들은 모두 삭제해준다.
        startActivity(intent);
    }

}