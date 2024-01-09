package com.example.sns_project.member;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sns_project.MainActivity;
import com.example.sns_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private EditText address_result, name_result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.btn_logout).setOnClickListener(onClickListener);
        findViewById(R.id.btn_update).setOnClickListener(onClickListener);
        findViewById(R.id.btn_mainBack).setOnClickListener(onClickListener);

        Spinner spinner_address = findViewById(R.id.spinner_address);   // 농장 주소 스피너 생성
        address_result = findViewById(R.id.et_address);                 // 최근 저장된 농장 주소
        name_result = findViewById(R.id.et_name);                       // 최근 저장된 닉네임

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();                        // 현재 user 객체 생성
        FirebaseFirestore db = FirebaseFirestore.getInstance();                                 // 파이어베이스에 접근할 db생성
        DocumentReference docRef = db.collection("users").document(user.getUid());  // db와 연결한 문서내용을 확인한다.
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {                                // 성공시
                    DocumentSnapshot document = task.getResult();         // 결과를 document에 넣는다.
                    String name = document.getString("userName");
                    String address = document.getString("userAddress");
                    name_result.setText(name);                            // 최근 저장된 닉네임, 농장주소 내용을 입력 텍스트에 보이게 한다.
                    address_result.setText(address);
                }
            }
        });

        spinner_address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {      // 농장지역 선택하기 위해 필요한 스피너 설정
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                address_result.setText(parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }


    /**
     * 각 버튼 클릭 시 이벤트 발생
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_update:    // 정보등록 버튼을 클릭했을 때 정보 업데이트 함수를 실행한다.
                    profileUpdate();
                    break;

                case R.id.btn_mainBack:  // 메인 화면으로 돌아간다.
                    IntentActivity(MainActivity.class);
                    break;

                case R.id.btn_logout:    // 로그아웃 버튼 클릭시 로그아웃되면서 로그인 액티비티로 이동한다.
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);    // Builder을 먼저 생성하여 옵션을 설정
                    builder.setTitle("로그아웃");                // 타이틀을 지정
                    builder.setMessage("로그아웃 하시겠습니까?");  // 메시지를 지정

                    builder.setPositiveButton("네", new DialogInterface.OnClickListener() {        // 확인 버튼을 생성하고 클릭시 동작을 구현
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            //"YES" 버튼 클릭시
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(ProfileActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                            IntentActivity(LoginActivity.class);
                        }
                    });

                    builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {   // 취소 버튼을 생성하고 클릭시 동작을 구현
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //"NO" 버튼 클릭시
                        }
                    });

                    AlertDialog alert = builder.create();    // 빌더를 이용하여 AlertDialog객체를 생성
                    alert.show();                            // 알림을 보여준다.

                    break;
            }
        }
    };


    /**
     * 정보 업데이트 메서드
     */
    private void profileUpdate() {
        String userName = ((EditText)findViewById(R.id.et_name)).getText().toString();        // 텍스트에 적은 닉네임과 주소를 String 형식으로 가져온다.
        String userAddress = ((EditText)findViewById(R.id.et_address)).getText().toString();

        if (userName.length() > 0 && userAddress.length() > 0){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  // 인증해서 아이디를 만들면 사용자 고유 id가 부여받아지므로 db를 구분하는 고유키가 필요하여 user 변수로 선언해 사용
            FirebaseFirestore db = FirebaseFirestore.getInstance();           // 파이어베이스에 접근하기 위한 db 생성
            MemberInfo memberInfo = new MemberInfo(userName, userAddress);    // 따로 생성한 MemberInfo 클래스 사용

            if (user != null) {                                                               // user가 인증된 상태이면
                db.collection("users").document(user.getUid()).set(memberInfo)  // users라는 collectionPath 카테고리에 입력한 내용을 저장한다.
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {               // 성공시 프로필 화면을 종료한다.
                                Toast.makeText(ProfileActivity.this, "회원정보가 등록되었습니다", Toast.LENGTH_SHORT).show();
                                Intent intent_main = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent_main);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "회원정보 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }

        } else {  // 입력 안 했을 때
            Toast.makeText(ProfileActivity.this, "회원정보를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }


    private void IntentActivity(Class c){  // 인텐트 함수
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    // 이동하고자 하는 화면을 스택 상단에 남기고 해당 화면의 나머지인 위의 화면들은 모두 삭제 해준다.
        startActivity(intent);
    }
}