package com.example.sns_project.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;


public abstract class BaseViewHolder<B extends ViewDataBinding, D> extends RecyclerView.ViewHolder {
    protected B binding;
    private Context context;

    public BaseViewHolder(ViewGroup parent, @LayoutRes int layoutResId) { //BaseViewHolder 생성자 초기화.  ViewHolder에 쓸 View들을 셋팅한다.
        super(LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false));
        this.binding = DataBindingUtil.bind(itemView);
        context = itemView.getContext();
    }

    protected abstract void bind(D data, int position);

    protected void recycled() {
        // no-op
    }

    protected Context getContext() {
        return context;
    }
}
