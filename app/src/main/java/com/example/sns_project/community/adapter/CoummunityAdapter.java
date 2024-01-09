package com.example.sns_project.community.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.sns_project.R;
import com.example.sns_project.data.Community;
import com.example.sns_project.databinding.ItemCommunityRowBinding;

public class CoummunityAdapter extends BaseRecyclerAdapter<Community, CoummunityAdapter.ViewHolder> {
    //BaseRecyclerAdapter 상속
    //extends BaseRecyclerAdapter<Community, CoummunityAdapter.ViewHolder> 이부분은..
    //이 리스트뷰에서는 Community 의 dto 클래스를 이용하여 리스트뷰에 아이템을 생성하며
    //그 Community의 각각의 ViewHolder는  아래 ViewHolder로 쓴다는 의미이다.
    public CoummunityAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new ViewHolder(parent);
    }

    public class ViewHolder extends BaseViewHolder<ItemCommunityRowBinding, Community> {

        //초기화 CoummunityAdapter은  R.layout.item_community_row);로 쓰겠다는이야기
        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_community_row);
        }

        //BaseRecyclerAdapter의 53번줄  holder.bind(item,position); 에서 데이터를 보내면 아래의 부분에서 받는다.
        @Override
        protected void bind(Community community, int position) {
            binding.setItem(community);
        }
    }
}
