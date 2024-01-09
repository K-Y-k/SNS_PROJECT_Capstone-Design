package com.example.sns_project.community.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.sns_project.R;
import com.example.sns_project.data.Community;
import com.example.sns_project.databinding.ItemMyCommunityRowBinding;

public abstract class MyCoummunityAdapter extends BaseRecyclerAdapter<Community, MyCoummunityAdapter.ViewHolder> {
    //BaseRecyclerAdapter 상속
    //extends BaseRecyclerAdapter<Community, MyCoummunityAdapter.ViewHolder> 이부분은..
    //이 리스트뷰에서는 Community 의 dto 클래스를 이용하여 리스트뷰에 아이템을 생성하며
    //그 Community의 각각의 ViewHolder는  아래 ViewHolder로 쓴다는 의미이다.
    abstract protected void deletedItem(Community community);

    public MyCoummunityAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new ViewHolder(parent);
    }

    public class ViewHolder extends BaseViewHolder<ItemMyCommunityRowBinding, Community> {

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_my_community_row);
        }

        @Override
        protected void bind(Community community, int position) {
            binding.setItem(community);
            binding.btnDelete.setOnClickListener(view -> {
                deletedItem(community);
            });
        }


    }
}
