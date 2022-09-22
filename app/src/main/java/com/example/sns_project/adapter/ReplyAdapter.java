package com.example.sns_project.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.sns_project.R;
import com.example.sns_project.data.Message;
import com.example.sns_project.databinding.ItemReplyRowBinding;

public class ReplyAdapter extends BaseRecyclerAdapter<Message, ReplyAdapter.ViewHolder> {
    //BaseRecyclerAdapter 상속
    //extends BaseRecyclerAdapter<Message, ReplyAdapter.ViewHolder> 이부분은..
    //이 리스트뷰에서는 Message 의 dto 클래스를 이용하여 리스트뷰에 아이템을 생성하며
    //그 Message의 각각의 ViewHolder는  아래 ViewHolder로 쓴다는 의미이다.
    public ReplyAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new ViewHolder(parent);
    }

    public class ViewHolder extends BaseViewHolder<ItemReplyRowBinding, Message> {

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_reply_row);
        }

        @Override
        protected void bind(Message message, int position) {
            binding.setItem(message);
        }
    }
}
