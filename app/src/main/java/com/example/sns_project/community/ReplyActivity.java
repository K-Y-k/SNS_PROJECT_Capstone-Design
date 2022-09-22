package com.example.sns_project.community;

import static android.widget.LinearLayout.VERTICAL;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.sns_project.FirebaseConst;
import com.example.sns_project.L;
import com.example.sns_project.R;
import com.example.sns_project.adapter.ReplyAdapter;
import com.example.sns_project.data.Community;
import com.example.sns_project.data.Message;
import com.example.sns_project.databinding.ActivityReplyBinding;
import com.example.sns_project.event.BoardRefreshEvent;
import com.example.sns_project.event.CommentRefreshEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReplyActivity extends AppCompatActivity {

    // 댓글
    private ActivityReplyBinding binding;

    // 댓글 db
    private CollectionReference replyRef = FirebaseFirestore.getInstance().collection(FirebaseConst.REPLY);

    // 게시물 db
    private CollectionReference postRef = FirebaseFirestore.getInstance().collection(FirebaseConst.POST);

    // 댓글 리스트뷰
    private ReplyAdapter adapter;

    // 이전 게시물 정보
    private Community selectedCommunity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reply, DataBindingUtil.getDefaultComponent());

        // 이전 게시물 상태값 셋팅
        selectedCommunity = (Community) getIntent().getSerializableExtra("EXTRA_ITEM");

        initRecyclerView();
        fetchReplyItems();


        // 전송 클릭시
        binding.btnSend.setOnClickListener(view -> {
            if (TextUtils.isEmpty(binding.etContent.getText())) {
                Toast.makeText(getApplicationContext(), "양식을 채워주세요", Toast.LENGTH_LONG).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();                        // 현재 user 객체 생성
            FirebaseFirestore db = FirebaseFirestore.getInstance();                                 // 파이어베이스에 접근할 db생성
            DocumentReference docRef = db.collection("users").document(user.getUid());  // db와 연결한 문서내용을 확인한다.
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {                                 // 성공시
                        DocumentSnapshot document = task.getResult();          // 결과를 document에 넣는다.
                        String userName = document.getString("userName"); //document에 사용자가 입력했던 닉네임 내용을 저장한다.

                        // 댓글에 포함할 글쓴이 uid, 내용, 시간, 닉네임 정보를 메시지 클래스에 담는다.
                        String addedbyUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String content = binding.etContent.getText().toString();
                        Long timeStemp = System.currentTimeMillis();

                        Message message = new Message(addedbyUser, content, timeStemp, selectedCommunity.getUid(), userName);

                        L.i("::::message " + message);

                        // reply firestore db로 전송
                        replyRef.add(message).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) { // 성공 시
                                Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_LONG).show();
                                L.i(":::::성공....");

                                // 성공시 댓글 카운트를 증가시켜
                                selectedCommunity.setCommentCount(String.valueOf(Integer.parseInt(selectedCommunity.getCommentCount()) + 1));

                                // 이전 상세화면과 게시물 홈화면 데이터를 갱신시킨다.
                                EventBus.getDefault().postSticky(new BoardRefreshEvent());
                                EventBus.getDefault().postSticky(new CommentRefreshEvent(selectedCommunity.getCommentCount()));

                                // 기존 게시물 데이터를 댓글 카운트를 1증가시킨다.
                                Map<String, Object> post = new HashMap<>();
                                post.put("commentCount", selectedCommunity.getCommentCount());
                                postRef.document(selectedCommunity.getUid()).update(post);
                                adapter.addItem(message);
                                clear();

                            } else {
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                                L.i(":::::실패....");
                                //실패
                            }
                        });

                    } else {
                    }
                }
            });
        });

    }


    private void initRecyclerView() {
        adapter = new ReplyAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ReplyActivity.this, VERTICAL);
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }


    private void fetchReplyItems() {
        // 각 게시물에 따른 댓글을 요청한다.
        replyRef.whereEqualTo("communityNo", selectedCommunity.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Message> list = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                L.i(document.getId() + " => " + document.toObject(Message.class));
                list.add(document.toObject(Message.class));
            }
            // 리스트뷰 갱신
            adapter.addItems(list);
        }).addOnFailureListener(e -> {
            L.e("::::::onFailure " + e.getMessage());
        });
    }


    private void clear() {
        binding.etContent.setText(null);
        hideKeyboard();
    }


    //키보드 제거
    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            String serviceName = Context.INPUT_METHOD_SERVICE;
            InputMethodManager imm = (InputMethodManager) getSystemService(serviceName);
            int stateHide = InputMethodManager.HIDE_NOT_ALWAYS;
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), stateHide);
        }
    }

}
