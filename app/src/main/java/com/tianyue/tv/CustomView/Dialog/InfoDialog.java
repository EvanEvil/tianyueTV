package com.tianyue.tv.CustomView.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tianyue.tv.R;

/**
 * Created by hasee on 2016/8/18.
 */
public class InfoDialog extends Dialog implements View.OnClickListener {
    /**
     * 加载到dialog的view
     */
    private View mView;
    private TextView title;
    private Button frist;
    private Button last;
    private EditText content;
    private DialogBtnCallBack fristClick;
    private DialogBtnCallBack lastClick;
    public InfoDialog(Context context) {
        this(context, R.style.dialog_untran);
    }

    public InfoDialog(Context context, int themeResId) {
        super(context, R.style.dialog_untran);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = Gravity.CENTER;
        params.y = Gravity.CENTER;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        this.onWindowAttributesChanged(params);
    }

    /**
     * 初始化修改资料对话框
     */
    private void init(Context context){
        mView = View.inflate(context,R.layout.info_dialog,null);
        title = (TextView) mView.findViewById(R.id.info_dialog_title);
        frist = (Button) mView.findViewById(R.id.info_dialog_frist);
        last = (Button) mView.findViewById(R.id.info_dialog_last);
        content = (EditText) mView.findViewById(R.id.info_dialog_content);
        setContentView(mView);
    }
    /**
     * 设置第一个按钮文字
     */
    public void setFristText(String s){
        frist.setText(s);
    }
    /**
     * 设置第二个按钮文字
     */
    public void setLastText(String s){
        last.setText(s);
    }
    /**
     * 设置标题
     */
    public void setTitle(String s){
        title.setText(s);
    }

    /**
     * 获取输入内容
     * @return
     */
    public String getText(){
        return content.getText().toString();
    }

    /**
     * 设置第一个按钮监听
     * @param callBack
     */
    public void setFristClick(DialogBtnCallBack callBack){
        if (callBack != null) {
            this.fristClick = callBack;
        }
    }
    /**
     * 设置第二个按钮监听
     * @param callBack
     */
    public void setLastClick(DialogBtnCallBack callBack){
        if (callBack != null) {
            this.lastClick = callBack;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_dialog_frist:
                if (fristClick != null) {
                    fristClick.onBtnClick();
                }
                break;
            case R.id.info_dialog_last:
                if (lastClick != null) {
                    lastClick.onBtnClick();
                }
                break;
        }
        dismiss();
    }
    public interface DialogBtnCallBack{
        void onBtnClick();
    }
}

