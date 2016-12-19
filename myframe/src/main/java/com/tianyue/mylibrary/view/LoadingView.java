package com.tianyue.mylibrary.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tianyue.mylibrary.R;

/**
 *
 * 登录加载控件
 * Created by hasee on 2016/11/3.
 */
public class LoadingView extends Dialog {
    private Context context;
    private TextView loadingText;
    /**
     * 加载到dialog的view
     */
    private View mView;
    public LoadingView(Context context) {
        this(context, R.style.dialog_untran);
    }

    public LoadingView(Context context, int themeResId) {
        super(context, R.style.dialog_untran);
        this.context = context;
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //获得当前窗口属性
        WindowManager.LayoutParams params = window.getAttributes();
        //设置控件显示位置
        params.x = Gravity.CENTER;
        params.y = Gravity.CENTER;
        //设置控件宽高
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        this.onWindowAttributesChanged(params);
    }

    /**
     * 初始化控件
     * @param context
     */
    private void init(Context context){
        mView = View.inflate(context,R.layout.loading_layout,null);
        loadingText = (TextView) mView.findViewById(R.id.loading_text);
        setContentView(mView);
    }

    /**
     * 设置提示文字
     * @param text
     */
    public void setLoadingViewText(String text){
        loadingText.setText(text);
    }

    /**
     * 点击外部屏幕是否可以取消显示
     * @param isOutside
     */
    public void setTouchOutside(boolean isOutside){
        setCanceledOnTouchOutside(isOutside);
    }
}
