package com.example.sns_project.weather;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.example.sns_project.MainActivity;

import com.koushikdutta.ion.Ion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather extends AsyncTask<String, Void, String> {      // 날씨 정보를 저장할 클래스 첫번째 String은 URL, Void는 아무 의미없음, 세번째 String은 리턴 받아올 타입

    @SuppressLint("StaticFieldLeak")
    private final MainActivity mainActivity;

    @SuppressLint("StaticFieldLeak")
    private TextView temperature_result;
    @SuppressLint("StaticFieldLeak")
    private TextView description_result;
    @SuppressLint("StaticFieldLeak")
    private TextView wind_humidity_result;
    @SuppressLint("StaticFieldLeak")
    private TextView area;
    @SuppressLint("StaticFieldLeak")
    private ImageView weatherIcon;

    public Weather(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setWeatherTextView(TextView temperature_result, TextView description_result, TextView wind_humidity_result, TextView area, ImageView weatherIcon) {
        this.temperature_result = temperature_result;
        this.description_result = description_result;
        this.wind_humidity_result = wind_humidity_result;
        this.area = area;
        this.weatherIcon = weatherIcon;
    }


    /**
     * execute() 메서드가 실행될 때 자동으로 실행되는 메서드로
     * 백그라운드에서 주어진 URL를 읽어와 문자열로 반환하는 역할을 한다. 
     * 네트워크 통신에 사용된다.
     */
    @Override
    public String doInBackground(String... address) {                                  // array 형식으로 주소의 내용을 보낸다.
        try {
            URL url = new URL(address[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();   // 주소와 연결설정으로 URL을 연결한다.
            connection.connect();

            InputStream is = connection.getInputStream();                              // url에서 데이터를 검색한다.
            InputStreamReader isr = new InputStreamReader(is);

            int data = isr.read();                                                     // 데이터를 하나씩 읽어와 content에 저장하고 문자열로 반환한다.
            StringBuilder content = new StringBuilder();

            while (data != -1) {
                char ch = (char) data;
                content.append(ch);
                data = isr.read();
            }

            return content.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 현재 입력한 농장의 주소의 날씨 메서드
     */
    public void searchWeather(String cityWeather) {
        String content; // api를 저장할 변수

        try {
            // doInBackground 호출
            content = this.execute("https://api.openweathermap.org/data/2.5/weather?q=" +         // q=와 &appid 사이는 도시의 이름이 들어가므로 사이에 cityWeather로 지정해서 넣는다.
                    cityWeather + "&appid=b4caa9a0696608b54527be88d348f19b&units=metric").get();  // 날씨 api 정보가 content에 저장된다.
            Log.i("contentData", content);


            // JSON 변환
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");   // weather 항목의 배열들이 저장위치 선언
            String main = jsonObject.getString("main");             // 기온, 습도는 main 항목안에 있기에 따로 분리한 것이다.
            String wind = jsonObject.getString("wind");             // 풍속은 wind 항목안에 있기에 따로 분리한 것이다.

            Log.i("weatherData", weatherData);


            // JSON의 날씨 정보 읽기 위한 작업
            JSONArray array = new JSONArray(weatherData);

            int temperature;      // 현재 온도
            int humidity;         // 습도
            int windSpeed;        // 풍속

            String state = getState(array, weatherIcon);         // 날씨 상태 설명
            state = korReTranslateState(state);                  // 제대로 된 한국어로 재번역

            Log.i("aaa", "aaaa");

            JSONObject mainPart = new JSONObject(main);          // 각 배열의 항목이 다르기에 각자 분리해준다. -> main 항목 생성
            temperature = mainPart.getInt("temp");         // main 항목 안의 temp를 가져옴
            humidity = mainPart.getInt("humidity");        // main 항목 안의 humidity를 가져옴

            JSONObject windPart = new JSONObject(wind);          // wind 항목 생성
            windSpeed = windPart.getInt("speed");          // wind 항목 안의 speed를 가져옴


            String result1 = " "     + temperature + "℃";       // 각 내용들을 reult1~3변수에 저장한다.
            String result2 = "    "  + state;
            String result3 = "     " + windSpeed   + "m/s              "   + humidity + "%";


            // 새로 갱신된 상태 값으로 변환
            temperature_result.setText(result1);                // 현재 기온       결과를 보여줌
            description_result.setText(result2);                // 날씨 상태 설명   결과를 보여줌
            wind_humidity_result.setText(result3);              // 풍속, 습도      결과를 보여줌
            area.setText(cityWeather);                          // 지역           결과를 보여줌

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 닐씨 아이콘 이미지 적용 메서드
     */
    private void loadIcon(String icon, ImageView weatherIcon) {                // 아이콘 연결 함수
        Ion.with(mainActivity)                                                 // gradle에 설치해야 Ion 함수를 이용할 수 있다!! (중요)
                .load("http://openweathermap.org/img/wn/" + icon + ".png") // wn/와 .png 사이의 icon 고유 값에 따라 능동적으로 이미지뷰의 아이콘이 바뀐다.
                .intoImageView(weatherIcon);
    }

    /**
     * 날씨 상태 설명과 날씨 아이콘 추출 메서드
     */
    private String getState(JSONArray array, ImageView weatherIcon) throws JSONException {
        String state = "";
        String icon;

        for(int i = 0; i< array.length(); i++) {                 // weather의 모든 항목을 조사하여 저장한다.
            JSONObject weatherPart = array.getJSONObject(i);

            icon = weatherPart.getString("icon");          // weather 항목 안의 icon을 가져옴
            loadIcon(icon, weatherIcon);                         // 가져온 아이콘명과 일치하는 아이콘 이미지를 설정 메서드 호출

            state = weatherPart.getString("description");  // weather 항목 안의 description을 가져옴
        }

        return state;
    }

    /**
     * 날씨 상태 설명이 한글로 번역해와서 단어가 이상하여 재번역 메서드
     */
    @NonNull
    private String korReTranslateState(String state) {
        switch (state) {
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
        return state;
    }
}
