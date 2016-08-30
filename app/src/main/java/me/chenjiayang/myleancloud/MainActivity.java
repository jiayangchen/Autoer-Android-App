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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;

import cn.pedant.SweetAlert.SweetAlertDialog;
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

        login();//登录逻辑

    }

    private void init(){
        /*login_button = (Button) findViewById(R.id.login_button);
        register_button = (Button) findViewById(R.id.register_button);*/
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
