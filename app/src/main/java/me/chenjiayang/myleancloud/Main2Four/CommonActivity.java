package me.chenjiayang.myleancloud.Main2Four;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.jude.swipbackhelper.SwipeBackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.chenjiayang.myleancloud.FeedBackActivity;
import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class CommonActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter simpleAdapter;
    private List<AVObject> commonlist = null;  //所有qa信息
    private ArrayList<HashMap<String, Object>> item = new ArrayList<>();
    //下拉刷新控件
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_qa);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true);

        //初始化函数
        init();

        //下拉刷新函数
        refresh();
        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }
    public void setAdapter(){
        mListView = (ListView)this.findViewById(R.id.common_listview);
        //创建简单适配器SimpleAdapter
        simpleAdapter = new SimpleAdapter(this,item, R.layout.activity_common,
                new String[] {"itemTitle","itemContent"},
                new int[] {R.id.common_title, R.id.common_content});
        //加载SimpleAdapter到ListView中
        mListView.setAdapter(simpleAdapter);
    }
    public ArrayList<HashMap<String, Object>> getItem() {
        return item;
    }

    private void init(){
        //查询汽车列表
        AVQuery<AVObject> query = new AVQuery<>("QA");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                commonlist = list;
                for (int i = 0; i < commonlist.size(); i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    String title = commonlist.get(i).get("title").toString();
                    String content = commonlist.get(i).get("content").toString();
                    map.put("itemTitle", title);
                    map.put("itemContent", content);
                    item.add(map);
                }
                setAdapter();
            }
        });
    }


    private void refresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.common_qa_swipe_container);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub

                //refresh页面
                commonlist.clear();
                item.clear();
                init();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(CommonActivity.this,"Sync...OK");
                        // TODO Auto-generated method stub
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);  //时间2s
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.common_refresh:
                ToastUtil.show(CommonActivity.this,"下拉刷新");
                break;
            case R.id.common_feedback:
                startActivity(new Intent(CommonActivity.this, FeedBackActivity.class));
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }
}
