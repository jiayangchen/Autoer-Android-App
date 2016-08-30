package me.chenjiayang.myleancloud;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.jude.swipbackhelper.SwipeBackHelper;

import me.chenjiayang.myleancloud.util.ToastUtil;

public class FeedBackActivity extends AppCompatActivity {

    private EditText Feed_content;
    private EditText Feed_phone;
    private EditText Feed_email;
    private BootstrapButton Feed_commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this).setSwipeRelateEnable(true);

        Feed_content = (EditText) findViewById(R.id.feed_content);
        Feed_phone = (EditText) findViewById(R.id.feedback_phone);
        Feed_email = (EditText) findViewById(R.id.feedback_email);
        Feed_commit = (BootstrapButton) findViewById(R.id.feedback_commit);

        Feed_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = Feed_content.getEditableText().toString();
                if(content.isEmpty()){
                    ToastUtil.show(FeedBackActivity.this,"反馈内容不可为空");
                }else{
                    AVObject ao = new AVObject("FeedBack");
                    ao.put("UserID", AVUser.getCurrentUser().getObjectId());
                    ao.put("Feed_content",content);
                    ao.put("Phone",Feed_phone.getEditableText().toString());
                    ao.put("Email",Feed_email.getEditableText().toString());
                    ao.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e == null){
                                ToastUtil.show(FeedBackActivity.this,"反馈成功");
                            }else{
                                ToastUtil.show(FeedBackActivity.this,e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
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
