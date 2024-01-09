package com.example.sns_project.member;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sns_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();  // 인증과 관련된 파이어베이스 선언

        findViewById(R.id.btn_register).setOnClickListener(onClickListener);
        findViewById(R.id.btn_login).setOnClickListener(onClickListener);
    }


    /**
     * 각 버튼 클릭 시 이벤트 발생
     */
    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_register:  // 회원가입 버튼을 클릭하면 회원가입 함수가 실행된다.
                    signUp();
                    break;
                case R.id.btn_login:     // 돌아가기 버튼을 클릭하면 로그인 화면으로 돌아간다.
                    Intent intent_login = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent_login);
                    break;
            }
        }
    };


    /**
     * 회원가입 메서드
     */
    private void signUp() {
        String userEmail = ((EditText)findViewById(R.id.et_email)).getText().toString();   // 텍스트에 입력한 이메일, 비밀번호를 String 형식으로 가져온다.
        String userPass = ((EditText)findViewById(R.id.et_pass)).getText().toString();

        mAuth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){      // 성공하면 인증 파이어베이스에 저장되고 로그인화면으로 이동한다.
                            Log.d(TAG, "success");
                            Toast.makeText(getApplicationContext(),"회원가입 되었습니다",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            finish();
                        }else{                        // 실패시 형식 확인 메시지 출력
                            Log.w(TAG, "failure", task.getException());
                            Toast.makeText(getApplicationContext(),"회원가입에 실패하였습니다",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),"이메일 형식과 비밀번호 6자리 이상인지 확인해주세요",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}