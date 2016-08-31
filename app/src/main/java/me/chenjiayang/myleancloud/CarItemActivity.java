package me.chenjiayang.myleancloud;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.google.zxing.WriterException;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.zxing.encoding.EncodingHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class CarItemActivity extends AppCompatActivity {

    private ListView listView;
    private Button car_qr_btn;
    private Button car_delete_bound_btn;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private String carinfo = "iscar&";  //标记一个tag，用于二维码扫描时判断这是一辆汽车的信息
    private Bundle bundle;
    private Bundle bundle_id;
    private Intent intent;
    private TextView head_tv;
    private BootstrapCircleThumbnail head_iv;

    //下拉刷新控件
    private SwipeRefreshLayout swipeRefreshLayout;

    String[] from={"name","id"};              //这里是ListView显示内容每一列的列名
    int[] to={R.id.user_name,R.id.user_id};   //这里是ListView显示每一列对应的list_item中控件的id

    String[] userName={"CarName","License_plate_number","Engine_no","mileage","Amount_of_gasoline",
            "Engine_situation","CarLight","transmission"};  //这里第一列所要显示的人名


    private ArrayList<HashMap<String,String>> list=null;
    private HashMap<String,String> map=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_item);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true);

        init();

        //下拉刷新函数
        refresh();

    }

    private void init(){

        listView = (ListView) findViewById(R.id.car_info_elem);
        car_qr_btn = (Button) findViewById(R.id.car_qr_btn); //生成二维码的按钮
        car_delete_bound_btn = (Button) findViewById(R.id.car_delete_bound);

        /*LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.caritem_header, null);*/

        head_tv = (TextView) findViewById(R.id.car_item_name);
        head_iv = (BootstrapCircleThumbnail) findViewById(R.id.car_item_head);
        head_iv.setImageDrawable(getResources().getDrawable(R.mipmap.head_portrait));
        head_tv.setText(AVUser.getCurrentUser().get("username").toString());

        list=new ArrayList<>();

        intent = getIntent();
        bundle = intent.getBundleExtra("carlist_elem");  //获取汽车列表页面传来的bundle数据
        bundle_id = intent.getBundleExtra("car_ObjectId");


        AVQuery<AVObject> avQuery = new AVQuery<>("Car");
        avQuery.getInBackground(bundle_id.getString("ObjectId"), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                for(int i=0; i<userName.length; i++){
                    carinfo+=avObject.get(userName[i]).toString()+(i == (userName.length-1) ? "":"&");
                    map=new HashMap<>();
                    map.put("name", userName[i]);
                    map.put("id", avObject.get(userName[i]).toString()+((i == 3 ? "km":"")+(i == 4 ? "%":"")));
                    list.add(map);
                }
                setAdapter();
            }
        });
    }



    private void setAdapter(){
        //创建一个SimpleAdapter对象
        SimpleAdapter adapter=new SimpleAdapter(this,list,R.layout.list_car_info_elem,from,to);
        //调用ListActivity的setListAdapter方法，为ListView设置适配器
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListItemClickListener());

        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        //生成二维码的响应事件
        car_qr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Bitmap bitmap = EncodingHandler.createQRCode(carinfo, 900);
                    final ImageView imageView = new ImageView(CarItemActivity.this);
                    imageView.setImageBitmap(bitmap);
                    builder = new AlertDialog.Builder(CarItemActivity.this);
                    alert = builder.setView(imageView)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    alert.show();
                }catch(WriterException e){
                    ToastUtil.show(CarItemActivity.this,"生成二维码失败");
                }
            }
        });

        car_delete_bound_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //创建sweet dialog
                new SweetAlertDialog(CarItemActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this car!")
                        .setConfirmText("Yes,delete it!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog
                                        .setTitleText("Deleted!")
                                        .setContentText("Your imaginary car has been deleted!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                AVQuery<AVObject> avQuery = new AVQuery<>("Car");
                                avQuery.getInBackground(bundle_id.getString("ObjectId"), new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
                                        avObject.deleteInBackground();
                                        ToastUtil.show(CarItemActivity.this,"解除绑定成功");
                                    }
                                });

                            }
                        })
                        .show();
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
                ToastUtil.show(CarItemActivity.this,"下拉刷新");
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击对应订单调转到详情
     */
    private final class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            //ToastUtil.show(CarItemActivity.this, "Clicked on List Item " + position);

            final int x = position;
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.car_item_edit, null);
            TextView tv = (TextView) layout.findViewById(R.id.car_item_editdialog_textview);
            final EditText et = (EditText) layout.findViewById(R.id.car_item_editdialog_edittext);

            String hintText =  bundle.getString(userName[position]);
            tv.setText(hintText);

            builder = new AlertDialog.Builder(CarItemActivity.this);
            alert = builder.setView(layout)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Object edittext;
                            String key = userName[x];
                            String ObjectId = bundle_id.getString("ObjectId");
                            if(position == 3 || position == 4) {
                                edittext = Integer.parseInt(et.getText().toString());
                            }
                            else{
                                edittext = et.getText().toString();
                            }

                            AVObject caritem_edit = AVObject.createWithoutData("Car",ObjectId);
                            caritem_edit.put(key,edittext);
                            caritem_edit.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e == null){
                                        ToastUtil.show(CarItemActivity.this,"修改成功");
                                    }else{
                                        ToastUtil.show(CarItemActivity.this,e.getMessage());
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alert.show();
        }
    }


    private void refresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_car_item_edit);
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
                list.clear();
                init();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(CarItemActivity.this,"同步成功");
                        // TODO Auto-generated method stub
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);  //时间2s
            }
        });
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
