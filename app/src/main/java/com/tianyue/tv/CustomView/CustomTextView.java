package com.tianyue.tv.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.tianyue.tv.R;

/**
 * Created by hasee on 2016/8/3.
 */
public class CustomTextView extends View {
    Paint paint;
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.red));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureSpecWidth(widthMeasureSpec);
        measureSpecHeight(heightMeasureSpec);
    }

    /**
     * 测量宽度
     * @param measureSpec
     * @return
     */
    private int measureSpecWidth(int measureSpec){
        int result = 0;
        int Mode = MeasureSpec.getMode(measureSpec);
        if (Mode == MeasureSpec.EXACTLY) {
            result = measureSpec;
        }
        if (Mode == MeasureSpec.AT_MOST) {
            result = 200;
            if (Mode == MeasureSpec.UNSPECIFIED){
                result = Math.min(result,measureSpec);
            }
        }
      return result;
    }

    /**
     * 测量高度
     * @param measureSpec
     * @return
     */
    private int measureSpecHeight(int measureSpec){
        int result = 0;
        int Mode = MeasureSpec.getMode(measureSpec);
        if (Mode == MeasureSpec.EXACTLY) {
            result = measureSpec;
        }
        if (Mode == MeasureSpec.AT_MOST) {
            result = 200;
            if (Mode == MeasureSpec.UNSPECIFIED){
                result = Math.min(result,measureSpec);
            }
        }
        return result;
    }
}
