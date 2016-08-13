package me.chenjiayang.myleancloud.cardlayout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jude.swipbackhelper.SwipeBackHelper;
import me.chenjiayang.myleancloud.R;

public class CardLayoutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_layout);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new CardLayoutFragment()).commit();
        }

        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_layout, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_message).setTitle(R.string.app_name);
            builder.setPositiveButton(R.string.dialog_ok, null);
            builder.setIcon(R.drawable.ic_launcher);
            
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        else */if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 侧滑的生命周期函数
     * @param savedInstanceState
     */
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
