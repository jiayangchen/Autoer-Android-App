package me.chenjiayang.myleancloud;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
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

import me.chenjiayang.myleancloud.util.ToastUtil;

public class CarInfoActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter simpleAdapter;
    private List<AVObject> carlist = null;  //所有车辆
    private ArrayList<HashMap<String, Object>> item = new ArrayList<>();
    private Bundle bundle;
    private Bundle bundle_id;
    //下拉刷新控件
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carinfo);

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
        mListView = (ListView)this.findViewById(R.id.listview);
        //创建简单适配器SimpleAdapter
        simpleAdapter = new SimpleAdapter(this,item, R.layout.activity_car_info,
                new String[] {"itemTitle","itemPhoto", "itemSummary"},
                new int[] {R.id.title, R.id.photograph, R.id.summary});
        //加载SimpleAdapter到ListView中
        mListView.setAdapter(simpleAdapter);
    }


    public ArrayList<HashMap<String, Object>> getItem() {
        return item;
    }

    private void init(){

        final int[] resImags = {
                R.drawable.aodilogo,
                R.drawable.mashaladi
        };

        //查询汽车列表
        AVQuery<AVObject> query = new AVQuery<>("Car");
        query.whereEqualTo("currUserID", AVUser.getCurrentUser().getObjectId())
                .findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        carlist = list;
                        for (int i = 0; i <carlist.size(); i++) {
                            HashMap<String, Object> map = new HashMap<>();
                            String carname = carlist.get(i).get("CarName").toString();
                            String carsummary = "车牌号："+carlist.get(i).get("License_plate_number").toString()+"\n" +"发动机号："+ carlist.get(i).get("Engine_no").toString()+"\n" +
                                    "里程数："+ carlist.get(i).get("mileage").toString() +"\n"+"汽油量："+
                                    carlist.get(i).get("Amount_of_gasoline").toString() +"%\n"+"变速器情况："+ carlist.get(i).get("transmission").toString();

                            map.put("itemTitle", carname);
                            map.put("itemPhoto", resImags[i]);
                            map.put("itemSummary", carsummary);
                            item.add(map);
                        }
                        setAdapter();
                        final class ListItemClickListener implements AdapterView.OnItemClickListener {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                //用于传递到车辆详情页面的bundle

                                bundle_id = new Bundle();
                                bundle_id.putString("ObjectId",carlist.get(position).getObjectId());

                                bundle = new Bundle();
                                bundle.putString("CarName",carlist.get(position).get("CarName").toString());
                                bundle.putString("License_plate_number",carlist.get(position).get("License_plate_number").toString());
                                bundle.putString("Engine_no",carlist.get(position).get("Engine_no").toString());
                                bundle.putString("mileage",carlist.get(position).get("mileage").toString());
                                bundle.putString("Amount_of_gasoline",carlist.get(position).get("Amount_of_gasoline").toString());
                                bundle.putString("Engine_situation",carlist.get(position).get("Engine_situation").toString());
                                bundle.putString("CarLight",carlist.get(position).get("CarLight").toString());
                                bundle.putString("transmission",carlist.get(position).get("transmission").toString());
                                Intent intent = new Intent(CarInfoActivity.this,CarItemActivity.class);
                                intent.putExtra("carlist_elem",bundle);
                                intent.putExtra("car_ObjectId",bundle_id);
                                startActivity(intent);
                            }
                        }

                        // 点击item的响应事件
                        mListView.setOnItemClickListener(new ListItemClickListener());
                    }
                });
    }



    private void refresh(){

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
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
                carlist.clear();
                item.clear();
                init();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(CarInfoActivity.this,"同步成功");
                        // TODO Auto-generated method stub
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);  //时间3s
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    /**
     * 返回Main2Activity的监听方法
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tool_refresh:
                ToastUtil.show(CarInfoActivity.this,"下拉刷新");
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
