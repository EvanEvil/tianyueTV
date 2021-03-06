package com.tianyue.mylibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Scroller;

import com.tianyue.mylibrary.R;
import com.tianyue.mylibrary.util.ColorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * 滚动选择器
 * Created by hasee on 2016/11/4.
 */
public class ScrollPickerView extends View {
    private int mTextMinSize = 17;//最小的字体
    private int mTextMaxSize = 25;//最大的字体

    private int mVisibleItemCount = 3;//可见item数

    private int mCheckedColor = Color.BLACK;//选中item项的颜色
    private int mUnCheckedColor = Color.GRAY;//未选中item项的颜色

    private boolean isInertiaScroll = true; // 快速滑动时是否惯性滚动一段距离，默认开启
    private boolean isCirculation = true; // 是否循环滚动，默认开启
    /**
     *不允许父组件拦截触摸事件，设置为true为不允许拦截，此时该设置才生效
     *当嵌入到ScrollView等滚动组件中，为了使该自定义滚动选择器可以正常工作，请设置为true
     */
    private boolean mDisallowInterceptTouch = false;

    private Paint mPaint; //
    private int mMeasureWidth;
    private int mMeasureHeight;
    private int mSelected; // 当前选中的item下标
    private List<String> mData;
    private int mItemHeight = 0; // 每个条目的高度=mMeasureHeight／mVisibleItemCount
    private int mCenterY; // 中间item的起始坐标y
    private float mLastMoveY; // 触摸的坐标y

    private float mMoveLength = 0; // item移动长度，负数表示向上移动，正数表示向下移动

    private GestureDetectorCompat mGestureDetector;
    private OnSelectedListener mListener;

    private Scroller mScroller;
    private boolean mIsFling; // 是否正在惯性滑动
    private boolean mIsMovingCenter; // 是否正在滑向中间
    // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次触屏滑动的坐标
    private int mLastScrollY = 0; // Scroller的坐标y

    public ScrollPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mGestureDetector = new GestureDetectorCompat(getContext(),
                new FlingOnGestureListener());
        mScroller = new Scroller(getContext());

