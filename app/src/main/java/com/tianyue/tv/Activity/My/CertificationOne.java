package com.tianyue.tv.Activity.My;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.R;
import butterknife.BindView;
import butterknife.OnClick;
/**
 * Created by hasee on 2016/8/24.
 */
public class CertificationOne extends BaseActivity {
    @BindView(R.id.certification_notice_finish)
    Button read;
    @BindView(R.id.certification_notice_toolbar)
    Toolbar toolbar;

    @Override
    protected void initView() {
        setContentView(R.layout.certification_notice_layout);
        toolbar.setNavigationOnClickListener(v -> finish());
    }


    @OnClick(R.id.certification_notice_finish)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.certification_notice_finish:
                startActivity(CertificationTwo.class);
                break;
        }
    }

}
