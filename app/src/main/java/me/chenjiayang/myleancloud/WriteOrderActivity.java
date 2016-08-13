package me.chenjiayang.myleancloud;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import me.chenjiayang.myleancloud.cardlayout.CardLayoutActivity;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class WriteOrderActivity extends AppCompatActivity
        implements NumberPicker.OnValueChangeListener,NumberPicker.OnScrollListener,NumberPicker.Formatter {

    private String gasType;
    private String pTime;
    private String pName;
    private String pAddr;
    private int quantity;
    private EditText pNameEditText;
    private EditText pAddrEditText;
    private EditText pTimeEditText;
    private NumberPicker numberPicker;
    private RadioGroup radioGroup;
    private Button confirmBtn;
    private ImageView qrImgImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_order);

        numberPicker=(NumberPicker) findViewById(R.id.minuteicker);
        init();
        pNameEditText = (EditText) findViewById(R.id.pstationName);
        pAddrEditText = (EditText) findViewById(R.id.pAddress);
        pTimeEditText = (EditText) findViewById(R.id.pTime);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        confirmBtn = (Button) findViewById(R.id.confirm_order);
        qrImgImageView = (ImageView) this.findViewById(com.ericssonlabs.R.id.iv_qr_image);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
         pName = bundle.getString("pName");
         pAddr = bundle.getString("pAddr");
         pTime = bundle.getString("pTime");

        pNameEditText.setText(pName);
        pAddrEditText.setText(pAddr);
        pTimeEditText.setText(pTime);

        radioGroup.setOnCheckedChangeListener(new RadioChange());
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1();
            }
        });
        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void init() {
        numberPicker.setFormatter(this);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setOnScrollListener(this);
        numberPicker.setMaxValue(30);
        numberPicker.setMinValue(0);
        numberPicker.setValue(0);
    }

    //数字选择器，为0-9的数字添零格式化
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        quantity = newVal;
    }

    public void onScrollStateChange(NumberPicker view, int scrollState) {
    }

    class RadioChange implements RadioGroup.OnCheckedChangeListener {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radio=(RadioButton) group.findViewById(checkedId);
            gasType = radio.getText().toString();
            ToastUtil.show(WriteOrderActivity.this,gasType);
        }
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
            case R.id.actiob_order_list:
                startActivity(new Intent(WriteOrderActivity.this, CardLayoutActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dialog1(){
        final String items[]={"加油站名称："+pName,"加油站地址："+pAddr,"预约日期："+ pTime
                ,"汽油类型为："+gasType,"数量："+quantity+"升","价格："+5.58*quantity+"元"};

        final String orderinfo = "加油站名称："+pName+"加油站地址："+pAddr+"预约日期："+ pTime
                +"汽油类型为："+gasType+"数量："+quantity+"升"+"价格："+5.58*quantity+"元";

        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("Order Details"); //设置标题
        //builder.setMessage("是否确认退出?"); //设置内容
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(WriteOrderActivity.this, items[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {

                AVObject order = new AVObject("Order");
                order.put("pName",pName);
                order.put("pAddr",pAddr);
                order.put("pTime",pTime);
                order.put("pGasType",gasType);
                order.put("pQuantity",quantity);
                order.put("pPrice",5.58*quantity);
                order.put("currUserID", AVUser.getCurrentUser().getObjectId());
                order.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e == null){
                            Toast.makeText(WriteOrderActivity.this, "Commit Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(WriteOrderActivity.this,CardLayoutActivity.class));
                            finish();
                        }else {
                            ToastUtil.show(WriteOrderActivity.this,e.getMessage());
                        }
                    }
                });
                dialog.dismiss(); //关闭dialog
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        return true;
    }
}