        setData(new ArrayList<String>(Arrays.asList("one", "two", "three", "four", "five", "six", "seven")));
        init(attrs);
    }


    /**
     * 初始化属性
     * @param attrs
     */
    private void init(AttributeSet attrs){
        if (attrs != null) {
            TypedArray arrays = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollPickerView);
            mTextMinSize = arrays.getDimensionPixelSize(R.styleable.ScrollPickerView_min_text_size,mTextMinSize);
            mTextMaxSize = arrays.getDimensionPixelSize(R.styleable.ScrollPickerView_max_text_size,mTextMaxSize);

            mCheckedColor = arrays.getColor(R.styleable.ScrollPickerView_checked_color,mCheckedColor);
            mUnCheckedColor = arrays.getColor(R.styleable.ScrollPickerView_unchecked_color,mUnCheckedColor);

            mVisibleItemCount = arrays.getInt(R.styleable.ScrollPickerView_visible_item_count,mVisibleItemCount);
            isCirculation = arrays.getBoolean(R.styleable.ScrollPickerView_is_circulation,isCirculation);
            mDisallowInterceptTouch = arrays.getBoolean(R.styleable.ScrollPickerView_disallow_intercept_touch,mDisallowInterceptTouch);

            arrays.recycle();
        }

    }
    @Override
    protected void onDraw(Canvas canvas) {
        // 中间item
        drawItem(canvas, mSelected, 0);
        int length = mVisibleItemCount / 2 + 1;
        int positon;
        // 上下两边
        for (int i = 1; i <= length && i <= mData.size() / 2; i++) {

            // 上面的items
            positon = mSelected - i < 0 ? mData.size() + mSelected - i
                    : mSelected - i;
            if (isCirculation) {
                drawItem(canvas, positon, -i);
            } else if (mSelected - i >= 0) { // 非循环滚动
                drawItem(canvas, positon, -i);
            }

            // 下面的items
            positon = mSelected + i >= mData.size() ? mSelected + i
                    - mData.size() : mSelected + i;
            if (isCirculation) {
                drawItem(canvas, positon, i);
            } else if (mSelected + i < mData.size()) { // 非循环滚动
                drawItem(canvas, positon, i);
            }
        }
    }

    /**
     * @param canvas
     * @param position 在mData数据集中的位置
     * @param relative 相对中间item的位置　postion-mSelected
     */
    private void drawItem(Canvas canvas, int position, int relative) {
        String text = mData.get(position);
        float x = 0;
        if (relative == -1) { // 上一个
            if (mMoveLength < 0) { // 向上滑动
                mPaint.setTextSize(mTextMinSize);
            } else { // 向下滑动
                mPaint.setTextSize(mTextMinSize + (mTextMaxSize - mTextMinSize)
                        * mMoveLength / mItemHeight);
            }
        } else if (relative == 0) { // 中间item,当前选中
            mPaint.setTextSize(mTextMinSize + (mTextMaxSize - mTextMinSize)
                    * (mItemHeight - Math.abs(mMoveLength)) / mItemHeight);
        } else if (relative == 1) { // 下一个
            if (mMoveLength > 0) { // 向下滑动
                mPaint.setTextSize(mTextMinSize);
            } else { // 向上滑动
                mPaint.setTextSize(mTextMinSize + (mTextMaxSize - mTextMinSize)
                        * -mMoveLength / mItemHeight);
            }
        } else { // 其他
            mPaint.setTextSize(mTextMinSize);
        }
        x = (mMeasureWidth - mPaint.measureText(text)) / 2;

        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        // 绘制文字时，文字的baseline是对齐ｙ坐标的，下面换算使其垂直居中。fmi.top值是相对baseline的，为负值
        float y = mCenterY + relative * mItemHeight + mItemHeight / 2
                - fmi.descent + (fmi.bottom - fmi.top) / 2;
        computeColor(relative);
        canvas.drawText(text, x, y + mMoveLength, mPaint);
    }

    /**
     * 计算字体颜色，渐变
     *
     * @param relative 　相对中间item的位置
     */
    private void computeColor(int relative) {

        int color = mUnCheckedColor; // 　其他默认为ｍEndColor

        if (relative == -1 || relative == 1) { // 上一个或下一个
            // 处理上一个item且向上滑动　或者　处理下一个item且向下滑动　，颜色为mEndColor
            if ((relative == -1 && mMoveLength < 0)
                    || (relative == 1 && mMoveLength > 0)) {
                color = mUnCheckedColor;
            } else { // 计算渐变的颜色
                float rate = (mItemHeight - Math.abs(mMoveLength))
                        / mItemHeight;
                color = ColorUtil.computeGradientColor(mCheckedColor, mUnCheckedColor, rate);
            }
        } else if (relative == 0) { // 中间item
            float rate = Math.abs(mMoveLength) / mItemHeight;
            color = ColorUtil.computeGradientColor(mCheckedColor, mUnCheckedColor, rate);
        }

        mPaint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();
        mItemHeight = mMeasureHeight / mVisibleItemCount;
        mCenterY = mVisibleItemCount / 2 * mItemHeight;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mDisallowInterceptTouch) {  // 不允许父组件拦截事件
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                // 点击时取消所有滚动效果
                cancelScroll();
                mLastMoveY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getY() - mLastMoveY) < 0.1f) {
                    return true;
                }
                mMoveLength += event.getY() - mLastMoveY;
                mLastMoveY = event.getY();
                checkCirculation();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mLastMoveY = event.getY();
                moveToCenter();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) { // 正在滚动
            // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次滑动的坐标
            mMoveLength = mMoveLength + mScroller.getCurrY() - mLastScrollY;
            mLastScrollY = mScroller.getCurrY();
            checkCirculation();
            invalidate();
        } else { // 滚动完毕
            if (mIsFling) {
                mIsFling = false;
                moveToCenter();
            } else if (mIsMovingCenter) {
                mIsMovingCenter = false;
                notifySelected();
            }
        }

    }

    public void cancelScroll() {
        mIsFling = mIsMovingCenter = false;
        mScroller.abortAnimation();
    }

    // 循环滚动
    private void checkCirculation() {
        if (mMoveLength >= mItemHeight) { // 向下滑动,最后一个元素放在头部
            mSelected--;
            if (mSelected < 0) {  // 滚动顶部，判断是否循环滚动
                if (isCirculation) {
                    mSelected = mData.size() - 1;
                    mMoveLength = 0;
                } else { // 非循环滚动
                    mSelected = 0;
                    mMoveLength = mItemHeight;
                    if (mIsFling) { // 停止惯性滑动，根据computeScroll()中的逻辑，下一步将调用moveToCenter()
                        mScroller.forceFinished(true);
                    }
                    if (mIsMovingCenter) { //  移回中间位置
                        scroll(mMoveLength, 0);
                    }
                }
            } else {
                mMoveLength = 0;
            }

        } else if (mMoveLength <= -mItemHeight) { // 向上滑动，第一个元素放在尾部
            mSelected++;
            if (mSelected >= mData.size()) { // 滚动末尾，判断是否循环滚动
                if (isCirculation) {
                    mSelected = 0;
                    mMoveLength = 0;
                } else { // 非循环滚动
                    mSelected = mData.size() - 1;
                    mMoveLength = -mItemHeight;
                    if (mIsFling) { // 停止惯性滑动，根据computeScroll()中的逻辑，下一步将调用moveToCenter()
                        mScroller.forceFinished(true);
                    }
                    if (mIsMovingCenter) { //  移回中间位置
                        scroll(mMoveLength, 0);
                    }
                }
            } else {
                mMoveLength = 0;
            }
        }
    }

    // 移动到中间位置
    private void moveToCenter() {

        if (!mScroller.isFinished() || mIsFling) {
            return;
        }
        cancelScroll();

        // 向下滑动
        if (mMoveLength > 0) {
            if (mMoveLength < mItemHeight / 2) {
                scroll(mMoveLength, 0);
            } else {
                scroll(mMoveLength, mItemHeight);
            }
        } else {
            if (-mMoveLength < mItemHeight / 2) {
                scroll(mMoveLength, 0);
            } else {
                scroll(mMoveLength, -mItemHeight);
            }
        }
    }

    // 平滑滚动
    private void scroll(float from, int to) {

        mLastScrollY = (int) from;
        mIsMovingCenter = true;
        mScroller.startScroll(0, (int) from, 0, 0);
        mScroller.setFinalY(to);
        invalidate();
    }

    // 惯性滑动，
    private void fling(float from, float vY) {
        mLastScrollY = (int) from;
        mIsFling = true;
        // 最多可以惯性滑动10个item
        mScroller.fling(0, (int) from, 0, (int) vY, 0, 0, -10 * mItemHeight,
                10 * mItemHeight);
        invalidate();
    }

    private void notifySelected() {
        if (mListener != null) {
            // 告诉监听器选择完毕
            post(new Runnable() {
                @Override
                public void run() {
                    mListener.onSelected(mData, mSelected);
                }
            });
        }
    }

    /**
     * 快速滑动时，惯性滑动一段距离
     *
     * @author huangziwei
     */
    private class FlingOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               final float velocityY) {
            // 惯性滑动
            if (isInertiaScroll) {
                cancelScroll();
                fling(mMoveLength, velocityY);
            }
            return true;
        }
    }

    public List<String> getData() {
        return mData;
    }

    public void setData(List<String> data) {
        if (data == null) {
            mData = new ArrayList<String>();
        }
        this.mData = data;
        mSelected = data.size() / 2;
        invalidate();
    }

    /**
     * @param startColor 正中间的颜色
     * @param endColor   上下两边的颜色
     */
    public void setColor(int startColor, int endColor) {
        mCheckedColor = startColor;
        mUnCheckedColor = endColor;
        invalidate();
    }

    public void setMinTestSize(int size) {
        mTextMinSize = size;
        invalidate();
    }

    public void setMaxTestSize(int size) {
        mTextMaxSize = size;
        invalidate();
    }

    public String getSelectedItem() {
        return mData.get(mSelected);
    }

    public void setSelectedPosition(int position) {
        if (position < 0 || position > mData.size() - 1
                || position == mSelected) {
            return;
        }
        mSelected = position;
        invalidate();
        if (mListener != null) {
            notifySelected();
        }
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        mListener = listener;
    }

    public OnSelectedListener getListener() {
        return mListener;
    }

    public boolean isInertiaScroll() {
        return isInertiaScroll;
    }

    public void setInertiaScroll(boolean inertiaScroll) {
        this.isInertiaScroll = inertiaScroll;
    }

    public boolean isIsCirculation() {
        return isCirculation;
    }

    public void setIsCirculation(boolean isCirculation) {
        this.isCirculation = isCirculation;
    }

    /**
     * @author
     */
    public interface OnSelectedListener {
        void onSelected(List<String> data, int position);
    }

}
