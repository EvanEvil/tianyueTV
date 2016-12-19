package com.tianyue.tv.Base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by hasee on 2016/12/7.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    
    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        init();
    }

    protected void init(){

    }
}
