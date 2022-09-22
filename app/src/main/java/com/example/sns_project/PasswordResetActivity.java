package com.example.sns_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
    private static final String TAG = "PasswordResetActivity";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_send).setOnClickListener(onClickListener);
        findViewById(R.id.btn_login).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {   // 각 버튼 클릭 시 이벤트 발생
            switch (v.getId()) {
                case R.id.btn_send:     // 비밀번호 재설정 이메일 발송 버튼을 클릭했을 때 비밀번호 재설정 함수를 실행한다.
                    send();
                    break;
                case R.id.btn_login:    // 로그인 화면으로 돌아간다.
                    Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;

            }
        }
    };


    private void send() {   // 비밀번호 재설정 메서드
        String userEmail = ((EditText)findViewById(R.id.et_email)).getText().toString();   // 텍스트에 적은 이메일를 String 형식으로 가져온다.

        if(userEmail.length() > 0) {
            mAuth.sendPasswordResetEmail(userEmail)   // 이메일이 입력되었을 때
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {  // 일치하여 성공하면 입력한 이메일에 재설정 메일을 발송과 동시에 현재 화면을 종료한다.
                                Toast.makeText(PasswordResetActivity.this, "입력하신 이메일에 발송했습니다. 확인해주세요", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Email sent.");
                                finish();
                            }
                        }
                    });
        }else   {
            Toast.makeText(PasswordResetActivity.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

}