package me.chenjiayang.myleancloud;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ericssonlabs.BarCodeTestActivity;
import com.zxing.activity.CaptureActivity;

import me.chenjiayang.myleancloud.cardlayout.CardLayoutActivity;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView scanQRCodeTextView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
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

        scanQRCodeTextView = (TextView) findViewById(R.id.scanQRCodeTextView);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_search) {
                    Toast.makeText(Main2Activity.this, R.string.menu_search, Toast.LENGTH_SHORT).show();

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
                }else if(menuItemId == R.id.action_seif_info){
                    /**
                     * 个人信息界面
                     */
                    Intent intent = new Intent(Main2Activity.this, InfoActivity.class);
                    startActivity(intent);
                }
                /**
                 * 汽车信息界面
                 */
                else if(menuItemId == R.id.action_car_info){
                    Intent intent = new Intent(Main2Activity.this, CarInfoActivity.class);
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
     * 用来实现将二维码信息返回到Main2Activity的函数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = data.getExtras().getString("result");
        scanQRCodeTextView.setText(result);
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
        getMenuInflater().inflate(R.menu.main2, menu);
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
            Intent intent = new Intent(Main2Activity.this, InfoActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_manage){
            Intent settings = new Intent(Main2Activity.this, SettingsActivity.class);
            startActivity(settings);
        }
        else if(id == R.id.nav_share){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
