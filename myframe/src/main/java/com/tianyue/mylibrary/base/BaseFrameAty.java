package com.tianyue.mylibrary.base;

import android.support.v7.app.AppCompatActivity;

import com.tianyue.mylibrary.view.LoadingView;

/**
 * 基类Activity
 * Created by hasee on 2016/11/24.
 */
public abstract class BaseFrameAty extends AppCompatActivity {
    /**
     * 加载进度框
     */
    protected LoadingView mLoadingView;





    /**
     * 显示对话框
     * @param message 消息内容
     */
    protected void showDialogs(String message){
        showDialogs(message,false);
    }

    /**
     * 显示对话框
     * @param message 消息内容
     * @param outSide 是否点击外部消失
     */
    protected void showDialogs(String message,boolean outSide){
        if (mLoadingView == null) {
            mLoadingView = new LoadingView(this);
        }
        mLoadingView.setLoadingViewText(message);
        mLoadingView.setTouchOutside(outSide);
        mLoadingView.show();
    }

    /**
     * 关闭对话框
     */
    protected void dismissDialogs(){
        if (mLoadingView != null) {
            mLoadingView.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingView != null) {
            mLoadingView.dismiss();
            mLoadingView = null;
        }
    }

}
