package me.chenjiayang.myleancloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
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
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.jude.library.imageprovider.ImageProvider;
import com.jude.swipbackhelper.SwipeBackHelper;
import java.util.ArrayList;
import java.util.HashMap;

import me.chenjiayang.myleancloud.util.ToastUtil;

public class EditInfoActivity extends AppCompatActivity{

    private ListView listView;
    private BootstrapButton change_pwd_btn;
    private BootstrapButton save_info_btn;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private BootstrapCircleThumbnail head_protrait;

    //下拉刷新控件
    private SwipeRefreshLayout swipeRefreshLayout;

    String[] from={"name","id"};              //这里是ListView显示内容每一列的列名
    int[] to={R.id.self_user_name,R.id.self_user_id};   //这里是ListView显示每一列对应的list_item中控件的id

    String[] userName={"username","phone","email","address"};  //这里第一列所要显示的人名

    private ArrayList<HashMap<String,String>> list=null;
    private HashMap<String,String> map=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true);

        init();

        //下拉刷新函数
        refresh();

    }

    private void init(){
        listView = (ListView) findViewById(R.id.self_info_elem);
        change_pwd_btn = (BootstrapButton) findViewById(R.id.self_info_pwd_btn); //生成二维码的按钮
        save_info_btn = (BootstrapButton) findViewById(R.id.save_self_info_btn);
        head_protrait = (BootstrapCircleThumbnail) findViewById(R.id.edit_info_head);
        head_protrait.setImageDrawable(getResources().getDrawable(R.mipmap.head_portrait));

        list=new ArrayList<>();

        for(int i=0; i<userName.length; i++){
            map=new HashMap<>();
            map.put("name", userName[i]);
            map.put("id", AVUser.getCurrentUser().get(userName[i]).toString());
            list.add(map);
        }
        setAdapter();
    }

    private void setAdapter(){
        //创建一个SimpleAdapter对象
        SimpleAdapter adapter=new SimpleAdapter(this,list,R.layout.list_self_info_elem,from,to);
        //调用ListActivity的setListAdapter方法，为ListView设置适配器
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListItemClickListener());

        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        change_pwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置密码
                /*if(AVUser.getCurrentUser().getString("email") == null){
                    ToastUtil.show(EditInfoActivity.this,"尚未填写邮箱");
                }
                else{
                    builder = new AlertDialog.Builder(EditInfoActivity.this);
                    alert = builder.setTitle("Change Password").setMessage("The confirmation email" +
                            "has been sent to your mailbox, please check it as soon as possible!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AVUser.requestPasswordResetInBackground(AVUser.getCurrentUser().getString("email"),
                                    new RequestPasswordResetCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {

                                            } else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            dialog.dismiss();
                        }
                    }).create();
                    alert.show();
                }*/

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.self_item_password_edit, null);
                final EditText etold = (EditText) layout.findViewById(R.id.self_item_password_edittext);
                final EditText etnew = (EditText) layout.findViewById(R.id.self_item_new_password_edittext);
                final EditText etensure = (EditText) layout.findViewById(R.id.self_item_ensure_password_edittext);
                builder = new AlertDialog.Builder(EditInfoActivity.this);
                alert = builder.setView(layout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etold.getText().toString().isEmpty() ||
                                etnew.getText().toString().isEmpty() ||
                                etensure.getText().toString().isEmpty()) {
                            ToastUtil.show(EditInfoActivity.this, "输入不可为空");
                        } else {
                            if (etnew.getText().toString().equals(etensure.getText().toString())) {

                                AVUser.getCurrentUser().updatePasswordInBackground(etold.getText().toString(), etnew.getText().toString(), new UpdatePasswordCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            ToastUtil.show(EditInfoActivity.this, "密码更改成功");
                                        }else{
                                            ToastUtil.show(EditInfoActivity.this,"原密码错误");
                                        }
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                alert.show();
            }
        });

        save_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("userInfo",0);
                sp.edit().putBoolean("autologin", false).commit();
                sp.edit().putBoolean("isFirst",true).commit();
                startActivity(new Intent(EditInfoActivity.this,MainActivity.class));
                finish();
                Main2Activity.tag_main2.finish();
            }
        });

        head_protrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(EditInfoActivity.this,"head portrait");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.editinfo_setting:
                startActivity(new Intent(EditInfoActivity.this,SettingsActivity.class));
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
            //ToastUtil.show(EditInfoActivity.this, "Clicked on List Item " + position);
            if(position == 0){
                ToastUtil.show(EditInfoActivity.this,"You cannot modify the username");
            }
            else {
                final int x = position;
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.self_elem_edit, null);
                final EditText et = (EditText) layout.findViewById(R.id.self_item_editdialog_edittext);

                builder = new AlertDialog.Builder(EditInfoActivity.this);
                alert = builder.setView(layout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Object edittext;
                                String key = userName[x];
                                edittext = et.getText().toString();

                                AVUser currentUser = AVUser.getCurrentUser();
                                currentUser.put(key, edittext);
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            ToastUtil.show(EditInfoActivity.this, "修改成功");
                                        } else {
                                            ToastUtil.show(EditInfoActivity.this, e.getMessage());
                                        }
                                    }
                                });
                                ToastUtil.show(EditInfoActivity.this, "Please pull down to refresh");
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
    }


    private void refresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_self_item_edit);
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
                        ToastUtil.show(EditInfoActivity.this,"同步成功");
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
