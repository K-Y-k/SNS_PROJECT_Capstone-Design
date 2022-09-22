package com.example.sns_project.community;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sns_project.FirebaseConst;
import com.example.sns_project.L;
import com.example.sns_project.PasswordResetActivity;
import com.example.sns_project.R;
import com.example.sns_project.adapter.CoummunityAdapter;
import com.example.sns_project.data.Community;
import com.example.sns_project.databinding.FragmentBoardBinding;
import com.example.sns_project.databinding.FragmentSearchBinding;
import com.example.sns_project.event.BoardRefreshEvent;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFragment extends BaseFragment<FragmentSearchBinding> {

    // 검색화면
    private CollectionReference boardRef = FirebaseFirestore.getInstance().collection(FirebaseConst.POST);

    //검색 리스트뷰
    private CoummunityAdapter adapter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    int contentLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    void initViews() {
        L.i("::SearchFragment initViews");
        initRecyclerView();


        //검색창에서 입력후 돋보기버튼클릭시받아오는 이벤트
        binding.textField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                //키보드 제거후
                hideKeyboard();
                //서버에서 키워드를 검색
                fetchSearchItems(textView.getText().toString());
                binding.textField.setText(null);
            }
            return false;
        });

    }


    private void initRecyclerView() {
        adapter = new CoummunityAdapter(requireContext());
        binding.recyclerView.setAdapter(adapter);



        //상세화면 진입
        adapter.setOnItemClickListener(index -> {
            Community item = adapter.getItem(index);
            L.i(":::selected Item " + item);
            startActivity(new Intent(requireActivity(), DetailActivity.class).putExtra("EXTRA_ITEM", item));
        });

    }


    private void fetchSearchItems(String query) {
        L.i(":::fetchSearchItems " + query);
        //로딩 활성화
        binding.progressBar.setVisibility(View.VISIBLE);

        //데이터를 받아온후
        boardRef.get().addOnSuccessListener(queryDocumentSnapshots -> {

            List<Community> list = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // L.i(document.getId() + " => " + document.toObject(Community.class));
                list.add(document.toObject(Community.class));
            }

            //검색어를 필터링한다.
            List<Community> filter = list.stream().filter(item -> item.getTitle().contains(query)).collect(Collectors.toList());

            //필터링된 결과가없으면?
            if(filter.isEmpty()){
                Toast.makeText(requireActivity(), "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
                adapter.clearItems();
                binding.progressBar.setVisibility(View.GONE);
                return;
            }

            L.i(":::::filter " + filter.size());

            //로딩 비활성화
            binding.progressBar.setVisibility(View.GONE);

            //필터링된 데이터 리스트뷰 갱신
            adapter.clearItems();
            adapter.addItems(filter);

        }).addOnFailureListener(e -> {
            binding.progressBar.setVisibility(View.GONE);
            L.e("::::::onFailure " + e.getMessage());
        });
    }


    //키보드 제거
    private void hideKeyboard() {
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            String serviceName = Context.INPUT_METHOD_SERVICE;
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(serviceName);
            int stateHide = InputMethodManager.HIDE_NOT_ALWAYS;
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), stateHide);
        }
    }

}
