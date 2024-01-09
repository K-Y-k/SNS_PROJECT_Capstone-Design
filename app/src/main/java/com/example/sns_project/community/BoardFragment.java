package com.example.sns_project.community;

import android.content.Intent;
import android.view.View;

import com.example.sns_project.R;
import com.example.sns_project.community.adapter.CoummunityAdapter;
import com.example.sns_project.data.Community;
import com.example.sns_project.databinding.FragmentBoardBinding;
import com.example.sns_project.community.event.BoardRefreshEvent;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends BaseFragment<FragmentBoardBinding> {
    //게시물 사람들이 올린 커뮤니티 구경.

    private CollectionReference boardRef = FirebaseFirestore.getInstance().collection(FirebaseConst.POST);

    private CoummunityAdapter adapter;

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    @Override
    int contentLayoutId() {
        return R.layout.fragment_board;
    }

    @Override
    void initViews() {
        L.i("::BoardFragment initViews");
        //리스트뷰 생성 및 firestsore에서 데이터를 요청한다.
        initRecyclerView();
        fetchBoardItems();
    }

    @Override
    public void onStart() {
        super.onStart();
        //이벤트 버스 등록
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }


    @Override
    public void onDestroyView() {
        //이벤트 버스 해제
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }


    //이벤트 버스 callback 함수
    //이벤트버스를 사용하는 이유는 다른화면에서 데이터업데이트를 할시 갱신이필요해서 사용
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBoardUpdate(BoardRefreshEvent event){
        L.d("onBoardUpdate... Subscribe");
         fetchBoardItems();
    }


    //리스트뷰 셋팅
    private void initRecyclerView() {
        adapter = new CoummunityAdapter(requireContext());
        binding.recyclerView.setAdapter(adapter);


        //아이탬 클릭시
        adapter.setOnItemClickListener(index -> {
            Community item = adapter.getItem(index);
            L.i(":::selected Item " + item);
            //상세화면으로 전환
            startActivity(new Intent(requireActivity(),DetailActivity.class).putExtra("EXTRA_ITEM",item));
        });
    }


    //게시물 데이터요청
    private void fetchBoardItems() {
        //로딩 on
        binding.progressBar.setVisibility(View.VISIBLE);

        //내림차순 청렬 올린시간에따라
        boardRef.orderBy("timeStemp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {


            List<Community> list = new ArrayList<>();

            //데이터를 받아서 list에 담는다.
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                L.i(document.getId() + " => " + document.toObject(Community.class));
                list.add(document.toObject(Community.class));
            }
            //로딩 off
            binding.progressBar.setVisibility(View.GONE);


            //리스트뷰를 한번 클리어하고 다시그린다.
            adapter.clearItems();

            //데이터 삽입
            adapter.addItems(list);

        }).addOnFailureListener(e -> {
            //로딩 off
            binding.progressBar.setVisibility(View.GONE);
            L.e("::::::onFailure " + e.getMessage());
        });
    }


}
