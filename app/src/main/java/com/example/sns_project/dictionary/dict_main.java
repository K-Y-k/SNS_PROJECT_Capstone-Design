package com.example.sns_project.dictionary;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.sns_project.R;
import com.google.android.material.tabs.TabLayout;

public class dict_main extends AppCompatActivity {

    //private static final String TAG = "Main_Activity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict_main);

        tabLayout=findViewById(R.id.tablaout_id);
        viewPager=findViewById(R.id.viewpager_id);
        adapter=new ViewPagerAdapter(getSupportFragmentManager(),1);

        //FragmentAdapter에 컬렉션 담기
        adapter.addFragment(new dict_list());
        adapter.addFragment(new dict_star_list());

        //ViewPager Fragment 연결
        viewPager.setAdapter(adapter);

        //ViewPager과 TabLayout 연결
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.tab_icon_search);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_grade_black_18);
    }

    public void onBackPressed() {   // 뒤로가기로 누를 시
        super.onBackPressed();;
    }
}