package me.chenjiayang.myleancloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.sdk.android.Parameters;
import com.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import me.chenjiayang.myleancloud.Main2Four.CollectActivity;
import me.chenjiayang.myleancloud.Main2Four.CommonActivity;
import me.chenjiayang.myleancloud.Main2Four.CompassActivity;
import me.chenjiayang.myleancloud.Main2Four.MaintenActivity;
import me.chenjiayang.myleancloud.Main2Four.NotificationActivity;
import me.chenjiayang.myleancloud.Main2Four.StatisticsActivity;
import me.chenjiayang.myleancloud.cardlayout.CardLayoutActivity;
import me.chenjiayang.myleancloud.music_bar.MusicActivity;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Main2Activity tag_main2 = null;
    String[] userName={"CarName","License_plate_number","Engine_no","mileage","Amount_of_gasoline",
            "Engine_situation","CarLight","transmission"};
    //private TextView scanQRCodeTextView;
    private RollPagerView mRollViewPager;

    private TextView now_drive_car;
    private TextView now_gas_num;
    private TextView now_mile_num;
    private TextView now_engine_situation;
    private TextView now_trans_situation;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private String[] items = null;
    private String[] car_id = null;
    private int x;

    private BootstrapCircleThumbnail head_Iv;
    private TextView head_tv;
    private TextView car_num_tv;

    private BootstrapButton change_car;
    private BootstrapButton bind_car;
    private BootstrapButton edit_car;
    private BootstrapButton collector;
    private BootstrapButton nav_settings;
    private BootstrapButton nav_quit;

    private NavigationView navigationView;

    private ImageView Main2_Music;
    private ImageView Main2_Tips;
    private ImageView Main2_Question;
    private ImageView Main2_Statistics;
    //private ImageView Main2_Sun;

    private TextView main2_pic_hint;
    private ImageView Notice_Img;

    private Bundle bundle;
    private Bundle bundle_id;
    private Bundle news;

    //weather
    private TextView w_city;
    private TextView w_tem;
    private TextView w_wind;
    private TextView w_situ;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tag_main2 = this;

        now_drive_car = (TextView) findViewById(R.id.now_drive_car);
        now_gas_num = (TextView) findViewById(R.id.now_gas_num);
        now_mile_num = (TextView) findViewById(R.id.now_mile_num);
        now_engine_situation = (TextView) findViewById(R.id.now_engine_situation);
        now_trans_situation = (TextView) findViewById(R.id.now_trans_situation);
        nav_settings = (BootstrapButton) findViewById(R.id.main2_nav_settings);
        nav_quit = (BootstrapButton) findViewById(R.id.main2_nav_quit);
        main2_pic_hint = (TextView) findViewById(R.id.main2_pic_hint_bg);
        main2_pic_hint.getBackground().setAlpha(80);

        nav_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if(MusicActivity.music_tag == null){
                    System.exit(0);
                }else {
                    MusicActivity.music_tag.finish();
                    System.exit(0);
                }
            }
        });

        nav_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this,SettingsActivity.class));
            }
        });

        change_car = (BootstrapButton) findViewById(R.id.main2_change_car);
        change_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVQuery<AVObject> query = new AVQuery<>("Car");
                query.whereEqualTo("currUserID", AVUser.getCurrentUser().getObjectId());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {

                        if(list.size()<1){
                            ToastUtil.show(Main2Activity.this,"You haven't bound any cars");
                        }
                        else if(list.size() == 1){
                            ToastUtil.show(Main2Activity.this,"You have just one car");
                        }
                        else {
                            items = new String[list.size()];
                            car_id = new String[list.size()];
                            int i=0;
                            for(AVObject ao : list){
                                items[i] = ao.get("CarName").toString();
                                car_id[i] = ao.getObjectId();
                                i++;
                            }
                            dialog_change_car();
                        }

                    }
                });
            }
        });

        SubMenuFourImg();

        setNowDriving();

        three_btn();

        mRollViewPager = (RollPagerView) findViewById(R.id.roll_view_pager);
        //设置播放时间间隔
        mRollViewPager.setPlayDelay(3000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(500);
        //设置适配器
        mRollViewPager.setAdapter(new TestLoopAdapter(mRollViewPager));

        //设置指示器（顺序依次）
        //自定义指示器图片
        //设置圆点指示器颜色
        //设置文字指示器
        //隐藏指示器
        mRollViewPager.setHintView(new ColorPointHintView(this, Color.YELLOW,Color.WHITE));
        mRollViewPager.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //ToastUtil.show(Main2Activity.this,"click on"+position);
                AVQuery<AVObject> query = new AVQuery<>("News");
                query.whereEqualTo("index", position+1);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if(list.size() == 1){
                            news = new Bundle();
                            news.putString("newsid",list.get(0).getObjectId());
                            news.putString("newsurl",list.get(0).get("url").toString());
                            Intent intent = new Intent(Main2Activity.this,NewsScrollingActivity.class);
                            intent.putExtra("news",news);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        //swipselector();

        //保存推送id
        saveInsID();

        //判断是否推送加油信息
        //pushMsg();

        setNoticeImg();

        //Toolbar的操作
        ToolBarOperation();

        setNavViewCount();

        //天气预报
        //weather();

        //Sun_Rotate();
    }
    /*private void weather(){
        w_city = (TextView) findViewById(R.id.weather_city);
        w_tem = (TextView) findViewById(R.id.weather_tem);
        w_wind = (TextView) findViewById(R.id.weather_wind);
        w_situ = (TextView) findViewById(R.id.weather_situ);

        JuheSDKInitializer.initialize(getApplicationContext());
        Parameters params = new Parameters();
        params.add("cityname","苏州");
        params.add("dtype","json");
        params.add("format",1);
        JuheData.executeWithAPI(getApplicationContext(), 39, "http://v.juhe.cn/weather/index",
                JuheData.GET, params, new DataCallBack() {
            @Override
            public void onSuccess(int i, String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    JSONObject result = object.getJSONObject("result");
                    JSONObject sk = result.getJSONObject("today");

                    w_city.setText(sk.getString("city"));
                    w_tem.setText(sk.getString("temperature"));
                    w_situ.setText(sk.getString("weather"));
                    w_wind.setText(sk.getString("wind"));

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                ToastUtil.show(Main2Activity.this,"同步成功");
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                ToastUtil.show(Main2Activity.this,throwable.getMessage());
            }
        });
    }*/

    /*private void Sun_Rotate(){
        Main2_Sun = (ImageView) findViewById(R.id.main2_sun);
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            Main2_Sun.startAnimation(operatingAnim);
        }
    }*/

    private void setNoticeImg(){
        Notice_Img = (ImageView) findViewById(R.id.notice_img);

        int noticeNum = 2;

        if(noticeNum==1) {
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_100));
        }else if(noticeNum==2){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_200));
        }else if(noticeNum==3){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_300));
        }else if(noticeNum==4){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_400));
        }else if(noticeNum==5){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_500));
        }else if(noticeNum==6){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_600));
        }else if(noticeNum==7){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_700));
        }else if(noticeNum==8){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_800));
        }else if(noticeNum==9){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_900));
        }else if(noticeNum>9){
            Notice_Img.setImageDrawable(getResources().getDrawable(R.mipmap.circled_more));
        }
    }

    private void SubMenuFourImg(){
        Main2_Tips = (ImageView) findViewById(R.id.main2_tips);
        Main2_Statistics = (ImageView) findViewById(R.id.main2_statistics);
        Main2_Music = (ImageView) findViewById(R.id.main2_music);
        Main2_Question = (ImageView) findViewById(R.id.main2_question);

        Main2_Music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, MusicActivity.class));
                finish();
            }
        });

        Main2_Question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, CommonActivity.class));
            }
        });

        Main2_Tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, MaintenActivity.class));
            }
        });

        Main2_Statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, StatisticsActivity.class));
            }
        });
    }

    private void setNavViewCount(){
        AVQuery<AVObject> query_car_num_badge = new AVQuery<>("Car");
        query_car_num_badge.whereEqualTo("currUserID", AVUser.getCurrentUser().getObjectId());
        query_car_num_badge.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                setMenuCounter(R.id.nav_car,list.size());
            }
        });

        AVQuery<AVObject> query_list_num_badge = new AVQuery<>("Order");
        query_list_num_badge.whereEqualTo("currUserID", AVUser.getCurrentUser().getObjectId());
        query_list_num_badge.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                setMenuCounter(R.id.nav_order,list.size());
            }
        });
    }

    private void three_btn(){

        bind_car = (BootstrapButton) findViewById(R.id.main2_bind_car);
        edit_car = (BootstrapButton) findViewById(R.id.main2_edit_car);

        bind_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this,CaptureActivity.class));
            }
        });

        edit_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(Main2Activity.this,CarInfoActivity.class));
                AVQuery<AVObject> avQuery = new AVQuery<>("Car");
                avQuery.getInBackground(AVUser.getCurrentUser().get("NowDriving").toString(), new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {

                        bundle_id = new Bundle();
                        bundle_id.putString("ObjectId",avObject.getObjectId());

                        bundle = new Bundle();
                        bundle.putString("CarName",avObject.get("CarName").toString());
                        bundle.putString("License_plate_number",avObject.get("License_plate_number").toString());
                        bundle.putString("Engine_no",avObject.get("Engine_no").toString());
                        bundle.putString("mileage",avObject.get("mileage").toString());
                        bundle.putString("Amount_of_gasoline",avObject.get("Amount_of_gasoline").toString());
                        bundle.putString("Engine_situation",avObject.get("Engine_situation").toString());
                        bundle.putString("CarLight",avObject.get("CarLight").toString());
                        bundle.putString("transmission",avObject.get("transmission").toString());

                        Intent intent = new Intent(Main2Activity.this,CarItemActivity.class);
                        intent.putExtra("carlist_elem",bundle);
                        intent.putExtra("car_ObjectId",bundle_id);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void dialog_change_car(){

        builder=new AlertDialog.Builder(Main2Activity.this);  //先得到构造器
        alert = builder.setTitle("Change Car")
                        .setSingleChoiceItems(items,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ToastUtil.show(Main2Activity.this,items[which]);
                        x = which;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() { //设置确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //关闭dialog
                        try {
                                AVObject todo = AVObject.createWithoutData("_User", AVUser.getCurrentUser().getObjectId());
                                todo.put("NowDriving",car_id[x]);
                                todo.saveInBackground();
                                setNowDriving();
                                x = 0;
                        }catch (Exception e){
                            //ToastUtil.show(Main2Activity.this, items[0]);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { //设置取消按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        alert.show();
    }

    private void setNowDriving(){
        AVQuery<AVObject> query = new AVQuery<>("Car");
        query.whereEqualTo("currUserID", AVUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size() == 0 || AVUser.getCurrentUser().getString("NowDriving")==null){
                    now_drive_car.setText("无汽车");
                }else {
                    AVQuery<AVObject> avQuery = new AVQuery<>("Car");
                    avQuery.getInBackground(AVUser.getCurrentUser().get("NowDriving").toString(), new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            // object 就是 id 为 558e20cbe4b060308e3eb36c 的 Todo 对象实例
                            now_drive_car.setText(avObject.get("CarName").toString());
                            now_gas_num.setText(avObject.get("Amount_of_gasoline").toString() + "%");
                            now_mile_num.setText(avObject.get("mileage").toString() + "km");
                            now_engine_situation.setText((avObject.get("Engine_situation").toString()) == "true" ? "OK" : "Bad");
                            now_trans_situation.setText((avObject.get("transmission").toString()) == "true" ? "OK" : "Bad");
                        }
                    });
                }
            }
        });


    }

    /*private void swipselector(){
        SwipeSelector swipeSelector = (SwipeSelector) findViewById(R.id.swipeSelector);
        swipeSelector.setItems(
                new SwipeItem(0, "Welcome："+AVUser.getCurrentUser().get("username"), "现在正驾驶车辆：奥迪"),
                new SwipeItem(1, "Slide two", "Description for slide two."),
                new SwipeItem(2, "Slide three", "Description for slide three.")
        );
    }*/


    /**
     * 用于保存推送设备id
     */
    private void saveInsID(){
        //保存推送id
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e != null) {
                    ToastUtil.show(Main2Activity.this,"关联失败");
                }else{
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    AVObject avObject = AVObject.createWithoutData("_User",AVUser.getCurrentUser().getObjectId());
                    avObject.put("installationId",installationId);
                    avObject.saveInBackground();
                }
            }
        });
    }

    /*private void savePushMsg(String msg){
        AVObject todoFolder = new AVObject("Push");// 构建对象
        todoFolder.put("push_msg", msg);// 设置名称
        todoFolder.put("currUserID", AVUser.getCurrentUser().getObjectId());// 设置名称
        todoFolder.saveInBackground();// 保存到服务端
    }*/

    /**
     * 推送信息，每次启动时先判断是否需要推送信息
     */
    /*private void pushMsg(){
        AVQuery<AVObject> query = new AVQuery<>("Car");
        query.whereEqualTo("currUserID", AVUser.getCurrentUser().getObjectId());
        query.whereLessThan("Amount_of_gasoline",10);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                List<AVObject> need_gas_cars = list;
                if(need_gas_cars.size()>0){
                    String need_gas_msg = "";
                    for(int i=0; i<need_gas_cars.size();i++){
                        need_gas_msg+=need_gas_cars.get(i).get("CarName")+"剩余油量："
                                +need_gas_cars.get(i).get("Amount_of_gasoline")+"%\n";
                    }
                    // 设置默认打开的 Activity
                    PushService.setDefaultPushCallback(Main2Activity.this, NotificationActivity.class);
                    // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                    PushService.subscribe(Main2Activity.this, "public", NotificationActivity.class);
                    AVQuery pushQuery = AVInstallation.getQuery();
                    // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                    // 可以在应用启动的时候获取并保存到用户表
                    pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                    AVPush.sendMessageInBackground(need_gas_msg, pushQuery);
                    savePushMsg(need_gas_msg);

                    noticeNum++;
                }
            }
        });

        AVQuery<AVObject> query_mileage = new AVQuery<>("Car");
        query_mileage.whereEqualTo("currUserID", AVUser.getCurrentUser().getObjectId());
        query_mileage.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for(int i=0; i<list.size(); i++){
                    int mileage = (int) list.get(i).getNumber("mileage");
                    if(mileage >= 15000 && (mileage %15000 == 0)){
                        PushService.setDefaultPushCallback(Main2Activity.this, NotificationActivity.class);
                        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                        PushService.subscribe(Main2Activity.this, "public", NotificationActivity.class);
                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                        // 可以在应用启动的时候获取并保存到用户表
                        pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                        String msg = list.get(i).getString("CarName")+
                                "的行驶路程已达15000km，需要保养";
                        AVPush.sendMessageInBackground(msg, pushQuery);
                        savePushMsg(msg);

                        noticeNum++;
                    }

                    boolean trans = list.get(i).getBoolean("transmission");
                    boolean engine = list.get(i).getBoolean("Engine_situation");
                    boolean light = list.get(i).getBoolean("CarLight");

                    if(!trans){
                        PushService.setDefaultPushCallback(Main2Activity.this, NotificationActivity.class);
                        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                        PushService.subscribe(Main2Activity.this, "public", NotificationActivity.class);
                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                        // 可以在应用启动的时候获取并保存到用户表
                        pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                        String msg = list.get(i).getString("CarName")+
                                "的变速器需要维修";
                        AVPush.sendMessageInBackground(msg, pushQuery);
                        savePushMsg(msg);

                        noticeNum++;
                    }
                    else if(!engine){
                        PushService.setDefaultPushCallback(Main2Activity.this, NotificationActivity.class);
                        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                        PushService.subscribe(Main2Activity.this, "public", NotificationActivity.class);
                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                        // 可以在应用启动的时候获取并保存到用户表
                        pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                        String msg = list.get(i).getString("CarName")+
                                "的发动机需要维修";
                        AVPush.sendMessageInBackground(msg, pushQuery);
                        savePushMsg(msg);

                        noticeNum++;
                    }
                    else if(!light){
                        PushService.setDefaultPushCallback(Main2Activity.this, NotificationActivity.class);
                        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                        PushService.subscribe(Main2Activity.this, "public", NotificationActivity.class);
                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                        // 可以在应用启动的时候获取并保存到用户表
                        pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                        String msg = list.get(i).getString("CarName")+
                                "的车灯需要维修";
                        AVPush.sendMessageInBackground(msg, pushQuery);
                        savePushMsg(msg);

                        noticeNum++;
                    }
                }
            }
        });

    }*/


    /**
     * ToolBar 的导航栏
     */
    private void ToolBarOperation(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置导航栏图标

        //设置主标题
        toolbar.setTitle("Autoer");
        //设置主标题颜色
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        //修改主标题的外观，包括文字颜色，文字大小等
        toolbar.setTitleTextAppearance(this, R.style.Theme_ToolBar_Base_Title);
        //设置右上角的填充菜单
        toolbar.inflateMenu(R.menu.main2);

        //显示二维码扫描结果
        //scanQRCodeTextView = (TextView) findViewById(R.id.scanQRCodeTextView);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_search) {
                    //Toast.makeText(Main2Activity.this, R.string.menu_search, Toast.LENGTH_SHORT).show();
                    /*PackageManager packageManager = getPackageManager();
                    Intent intent= new Intent();
                    intent = packageManager.getLaunchIntentForPackage("com.juhe.petrolstation");
                    startActivity(intent);*/
                    startActivity(new Intent(Main2Activity.this, CompassActivity.class));
                } else if (menuItemId == R.id.action_notification) {
                    //Toast.makeText(Main2Activity.this, R.string.menu_notifications, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Main2Activity.this, NotificationActivity.class));
                } else if (menuItemId == R.id.action_item2) {
                    /**
                     * 跳转 Mylibrary 实现二维码扫描功能
                     */
                    Intent scanStart = new Intent(Main2Activity.this, CaptureActivity.class);
                    startActivityForResult(scanStart,0);
                }else if(menuItemId == R.id.action_item3){
                    SharedPreferences sp = getSharedPreferences("userInfo",0);
                    sp.edit().putBoolean("autologin", false).commit();
                    sp.edit().putBoolean("isFirst",true).commit();
                    startActivity(new Intent(Main2Activity.this, me.chenjiayang.myleancloud.MainActivity.class));
                    finish();
                }

                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.head_portrait_80);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        //toggle.setHomeAsUpIndicator(R.mipmap.head_portrait_64);
        drawer.setDrawerListener(toggle);
        toggle.syncState();





        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main2);

        head_Iv = (BootstrapCircleThumbnail) headerLayout.findViewById(R.id.HeadimageView);
        head_tv = (TextView) headerLayout.findViewById(R.id.Head_name);
        collector = (BootstrapButton) headerLayout.findViewById(R.id.collector);
        head_Iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this,EditInfoActivity.class));
            }
        });
        head_Iv.setImageDrawable(getResources().getDrawable(R.mipmap.head_portrait));
        head_tv.setText(AVUser.getCurrentUser().get("username").toString());
        collector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this,CollectActivity.class));
            }
        });

        /*View garage = navigationView.getMenu().findItem(0).getActionView();
        car_num_tv = (TextView) garage.findViewById(R.id.msg);
        car_num_tv.setText("2");*/


    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(count > 0 ? "+"+String.valueOf(count) : null);
    }


    /**
     * 用来实现将二维码信息返回到Main2Activity的函数，判断是否是车辆信息，并显示在dialog里面
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            ToastUtil.show(Main2Activity.this,"已取消");
        }else if(data != null) {
            String result = data.getExtras().getString("result");
            final String[] array = result.split("\\&");

            if (array[0].equals("iscar")) {

                String carmsg = "";
                for (int i = 1; i < array.length; i++) {
                    carmsg += userName[i - 1] + "：" + array[i] + "\n";
                }

                builder = new AlertDialog.Builder(Main2Activity.this);
                alert = builder.setTitle("绑定汽车信息").setMessage(carmsg).setPositiveButton("绑定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AVObject todoFolder = new AVObject("Car");// 构建对象
                        for (int i = 1; i < array.length; i++) {
                            if (i == 4 || i == 5) {
                                todoFolder.put(userName[i - 1], Integer.parseInt(array[i]));
                            } else if (i >= 6) {
                                if (array[i].equals("true")) {
                                    boolean tag = true;
                                    todoFolder.put(userName[i - 1], tag);
                                } else if (array[i].equals("false")) {
                                    boolean tag = false;
                                    todoFolder.put(userName[i - 1], tag);
                                }
                            } else {
                                todoFolder.put(userName[i - 1], array[i]);
                            }
                        }
                        todoFolder.put("currUserID", AVUser.getCurrentUser().getObjectId());
                        todoFolder.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    ToastUtil.show(Main2Activity.this, "绑定成功");
                                    startActivity(new Intent(Main2Activity.this, CarInfoActivity.class));
                                } else {
                                    ToastUtil.show(Main2Activity.this, e.getMessage());
                                }
                            }
                        });// 保存到服务端

                        dialog.dismiss();

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.show(Main2Activity.this, "取消");
                        dialog.dismiss();
                    }
                }).create();
                alert.show();
            }
            else if(array[0].equals("isorder")){
                AVQuery<AVObject> avQuery = new AVQuery<>("Order");
                avQuery.getInBackground(array[1], new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        String ordermsg = "订单号："+avObject.getObjectId().toString()+"\n油号："
                                +avObject.get("pGasType")+"\n数量："+avObject.get("pQuantity")+"升\n金额："
                                +avObject.get("pPrice")+"元\n";

                        builder = new AlertDialog.Builder(Main2Activity.this);
                        alert = builder.setTitle("订单信息").setMessage(ordermsg).setPositiveButton("付款", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ToastUtil.show(Main2Activity.this,"付款成功");
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                        alert.show();

                    }
                });
            }
            else {
                builder = new AlertDialog.Builder(Main2Activity.this);
                alert = builder.setTitle("二维码信息").setMessage(result).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                alert.show();
                //scanQRCodeTextView.setText(result+"&"+array[0]);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 左滑菜单的点击操作响应
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_order){
            Intent intent = new Intent(Main2Activity.this, CardLayoutActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_car){
            Intent intent = new Intent(Main2Activity.this, CarInfoActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_self_info){
            Intent intent = new Intent(Main2Activity.this, QueryActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_share){
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my Share text.");
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "分享到"));
        }
        else if(id == R.id.nav_feedback){
            startActivity(new Intent(Main2Activity.this,FeedBackActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //图片轮播
    private class TestLoopAdapter extends LoopPagerAdapter {
        private int[] imgs = {
                R.drawable.img4,
                R.drawable.img2,
                R.drawable.img3,
                R.drawable.img1,
        };

        public TestLoopAdapter(RollPagerView viewPager) {
            super(viewPager);
        }

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        @Override
        public int getRealCount() {
            return imgs.length;
        }
    }

}
