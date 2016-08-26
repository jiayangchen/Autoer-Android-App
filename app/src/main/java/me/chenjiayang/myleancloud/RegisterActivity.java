package me.chenjiayang.myleancloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;

public class RegisterActivity extends AppCompatActivity {

    private EditText re_usernameEditText;
    private EditText re_passwordEditText;
    private EditText repeat_passwordEditText;
    //private Button registerAction_butoon;
    private BootstrapButton registerAction_butoon;
    private BootstrapButton backlogin_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //注册控件
        re_usernameEditText = (EditText) findViewById(R.id.re_usernameEditText);
        re_passwordEditText = (EditText) findViewById(R.id.re_passwordEditText);
        repeat_passwordEditText = (EditText) findViewById(R.id.re_ensure_passwordEditText);
        registerAction_butoon = (BootstrapButton) findViewById(R.id.registerAction_button);
        backlogin_btn = (BootstrapButton) findViewById(R.id.back_login_button);

        backlogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                finish();
            }
        });
        registerAction_butoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 拿到注册输入值
                 * rename 用户名
                 * repass 密码
                 * repeatpass 确认密码
                 */
                String rename = re_usernameEditText.getText().toString();
                String repass = re_passwordEditText.getText().toString();
                String repeatpass = repeat_passwordEditText.getText().toString();

                /**
                 * 对注册信息
                 * 合法性进行判断
                 */
                if (rename.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "用户名不可为空", Toast.LENGTH_SHORT).show();
                } else if (repass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "密码不可为空", Toast.LENGTH_SHORT).show();
                } else if (!repass.equals(repeatpass)) {
                    Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                } else {

                    /**
                     * 调用LeanCloudAPI进行注册
                     */
                    AVUser user = new AVUser();
                    user.setUsername(rename);
                    user.setPassword(repass);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {

                                /**
                                 * 返回主页面
                                 */
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                            }
                        }
                    });
                }
            }
        });
    }
}
