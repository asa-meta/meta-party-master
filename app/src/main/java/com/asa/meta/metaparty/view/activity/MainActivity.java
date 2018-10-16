package com.asa.meta.metaparty.view.activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.asa.meta.basehabit.base.LanguageActivity;
import com.asa.meta.helpers.language.LocalManageUtil;
import com.asa.meta.metaparty.BR;
import com.asa.meta.metaparty.R;
import com.asa.meta.metaparty.databinding.ActivityMainBinding;
import com.asa.meta.metaparty.viewmodel.MainViewModel;

import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.SimpleRxGalleryFinal;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IMultiImageCheckedListener;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;

public class MainActivity extends LanguageActivity<ActivityMainBinding, MainViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public MainViewModel initViewModel() {
        return ViewModelProviders.of(this).get(MainViewModel.class);
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.switchLanguage.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LocalManageUtil.saveSelectLanguage(mContext, integer);
                reStart(mContext);
            }
        });

        viewModel.openGallery.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                openGallery();
            }
        });

        viewModel.openCamera.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                SimpleRxGalleryFinal.get().init(new SimpleRxGalleryFinal.RxGalleryFinalCropListener() {

                    @Override
                    public Activity getSimpleActivity() {
                        return MainActivity.this;
                    }

                    @Override
                    public void onCropCancel() {
                        Log.i(TAG, "onCropCancel: ");
                    }

                    @Override
                    public void onCropSuccess(@Nullable Uri uri) {
                        Log.i(TAG, "onCropSuccess: " + uri.getPath());
                        viewModel.setPhonePath(uri.getPath());
                    }

                    @Override
                    public void onCropError(String errorMessage) {
                        Log.i(TAG, "onCropError: " + errorMessage);
                        viewModel.showToast(errorMessage);
                    }
                }).openCamera();
            }
        });
    }

    @Override
    public void initView() {
        initGallery();
    }

    private void initGallery() {
        RxGalleryListener.getInstance().setRadioImageCheckedListener(new IRadioImageCheckedListener() {
            @Override
            public void cropAfter(Object t) {
                Log.i(TAG, "cropAfter: " + t.toString());
            }

            @Override
            public boolean isActivityFinish() {
                return false;
            }
        });

        RxGalleryListener
                .getInstance()
                .setMultiImageCheckedListener(
                        new IMultiImageCheckedListener() {
                            @Override
                            public void selectedImg(Object t, boolean isChecked) {
                                viewModel.showToast(isChecked ? "选中" : "取消选中");
                            }

                            @Override
                            public void selectedImgMax(Object t, boolean isChecked, int maxSize) {
                                viewModel.showToast("你最多只能选择" + maxSize + "张图片");
                            }
                        });
    }

    private void openGallery() {
        RxGalleryFinalApi.getInstance(this).openGalleryRadioImgDefault(new RxBusResultDisposable<ImageRadioResultEvent>() {
            @Override
            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                Log.i(TAG, "onEvent: " + imageRadioResultEvent.getResult().getOriginalPath());
            }
        });
    }

}