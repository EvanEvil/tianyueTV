package com.tianyue.tv.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tianyue.tv.R;

/**
 * Created by hasee on 2016/11/30.
 */
public class LiveGiftFragment extends BaseFragment {
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gift_fragment,null);
        return view;
    }

    @Override
    protected void init() {

    }

    @Override
    public void finishCreateView(Bundle state) {

    }
}
