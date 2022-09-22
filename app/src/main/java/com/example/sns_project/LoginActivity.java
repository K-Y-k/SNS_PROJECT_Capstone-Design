package com.example.sns_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();  // 인증과 관련된 파이어베이스 선언

        findViewById(R.id.btn_login).setOnClickListener(onClickListener);
        findViewById(R.id.btn_register).setOnClickListener(onClickListener);
        findViewById(R.id.btn_passwordReset).setOnClickListener(onClickListener);
    }


    @Override
    public void onBackPressed() {   // 다른 화면으로 이동되지 않고 바로 종료되는 메서드
        super.onBackPressed();;
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {           // 각 버튼 클릭 시 이벤트 발생
            switch (v.getId()) {
                case R.id.btn_login:            // 로그인 버튼을 클릭했을 때 데이터베이스의 내용과 일치하면 메인 화면으로 이동하는 로그인 메서드 실행
                    signUp();
                    break;
                case R.id.btn_passwordReset:    // 비밀번호 찾기 버튼을 클릭했을 때 비밀번호 재설정 화면으로 이동
                    IntentActivity(PasswordResetActivity.class);
                    break;
                case R.id.btn_register:         // 회원가입 화면으로 이동
                    IntentActivity(RegisterActivity.class);
                    break;
            }
        }
    };


    private void signUp() {   // 로그인 메서드
        String userEmail = ((EditText)findViewById(R.id.et_email)).getText().toString();   // 텍스트에 입력한 이메일, 비밀번호를 String 형식으로 가져온다.
        String userPass = ((EditText)findViewById(R.id.et_pass)).getText().toString();

        if(userEmail.length() > 0 && userPass.length() > 0) {
            mAuth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {    // 인증 파이어베이스에 저장된 내용과 일치하면 메인화면으로 이동한다.
                                Log.d(TAG, "success");
                                Toast.makeText(LoginActivity.this, "로그인 되었습니다", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                IntentActivity(MainActivity.class);
                            } else {                     // 실패시 재입력 메시지 출력
                                Log.w(TAG, "failure", task.getException());
                                Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, "아이디, 비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }else   {
            Toast.makeText(LoginActivity.this, "이메일, 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }


    private void IntentActivity(Class c){   // 인텐트 메서드
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);     // 이동하고자하는 화면을 스택 상단에 남기고 해당 화면의 나머지인 위의 화면들은 모두 삭제해준다.
        startActivity(intent);
    }

}