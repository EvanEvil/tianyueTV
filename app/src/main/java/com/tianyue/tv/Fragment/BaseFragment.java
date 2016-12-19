package com.tianyue.tv.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhotoFragment;
import com.tianyue.tv.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 基类Fragment
 * Created by hasee on 2016/11/18.
 */
public abstract class BaseFragment extends TakePhotoFragment {
    /**
     * 第二次显示的时间
     */
    private long oneTime = 0;
    /**
     * 第一次显示的时间
     */
    private long twoTime = 0;
    /**
     * 单例toast对象
     */
    private Toast toast;
    /**
     * 当前msg的信息
     */
    private String msg;

    protected Context context;

    Unbinder unbinder;
    protected String TAG = this.getClass().getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView(inflater,container);
        unbinder = ButterKnife.bind(this,view);
        init();
        return view;
    }
    protected abstract View initView(LayoutInflater inflater,ViewGroup container);

    protected abstract void init();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected boolean isHasAnimiation(){
        return true;
    }
    /**
     * 启动页面
     * @param className
     */
    protected void startActivity(Class<?> className) {
        Intent intent = new Intent(getActivity(), className);
        startActivity(intent);
        if (isHasAnimiation()) {
            getActivity().overridePendingTransition(R.anim.slide_right_in,
                    R.anim.slide_left_out);
        }
    }
    /**
     * 启动页面
     * @param className
     * @param bundle
     */
    protected void startActivity(Class<?> className,Bundle bundle) {
        Intent intent = new Intent(getActivity(), className);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (isHasAnimiation()) {
            getActivity().overridePendingTransition(R.anim.slide_right_in,
                    R.anim.slide_left_out);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach: ");
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach: ");
        super.onDetach();
        this.context = null;
    }

    /**
     * 显示Toast消息
     * @param message
     */
    protected void showToast(String message){
        if (toast == null) {
            toast = new Toast(getActivity());
            toast = Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT);
            LinearLayout parent = (LinearLayout) toast.getView();
            TextView mes = (TextView) parent.getChildAt(0);
            mes.setTextSize(13);
            toast.show();
            msg = message;
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (message.equals(msg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                toast.setText(message);
                toast.show();
                msg = message;
            }
            oneTime = twoTime;
        }
    }
}
