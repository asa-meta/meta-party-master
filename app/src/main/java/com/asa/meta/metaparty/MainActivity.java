package com.asa.meta.metaparty;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.asa.meta.helpers.files.FileUtils;
import com.asa.meta.helpers.files.InstallApkUtils;
import com.asa.meta.helpers.notify.NotifyHelper;
import com.asa.meta.helpers.notify.NotifySettingUtils;
import com.asa.meta.helpers.os.OSRomUtils;
import com.asa.meta.helpers.service.ServiceUtils;
import com.asa.meta.helpers.test.TestActivity;
import com.asa.meta.helpers.toast.ToastUtils;
import com.asa.meta.metaparty.databinding.ActivityMainBinding;
import com.asa.meta.metaparty.socket.UDPService;
import com.asa.meta.rxhttp.cache.model.CacheMode;
import com.asa.meta.rxhttp.callback.DownloadProgressCallBack;
import com.asa.meta.rxhttp.exception.ApiException;
import com.asa.meta.rxhttp.interfaces.ProgressDialog;
import com.asa.meta.rxhttp.main.RxHttp;
import com.asa.meta.rxhttp.subsciber.JSONObjectSubscriber;
import com.asa.meta.rxhttp.subsciber.ProgressDialogSubscriber;
import com.asa.meta.rxhttp.utils.HttpLog;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;

import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.SimpleRxGalleryFinal;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IMultiImageCheckedListener;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;

