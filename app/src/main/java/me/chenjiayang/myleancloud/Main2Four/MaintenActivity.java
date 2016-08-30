package me.chenjiayang.myleancloud.Main2Four;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.jude.swipbackhelper.SwipeBackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.chenjiayang.myleancloud.CarItemActivity;
import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class MaintenActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter simpleAdapter;
    private List<AVObject> maintenlist = null;  //所有保养信息
    private ArrayList<HashMap<String, Object>> item = new ArrayList<>();
    //下拉刷新控件
    private SwipeRefreshLayout swipeRefreshLayout;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainten);

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
        mListView = (ListView)this.findViewById(R.id.mainten_listview);
        //创建简单适配器SimpleAdapter
        simpleAdapter = new SimpleAdapter(this,item, R.layout.activity_mainten,
                new String[] {"itemTitle","itemContent","itemHttp"},
                new int[] {R.id.mainten_title, R.id.mainten_content,R.id.mainten_http});
        //加载SimpleAdapter到ListView中
        mListView.setAdapter(simpleAdapter);
    }

    public ArrayList<HashMap<String, Object>> getItem() {
        return item;
    }

    private void init(){
        //查询汽车列表
        AVQuery<AVObject> query = new AVQuery<>("Maintenance");
        query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        maintenlist = list;
                        for (int i = 0; i <maintenlist.size(); i++) {
                            HashMap<String, Object> map = new HashMap<>();
                            String title = maintenlist.get(i).get("title").toString();
                            String content =maintenlist.get(i).get("content").toString();
                            content = content.substring(0,100)+"...";
                            String url = maintenlist.get(i).get("url").toString();
                            url = url.substring(0,20)+"...";
                            map.put("itemTitle", title);
                            map.put("itemContent", content);
                            map.put("itemHttp",url);
                            item.add(map);
                        }
                        setAdapter();
                        final class ListItemClickListener implements AdapterView.OnItemClickListener {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                bundle = new Bundle();
                                bundle.putString("url",maintenlist.get(position).get("url").toString());
                                Intent intent = new Intent(MaintenActivity.this,MaintenItemActivity.class);
                                intent.putExtra("mainten",bundle);
                                startActivity(intent);
                            }
                        }
                        // 点击item的响应事件
                        mListView.setOnItemClickListener(new ListItemClickListener());
                    }
                });

    }


    private void refresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mainten_swipe_container);
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
                maintenlist.clear();
                item.clear();
                init();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(MaintenActivity.this,"Sync...OK");
                        // TODO Auto-generated method stub
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);  //时间2s
            }
        });
    }

    /**
     * 返回Main2Activity的监听方法
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
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
