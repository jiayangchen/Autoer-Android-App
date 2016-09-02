package me.chenjiayang.myleancloud.Main2Four;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class MaintenItemActivity extends AppCompatActivity {

    private WebView webView;
    private Intent intent;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window=MaintenItemActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        setContentView(R.layout.activity_mainten_item);

        intent = getIntent();
        bundle = intent.getBundleExtra("mainten");

        init();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void init(){
        webView = (WebView) findViewById(R.id.mainten_item_webView);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
        webView.loadUrl(bundle.get("url").toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainten_collector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.maintenitem_refresh:
                webView.loadUrl(bundle.get("url").toString());
                break;
            case R.id.maintenitem_collector:
                AVQuery<AVObject> avQuery = new AVQuery<>("Maintenance");
                avQuery.getInBackground(bundle.get("maintenanceID").toString(), new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        // object 就是 id 为 558e20cbe4b060308e3eb36c 的 Todo 对象实例
                        if(avObject.getBoolean("isCollect")){
                            avObject.put("isCollect", false);
                            avObject.saveInBackground();
                            ToastUtil.show(MaintenItemActivity.this,"取消收藏");
                        }
                        else {
                            avObject.put("isCollect", true);
                            avObject.saveInBackground();
                            ToastUtil.show(MaintenItemActivity.this,"收藏成功");
                        }
                    }
                });
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