public class MainActivity extends TestActivity implements ProgressDialog {
    private android.app.ProgressDialog progressDialog;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setOnClickEvent(new OnClickEvent(this));
        progressDialog = new android.app.ProgressDialog(this);
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
                                Toast.makeText(getBaseContext(), isChecked ? "选中" : "取消选中", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void selectedImgMax(Object t, boolean isChecked, int maxSize) {
                                Toast.makeText(getBaseContext(), "你最多只能选择" + maxSize + "张图片", Toast.LENGTH_SHORT).show();
                            }
                        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SimpleRxGalleryFinal.get().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showProgressDialog() {
        HttpLog.d("----------showProgressDialog---------------");
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        HttpLog.d("----------hideProgressDialog---------------");
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    public class OnClickEvent {
        private Activity context;
        private int i;

        public OnClickEvent(Activity context) {
            this.context = context;
        }

        public void buildSerivce(View view) {
            try {
                UDPService.getInstance().startHeartThread();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        public void buildClient(View view) {
            try {
                UDPService.getInstance().startClient();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        public void stopReceiveClient(View view) {
            try {
                UDPService.getInstance().sentStopMessage();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        public void check(View view) {
            ToastUtils.showToast("手机版本信息：" + OSRomUtils.getSystemInfo().toString());
        }

        public void expNotify(View view) {
            NotifySettingUtils.expandNotification(context);
        }

        public void collNotify(View view) {
            NotifySettingUtils.collapsingNotification(context);
        }

        public void onClickXiaoMi(View view) {
            if (!OSRomUtils.getSystem().equals(OSRomUtils.SYS_MIUI)) {
                ToastUtils.showToast("不是小米手机，这个手机是：" + OSRomUtils.getSystemInfo().toString());
                return;
            }

            try {
                NotifySettingUtils.openMIUINotifySetting(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onClickNotify1(View view) {
            i++;
//            NotifyController.notifyTest2(context, "测试1", "测试：" + i);

            ServiceUtils.startService(context, new Intent(context, MyService.class));
        }

        public void checkNotify(View view) {
            mBinding.btnCheckNotify.setText("是否有通知权限：" + NotifySettingUtils.hasNotifyPermission(context));
        }

        public void onClickNotify2(View view) {
            NotifyController.notifyTest3(context, "测试2", "鸟儿啄完稻谷，轻轻梳理着光润的羽毛。\n" +
                    "“现在你爱这稻谷吗？”精灵又取出一束黄澄澄的稻谷。\n" +
                    "鸟儿抬头望着远处的一湾泉水回答：“现在我爱那一湾泉水，我有点渴了。”\n" +
                    "精灵摘下一片树叶，里面盛了一汪泉水。\n" +
                    "鸟儿喝完泉水，准备振翅飞去。");
        }

        public void onClickNotify3(View view) {
            final NotifyHelper notifyHelper = NotifyController.notifyProgress(context);
            RxHttp.downLoad("http://zhstatic.zhihu.com/pkg/store/daily/zhihu-daily-zhihu-2.6.0(744)-release.apk")
                    .saveName("zhihu.apk")
                    .execute(TAG, new DownloadProgressCallBack<String>() {
                        private int cacheProgress;

                        @Override
                        public void onStart() {
                            HttpLog.e("下载开始");
                            mBinding.btnDownload.setText("下载开始");
                            NotifyController.notifyProgressStart(notifyHelper).notifyProgress(0);
                        }

                        @Override
                        public void onError(ApiException e) {
                            HttpLog.e("下载错误" + e.getMessage());
                            mBinding.btnDownload.setText("下载错误");
                            NotifyController.notifyProgressFail(notifyHelper, e.getMessage()).notifyProgressEnd();
                        }

                        @Override
                        public void update(long bytesRead, long contentLength, boolean done) {
                            int progress = (int) (bytesRead * 100 / contentLength);
                            if (cacheProgress == progress && contentLength != 100) {
                                return;
                            }

                            cacheProgress = progress;
                            HttpLog.e(progress + "% ");
                            NotifyController.notifyProgressIng(notifyHelper).notifyProgress(progress);
                            mBinding.btnDownload.setText(progress + "%");
                            if (done) {//下载完成
                                HttpLog.e("下载完成");

                            }
                        }

                        @Override
                        public void onComplete(String path) {
                            HttpLog.e("保存的路径" + path);
                            File file = new File(path);
                            if (FileUtils.checkFile(file) && InstallApkUtils.checkApkFile(mContext, file.getPath())) {
                                mBinding.btnDownload.setText("下载完成:点击重新下载");
                                NotifyController.notifyProgressEnd(notifyHelper, mContext, file).notifyProgressEnd();
                                InstallApkUtils.install(mContext, file);
                            }
                        }
                    });
        }

        public void onClickNotify4(View view) {
            RxHttp.getInstance()
                    .get("version/android/2.3.0")
                    .cacheMode(CacheMode.FIRSTCACHE)
                    .cacheKey("test1")
                    .execute()
                    .subscribe(new ProgressDialogSubscriber(TAG, MainActivity.this) {
                        @Override
                        public void onError(ApiException e) {
                            Log.e(TAG, "onError: ", e);
                        }

                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            HttpLog.e(Thread.currentThread().getName());
                            mBinding.showTextView.setText(jsonObject.toString());
                        }
                    });
        }

        public void onClickNotify5(View view) {
            RxHttp.post(API.URL_MEMBER_LOGIN)
                    .params("loginName", "13726279075")
                    .params("password", "000000")
                    .execute()
                    .subscribe(new JSONObjectSubscriber(TAG) {
                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            mBinding.showTextView.setText(jsonObject.toString());
                        }

                        @Override
                        public void onError(ApiException e) {

                        }

                        @Override
                        public void onFinish() {

                        }
                    });

        }

        public void onClickHuaWei(View view) {
            if (!OSRomUtils.getSystem().equals(OSRomUtils.SYS_EMUI)) {
                ToastUtils.showToast("不是华为手机，这个手机是：" + OSRomUtils.getSystemInfo().toString());
                return;
            }
            NotifySettingUtils.openOtherNotifySetting(context);

        }

        public void onClickOther(View view) {
            NotifySettingUtils.openOtherNotifySetting(context);
        }

        public void onClickOpenGallery(View view) {
            RxGalleryFinalApi.getInstance(context).openGalleryRadioImgDefault(new RxBusResultDisposable<ImageRadioResultEvent>() {
                @Override
                protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                    Log.i(TAG, "onEvent: " + imageRadioResultEvent.getResult().getOriginalPath());
                }
            });
        }

        public void openCamera(View view) {
            SimpleRxGalleryFinal.get().init(new SimpleRxGalleryFinal.RxGalleryFinalCropListener() {
                @NonNull
                @Override
                public Activity getSimpleActivity() {
                    return context;
                }

                @Override
                public void onCropCancel() {
                    Log.i(TAG, "onCropCancel: ");
                }

                @Override
                public void onCropSuccess(@Nullable Uri uri) {
                    Log.i(TAG, "onCropSuccess: " + uri.getPath());
                    Glide.with(context).load(uri).into(mBinding.ivTest);
                }

                @Override
                public void onCropError(@NonNull String errorMessage) {
                    Log.i(TAG, "onCropError: " + errorMessage);
                }
            }).openCamera();
        }

    }
}
