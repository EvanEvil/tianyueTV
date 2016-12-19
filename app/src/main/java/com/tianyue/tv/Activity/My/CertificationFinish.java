package com.tianyue.tv.Activity.My;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Bean.User;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.MyApplication;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.AppManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hasee on 2016/8/24.
 */
public class CertificationFinish extends BaseActivity {
    @BindView(R.id.certification_finish)
    Button finish;
    @BindView(R.id.certification_finish_toolbar)
    Toolbar toolbar;

    @Override
    protected void initView() {
        setContentView(R.layout.certification_finish_layout);
        updateUser();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAct();
            }
        });

    }


    @OnClick(R.id.certification_finish)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.certification_finish:
                deleteAct();
                break;
        }
    }

    private void deleteAct() {
        List<Class<?>> activities = new ArrayList<>();
        activities.add(CertificationOne.class);
        activities.add(CertificationTwo.class);
        activities.add(this.getClass());
        AppManager.getAppManager().finishActivities(activities);
    }
}
