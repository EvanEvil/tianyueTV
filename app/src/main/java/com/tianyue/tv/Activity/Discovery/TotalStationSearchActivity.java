package com.tianyue.tv.Activity.Discovery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.tianyue.tv.Activity.BaseActivity;
import com.tianyue.tv.Activity.Live.LiveDetails;
import com.tianyue.tv.Adapter.SearchInfoAdapter;
import com.tianyue.tv.Bean.LiveHomeColumn;
import com.tianyue.tv.Bean.SearchInfo;
import com.tianyue.tv.Config.InterfaceUrl;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.ConstantUtil;
import com.tianyue.tv.Util.KeyBoardUtil;
import com.tianyue.tv.Util.LogUtil;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

public class TotalStationSearchActivity extends BaseActivity {


    @BindView(R.id.search_img)
    ImageView mSearchBtn;   //搜索button

    @BindView(R.id.search_layout)
    LinearLayout mSearchLayout; //搜索结果
    @BindView(R.id.ll_search_failed)
    LinearLayout ll_search_failed;//搜索失败
    @BindView(R.id.search_edit)
    EditText mSearchEdit;   //搜索框
    @BindView(R.id.recyclerview)
    public XRecyclerView mXRecyclerView;
    @BindView(R.id.search_text_clear)
    ImageView mSearchTextClear; //清除搜索
    @BindView(R.id.search_card_view)
    CardView search_card_view;

    private String tag;
    private AnimationDrawable mAnimationDrawable;
    private SearchInfoAdapter mAdapter;
    private ProgressDialog progressDialog;


    /**
     * 初始化
     */
    @Override
    protected void initView() {
        //加载布局
        setContentView(R.layout.activity_total_station_search);



        //获取传递过来的热搜词
        Intent intent = getIntent();
        if (intent != null) {
            tag = intent.getStringExtra(ConstantUtil.TAG);
            if (tag == null || "".equals(tag)) {
                //如果为空,则是直接点击跳转过来的
                setEmptyLayout();
            } else {
                //带热搜词点击跳转过来的

                mSearchEdit.clearFocus();   //清除搜索框的焦点
                mSearchEdit.setText(tag);
                //从服务器拉取数据
                getSearchDataFromServer(tag);
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

            KeyBoardUtil.closeKeybord(mSearchEdit, context);
            String searchString = mSearchEdit.getText().toString();
            if (TextUtils.isEmpty(searchString)) {
                showToast("搜索内容不能为空");
                return;
            }
            getSearchDataFromServer(searchString);


        });
    }


    /**
     * 获取搜索数据
     *
     * @param searchString
     */
    private void getSearchDataFromServer(String searchString) {


        OkGo.post(InterfaceUrl.SEARCH_INFO)
                .tag(this)
                .params("name", searchString)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String result, Call call, Response response) {

                        LogUtil.e("onSuccess:" + result);
                        processData(result);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        LogUtil.e("onError:");
                        setEmptyLayout();
                    }

                    @Override
                    public void parseError(Call call, Exception e) {

                        System.out.println("parseError");
                        super.parseError(call, e);

                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        Log.e(TAG, "onBefore");
                        showSearchAnim();
                    }

                });
    }

    /**
     * 解析json数据
     *
     * @param result
     */
    private void processData(String result) {
        Gson gson = new Gson();
        SearchInfo searchInfo = gson.fromJson(result, SearchInfo.class);
        if (searchInfo != null && searchInfo.getRet().equals("success")) {
            //有数据,broadCastUser 所有正在直播的集合
            List<SearchInfo.BroadCastUserBean> broadCastUser = searchInfo.getBroadCastUser();
            //隐藏动画
            showSearchResult();
            //设置适配器
            mAdapter = new SearchInfoAdapter(context, broadCastUser);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mXRecyclerView.setLayoutManager(gridLayoutManager);
            mXRecyclerView.setAdapter(mAdapter);
            //点击事件
            mAdapter.setOnItemClickListener((view, postion) -> {
                LiveHomeColumn.LiveHomeColumnContent content = fillBroadColumn(broadCastUser.get(postion));
                Intent intent = new Intent(this, LiveDetails.class);
                intent.putExtra("live_column", content);
                startActivity(intent);
                finish();
            });

        }else if(searchInfo != null && searchInfo.getRet().equals("error")){
            setEmptyLayout();
        }
    }

    /**
     * 显示搜索结果（有数据）
     */
    public void showSearchResult() {
        progressDialog.dismiss();
        ll_search_failed.setVisibility(View.GONE);
        mSearchLayout.setVisibility(View.VISIBLE);

    }


    /**
     * 搜索中的动画
     */
    private void showSearchAnim() {
        //显示ProgressDialog
        progressDialog = ProgressDialog.show(this, "拼命加载中...", "请稍后...", true, false);


    }

    /**
     * 搜索失败
     */
    public void setEmptyLayout() {
        progressDialog.dismiss();
        ll_search_failed.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);

    }

    /**
     * 初始化数据
     */
    @Override
    protected void init() {
        super.init();
    }


    /**
     * 填充单个直播间信息
     */
    private LiveHomeColumn.LiveHomeColumnContent fillBroadColumn(SearchInfo.BroadCastUserBean bean) {
        LiveHomeColumn.LiveHomeColumnContent content = new LiveHomeColumn.LiveHomeColumnContent();
        content.setPicUrl(bean.getImage());
        content.setTitle(bean.getName());
        content.setNumber(bean.getOnlineNum() + "");
        content.setUserId(bean.getUser_id() + "");
        content.setHeadUrl(bean.getHeadUrl());
        content.setNickName(bean.getNickName());
        content.setIsPushPOM(bean.getIsPushPOM());
        content.setQl_push_flow(bean.getQl_push_flow());
        content.setPlayAddress(bean.getPlayAddress());
        content.setFocusNum(bean.getFocusNum());
        return content;
    }
}
