package com.example.sns_project.community;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.sns_project.FirebaseConst;
import com.example.sns_project.L;
import com.example.sns_project.MemberInfo;
import com.example.sns_project.R;
import com.example.sns_project.data.Community;
import com.example.sns_project.databinding.FragmentPostBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.util.FileUriUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostFragment extends BaseFragment<FragmentPostBinding> {

    private StorageReference imageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    private String filePath = null;


    public static PostFragment newInstance() {
        return new PostFragment();
    }

    @Override
    int contentLayoutId() {
        return R.layout.fragment_post;
    }

    @Override
    void initViews() {
        L.i("::PostFragment initViews");


        binding.btnGallery.setOnClickListener(view -> ImagePicker.with(PostFragment.this)
                .galleryOnly()
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());

        binding.btnRegister.setOnClickListener(view -> {

            if (TextUtils.isEmpty(binding.etContent.getText()) || TextUtils.isEmpty(binding.etTitle.getText())) {
                Toast.makeText(requireActivity(), "양식을 채워주세요", Toast.LENGTH_LONG).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();                        // 현재 user 객체 생성
            FirebaseFirestore db = FirebaseFirestore.getInstance();                                 // 파이어베이스에 접근할 db생성
            DocumentReference docRef = db.collection("users").document(user.getUid());  // db와 연결한 문서내용을 확인한다.
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {                                // 성공시
                        DocumentSnapshot document = task.getResult();         // 결과를 document에 넣는다.
                        String userName = document.getString("userName"); //document에 사용자가 입력했던 닉네임 내용을 저장한다.

                        DocumentReference postDocument = dbRef.collection(FirebaseConst.POST).document();

                        String addedbyUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String uid = postDocument.getId(); //키값 자동생성.
                        String title = binding.etTitle.getText().toString();
                        String content = binding.etContent.getText().toString();

                        Community postCommunity = new Community();

                        postCommunity.setAddedbyUser(addedbyUser);
                        postCommunity.setUserName(userName);
                        postCommunity.setUid(uid);
                        postCommunity.setTitle(title);
                        postCommunity.setContent(content);
                        postCommunity.setTimeStemp(System.currentTimeMillis());

                        if (filePath != null) {
                            Toast.makeText(requireActivity(), "업로드 중입니다.", Toast.LENGTH_LONG).show();

                            String fileFormat = filePath.substring(filePath.lastIndexOf(".") + 1);
                            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
                            String saveFileName = String.format("%s.%s", fileName, fileFormat);

                            imageRef.child(saveFileName).putFile(Uri.parse(filePath)).addOnSuccessListener(taskSnapshot -> {

                                if (taskSnapshot.getMetadata() != null) {
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            postCommunity.setImgPath(uri.toString());
                                            postDocument.set(postCommunity).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(requireActivity(), "성공", Toast.LENGTH_LONG).show();
                                                    clear();
                                                    //성공
                                                } else {
                                                    Toast.makeText(requireActivity(), "실패", Toast.LENGTH_LONG).show();
                                                    //실패
                                                }
                                            });
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    L.e(":::::::::::::::::::::::E " + e.getMessage());
                                }
                            });
                        } else {
                            //선택한 사진이 없을시.
                            postDocument.set(postCommunity).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) { //성공
                                    Toast.makeText(requireActivity(), "성공", Toast.LENGTH_LONG).show();
                                    L.i(":::::성공....");
                                    clear();
                                } else { //실패
                                    Toast.makeText(requireActivity(), "실패", Toast.LENGTH_LONG).show();
                                    L.i(":::::실패....");
                                }
                            });
                        }

                    } else {
                    }
                }
            });
        });
    }

    private void clear() {
        binding.etTitle.setText(null);
        binding.etContent.setText(null);
        binding.ivSelectedPhoto.setImageURI(null);
        filePath = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.i("::::::::::::::onActivityResult");

        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                //선택한 이미지.
                Uri uri = data.getData();
                filePath = uri.toString();
                L.i(":::filePath " + filePath);
                binding.ivSelectedPhoto.setImageURI(uri);

            }
        }
    }

}
