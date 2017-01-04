package com.tianyue.tv.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tianyue.tv.Base.BaseViewHolder;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.LogUtil;

import java.util.List;

import butterknife.BindView;

/**
 * Created by hasee on 2016/12/7.
 */
public class ColumnContentViewAdapter extends RecyclerView.Adapter<ColumnContentViewAdapter.ColumnChildViewHolder> {
    Context context;
    LayoutInflater inflater;
    List<LiveHomeColumn.LiveHomeColumnContent> columnContent;

    public static final int TYPE_HOME = 0xff01;

    public static final int TYPE_CONTENT = 0xff02;

    OnColumnChildClickListener onColumnChildClickListener;

    int type = TYPE_CONTENT;

    public ColumnContentViewAdapter(Context context, List<LiveHomeColumn.LiveHomeColumnContent> columnContent) {
        this(context, columnContent, TYPE_CONTENT);
    }

    public ColumnContentViewAdapter(Context context, List<LiveHomeColumn.LiveHomeColumnContent> columnContent, int type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.columnContent = columnContent;
        this.type = type;
    }

    @Override
    public ColumnChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColumnChildViewHolder(inflater.inflate(R.layout.live_column_child_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ColumnChildViewHolder holder, final int position) {

        LiveHomeColumn.LiveHomeColumnContent column = columnContent.get(position);
        if (column.getPicUrl() == null || column.getPicUrl().equals("")) {
//            Picasso.with(context).load(column.getResourceId()).into(holder.preview);
        } else {
            Picasso.with(context).load(column.getPicUrl()).into(holder.preview);
        }
        holder.nickName.setText(column.getNickName());
        holder.title.setText(column.getTitle());
        holder.number.setText(column.getNumber());
        holder.preview.setOnClickListener(v -> {
            if (onColumnChildClickListener != null) {
                onColumnChildClickListener.onChildClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (type == TYPE_HOME) {
            return columnContent == null ? 0 : columnContent.size() >= 4 ? 4 : columnContent.size();
        }
        return columnContent == null ? 0 : columnContent.size();
    }

    public void setOnColumnChildClickListener(OnColumnChildClickListener onColumnChildClickListener) {
        this.onColumnChildClickListener = onColumnChildClickListener;
    }

    class ColumnChildViewHolder extends BaseViewHolder {
        @BindView(R.id.live_column_child_pic)
        ImageView preview;
        @BindView(R.id.live_column_child_title)
        TextView title;
        @BindView(R.id.live_column_child_nickName)
        TextView nickName;
        @BindView(R.id.live_column_child_number)
        TextView number;

        public ColumnChildViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void init() {

        }
    }

    public interface OnColumnChildClickListener {
        void onChildClick(int childPosition);
    }
}
