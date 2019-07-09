package com.suncere.app.takephotodemo;


import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.listener.OnCheckedListener;
import com.zhihu.matisse.listener.OnSelectedListener;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends TakePhotoActivity implements View.OnClickListener {

    Button Matisse_btn, navitive_btn,to_fragment_btn;
    ImageView result_iv;

    private ActionSheetDialog actionSheet;

    private TakePhoto takePhoto;
    private CustomHelper mCustomHelper;

    private final int REQUEST_CODE_CHOOSE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Matisse_btn = (Button) findViewById(R.id.Matisse_btn);
        Matisse_btn.setOnClickListener(this);
        navitive_btn = findViewById(R.id.navitive_btn);
        navitive_btn.setOnClickListener(this);
        to_fragment_btn=findViewById(R.id.to_fragment_btn);
        to_fragment_btn.setOnClickListener(this);

        result_iv = findViewById(R.id.result_iv);

        Log.e("lys", "包名：" + getPackageName());

        mCustomHelper = CustomHelper.of();
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }


    @Override
    public void onClick(final View v) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            switch (v.getId()) {
                                case R.id.Matisse_btn:
                                    Matisse.from(MainActivity.this)
                                            .choose(MimeType.ofAll(), false)
                                            .countable(true)
                                            .capture(true)
                                            .captureStrategy(new CaptureStrategy(true, getPackageName() + ".fileprovider"))
                                            .maxSelectable(9)
                                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                            .gridExpectedSize(
                                                    getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                            .thumbnailScale(0.85f)
//                                            .imageEngine(new GlideEngine())  // for glide-V3
                                            .imageEngine(new Glide4Engine())    // for glide-V4
                                            .setOnSelectedListener(new OnSelectedListener() {
                                                @Override
                                                public void onSelected(
                                                        @NonNull List<Uri> uriList, @NonNull List<String> pathList) {
                                                    // DO SOMETHING IMMEDIATELY HERE
                                                    Log.e("onSelected", "onSelected: pathList=" + pathList);
                                                }
                                            })
                                            .originalEnable(true)
                                            .maxOriginalSize(10)
                                            .setOnCheckedListener(new OnCheckedListener() {
                                                @Override
                                                public void onCheck(boolean isChecked) {
                                                    // DO SOMETHING IMMEDIATELY HERE
                                                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                                                }
                                            })
                                            .forResult(REQUEST_CODE_CHOOSE);
                                    break;

                                case R.id.navitive_btn:
                                    showSheet();
                                    break;

                                case R.id.to_fragment_btn:
                                    Intent intent = new Intent(MainActivity.this, FragmentTestActivity.class);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showSheet() {
        actionSheet = new ActionSheetDialog.DialogBuilder(this)
                .addSheet("拍照", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomHelper.takePhoto(1, getTakePhoto());
                        actionSheet.dismiss();
                    }
                })
                .addSheet("从相册中选取", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomHelper.takePhoto(2, getTakePhoto());
                        actionSheet.dismiss();
                    }
                })
                .addCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actionSheet.dismiss();
                    }
                })
                .create();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> result = Matisse.obtainResult(data);
            if (!result.isEmpty()) {
//                Glide.with(MainActivity.this).load(result.get(0)).transform(new CircleCrop()).into(result_iv);//圆形
//                Glide.with(MainActivity.this).load(result.get(0)).transform(new RoundedCorners(100)).into(result_iv);//圆角
                Glide.with(MainActivity.this).load(result.get(0)).into(result_iv);//无处理
            }
        }
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        File imgFile = new File(result.getImage().getCompressPath());
//        Glide.with(MainActivity.this).load(imgFile).transform(new RoundedCorners(100)).into(result_iv);//圆角
        Glide.with(MainActivity.this).load(imgFile).into(result_iv);//无处理
    }

}
