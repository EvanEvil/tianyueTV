package com.tianyue.tv.Activity.Discovery;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.ConstantUtil;
import com.tianyue.tv.Util.KeyBoardUtil;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

public class TotalStationSearchActivity extends BaseActivity {



    @BindView(R.id.search_img)
    ImageView mSearchBtn;   //搜索button
    @BindView(R.id.iv_search_loading)
    ImageView mmLoadingView;    //加载中
    @BindView(R.id.search_layout)
    LinearLayout mSearchLayout; //搜索结果
    @BindView(R.id.search_edit)
    EditText mSearchEdit;   //搜索框

    @BindView(R.id.search_text_clear)
    ImageView mSearchTextClear; //清除搜索

    private String tag;
    private AnimationDrawable mAnimationDrawable;



    /**
     * 初始化
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_total_station_search);
        mmLoadingView.setImageResource(R.drawable.anim_search_loading);
        mAnimationDrawable = (AnimationDrawable) mmLoadingView.getDrawable();
        Intent intent = getIntent();
        if (intent != null) {
            tag = intent.getStringExtra(ConstantUtil.TAG);
            if(tag==null || "".equals(tag)){
                setEmptyLayout();
            }else{


                showSearchAnim();
                mSearchEdit.clearFocus();
                mSearchEdit.setText(tag);
                getSearchData();
            }
        }



        search();
        setUpEditText();
    }

    private void setUpEditText() {
        mSearchTextClear.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(tag))
                mSearchTextClear.setVisibility(View.VISIBLE);
            else
                mSearchTextClear.setVisibility(View.GONE);
        });
    }

    /**
     * 搜索button的点击监听
     */
    private void search() {
        mSearchBtn.setOnClickListener(v -> {
            KeyBoardUtil.closeKeybord(mSearchEdit,context);
            getSearchData();


        });
    }


    /**
     * 获取搜索数据
     */
    private void getSearchData() {
        String searchString = mSearchEdit.getText().toString();
        if(TextUtils.isEmpty(searchString)){
            showToast("搜索内容不能为空");
            return;
        }

        OkGo.post(InterfaceUrl.SEARCH_INFO)
            .tag(this)
            .params("name",searchString)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e(TAG,"onSuccess，结果是："+s);
                        showToast("onSuccess");
                        System.out.println("onSuccess");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        showToast("onError");
                        Log.e(TAG,"onError");
                        System.out.println("onError");
                        setEmptyLayout();
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        showToast("parseError");
                        Log.e(TAG,"onError");
                        System.out.println("parseError");
                        super.parseError(call, e);

                    }
                    //                    @Override
//                    public void onBefore(BaseRequest request) {
//                        super.onBefore(request);
//                        Log.e(TAG,"onBefore");
//                        showSearchAnim();
//                    }

                });
    }

    /**
     * 搜索中的动画
     */
    private void showSearchAnim()
    {


        mmLoadingView.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mAnimationDrawable.start();
    }

    public void setEmptyLayout()
    {

        mmLoadingView.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mmLoadingView.setImageResource(R.mipmap.search_failed);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        super.init();
    }
}
