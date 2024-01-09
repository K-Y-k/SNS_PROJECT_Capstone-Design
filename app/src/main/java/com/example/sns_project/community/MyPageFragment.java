package com.example.sns_project.community;

import android.view.View;
import android.widget.Toast;

import com.example.sns_project.R;
import com.example.sns_project.community.adapter.MyCoummunityAdapter;
import com.example.sns_project.data.Community;
import com.example.sns_project.databinding.FragmentMypageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyPageFragment extends BaseFragment<FragmentMypageBinding> {
    //마이페이지

    private CollectionReference boardRef = FirebaseFirestore.getInstance().collection(FirebaseConst.POST);

    private MyCoummunityAdapter adapter;

    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }

    @Override
    int contentLayoutId() {
        return R.layout.fragment_mypage;
    }

    @Override
    void initViews() {
        L.i("::MyPageFragment initViews");
        initRecyclerView();
        fetchMyBoardItems();
    }


    private void initRecyclerView() {
        adapter = new MyCoummunityAdapter(requireContext()) {
            @Override
            protected void deletedItem(Community community) {
                L.i(":::::cdeletedItem " + community);
                boardRef.document(community.getUid()).delete();
                adapter.removeItem(adapter.getItemList().indexOf(community));
                Toast.makeText(requireActivity(), "삭제 되었습니다.", Toast.LENGTH_LONG).show();
            }
        };
        binding.recyclerView.setAdapter(adapter);
    }

    private void fetchMyBoardItems() {
        //로그인된 유저들의 정보만 요청한다. whereEqualTo 참고
        Query query = FirebaseFirestore.getInstance().collection(FirebaseConst.POST).whereEqualTo("addedbyUser", FirebaseAuth.getInstance().getCurrentUser().getUid());
        binding.progressBar.setVisibility(View.VISIBLE);
        query.orderBy("timeStemp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {


            List<Community> list = new ArrayList<>();

            //데이터를 받아서 list에 담는다.
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                L.i(document.getId() + " => " + document.toObject(Community.class));
                list.add(document.toObject(Community.class));
            }

            //로딩 off
            binding.progressBar.setVisibility(View.GONE);
            adapter.clearItems();
            adapter.addItems(list);

        }).addOnFailureListener(e -> {
            binding.progressBar.setVisibility(View.GONE);
            L.e("::::::onFailure " + e.getMessage());
        });
    }


}
