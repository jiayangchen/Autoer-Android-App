package me.chenjiayang.myleancloud.Main2Four;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.jude.swipbackhelper.SwipeBackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class MaintenItemActivity extends AppCompatActivity {

    private Intent intent;
    private Bundle bundle;
    private ListView mListView;
    private SimpleAdapter simpleAdapter;
    private List<AVObject> maintenitemlist = null;  //所有保养信息
    private ArrayList<HashMap<String, Object>> item = new ArrayList<>();
    //下拉刷新控件
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainitem);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true);

        //初始化函数
        init();

        //下拉刷新函数
        refresh();



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void setAdapter(){
        mListView = (ListView)this.findViewById(R.id.mainten_item_listview);
        //创建简单适配器SimpleAdapter
        simpleAdapter = new SimpleAdapter(this,item, R.layout.activity_mainten_item,
                new String[] {"itemTitle","itemAuthor","itemP1","itemI1","itemP2","itemP3","itemP4","itemI2","itemP5"},
                new int[] {R.id.main_title, R.id.main_author,R.id.main_p1,R.id.main_i1,R.id.main_p2,R.id.main_p3,R.id.main_p4,R.id.main_i2,R.id.main_p5});
        //加载SimpleAdapter到ListView中
        mListView.setAdapter(simpleAdapter);
    }

    public ArrayList<HashMap<String, Object>> getItem() {
        return item;
    }

    private void init(){
        intent = getIntent();
        bundle = intent.getBundleExtra("mainten");

        HashMap<String, Object> map = new HashMap<>();
        map.put("itemTitle",bundle.getString("main_title"));
        map.put("itemAuthor",bundle.getString("main_author"));
        map.put("itemP1","\t\t\t\t"+bundle.getString("main_p1"));
        map.put("itemI1",R.drawable.mainten1);
        map.put("itemP2","\t\t\t\t"+bundle.getString("main_p2"));
        map.put("itemP3","\t\t\t\t"+bundle.getString("main_p3"));
        map.put("itemP4","\t\t\t\t"+bundle.getString("main_p4"));
        map.put("itemI2",R.drawable.mainten1);
        map.put("itemP5","\t\t\t\t"+bundle.getString("main_p5"));

        item.add(map);
        setAdapter();
    }

    private void refresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.maintenitem_swipe_container);
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
                //maintenlist.clear();
                item.clear();
                init();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(MaintenItemActivity.this,"Sync...OK");
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
        getMenuInflater().inflate(R.menu.mainten_collector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.maintenitem_refresh:
                ToastUtil.show(MaintenItemActivity.this,"下拉刷新");
                break;
            case R.id.maintenitem_collector:
                AVObject todoFolder = new AVObject("isCollect");// 构建对象
                todoFolder.put("ArticleID", bundle.getString("maintenanceID"));// 设置名称
                todoFolder.put("title", bundle.getString("main_title"));// 设置名称
                todoFolder.put("Author", bundle.getString("main_author"));// 设置名称
                todoFolder.put("P1", bundle.getString("main_p1"));// 设置名称
                todoFolder.put("P2", bundle.getString("main_p2"));// 设置名称
                todoFolder.put("P3", bundle.getString("main_p3"));// 设置名称
                todoFolder.put("P4", bundle.getString("main_p4"));// 设置名称
                todoFolder.put("P5", bundle.getString("main_p5"));// 设置名称
                todoFolder.put("url", bundle.getString("main_url"));// 设置名称
                todoFolder.put("currUserID", AVUser.getCurrentUser().getObjectId());// 设置优先级
                todoFolder.saveInBackground();// 保存到服务端

                ToastUtil.show(MaintenItemActivity.this,"收藏成功");
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
