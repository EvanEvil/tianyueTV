package com.tianyue.tv.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyFlowLayout extends ViewGroup {
	private ArrayList<Line> lineList;
	private int verticalSpacing,horizontalSpacing;
	public MyFlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyFlowLayout(Context context) {
		super(context);
		init();
	}
	
	
	private void init(){
		lineList = new ArrayList<Line>();
	}
	private Line line;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		resetLines();
		
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);//总宽度
		//除去左右padding的宽度
		int unpadWidth = measuredWidth - getPaddingLeft() - getPaddingRight();
		int totalHeight = getPaddingTop()+getPaddingBottom();//总高度
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		for (int i = 0; i < getChildCount(); i++) {
			
			if(line==null)line = new Line();
			
			TextView childView = (TextView) getChildAt(i);
			//EXACTLY:match_parent，具体的dp值
			//AT_MOST:wrap_content
			//UNSPECIPED:adapter
//			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(unpadWidth, widthMode==MeasureSpec.EXACTLY?MeasureSpec.AT_MOST:widthMode);
//			int childHeightMeasureSpec= MeasureSpec.makeMeasureSpec(totalHeight, heightMode==MeasureSpec.EXACTLY?MeasureSpec.AT_MOST:heightMode);
//			childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			
			childView.measure(0, 0);
			
			if(line.getViewList().size()==0){
				//如果line是空的，则不用判断，直接放入，要保证一行至少有一个
				line.addView(childView);
			}else {
				if(line.getWidth() + childView.getMeasuredWidth() + horizontalSpacing>unpadWidth){
					//超过一行的宽度，先保存当前行，然后重新创建一行
					lineList.add(line);
					
					line = new Line();
					line.addView(childView);
					
					if(i==(getChildCount()-1)){
						//是最后一个
						lineList.add(line);
					}
				}else {
					line.addView(childView);
					if(i==(getChildCount()-1)){
						//是最后一个
						lineList.add(line);
					}
				}
			}
		}
		line = null;//line对象要及时弄空
		
		//测量高
		for (int i = 0; i < lineList.size(); i++) {
			totalHeight+=lineList.get(i).getHeight();
		}
		totalHeight += (lineList.size()-1)*verticalSpacing;
		setMeasuredDimension(measuredWidth, totalHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int padingTop = getPaddingTop();
		int paddingLeft = getPaddingLeft();
		for (int i = 0; i < lineList.size(); i++) {
			Line line = lineList.get(i);
			
			if(i!=0){
				padingTop += line.getHeight() + verticalSpacing;
			}
//			padingTop += (i==0?0:(line.getHeight() + verticalSpacing));
			
			//得到剩余的宽度，然后平均分给每个子view
			float remainSpacing = getMeasuredWidth()-getPaddingLeft()-getPaddingRight() - line.getWidth();
			float splitSpacing = remainSpacing/line.getViewList().size();
			
			for (int j = 0; j < line.getViewList().size(); j++) {
				TextView childView = (TextView) line.getViewList().get(j);
				int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childView.getMeasuredWidth()+splitSpacing), MeasureSpec.EXACTLY);
				int childHeightMeasureSpec= MeasureSpec.makeMeasureSpec(line.getHeight(), MeasureSpec.EXACTLY);
				childView.measure(childWidthMeasureSpec,childHeightMeasureSpec);
				
				if(j==0){
					//第一个子view，不需要加horizontalSpacing
//					LogUtil.e(this, "width: "+childView.getMeasuredWidth() +"  -- "+childView.getText());
					childView.layout(paddingLeft, padingTop, paddingLeft+childView.getMeasuredWidth(), padingTop+childView.getMeasuredHeight());
				}else {
					View lastView = line.getViewList().get(j-1);
					int left = lastView.getRight()+horizontalSpacing;
					int top = padingTop;
					childView.layout(left, top, left+childView.getMeasuredWidth(), top+childView.getMeasuredHeight());
				}
			}
		}
	}
	
	private void resetLines(){
		lineList.clear();
	}
	
	/**
	 * 封装每一行的view
	 * @author Administrator
	 *
	 */
	private class Line{
		private ArrayList<View> viewList;
		private int width;
		private int height;
		
		public ArrayList<View> getViewList() {
			return viewList;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}


		public Line(){
			viewList = new ArrayList<View>();
		}
		
		public void addView(View view){
			if(!viewList.contains(view)){
				viewList.add(view);
				
				if(viewList.size()==1){
					//只有一个view时，line的宽就是view的宽
					width = view.getMeasuredWidth();
				}else {
					
					width += (horizontalSpacing + view.getMeasuredWidth());
				}
				height = Math.max(height, view.getMeasuredHeight());
			}
		}
	}

	public void setHorizontalSpacing(int i) {
		// TODO Auto-generated method stub
		this.horizontalSpacing = i;
	}

	public void setVerticalSpacing(int i) {
		// TODO Auto-generated method stub
		this.verticalSpacing = i;
	}

}
