package me.chenjiayang.myleancloud.Main2Four;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.jude.swipbackhelper.SwipeBackHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.chenjiayang.myleancloud.CarItemActivity;
import me.chenjiayang.myleancloud.FeedBackActivity;
import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class CollectActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter simpleAdapter;
    private List<AVObject> collectlist = null;  //所有qa信息
    private ArrayList<HashMap<String, Object>> item = new ArrayList<>();
    //下拉刷新控件
    private SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private List<AVObject>all = new ArrayList<>();
    private int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect);

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
        mListView = (ListView)this.findViewById(R.id.collect_listview);
        //创建简单适配器SimpleAdapter
        simpleAdapter = new SimpleAdapter(this,item, R.layout.activity_collect,
                new String[] {"itemTitle","itemContent"},
                new int[] {R.id.collect_title, R.id.collect_content});
        //加载SimpleAdapter到ListView中
        mListView.setAdapter(simpleAdapter);
    }


    public ArrayList<HashMap<String, Object>> getItem() {
        return item;
    }

    private void init(){
        //查询汽车列表
        AVQuery<AVObject> query = new AVQuery<>("News");
        query.whereEqualTo("isCollect",true)
                .findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                all = list;
                collectlist = list;
                x = list.size();
                for (int i = 0; i < collectlist.size(); i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    String title = collectlist.get(i).get("title").toString();
                    String url = collectlist.get(i).get("url").toString();
                    url = url.substring(0,20)+"...";
                    map.put("itemTitle", title);
                    map.put("itemContent", url);
                    item.add(map);
                }
                setAdapter();
            }
        });

        AVQuery<AVObject> query1 = new AVQuery<>("Maintenance");
        query1.whereEqualTo("isCollect",true)
                .findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        all.addAll(list);
                        collectlist = list;
                        for (int i = 0; i < collectlist.size(); i++) {
                            HashMap<String, Object> map = new HashMap<>();
                            String title = collectlist.get(i).get("title").toString();
                            String url = collectlist.get(i).get("url").toString();
                            url = url.substring(0,20)+"...";
                            map.put("itemTitle", title);
                            map.put("itemContent", url);
                            item.add(map);
                        }
                        setAdapter();
                        final class ListItemClickListener implements AdapterView.OnItemClickListener {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //ToastUtil.show(CollectActivity.this,"Click"+position);
                                Bundle bundle = new Bundle();
                                bundle.putString("url",all.get(position).getString("url"));
                                Intent intent = new Intent(CollectActivity.this,MaintenItemActivity.class);
                                intent.putExtra("mainten",bundle);
                                startActivity(intent);
                            }
                        }
                        // 点击item的响应事件
                        mListView.setOnItemClickListener(new ListItemClickListener());
                        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                builder=new AlertDialog.Builder(CollectActivity.this);  //先得到构造器
                                alert = builder.setMessage("Do you want to delete it?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(position>x-1) {
                                                    AVObject todo = AVObject.createWithoutData("Maintenance", all.get(position).getObjectId());
                                                    // 修改 content
                                                    todo.put("isCollect",false);
                                                    // 保存到云端
                                                    todo.saveInBackground();
                                                }else{
                                                    AVObject todo = AVObject.createWithoutData("News", all.get(position).getObjectId());
                                                    // 修改 content
                                                    todo.put("isCollect",false);
                                                    // 保存到云端
                                                    todo.saveInBackground();
                                                }
                                                dialog.dismiss();
                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                                alert.show();
                                return true;
                            }
                        });
                    }
                });
    }

    private void refresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.collect_swipe_container);
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
                collectlist.clear();
                item.clear();
                init();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(CollectActivity.this,"Sync...OK");
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
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tool_refresh:
                ToastUtil.show(CollectActivity.this,"下拉刷新");
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
