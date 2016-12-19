package com.tianyue.tv.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianyue.tv.R;

import java.util.List;

/**
 * Created by hasee on 2016/11/29.
 */
public class LabelManageAdapter extends RecyclerView.Adapter<LabelManageAdapter.MyViewHolder> {
    LayoutInflater inflater;
    Context context;
    List<String> data;
    LabelManageItemClick itemClick;
    public static final int ALTER_LABEL = 101;
    public static final int DELETE_LABEL = 102;
    String TAG = this.getClass().getSimpleName();

    public LabelManageAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.label_manage_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        holder.labelView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView labelView;
        ImageView labelCancel;
        ImageView labelAlter;

        public MyViewHolder(View itemView) {
            super(itemView);
            labelView = (TextView) itemView.findViewById(R.id.label_manage_label);
            labelCancel = (ImageView) itemView.findViewById(R.id.label_manage_label_cancel);
            labelAlter = (ImageView) itemView.findViewById(R.id.label_manage_label_alter);

            labelAlter.setOnClickListener(this);
            labelCancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.label_manage_label_alter:
                    if (itemClick != null) {
                        itemClick.itemOnClick(ALTER_LABEL,labelView.getText().toString(),getLayoutPosition());
                    }
                    break;
                case R.id.label_manage_label_cancel:
                    if (itemClick != null) {
                        itemClick.itemOnClick(DELETE_LABEL,labelView.getText().toString(),getLayoutPosition());
                    }
                    notifyItemRemoved(getLayoutPosition());
                    Log.i(TAG, "onClick: "+getLayoutPosition());
                    data.remove(getLayoutPosition());
                    notifyDataSetChanged();
                    break;
            }
        }
    }

    public void setOnItemClick(LabelManageItemClick itemClick){
        this.itemClick = itemClick;
    }

    public interface LabelManageItemClick{
        void itemOnClick(int requestCode,String message,int position);
    }
}
