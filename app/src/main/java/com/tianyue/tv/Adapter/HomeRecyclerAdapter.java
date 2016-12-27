package com.tianyue.tv.Adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.hintview.IconHintView;
import com.tianyue.tv.Base.BaseViewHolder;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.R;

import java.util.List;

import butterknife.BindView;

/**
 * Created by hasee on 2016/12/6.
 */
public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEAD_VIEW = 0xff01;

    public static final int TYPE_CONTENT_VIEW = 0xff02;

    OnHomeRecyclerListener onHomeRecyclerListener;

    OnColumnMoreListener onColumnMoreListener;

    OnColumnChildClickListener onColumnChildClickListener;

    Context context;

    LayoutInflater inflater;

    List<LiveHomeColumn> liveHomeColumns;

    String TAG = this.getClass().getSimpleName();

    public HomeRecyclerAdapter(Context context, List<LiveHomeColumn> liveHomeColumns) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.liveHomeColumns = liveHomeColumns;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEAD_VIEW:
                return new HeadViewHolder(inflater.inflate(R.layout.live_home_head, parent, false));
            case TYPE_CONTENT_VIEW:
                return new ContentViewHolder(inflater.inflate(R.layout.live_column_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  HeadViewHolder){
            bindHeadView((HeadViewHolder) holder,position);
        }else if (holder instanceof ContentViewHolder) {
            Log.i(TAG, "onBindViewHolder: ");
            bindContentView((ContentViewHolder) holder, position);
        }

    }

    private void bindHeadView(HeadViewHolder holder, int position){

    }

    private void bindContentView(ContentViewHolder holder, final int position) {
        Log.i(TAG, "bindContentView: " + position);
        holder.title.setText(liveHomeColumns.get(position - 1).getClassify());
        holder.more.setOnClickListener(v -> {
            if (onColumnMoreListener != null) {
                onColumnMoreListener.onMoreClick(position);
            }
        });
        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
        ColumnContentViewAdapter columnContentViewAdapter = new ColumnContentViewAdapter(context, liveHomeColumns.get(position - 1).
                getContents(),ColumnContentViewAdapter.TYPE_HOME);
        holder.recyclerView.setAdapter(columnContentViewAdapter);
        holder.recyclerView.setNestedScrollingEnabled(false);
        /**
         * 回调子项监听 抛出子项位置和父项位置
         */
        columnContentViewAdapter.setOnColumnChildClickListener(childPosition -> onColumnChildClickListener.onChildClick(position,childPosition));

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD_VIEW;
        }
        return TYPE_CONTENT_VIEW;
    }

    /**
     * 添加头部View 填充数据长度+1
     * @return
     */
    @Override
    public int getItemCount() {
        return liveHomeColumns == null ? 0 : liveHomeColumns.size() + 1;
    }

    class HeadViewHolder extends BaseViewHolder {
        @BindView(R.id.live_home_head_roll)
        RollPagerView rollPagerView;

        public HeadViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void init() {
            rollPagerView.setHintView(new IconHintView(context, R.mipmap.carousel_pic_check, R.mipmap.carousel_pic_uncheck));
            rollPagerView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (onHomeRecyclerListener != null) {
                        onHomeRecyclerListener.onRollItemClick(position);
                    }
                }
            });
            CarouselLoopAdapter carouselAdapter = new CarouselLoopAdapter(rollPagerView);
            rollPagerView.setAdapter(carouselAdapter);
        }

        class CarouselLoopAdapter extends LoopPagerAdapter {
            private int[] imgs = {
                    R.mipmap.test_one,
                    R.mipmap.test_two,
                    R.mipmap.test_three,
            };

            public CarouselLoopAdapter(RollPagerView viewPager) {
                super(viewPager);
            }

            @Override
            public View getView(ViewGroup container, int position) {
                ImageView view = new ImageView(container.getContext());
                view.setImageResource(imgs[position]);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return view;
            }

            @Override
            public int getRealCount() {
                return imgs.length;
            }
        }
    }

    class ContentViewHolder extends BaseViewHolder {
        @BindView(R.id.live_column_title)
        TextView title;
        @BindView(R.id.live_column_more)
        TextView more;
        @BindView(R.id.live_column_content)
        RecyclerView recyclerView;

        public ContentViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void init() {
        }
    }

    public void setOnHomeRecyclerListener(OnHomeRecyclerListener onHomeRecyclerListener) {
        this.onHomeRecyclerListener = onHomeRecyclerListener;
    }

    public void setOnColumnMoreListener(OnColumnMoreListener onColumnMoreListener){
        this.onColumnMoreListener = onColumnMoreListener;
    }

    public void setOnColumnChildClickListener(OnColumnChildClickListener onColumnChildClickListener){
        this.onColumnChildClickListener = onColumnChildClickListener;
    }
    /**
     * 栏目的点击
     */
    public interface OnColumnMoreListener {
        void onMoreClick(int position);
    }

    /**
     * 轮播的点击
     */
    public interface OnHomeRecyclerListener {
        void onRollItemClick(int position);
    }

    /**
     * 栏目子项的点击
     */
    public interface OnColumnChildClickListener{
        void onChildClick(int parentPosition,int childPosition);
    }
}
