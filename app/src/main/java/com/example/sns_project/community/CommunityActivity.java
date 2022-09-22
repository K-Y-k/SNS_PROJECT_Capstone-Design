package com.example.sns_project.community;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.sns_project.L;
import com.example.sns_project.MainActivity;
import com.example.sns_project.R;
import com.example.sns_project.databinding.ActivityCommunityBinding;
import com.ncapdevi.fragnav.FragNavController;

import java.util.List;

public class CommunityActivity extends AppCompatActivity implements FragNavController.RootFragmentListener {
    //커뮤니티 main Container 화면 이화면에 각각의 Fragment를 붙인다.

    private ActivityCommunityBinding binding;
    private FragNavController fragNavController;


    //게시물 , 나의 게시물, 좋아요, 업로드등 로직을 처리
    public int INDEX_BOARD = FragNavController.TAB1;
    public int INDEX_POST = FragNavController.TAB2;
    public int INDEX_MYPAGE = FragNavController.TAB3;
    public int INDEX_SEARCH = FragNavController.TAB4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community, DataBindingUtil.getDefaultComponent());


        initFragments(savedInstanceState);
        initBottomNavigationView();

    }

    private void initFragments(Bundle savedInstanceState) {
        //각각의 프래그먼트들을 셋팅
        fragNavController = new FragNavController(getSupportFragmentManager(), binding.container.getId());
        fragNavController.setRootFragmentListener(this);
        fragNavController.setCreateEager(true);
        //앱시작시 디폴트로 보여줄 fragment 설정
        fragNavController.initialize(INDEX_BOARD, savedInstanceState);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //현재 프래그먼트 상태 저장
        fragNavController.onSaveInstanceState(outState);
    }


    private void initBottomNavigationView() {
        //하단 BottomView 클릭이벤트
        binding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_board:
                    fragNavController.switchTab(INDEX_BOARD);
                    break;
                case R.id.menu_bookmark:
                    fragNavController.switchTab(INDEX_POST);
                    break;
                case R.id.menu_main_back:
                    IntentActivity(MainActivity.class);
                    break;
                case R.id.menu_mypage:
                    fragNavController.switchTab(INDEX_MYPAGE);
                    break;
                case R.id.menu_search:
                    fragNavController.switchTab(INDEX_SEARCH);
                    break;
                default:
                    throw new IllegalStateException("unknown Index..");
            }
            return true;
        });
    }



    //하단 fragment를 4개 사용하니까 4개
    @Override
    public int getNumberOfRootFragments() {
        return 4;
    }


    //position에 맞게 프래그먼트를 셋팅
    @Override
    public Fragment getRootFragment(int i) {
        if (INDEX_BOARD == i) {
            return BoardFragment.newInstance();
        } else if (INDEX_POST == i) {
            return PostFragment.newInstance();
        } else if (INDEX_MYPAGE == i) {
            return MyPageFragment.newInstance();
        } else if (INDEX_SEARCH == i) {
            return SearchFragment.newInstance();
        } else {
            throw new IllegalStateException("Need to send an index that we know");
        }

    }

    private void IntentActivity(Class c){  // 인텐트 함수
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    // 이동하고자 하는 화면을 스택 상단에 남기고 해당 화면의 나머지인 위의 화면들은 모두 삭제 해준다.
        startActivity(intent);
    }

}
