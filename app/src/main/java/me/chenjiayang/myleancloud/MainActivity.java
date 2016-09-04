package me.chenjiayang.myleancloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.PushService;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.chenjiayang.myleancloud.Main2Four.NotificationActivity;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox remember;
    private CheckBox autologin;
    /*private Button login_button;
    private Button register_button;*/
    private TextView forgetPwdBtn;
    private BootstrapButton login_button;
    private BootstrapButton register_button;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();//初始化控件

        pushMsg();

        login();//登录逻辑

    }

    private void savePushMsg(String msg){
        AVObject todoFolder = new AVObject("Push");// 构建对象
        todoFolder.put("push_msg", msg);// 设置名称
        todoFolder.put("currUserID", AVUser.getCurrentUser().getObjectId());// 设置名称
        todoFolder.saveInBackground();// 保存到服务端
    }

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
                    PushService.setDefaultPushCallback(MainActivity.this, NotificationActivity.class);
                    // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                    PushService.subscribe(MainActivity.this, "public", NotificationActivity.class);
                    AVQuery pushQuery = AVInstallation.getQuery();
                    // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                    // 可以在应用启动的时候获取并保存到用户表
                    pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                    AVPush.sendMessageInBackground(need_gas_msg, pushQuery);
                    savePushMsg(need_gas_msg);
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
                        PushService.setDefaultPushCallback(MainActivity.this, NotificationActivity.class);
                        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                        PushService.subscribe(MainActivity.this, "public", NotificationActivity.class);
                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                        // 可以在应用启动的时候获取并保存到用户表
                        pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                        String msg = list.get(i).getString("CarName")+
                                "的行驶路程已达15000km，需要保养";
                        AVPush.sendMessageInBackground(msg, pushQuery);
                        savePushMsg(msg);
                    }

                    boolean trans = list.get(i).getBoolean("transmission");
                    boolean engine = list.get(i).getBoolean("Engine_situation");
                    boolean light = list.get(i).getBoolean("CarLight");

                    if(!trans){
                        PushService.setDefaultPushCallback(MainActivity.this, NotificationActivity.class);
                        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                        PushService.subscribe(MainActivity.this, "public", NotificationActivity.class);
                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                        // 可以在应用启动的时候获取并保存到用户表
                        pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                        String msg = list.get(i).getString("CarName")+
                                "的变速器需要维修";
                        AVPush.sendMessageInBackground(msg, pushQuery);
                        savePushMsg(msg);
                    }
                    else if(!engine){
                        PushService.setDefaultPushCallback(MainActivity.this, NotificationActivity.class);
                        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                        PushService.subscribe(MainActivity.this, "public", NotificationActivity.class);
                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                        // 可以在应用启动的时候获取并保存到用户表
                        pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                        String msg = list.get(i).getString("CarName")+
                                "的发动机需要维修";
                        AVPush.sendMessageInBackground(msg, pushQuery);
                        savePushMsg(msg);
                    }
                    else if(!light){
                        PushService.setDefaultPushCallback(MainActivity.this, NotificationActivity.class);
                        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                        PushService.subscribe(MainActivity.this, "public", NotificationActivity.class);
                        AVQuery pushQuery = AVInstallation.getQuery();
                        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                        // 可以在应用启动的时候获取并保存到用户表
                        pushQuery.whereEqualTo("installationId", AVUser.getCurrentUser().get("installationId"));
                        String msg = list.get(i).getString("CarName")+
                                "的车灯需要维修";
                        AVPush.sendMessageInBackground(msg, pushQuery);
                        savePushMsg(msg);
                    }
                }
            }
        });

    }

    private void init(){
        login_button = (BootstrapButton) findViewById(R.id.login_button);
        register_button = (BootstrapButton) findViewById(R.id.register_button);
        remember = (CheckBox) findViewById(R.id.rememberCheckBox);
        autologin = (CheckBox) findViewById(R.id.autologinCheckBox);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        forgetPwdBtn = (TextView) findViewById(R.id.forgetPwd);

        forgetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(MainActivity.this)
                        .setTitleText("Forget Password")

                        .setContentText("The confirmation emai " +
                                "has been sent to your mailbox, please check it as soon as possible!")
                        .show();
            }
        });
    }

    private void login(){

        sp = getSharedPreferences("userInfo",0);
        String fill_name = sp.getString("USER_NAME","");
        String fill_pass = sp.getString("PASSWORD","");

        boolean choseRemember = sp.getBoolean("remember",false);
        boolean choseAutologin = sp.getBoolean("autologin",false);

        if(choseRemember){
            usernameEditText.setText(fill_name);
            passwordEditText.setText(fill_pass);
            remember.setChecked(true);
        }
        if(choseAutologin){
            autologin.setChecked(true);

            //自动登录
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                final SharedPreferences.Editor editor = sp.edit();

                if(username.isEmpty()){
                    Toast.makeText(MainActivity.this,"用户名不可为空",Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(MainActivity.this,"密码不可为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            if(e!=null){
                                Toast.makeText(MainActivity.this,e.getMessage() ,Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(avUser != null){

                                    editor.putString("USER_NAME",username);
                                    editor.putString("PASSWORD",password);

                                    if(remember.isChecked()){
                                        editor.putBoolean("remember",true);
                                    }
                                    else{
                                        editor.putBoolean("remember",false);
                                    }

                                    if(autologin.isChecked()){
                                        editor.putBoolean("autologin", true);
                                    }else{
                                        editor.putBoolean("autologin", false);
                                    }

                                    editor.putBoolean("isFirst",false);

                                    editor.commit();

                                    Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"用户名密码错误" ,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
