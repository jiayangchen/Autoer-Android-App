package me.chenjiayang.myleancloud;

import android.app.Application;
import android.view.KeyEvent;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;

public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"9k55DgHBy3vlUcADfgsuOFGb-gzGzoHsz","AkNIdMmXRftH2jmCkouYlaiW");
    }
}
