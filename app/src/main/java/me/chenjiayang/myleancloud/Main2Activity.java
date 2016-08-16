package me.chenjiayang.myleancloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.ImageButton;
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
import com.avos.avoscloud.SendCallback;
import com.ericssonlabs.BarCodeTestActivity;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.zxing.activity.CaptureActivity;
import com.zxing.encoding.EncodingHandler;

import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import me.chenjiayang.myleancloud.cardlayout.CardLayoutActivity;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String[] userName={"CarName","License_plate_number","Engine_no","mileage","Amount_of_gasoline",
            "Engine_situation","CarLight","transmission"};
    //private TextView scanQRCodeTextView;
    private RollPagerView mRollViewPager;
    private long exitTime = 0;

    private TextView now_drive_car;
    private TextView now_gas_num;
    private TextView now_mile_num;
    private TextView now_engine_situation;
    private TextView now_trans_situation;
    private CardView cardView;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private String[] items = null;
    private int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        now_drive_car = (TextView) findViewById(R.id.now_drive_car);
        now_gas_num = (TextView) findViewById(R.id.now_gas_num);
        now_mile_num = (TextView) findViewById(R.id.now_mile_num);
        now_engine_situation = (TextView) findViewById(R.id.now_engine_situation);
        now_trans_situation = (TextView) findViewById(R.id.now_trans_situation);
        cardView = (CardView) findViewById(R.id.main2_now_driving);

        setNowDriving();

        cardView.setOnClickListener(new View.OnClickListener() {
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
                            int i=0;
                            for(AVObject ao : list){
                                items[i] = ao.get("CarName").toString();
                                i++;
                            }
                            dialog_change_car();
                        }

                    }
                });
            }
        });



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
                ToastUtil.show(Main2Activity.this,"click on"+position);
            }
        });

        swipselector();

        //保存推送id
        saveInsID();

        //判断是否推送加油信息
        pushMsg();

        //Toolbar的操作
        ToolBarOperation();

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
                            ToastUtil.show(Main2Activity.this, items[x]);
                            x = 0;
                        }catch (Exception e){
                            ToastUtil.show(Main2Activity.this, items[0]);
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
        AVQuery<AVObject> avQuery = new AVQuery<>("Car");
        avQuery.getInBackground(AVUser.getCurrentUser().get("NowDriving").toString(), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // object 就是 id 为 558e20cbe4b060308e3eb36c 的 Todo 对象实例
                now_drive_car.setText(avObject.get("CarName").toString());
                now_gas_num.setText(avObject.get("Amount_of_gasoline").toString()+"%");
                now_mile_num.setText(avObject.get("mileage").toString()+"km");
                now_engine_situation.setText((avObject.get("Engine_situation").toString()) == "true" ? "OK" : "Bad");
                now_trans_situation.setText((avObject.get("transmission").toString()) == "true" ? "OK" : "Bad");
            }
        });
    }

    private void swipselector(){
        SwipeSelector swipeSelector = (SwipeSelector) findViewById(R.id.swipeSelector);
        swipeSelector.setItems(
                new SwipeItem(0, "Welcome："+AVUser.getCurrentUser().get("username"), "现在正驾驶车辆：奥迪"),
                new SwipeItem(1, "Slide two", "Description for slide two."),
                new SwipeItem(2, "Slide three", "Description for slide three.")
        );
    }


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

    /**
     * 推送信息，每次启动时先判断是否需要推送信息
     */
    private void pushMsg(){
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
                    PushService.setDefaultPushCallback(Main2Activity.this, Main2Activity.class);
                    // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                    PushService.subscribe(Main2Activity.this, "public", Main2Activity.class);
                    AVQuery pushQuery = AVInstallation.getQuery();
                    // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                    // 可以在应用启动的时候获取并保存到用户表
                    pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                    AVPush.sendMessageInBackground(need_gas_msg,  pushQuery);
                }
            }
        });
    }


    /**
     * ToolBar 的导航栏
     */
    private void ToolBarOperation(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置导航栏图标
        toolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
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
                    PackageManager packageManager = getPackageManager();
                    Intent intent= new Intent();
                    intent = packageManager.getLaunchIntentForPackage("com.juhe.petrolstation");
                    startActivity(intent);

                } else if (menuItemId == R.id.action_notification) {
                    Toast.makeText(Main2Activity.this, R.string.menu_notifications, Toast.LENGTH_SHORT).show();

                } else if (menuItemId == R.id.action_item1) {
                    /**
                     * 跳转设置页面
                     */
                    Intent settings = new Intent(Main2Activity.this, SettingsActivity.class);
                    startActivity(settings);
                } else if (menuItemId == R.id.action_item2) {
                    /**
                     * 跳转 Mylibrary 实现二维码扫描功能
                     */
                    Intent scanStart = new Intent(Main2Activity.this, CaptureActivity.class);
                    startActivityForResult(scanStart,0);
                }

                /**
                 * 退出登录，返回登录页面
                 */
                else if(menuItemId == R.id.action_quit){
                    Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 点击进入添加预约界面
                 */
                Intent intent = new Intent(Main2Activity.this, PoiAroundSearchActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            Intent intent = new Intent(Main2Activity.this, EditInfoActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_manage){
            PackageManager packageManager = getPackageManager();
            Intent intent= new Intent();
            intent = packageManager.getLaunchIntentForPackage("me.chenjiayang.myapplication");
            startActivity(intent);
        }
        else if(id == R.id.nav_share){
            ToastUtil.show(Main2Activity.this,"Share");
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

    //按两下退出应用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
