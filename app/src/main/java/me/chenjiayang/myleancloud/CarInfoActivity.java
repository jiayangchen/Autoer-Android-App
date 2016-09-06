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
                new String[] {"itemTitle","itemTag","itemPhoto", "itemSummary"},
                new int[] {R.id.title, R.id.ifNowDrivingTag, R.id.photograph, R.id.summary});
        //加载SimpleAdapter到ListView中
        mListView.setAdapter(simpleAdapter);
    }


    public ArrayList<HashMap<String, Object>> getItem() {
        return item;
    }

    private void init(){

        final int[] resImags = {
                R.drawable.aodilogo,
                R.drawable.mashaladi,
                R.drawable.dazhong,
                R.drawable.baoma,
                R.drawable.benchi,
                R.drawable.fute,
                R.drawable.fengtian,
                R.drawable.yingfeinidi,
                R.drawable.bentian,
                R.drawable.biyadi,
                R.drawable.nisang,
                R.drawable.bieke,
                R.drawable.mazida,
                R.drawable.leikesasi,
                R.drawable.woerwo,
                R.drawable.xuetielong,
                R.drawable.qirui,
                R.drawable.sanling,
                R.drawable.biaozhi,
                R.drawable.dongfeng,
                R.drawable.xiandai,
                R.drawable.qiya,
                R.drawable.baoshijie,
                R.drawable.lanbojini,
                R.drawable.falali,
                R.drawable.kaidilake,
                R.drawable.hanma,
                R.drawable.luhu,
                R.drawable.yiqi,
                R.drawable.mading,
                R.drawable.changcheng,
                R.drawable.changan,
                R.drawable.jili,
                R.drawable.jeep,
                R.drawable.kelaisile,
                R.drawable.nocar
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

                            if(carlist.get(i).getObjectId().equals(AVUser.getCurrentUser().get("NowDriving"))){
                                map.put("itemTag","正在驾驶>>");
                            } else{
                                map.put("itemTag","");
                            }

                            switch (carname){
                                case "奥迪" :
                                    map.put("itemPhoto", resImags[0]);
                                    break;
                                case "玛莎拉蒂" :
                                    map.put("itemPhoto", resImags[1]);
                                    break;
                                case "大众" :
                                    map.put("itemPhoto", resImags[2]);
                                    break;
                                case "宝马" :
                                    map.put("itemPhoto", resImags[3]);
                                    break;
                                case "奔驰" :
                                    map.put("itemPhoto", resImags[4]);
                                    break;
                                case "福特" :
                                    map.put("itemPhoto", resImags[5]);
                                    break;
                                case "丰田" :
                                    map.put("itemPhoto", resImags[6]);
                                    break;
                                case "英菲尼迪" :
                                    map.put("itemPhoto", resImags[7]);
                                    break;
                                case "本田" :
                                    map.put("itemPhoto", resImags[8]);
                                    break;
                                case "比亚迪" :
                                    map.put("itemPhoto", resImags[9]);
                                    break;
                                case "尼桑" :
                                    map.put("itemPhoto", resImags[10]);
                                    break;
                                case "别克" :
                                    map.put("itemPhoto", resImags[11]);
                                    break;
                                case "马自达" :
                                    map.put("itemPhoto", resImags[12]);
                                    break;
                                case "雷克萨斯" :
                                    map.put("itemPhoto", resImags[13]);
                                    break;
                                case "沃尔沃" :
                                    map.put("itemPhoto", resImags[14]);
                                    break;
                                case "雪铁龙" :
                                    map.put("itemPhoto", resImags[15]);
                                    break;
                                case "奇瑞" :
                                    map.put("itemPhoto", resImags[16]);
                                    break;
                                case "三菱" :
                                    map.put("itemPhoto", resImags[17]);
                                    break;
                                case "标志" :
                                    map.put("itemPhoto", resImags[18]);
                                    break;
                                case "东风" :
                                    map.put("itemPhoto", resImags[19]);
                                    break;
                                case "现代" :
                                    map.put("itemPhoto", resImags[20]);
                                    break;
                                case "起亚" :
                                    map.put("itemPhoto", resImags[21]);
                                    break;
                                case "保时捷" :
                                    map.put("itemPhoto", resImags[22]);
                                    break;
                                case "兰博基尼" :
                                    map.put("itemPhoto", resImags[23]);
                                    break;
                                case "法拉利" :
                                    map.put("itemPhoto", resImags[24]);
                                    break;
                                case "凯迪拉克" :
                                    map.put("itemPhoto", resImags[25]);
                                    break;
                                case "悍马" :
                                    map.put("itemPhoto", resImags[26]);
                                    break;
                                case "路虎" :
                                    map.put("itemPhoto", resImags[27]);
                                    break;
                                case "中国一汽" :
                                    map.put("itemPhoto", resImags[28]);
                                    break;
                                case "阿斯顿马丁" :
                                    map.put("itemPhoto", resImags[29]);
                                    break;
                                case "长城" :
                                    map.put("itemPhoto", resImags[30]);
                                    break;
                                case "长安" :
                                    map.put("itemPhoto", resImags[31]);
                                    break;
                                case "吉利" :
                                    map.put("itemPhoto", resImags[32]);
                                    break;
                                case "Jeep" :
                                    map.put("itemPhoto", resImags[33]);
                                    break;
                                case "克莱斯勒" :
                                    map.put("itemPhoto", resImags[34]);
                                    break;
                                default:
                                    map.put("itemPhoto", resImags[35]);
                                    break;
                            }


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
