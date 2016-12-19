package com.tianyue.tv.Activity.My;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Adapter.AttentionAnchorAdapter;
import com.tianyue.tv.Bean.AttentionAnchorInfo;
import com.tianyue.tv.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hasee on 2016/8/24.
 */
public class AttentionAnchor extends BaseActivity {
    @BindView(R.id.my_attention_anchor_list)
    GridView attention;
    @BindView(R.id.my_attention_anchor_manage)
    TextView manage;
    @BindView(R.id.my_attention_anchor_all)
    TextView all;
    @BindView(R.id.my_attention_anchor_toolbar)
    Toolbar toolbar;
    @BindView(R.id.my_attention_anchor_cancel)
    Button cancelAttention;//取消关注
    List<AttentionAnchorInfo> lists;
    AttentionAnchorAdapter anchorAdapter;
    boolean isAllChecked = false;//是否全选
    boolean isEditMode = false;//是否为编辑模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        setContentView(R.layout.my_attention_anchor_layout);
        lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new AttentionAnchorInfo());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        anchorAdapter = new AttentionAnchorAdapter(this, lists);
        attention.setAdapter(anchorAdapter);
        attention.setOnItemClickListener(itemClickListener);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isEditMode) {
                anchorAdapter.setItemChecked(position);
            }
        }
    };
    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    all.setVisibility(View.GONE);
                    manage.setText("管理");
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    @OnClick({R.id.my_attention_anchor_manage,R.id.my_attention_anchor_all,R.id.my_attention_anchor_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_attention_anchor_manage:
                if (!isEditMode) {
                    //进入编辑模式 显示控件
                    isEditMode = true;
                    all.setVisibility(View.VISIBLE);
                    cancelAttention.setVisibility(View.VISIBLE);
                    manage.setText("完成");
                } else {

                }
                break;
            case R.id.my_attention_anchor_all:
                if (!isAllChecked) {
                    anchorAdapter.setAllItemChecked();
                    isAllChecked = true;
                } else {
                    anchorAdapter.setAllItemUnChecked();
                    isAllChecked = false;
                }

                break;
        }
    }
}
