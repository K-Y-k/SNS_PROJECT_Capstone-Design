package com.example.sns_project.util;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class GlideUtil {
    @BindingAdapter({"imageCenterCrop"})
    public static void loadImageCenterCrop(ImageView imageView, String url){

        Glide.with(imageView).load(url).centerCrop().into(imageView);
    }
}
