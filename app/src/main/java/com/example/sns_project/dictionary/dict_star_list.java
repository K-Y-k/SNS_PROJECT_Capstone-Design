package com.example.sns_project.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sns_project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public class dict_star_list extends Fragment {

    RecyclerView recyclerview;
    DatabaseReference mUserDatabase;
    FirebaseRecyclerAdapter adapter;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_star_list,container,false);

        recyclerview = (RecyclerView)v.findViewById(R.id.recyclerView);


        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Crop_info");
        recyclerview.setItemAnimator(null); //*********수정필요*********
        recyclerview.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        //recyclerview.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2); //레이아웃매니저 생성
        recyclerview.setLayoutManager(layoutManager);


        return v;
    }


    @Override
    public void onStart() {
        super.onStart();


        Query firebaseStarQuery = mUserDatabase.orderByChild("starCount").equalTo(1);

        FirebaseRecyclerOptions<Crop_info> options = new FirebaseRecyclerOptions.Builder<Crop_info>()
                .setQuery(firebaseStarQuery, Crop_info.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Crop_info, UserViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Crop_info model) {
                final DatabaseReference ref = getRef(position);
                final String key1 = getRef(position).getKey();
                final String key2 = getRef(position).getKey();

                Glide.with(getActivity().getApplicationContext())
                        .load(model.getPicture())
                        .into(holder.iv_picture);
                holder.tv_crop_name.setText(model.getCrop_name());



                if (model.stars.containsKey(mAuth.getCurrentUser().getUid())) {
                    holder.iv_star_button.setImageResource(R.drawable.baseline_grade_black_18);
                } else {
                    holder.iv_star_button.setImageResource(R.drawable.baseline_star_border_black_18);
                }

                //즐겨찾기 기능
                holder.iv_star_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        onStarClicked(mUserDatabase.child(key2));
                    }
                });



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), dict_detail.class);
                        intent.putExtra("key", key1);
                        startActivity(intent);

                    }
                });
            }



            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_crop_star, parent, false);

                return new UserViewHolder(view);
            }
        };
        recyclerview.setAdapter(adapter);
        adapter.startListening();

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_picture;
        private TextView tv_crop_name;
        private ImageView iv_star_button;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_picture = itemView.findViewById(R.id.iv_picture);
            tv_crop_name = itemView.findViewById(R.id.tv_crop_name);
            iv_star_button = itemView.findViewById(R.id.star_button);

        }


    }

    @Override public void onStop()
    {
        super.onStop();
        adapter.stopListening();
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
