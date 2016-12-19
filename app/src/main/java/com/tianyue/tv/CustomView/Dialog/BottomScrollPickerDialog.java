package com.tianyue.tv.CustomView.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.tianyue.mylibrary.view.ScrollPickerView;
import com.tianyue.tv.Bean.TypeBean;
import com.tianyue.tv.R;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 底部滚动选择器
 * Created by hasee on 2016/12/13.
 */
public class BottomScrollPickerDialog extends Dialog implements View.OnClickListener{
    private Context context;

    private Button confirm;

    private Button cancel;

    private ScrollPickerView typeOne;

    private ScrollPickerView typeTwo;

    private BottomScrollCallback cancelCallback;

    private BottomScrollCallback confirmCallback;

    private BottomScrollDataCallBack bottomScrollDataCallBack;

    public static final int SCROLL_PICKER_ONE_TYPE = 0xff01;

    public static final int SCROLL_PICKER_TWO_TYPE = 0xff02;

    private int type;

    private HashMap<String,List<String>> mainAndMinorClassify;

    private List<String> mainListName;

    private List<String> minorListName;

    private String[] mainTypeName;

    private String[] mainTypeId;

    private List<String> mainListId;

    private String TAG = this.getClass().getSimpleName();

    public BottomScrollPickerDialog(Context context){
        this(context, R.style.dialog_untran);
    }

    public BottomScrollPickerDialog(Context context, int themeResId) {
        super(context, R.style.dialog_untran);
        init(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = getWindow().getWindowManager().getDefaultDisplay().getHeight();

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        onWindowAttributesChanged(params);
    }

    private void init(Context context){
        View view =  View.inflate(context,R.layout.bottom_picker_dialog,null);

        confirm = (Button) view.findViewById(R.id.bottom_picker_confirm);

        cancel = (Button) view.findViewById(R.id.bottom_picker_cancel);

        typeOne = (ScrollPickerView) view.findViewById(R.id.bottom_picker_type_one);

        typeTwo = (ScrollPickerView) view.findViewById(R.id.bottom_picker_type_two);

        confirm.setOnClickListener(this);

        cancel.setOnClickListener(this);

        typeOne.setOnSelectedListener(firstType);

        typeTwo.setOnSelectedListener(lastType);

        setContentView(view);

        setScrollPickerData();

    }
    public void setOnFristBottomScrollCallback(BottomScrollCallback cancelCallback){
        this.cancelCallback = cancelCallback;
    }
    public void setOnLastBottomScrollCallback(BottomScrollCallback confirmCallback ){
        this.confirmCallback = confirmCallback;
    }
    public void setOnBottomScrollDataCallBack(BottomScrollDataCallBack bottomScrollDataCallBack){
        this.bottomScrollDataCallBack = bottomScrollDataCallBack;
    }
    /**
     * 填充数据
     */
    private void setScrollPickerData(){
        TypeBean typeBean = new TypeBean();
        mainAndMinorClassify = typeBean.getMainAndMinorClassify();
        mainTypeId  = typeBean.getMainTypeId();
        mainTypeName = typeBean.getMainTypeName();
        mainListName = Arrays.asList(mainTypeName);
        typeOne.setData(mainListName);
        minorListName = mainAndMinorClassify.get(mainTypeId[3]);
        typeTwo.setData(minorListName);
     }

    ScrollPickerView.OnSelectedListener firstType  = new ScrollPickerView.OnSelectedListener() {
        @Override
        public void onSelected(List<String> data, int position) {
            minorListName = mainAndMinorClassify.get(mainTypeId[position]);
            typeTwo.setData(minorListName);
            if (bottomScrollDataCallBack != null) {
                bottomScrollDataCallBack.onSelected(data,position,SCROLL_PICKER_ONE_TYPE);
            }
        }
    };

    ScrollPickerView.OnSelectedListener lastType = new ScrollPickerView.OnSelectedListener() {
        @Override
        public void onSelected(List<String> data, int position) {
            if (bottomScrollDataCallBack != null) {
                bottomScrollDataCallBack.onSelected(data,position,SCROLL_PICKER_TWO_TYPE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_picker_cancel:
                if (cancelCallback != null) {
                    cancelCallback.onBtnCallback();
                }
                dismiss();
                break;
            case R.id.bottom_picker_confirm:
                if (confirmCallback != null) {
                    confirmCallback.onBtnCallback();
                }
                dismiss();
                break;
        }
    }

    public interface BottomScrollCallback{
        void onBtnCallback();
    }
    public interface BottomScrollDataCallBack{
        void onSelected(List<String> data,int position,int type);
    }
}
