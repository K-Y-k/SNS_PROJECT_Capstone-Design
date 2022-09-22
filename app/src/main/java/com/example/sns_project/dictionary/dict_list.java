package com.example.sns_project.dictionary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sns_project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public class dict_list extends Fragment {

    RecyclerView recyclerview;
    DatabaseReference mUserDatabase;
    EditText mSearchField;
    ImageButton mSearchBtn;
    FirebaseRecyclerAdapter adapter;
    FirebaseAuth mAuth;
    private FirebaseUser mUser;

//    public dict_list(){
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_crop_list, container, false);

        recyclerview = (RecyclerView)view.findViewById(R.id.recyclerView);
        mSearchField = (EditText) view.findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) view.findViewById(R.id.imageButton);


        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchField.getWindowToken(), 0);

                String searchText = mSearchField.getText().toString();
                firebaseUserSearch(searchText);
            }
        });


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Crop_info");

        recyclerview.setItemAnimator(null); //*********수정필요*********
        recyclerview.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        recyclerview.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()); //레이아웃매니저 생성
        recyclerview.setLayoutManager(layoutManager);

        return view;
    }





    @Override
    public void onStart() {
        super.onStart();


        Query query = mUserDatabase;
        FirebaseRecyclerOptions<Crop_info> options = new FirebaseRecyclerOptions.Builder<Crop_info>() //어떤데이터를 어디서갖고올거며 어떠한 형태의 데이터클래스 결과를 반환할거냐 옵션을 정의한다.
                .setQuery(query, Crop_info.class)
                .build();






        adapter = new FirebaseRecyclerAdapter<Crop_info, UserViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Crop_info model) {
                final String key = getRef(position).getKey();

                Glide.with(getActivity().getApplicationContext())
                        .load(model.getPicture())
                        .into(holder.iv_picture);
                holder.tv_crop_name.setText(model.getCrop_name());
                holder.tv_category.setText(model.getCategory());

                mAuth = FirebaseAuth.getInstance();
                if (model.stars.containsKey(mAuth.getCurrentUser().getUid())) {
                    holder.iv_star_button.setImageResource(R.drawable.baseline_grade_black_18);
                } else {
                    holder.iv_star_button.setImageResource(R.drawable.baseline_star_border_black_18);
                }

                //즐겨찾기 기능
                holder.iv_star_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        onStarClicked(mUserDatabase.child(key));
                    }
                });




                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), dict_detail.class);
                        intent.putExtra("key", key);
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_crop, parent, false);

                return new UserViewHolder(view);
            }
        };
        recyclerview.setAdapter(adapter);
        adapter.startListening();

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_picture;
        private TextView tv_crop_name;
        private TextView tv_category;
        private ImageView iv_star_button;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_picture = itemView.findViewById(R.id.iv_picture);
            tv_crop_name = itemView.findViewById(R.id.tv_crop_name);
            tv_category = itemView.findViewById(R.id.tv_category);
            iv_star_button = itemView.findViewById(R.id.star_button);

        }


    }

    @Override public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    private void firebaseUserSearch(String searchText){

        Query firebaseSearchQuery = mUserDatabase.orderByChild("crop_name").startAt(searchText).endAt(searchText + "\uf8ff");

        Query query = mUserDatabase;
        FirebaseRecyclerOptions<Crop_info> options1 = new FirebaseRecyclerOptions.Builder<Crop_info>() //어떤데이터를 어디서갖고올거며 어떠한 형태의 데이터클래스 결과를 반환할거냐 옵션을 정의한다.
                .setQuery(query, Crop_info.class)
//                .setLifecycleOwner(this)
                .build();


        FirebaseRecyclerOptions<Crop_info> option2 = new FirebaseRecyclerOptions.Builder<Crop_info>()
                .setQuery(firebaseSearchQuery,Crop_info.class)
//                .setLifecycleOwner(this)
                .build();


        if (searchText=="") {
            adapter.updateOptions(options1);

        }
        else {
            adapter.updateOptions(option2);
        }

    }
    

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Crop_info crop_info = mutableData.getValue(Crop_info.class);
                if (crop_info == null) {
                    return Transaction.success(mutableData);
                }

                if (crop_info.stars.containsKey(mAuth.getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    crop_info.starCount = crop_info.starCount - 1;
                    crop_info.stars.remove(mAuth.getCurrentUser().getUid());
                } else {
                    // Star the post and add self to stars
                    crop_info.starCount = crop_info.starCount + 1;
                    crop_info.stars.put(mAuth.getCurrentUser().getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(crop_info);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed

            }
        });
    }


}


