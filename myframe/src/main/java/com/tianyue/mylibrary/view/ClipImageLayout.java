package com.tianyue.mylibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import com.tianyue.mylibrary.R;
/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 * @author zhy
 *
 */
public class ClipImageLayout extends RelativeLayout
{

	private ClipZoomImageView mZoomImageView;
	private ClipImageBorderView mClipImageView;
    private int resDrawable;
	/**
	 * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
	 */
	private int mHorizontalPadding = 20;

	public ClipImageLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		mZoomImageView = new ClipZoomImageView(context);
		mClipImageView = new ClipImageBorderView(context);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		init(attrs);
		if (resDrawable != 0) {
			setDrawable(resDrawable);
		}
		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);

		
		// 计算padding的px
		mHorizontalPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
						.getDisplayMetrics());
		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
	}

	/**
	 * 自定义属性
	 * @param attr
     */
	private void init (AttributeSet attr){
		TypedArray arrays = getContext().obtainStyledAttributes(attr, R.styleable.ClipImageLayout);
		mHorizontalPadding = arrays.getDimensionPixelSize(R.styleable.ClipImageLayout_horizontal_padding,mHorizontalPadding);
		resDrawable = arrays.getResourceId(R.styleable.ClipImageLayout_clip_pic,0);
		arrays.recycle();
	}

	/**
	 * 对外公布设置边距的方法,单位为dp
	 * 
	 * @param mHorizontalPadding
	 */
	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;
	}

	/**
	 * 通过资源ID设置图片
	 * @param resDrawable
     */
	public void setDrawable(int resDrawable){
		mZoomImageView.setImageDrawable(getResources().getDrawable(resDrawable));
	}

	/**
	 * 直接设置图片
	 * @param drawable
     */
	public void setDrawable(Drawable drawable){
		mZoomImageView.setImageDrawable(drawable);
	}

	/**
	 * 裁切图片
	 * 
	 * @return
	 */
	public Bitmap clip()
	{
		return mZoomImageView.clip();
	}

}
