package me.chenjiayang.myleancloud.Gas;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class AroundGasActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter simpleAdapter;
    private List<AVObject> around_gas_list = null;  //所有qa信息
    private ArrayList<HashMap<String, Object>> item = new ArrayList<>();
    //下拉刷新控件
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.around_gas);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true);

        init();

        refresh();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void setAdapter(){
        mListView = (ListView)this.findViewById(R.id.around_gas_listview);
        //创建简单适配器SimpleAdapter
        simpleAdapter = new SimpleAdapter(this,item, R.layout.activity_around_gas,
                new String[] {"itemName","itemDistance","itemAddress","itemPrice","itemFwlsmc"},
                new int[] {R.id.around_gas_name, R.id.around_gas_distance,R.id.around_gas_address,R.id.around_gas_price,R.id.around_gas_fwlsmc});
        //加载SimpleAdapter到ListView中
        mListView.setAdapter(simpleAdapter);
    }

    public ArrayList<HashMap<String, Object>> getItem() {
        return item;
    }

    private void init(){
        JuheSDKInitializer.initialize(getApplicationContext());
        final Parameters params = new Parameters();
        params.add("lon","116.403119");
        params.add("lat","39.916042");
        params.add("r",3000);
        params.add("page",1);
        params.add("format",1);
        JuheData.executeWithAPI(getApplicationContext(), 7, "http://apis.juhe.cn/oil/local",
                JuheData.GET, params, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            JSONObject result = object.getJSONObject("result");
                            String sk = result.getString("data");
                            JSONArray jsonArray = new JSONArray(sk);
                            for(int j=0; j<jsonArray.length(); j++){

                                JSONObject object1 = jsonArray.getJSONObject(j);
                                JSONObject temp1 = object1.getJSONObject("gastprice");


                                HashMap<String, Object> map = new HashMap<>();
                                map.put("itemName", object1.getString("name"));
                                String dis = "("+object1.getString("distance")+"m"+")";
                                map.put("itemDistance", dis);
                                map.put("itemAddress", object1.getString("address"));
                                map.put("itemPrice",object1.getString("brandname")+"，"+
                                        object1.getString("type"));
                                map.put("itemFwlsmc",object1.getString("fwlsmc"));
                                item.add(map);
                            }
                            setAdapter();

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(getApplicationContext(),"同步成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {
                        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.around_gas_swipe_container);
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
                item.clear();
                init();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(AroundGasActivity.this,"Sync...OK");
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
                ToastUtil.show(AroundGasActivity.this,"下拉刷新");
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
