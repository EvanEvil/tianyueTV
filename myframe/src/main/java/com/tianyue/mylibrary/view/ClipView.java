package com.tianyue.mylibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 裁剪图片
 * Created by hasee on 2016/11/15.
 */
public class ClipView extends View {

    private int mHorizonalLeftPadding;

    public ClipView(Context context) {
        this(context,null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        this(context,null,0);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){

    }
}
