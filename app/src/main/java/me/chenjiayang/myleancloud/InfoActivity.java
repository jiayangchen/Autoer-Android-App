package me.chenjiayang.myleancloud;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.zxing.WriterException;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.zxing.encoding.EncodingHandler;

import me.chenjiayang.myleancloud.util.ToastUtil;

public class InfoActivity extends AppCompatActivity {

    //Self Info
    private EditText infoUsername;
    private EditText infoPhone;
    private EditText infoEmail;
    private EditText infoAddress;

    private Button saveInfoBtn;
    private Button changePwdBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true);

        init();

        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void init(){

        //编辑页面
        infoUsername = (EditText) findViewById(R.id.infoUsername);
        infoPhone = (EditText) findViewById(R.id.infoPhone);
        infoEmail = (EditText) findViewById(R.id.infoEmail);
        infoAddress = (EditText) findViewById(R.id.infoAddress);
        saveInfoBtn = (Button) findViewById(R.id.saveInfo);
        changePwdBtn = (Button) findViewById(R.id.changepwd);

        String username = (String) AVUser.getCurrentUser().get("username");
        String phone = (String) AVUser.getCurrentUser().get("phone");
        String email = (String) AVUser.getCurrentUser().get("email");
        String address = (String) AVUser.getCurrentUser().get("address");

        infoUsername.setText(username);
        infoPhone.setText(phone);
        infoEmail.setText(email);
        infoAddress.setText(address);

        saveInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AVUser.getCurrentUser().put("phone", infoPhone.getText().toString());
                AVUser.getCurrentUser().put("email", infoEmail.getText().toString());
                AVUser.getCurrentUser().put("address", infoAddress.getText().toString());
                AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e == null){
                            ToastUtil.show(InfoActivity.this,"更新成功");
                        }else{
                            ToastUtil.show(InfoActivity.this,e.getMessage());
                        }
                    }
                });
            }
        });

        changePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置密码
                if(AVUser.getCurrentUser().getString("email") == null){
                    ToastUtil.show(InfoActivity.this,"尚未填写邮箱");
                }
                else{
                    dialog();
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
                }
            }
        });
    }

    //重置密码
    private void dialog(){
        /*LayoutInflater layoutInflater = LayoutInflater.from(InfoActivity.this);
        final View view = layoutInflater.inflate(R.layout.fragment_info_item1,null);*/
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("修改密码"); //设置标题
        builder.setMessage("重置密码的邮件已发送到您的邮箱");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.create().show();
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
