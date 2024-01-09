package com.example.sns_project.cctv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sns_project.MainActivity;
import com.example.sns_project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Timer;
import java.util.TimerTask;


public class CCTVActivity extends AppCompatActivity {
    private static final String TAG = "CCTVActivity";

    ImageView cctv;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);

        findViewById(R.id.btn_mainBack).setOnClickListener(onClickListener);
        cctv = findViewById(R.id.cctv);

        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                CCTV_Update();  // 반복실행할 구문
            }
        };
        timer.schedule(TT, 0, 1000); //Timer 실행
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {    // 각 버튼 클릭 시 이벤트 발생
            switch (v.getId()) {
                case R.id.btn_mainBack:  // 돌아가기 버튼을 클릭 시 메인 화면으로 돌아간다.
                    timer.cancel();     // 타이머 종료
                    IntentActivity(MainActivity.class);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {   // 뒤로가기로 누를 시
        super.onBackPressed();;
        timer.cancel();            // 타이머 종료
    }



    private void CCTV_Update() {   // CCTV 출력 메서드
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("data/" + "photo"  + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(CCTVActivity.this).load(uri).into(cctv);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CCTVActivity.this, "불러오는데 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void IntentActivity(Class c){  // 인텐트 함수
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    // 이동하고자 하는 화면을 스택 상단에 남기고 해당 화면의 나머지인 위의 화면들은 모두 삭제 해준다.
        startActivity(intent);
    }

}