package com.example.sns_project.dictionary;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sns_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dict_detail extends AppCompatActivity {

    TextView tv_crop_name, tv_description1, tv_description2;
    ImageView iv_picture, iv_schedule,iv_star_button;
    DatabaseReference ref;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_crop_detail);

        tv_crop_name = findViewById(R.id.tv_crop_name);
        tv_description1 = findViewById(R.id.tv_description1);
        tv_description2 = findViewById(R.id.tv_description2);
        iv_picture = findViewById(R.id.iv_picture);
        iv_schedule = findViewById(R.id.iv_schedule);
        //iv_star_button = findViewById(R.id.star_button_detail);

        ref = FirebaseDatabase.getInstance().getReference("Crop_info");
        //mAuth = FirebaseAuth.getInstance();

        String key = getIntent().getStringExtra("key");


        ref.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                String crop_name = datasnapshot.child("crop_name").getValue().toString();
                String description1 = datasnapshot.child("description1").getValue().toString();
                String description2 = datasnapshot.child("description2").getValue().toString();
                String picture = datasnapshot.child("picture").getValue().toString();
                String schedule = datasnapshot.child("schedule").getValue().toString();
                //String stars = datasnapshot.child("stars").getValue().toString();

                tv_crop_name.setText(crop_name);
                tv_description1.setText(description1);
                tv_description2.setText(description2);
                Glide.with(getApplicationContext()).load(picture).into(iv_picture);
                Glide.with(getApplicationContext()).load(schedule).into(iv_schedule);

//                if(stars != null){
//                    iv_star_button.setImageResource(R.drawable.baseline_grade_black_18);
//                }
//                else{
//                    iv_star_button.setImageResource(R.drawable.baseline_star_border_black_18);
//                }

                //즐겨찾기 기능
//                iv_star_button.setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    public void onClick(View starView){
//                        onStarClicked(ref.child(key));
//                    }
//                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

}
