package com.example.sns_project.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.sns_project.FirebaseConst;
import com.example.sns_project.L;
import com.example.sns_project.R;
import com.example.sns_project.data.Community;
import com.example.sns_project.databinding.ActivityDetailBinding;
import com.example.sns_project.event.BoardRefreshEvent;
import com.example.sns_project.event.CommentRefreshEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;


public class DetailActivity extends AppCompatActivity {
    //상세화면

    //서버 요청 post 요청 객체
    private CollectionReference postRef = FirebaseFirestore.getInstance().collection(FirebaseConst.POST);
    private ActivityDetailBinding binding;

    //게시물화면에서 클릭시 넘겨준 데이터를 받을 community 데이터
    private Community community;

    //내가 좋아요를 눌렀는지 체크
    private Map<String, Boolean> isLikeUser = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail, DataBindingUtil.getDefaultComponent());

        //이전화면에서 넘겨준 데이터를받는다.
        community = (Community) getIntent().getSerializableExtra("EXTRA_ITEM");

        //받아온데이터를 바인딩시켜서 화면에 표시
        binding.setItem(community);

        //내가 좋아요를 눌렀는지 상태를 받아온다.
        isLikeUser = community.getLikeUsers();

        //댓글 클릭시
        binding.itemPosterComment.setOnClickListener(view -> startActivity(new Intent(DetailActivity.this, ReplyActivity.class).putExtra("EXTRA_ITEM", community)));

        //좋아요 클릭시
        binding.itemPosterLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //내가 좋아요를 눌렀으면 하트 제거 안눌렀으면 하트 생성과 카운트 1증가
                if (community.getLikeUsers().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    community.setLikeCount(String.valueOf(Integer.parseInt(community.getLikeCount()) - 1));
                    isLikeUser.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                } else {
                    community.setLikeCount(String.valueOf(Integer.parseInt(community.getLikeCount()) + 1));
                    isLikeUser.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                }


                //이전화면에 갱신 이벤트 통지
                EventBus.getDefault().postSticky(new BoardRefreshEvent());
                //기존 게시물데이터를 좋아요 카운트를 1증가시킨다.
                Map<String, Object> post = new HashMap<>();
                post.put("likeCount", community.getLikeCount());
                post.put("likeUsers", isLikeUser);

                //서버전송
                postRef.document(community.getUid()).update(post);


                //ui 갱신
                binding.setItem(community);
                binding.executePendingBindings();
            }
        });

        //이벤트 버스 등록
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    //이전 댓글을 눌렀으면 댓글 카운트 ui를 갱신하기위에 이벤트버스사용
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentUpdate(CommentRefreshEvent event) {
        L.d("onCommentUpdate... Subscribe");

        community.setCommentCount(event.getCommunity());
        binding.setItem(community);
        binding.executePendingBindings();

    }

    @Override
    protected void onDestroy() {
        //이벤트 버스 해제
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}
