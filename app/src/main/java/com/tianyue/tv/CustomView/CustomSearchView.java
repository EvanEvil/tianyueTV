package com.tianyue.tv.CustomView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.tianyue.tv.Adapter.HistorySearchAdapter;
import com.tianyue.tv.Adapter.HotSearchAdapter;
import com.tianyue.tv.R;
import com.tianyue.tv.Util.RecordManage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2016/8/8.
 */
public class CustomSearchView extends LinearLayout implements View.OnClickListener{
    private Context context;//上下文对象
    private SearchView mSearchView;//搜索的输入框
    private GridView hotGridView;//热搜显示GridView
    private GridView historyGridView;//历史记录显示GridView
    private HotSearchAdapter hotAdapter;//热搜的Adapter
    private Toolbar toolbar;
    private HistorySearchAdapter historyAdapter;//历史记录的Adapter
    private List<String> hotLists;//热搜记录集合
    private List<String> historyLists;//历史搜索记录集合
    private RecordManage recordManage;//数据管理类
    private Button clear;//清除按钮
    private final int REFRESH_ADAPTER = 10 ;//刷新adapter
    String TAG = "result";
    public CustomSearchView(Context context) {
        super(context);}
    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.search_layout,this);
        initView();
    }
    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    /**
     * 初始化控件
     */
    private void initView(){

        //List初始化
        hotLists = new ArrayList<String>();
        historyLists = new ArrayList<String>();
        recordManage = new RecordManage(context);
        historyLists = recordManage.getHistoryRecord();
        //控件初始化
        hotGridView = (GridView) findViewById(R.id.hot_search);
        historyGridView = (GridView) findViewById(R.id.history_search);
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbar.inflateMenu(R.menu.menu_search_toolbar);
        MenuItem item =  toolbar.getMenu().findItem(R.id.search_item_1);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setQueryHint("搜索主播的名字/房间名");
        clear = (Button) findViewById(R.id.search_clear);

        //SharedPreferences的初始化
        //adapter初始化
        historyAdapter = new HistorySearchAdapter(context,historyLists);
        hotAdapter = new HotSearchAdapter(context,hotLists);


        hotGridView.setAdapter(hotAdapter);
        historyGridView.setAdapter(historyAdapter);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        clear.setOnClickListener(this);
        mSearchView.setOnQueryTextListener(queryTextListener);
        historyGridView.setOnItemClickListener(itemClickListener);


    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           String history = (String) parent.getItemAtPosition(position);
            mSearchView.setIconifiedByDefault(false);
            mSearchView.setQuery(history,false);
        }
    };
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            Log.i(TAG, "onKey: "+query);
            recordManage.addHistoryRecord(query);
            historyLists = recordManage.getHistoryRecord();
            Log.i(TAG, "onKey: "+historyLists.size());
            handler.sendEmptyMessage(REFRESH_ADAPTER);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==10){
                historyAdapter.notifyDataSetChanged();
            }
        }
    };
    /**
     * 按钮的监听设置
     * @param v
     */

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.search_clear:
                 if (historyLists != null) {
                     recordManage.clearHistory();
                     historyLists.clear();
                     handler.sendEmptyMessage(REFRESH_ADAPTER);
                 }
                 break;
             default:
                 break;
         }
    }
}
