package com.tianyue.tv.Util;
import com.upyun.library.common.Params;
import com.upyun.library.common.UpConfig;
import com.upyun.library.common.UploadManager;
import com.upyun.library.listener.SignatureListener;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传文件至又拍云
 * Created by hasee on 2016/11/9.
 */
public class UpLoadFileUtil {
    private final String SAVE_PATH = "/uploads/{year}{mon}{day}/{random32}{.suffix}";
    private final String KEY = "28LU61t4/anqeXKM6sJwzx1toL4=";
    private final String BUCKET = "zswl-images";
    public static UpLoadFileUtil instance;
    private CompleteResultListener completeResultListener;
    private ProgressResultListener progressResultListener;
    private Map<String,Object> params;
    private UploadManager uploadManager;
    private UpLoadFileUtil(){
        uploadManager = UploadManager.getInstance();
        params = new HashMap<>();
    }

    public static UpLoadFileUtil getInstance(){
        if (instance == null) {
            synchronized (UpLoadFileUtil.class){
                if (instance == null) {
                    instance = new UpLoadFileUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 默认参数上传文件
     * @param file
     */
    public void upLoadFile(File file){
        //默认必要参数
        params.put(Params.BUCKET,BUCKET);
        params.put(Params.EXPIRATION, System.currentTimeMillis()/1000+1000);
        params.put(Params.SAVE_KEY,SAVE_PATH);
        if (progressResultListener == null && file != null) {
//            uploadManager.formUpload(file,params,KEY,completeListener,null);
            uploadManager.formUpload(file,params,signatureListener,completeListener,null);
        }
        if (progressResultListener != null && file != null) {
//            uploadManager.formUpload(file,params,KEY,completeListener,progressListener);
            uploadManager.formUpload(file,params,signatureListener,completeListener,progressListener);
        }
    }

    /**
     * 带参数的上传文件
     * @param file
     *        文件
     * @param params
     *        文件参数
     */
    public void upLoadFile(File file,HashMap<String,Object> params){
        if (params.get(Params.BUCKET) == null) {//必要参数未传自动补足
            params.put(Params.BUCKET,BUCKET);
        }
        if (params.get(Params.EXPIRATION) == null) {
            params.put(Params.EXPIRATION, System.currentTimeMillis()/1000+1000);
        }
        if (params.get(Params.SAVE_KEY) == null) {
            params.put(Params.SAVE_KEY,SAVE_PATH);
        }
        if (progressResultListener == null && file != null) {
//            uploadManager.formUpload(file,params,KEY,completeListener,null);
            uploadManager.formUpload(file,params,signatureListener,completeListener,null);
        }
        if (progressResultListener != null && file != null) {
//            uploadManager.formUpload(file,params,KEY,completeListener,progressListener);
            uploadManager.formUpload(file,params,signatureListener,completeListener,progressListener);
        }
    }
   SignatureListener signatureListener = new SignatureListener() {
       @Override
       public String getSignature(String policy) {
           return UpYunUtils.md5(policy+KEY);
       }
   };

    UpCompleteListener completeListener = new UpCompleteListener() {
        @Override
        public void onComplete(boolean isSuccess, String result) {
            if (completeResultListener != null) {
                completeResultListener.result(isSuccess,result);
            }
        }
    };

    UpProgressListener progressListener = new UpProgressListener() {
        @Override
        public void onRequestProgress(long bytesWrite, long contentLength) {
            if (progressResultListener != null) {
                progressResultListener.nowProgress(bytesWrite,contentLength);
            }
        }
    };

    /**
     * 设置上传结果监听
     * @param completeResultListener
     */
    public UpLoadFileUtil setCompleteResultListener(CompleteResultListener completeResultListener){
        this.completeResultListener = completeResultListener;
        return instance;
    }

    /**
     * 设置上传当前进度监听
     * @param progressResultListener
     */
    public UpLoadFileUtil setProgressResultListener(ProgressResultListener progressResultListener){
        this.progressResultListener = progressResultListener;
        return instance;
    }
    public interface CompleteResultListener{
        void result(boolean isSuccess, String result);
    }
    public interface ProgressResultListener{
        void nowProgress(long bytesWrite, long contentLength);
    }
}
