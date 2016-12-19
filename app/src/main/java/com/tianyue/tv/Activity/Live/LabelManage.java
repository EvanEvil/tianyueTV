package com.tianyue.tv.Activity.Live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Adapter.LabelManageAdapter;
import com.tianyue.tv.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 标签管理
 * Created by hasee on 2016/11/29.
 */
public class LabelManage extends BaseActivity implements LabelManageAdapter.LabelManageItemClick {
    @BindView(R.id.label_manage_cancel)
    TextView labelCancel;
    @BindView(R.id.label_manage_save)
    TextView labelSave;
    @BindView(R.id.label_manage_toolbar)
    Toolbar toolbar;
    @BindView(R.id.label_manage_custom)
    EditText labelCustom;
    @BindView(R.id.label_manage_button)
    Button labelButton;
    @BindView(R.id.label_manage_list)
    RecyclerView labelManageList;
    List<String> data;
    Intent intent;
    private int currentCode;

    private int currentPosition;
    LabelManageAdapter labelManageAdapter;

    @Override
    protected void initView() {
        setContentView(R.layout.label_manage_layout);
    }

    @Override
    protected void init() {
        super.init();
        data = new ArrayList<>();
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        String label = bundle.getString("label");
        if (label != null) {
            String[] labels = label.split("_");
            for (int i = 0; i < labels.length; i++) {
                data.add(labels[i]);
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        labelManageList.setLayoutManager(linearLayoutManager);
        labelManageList.setItemAnimator(new DefaultItemAnimator());
        labelManageAdapter = new LabelManageAdapter(this, data);
        labelManageAdapter.setOnItemClick(this);
        labelManageList.setAdapter(labelManageAdapter);
    }

    @Override
    public void itemOnClick(int requestCode, String message, int position) {
        switch (requestCode) {
            case LabelManageAdapter.ALTER_LABEL:
                labelCustom.setText(message);
                labelButton.setText("修改");
                currentCode = LabelManageAdapter.ALTER_LABEL;
                break;
            case LabelManageAdapter.DELETE_LABEL:
                currentCode = LabelManageAdapter.DELETE_LABEL;
                break;
        }
        currentPosition = position;
    }

    @OnClick({R.id.label_manage_cancel, R.id.label_manage_save, R.id.label_manage_custom, R.id.label_manage_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.label_manage_cancel:
                finish();
                break;
            case R.id.label_manage_save:
                String keyWord = "";
                for (String s : data) {
                    keyWord = s+"_"+keyWord;
                }
                Log.i(TAG, "onClick: "+keyWord);
                Bundle bundle = new Bundle();
                bundle.putString("label",keyWord);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.label_manage_custom:
                break;
            case R.id.label_manage_button:
                if (currentCode == LabelManageAdapter.ALTER_LABEL) {
                    data.remove(currentPosition);
                    data.add(currentPosition, labelCustom.getText().toString());
                } else {
                    String label = labelCustom.getText().toString();
                    if (label.equals("")) {
                        showToast("标签内容不能为空");
                        return;
                    }
                    if (data.size() < 4) {
                        data.add(labelCustom.getText().toString());
                    } else {
                        showToast("标签不能超过四个");
                    }
                }
                labelManageAdapter.notifyDataSetChanged();
                updateView();
                break;
        }
    }

    private void updateView() {
        labelButton.setText("贴上");
        labelCustom.setText("");
        currentCode = 1;
    }
}
