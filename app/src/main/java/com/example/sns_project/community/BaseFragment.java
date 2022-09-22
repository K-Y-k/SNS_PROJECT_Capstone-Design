package com.example.sns_project.community;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {
    //공통된 부분들을 관리하는 프래그먼트

    //layout 구조체.
    @LayoutRes
    abstract int contentLayoutId();


    //View 설정 구조체.
    abstract void initViews();


    protected DataBindingComponent bindingComponent = DataBindingUtil.getDefaultComponent();


    //binding 객체를 통해 xml 을 접근함.
    protected T binding = null;




    //binding 설정. contentLayoutId 의 binding 객체를 만든다
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, contentLayoutId(), container, false, bindingComponent);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }


    //binding 해제제
    @Override
   public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.unbind();
        }
    }
}
