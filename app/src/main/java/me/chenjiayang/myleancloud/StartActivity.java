package me.chenjiayang.myleancloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.chenjiayang.myleancloud.Main2Four.NotificationActivity;

public class StartActivity extends AppCompatActivity {

    private MediaPlayer mp = null;
    private VideoView myVideoView;
    private BootstrapButton start_login;
    private BootstrapButton start_register;
    private TextView Start_name;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window=StartActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        setContentView(R.layout.activity_start);

        initView();

        final String videoPath = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.aston_martin).toString();
        myVideoView.setVideoPath(videoPath);
        myVideoView.start();
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);

            }
        });

        myVideoView
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        myVideoView.setVideoPath(videoPath);
                        myVideoView.start();

                    }
                });

        sp = getSharedPreferences("userInfo",0);
        boolean isFirst = sp.getBoolean("isFirst",true);
        boolean choseAutologin = sp.getBoolean("autologin",false);
        if(!isFirst && choseAutologin){
            startActivity(new Intent(StartActivity.this,Main2Activity.class));
            finish();
        }else if(!isFirst && !choseAutologin){
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
    }



    private void initView() {

        myVideoView = (VideoView) findViewById(R.id.videoView);
        start_login = (BootstrapButton) findViewById(R.id.start_login);
        start_login.getBackground().setAlpha(200);
        start_register = (BootstrapButton) findViewById(R.id.start_register);
        start_register.getBackground().setAlpha(200);
        Start_name = (TextView) findViewById(R.id.start_name);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Tondu_Beta.otf");
        Start_name.setTypeface(typeface);

        start_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,MainActivity.class));
                finish();
            }
        });

        start_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,RegisterActivity.class));
                finish();
            }
        });

    }

}
