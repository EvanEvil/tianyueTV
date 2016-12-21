package com.tianyue.tv.Activity;

import android.util.Log;

import com.tianyue.mylibrary.view.ScrollPickerView;
import com.tianyue.tv.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 *
 * Created by hasee on 2016/12/12.
 */
public class Test extends BaseActivity {
    @BindView(R.id.test_type_1)
    ScrollPickerView testType1;
    @BindView(R.id.test_type_2)
    ScrollPickerView testType2;

    List<String> type_1 = new ArrayList<>();

    List<String> type_2 = new ArrayList<>();

    @Override
    protected void initView() {
        setContentView(R.layout.test_layout);
    }

    @Override
    protected void init() {
        String[] type = getResources().getStringArray(R.array.live_home_tab);
        type_1 = Arrays.asList(type);
        testType1.setData(type_1);
        testType1.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(List<String> data, int position) {
                Log.i(TAG, "onSelected: "+data.get(position));
            }
        });

        //增加
    }
    //测试
}
