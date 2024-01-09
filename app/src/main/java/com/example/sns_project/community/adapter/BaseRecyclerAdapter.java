package com.example.sns_project.community.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseRecyclerAdapter<T, H extends BaseViewHolder<? extends ViewDataBinding, T>> extends RecyclerView.Adapter<H> {
    //공통적인 부분은 상속받아서 쓰기위한 baseAdapter

    //리스트를 담을 변수
    protected List<T> itemList;

    //Context를 담을 변수
    protected Context context;

    //클릭이벤트를 담을 변수
    protected OnItemClickListener onItemClickListener;


    //생성자
    public BaseRecyclerAdapter(Context context) {
        this.context = context;
    }    //초기화 될때 BaseRecyclerAdapter을 호출만 Activity에서 context를 받아 변수설정을함

    //클릭 이벤트를 set하기 위해 구현된 함수
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    //지금까지 생성된 ViewHolder에 데이터를 바인딩해주는 함수
    @Override
    public void onBindViewHolder(@NonNull H holder, final int position) {

        holder.itemView.setOnClickListener(view -> {

            // item click listener 등록
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }

        });

        //생성된 ViewHolder에 데이터를 바인딩을위해.. 각 position을 이용해 각각의 아이템을 가져온다
        T item = getItem(position);
        //BaseRecyclerAdapter 을 상속받는 Adapter에 데이터를 넘겨준다.
        //각각의 Adapter에서 화면을 처리하기위함
        holder.bind(item,position);
        onBindView(holder, item, position);
    }


    @Override
    public int getItemCount() {
        if (this.itemList == null) {
            return 0;
        }
        return this.itemList.size();
    }



    // 맨 처음 item list 초기화 또는 , item list 마지막 position 뒤에 추가
    public void addItems(List<T> items) {
        if (this.itemList == null) {
            this.itemList = items;
            notifyDataSetChanged();
        } else {
            int position = this.itemList.size();
            this.itemList.addAll(items);
            notifyItemRangeInserted(position, items.size());
        }
    }


    //item list 마지막 position 뒤에 item 추가
    public void addItem(T item) {
        if (this.itemList == null) {
            this.itemList = new ArrayList<>();
            itemList.add(item);
            notifyDataSetChanged();
        } else {
            int position = this.itemList.size();
            this.itemList.add(item);
            notifyItemInserted(position);
        }
    }

    //item list 전체 삭제
    public void clearItems() {
        if (itemList != null) {
            itemList.clear();
            notifyDataSetChanged();
        }
    }



    //position 위치의 item 삭제
    public void removeItem(int position) {
        if (this.itemList != null && position < this.itemList.size()) {
            this.itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, this.itemList.size());
        }
    }

    public T getItem(int position) {
        if (this.itemList == null) {
            return null;
        }

        return this.itemList.get(position);
    }



    //전체 item list 반환
    public List<T> getItemList() {
        if (this.itemList == null) {
            return null;
        }

        return this.itemList;
    }


    //item list 전체 수정
    public void updateItems(List<T> items) {
        if (this.itemList == null) {
            itemList = new ArrayList<>();
        }
        this.itemList.clear();
        this.itemList.addAll(items);

        notifyDataSetChanged();
    }



    //해당 position 의 item 수정
    public void updateItem(int position, T item) {
        if (this.itemList == null) {
            return;
        }
        if (position > this.itemList.size()) {
            return;
        }
        this.itemList.remove(position);
        this.itemList.add(position, item);

        notifyItemChanged(position);
    }

    protected void onBindView(H holder, T item, int position) { }
}
