package com.suncere.app.takephotodemo;

import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.LubanOptions;
import org.devio.takephoto.model.TakePhotoOptions;

import java.io.File;


/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:4.0.0
 * 技术博文：http://www.devio.org
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
public class CustomHelper {

    int limit;//最多选择几张照片，选择多张图片时会自动切换到TakePhoto自带的相册
    boolean mIsCrop;//是否裁切 true：是  false:否
    boolean mWithWonCrop;//裁切工具 true;自带  false:第三方
    boolean mIsAspect;//尺寸/比例：true:尺寸 宽X高  false:比例：宽/高
    int mPhotoFrom;//1：从文件   2：从相册
    boolean mIsCorrect;//纠正拍照的照片旋转角度： true：是  false:否
    boolean mIsPickWithOwn;//使用TakePhoto自带相册：true:是  false:否
    boolean mIsCompress;//是否压缩 true:压缩   false:不压缩
    boolean mIsShowProgressBar;//是否显示压缩进度条
    boolean enableRawFile;//拍照压缩后是否保存原图：true:保存  false:否
    int mCompressToolType;//压缩工具 1：自带  2：Luban

    int mCropWidth;
    int mCropHeigh;

    int maxSize;//单位：B

    int width;//宽
    int height;//高


    int takePhotoType;//1：拍照  2：选择照片

    public static CustomHelper of() {
        return new CustomHelper();
    }

    private CustomHelper() {
        init();
    }

    private void init() {
        limit = 9;
        mIsCrop = true;
        mPhotoFrom = 2;
        takePhotoType = 1;
        mIsCorrect = false;
        mIsPickWithOwn = true;
        mIsCompress = true;
        mIsShowProgressBar = true;
        enableRawFile = true;
        mWithWonCrop = false;
        mIsAspect = false;
        mCompressToolType = 1;
        maxSize = 1 * 1000 * 1024;//默认照片最大1M

        mCropWidth =1800;
        mCropHeigh = 1800;

        width = 800;
        height = 800;
    }

    public void takePhoto(int takePhotoType, TakePhoto takePhoto) {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);

        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);

        if (takePhotoType == 1) {//拍照
            if (mIsCrop) {
                takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
            } else {
                takePhoto.onPickFromCapture(imageUri);
            }
        } else {//选择照片
            if (limit > 1) {
                if (mIsCrop) {
                    takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
                }else{
                    takePhoto.onPickMultiple(limit);
                }
                return;
            }

            if (mPhotoFrom == 1) {//从文件
                if (mIsCrop) {
                    takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromDocuments();
                }
                return;
            } else {//從相冊
                if (mIsCrop) {
                    takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromGallery();
                }
            }
        }
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        if (mIsPickWithOwn) {
            builder.setWithOwnGallery(mIsPickWithOwn);//使用TakePhoto自带相册
        }
        if (mIsCorrect) {
            builder.setCorrectImage(mIsCorrect);
        }
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private void configCompress(TakePhoto takePhoto) {
        if (!mIsCompress) {
            takePhoto.onEnableCompress(null, false);
            return;
        }
        CompressConfig config = null;
        if (mCompressToolType == 1) {//自带压缩工具
            config = new CompressConfig.Builder().setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else if (mCompressToolType == 2) {//Luban压缩工具
            LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config, mIsShowProgressBar);
    }

    private CropOptions getCropOptions() {
        if (!mIsCrop) {
            return null;
        }

        int height = mCropHeigh;
        int width = mCropWidth;

        CropOptions.Builder builder = new CropOptions.Builder();

        if (mIsAspect) {
            builder.setAspectX(width).setAspectY(height);
        } else {
            builder.setOutputX(width).setOutputY(height);
        }
        builder.setWithOwnCrop(mWithWonCrop);
        return builder.create();
    }

}
