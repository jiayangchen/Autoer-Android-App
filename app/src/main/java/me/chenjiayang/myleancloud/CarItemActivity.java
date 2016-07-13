package me.chenjiayang.myleancloud;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import java.util.ArrayList;
import java.util.HashMap;

import me.chenjiayang.myleancloud.util.ToastUtil;

public class CarItemActivity extends AppCompatActivity {

    private ListView listView;
    private Button car_qr_btn;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private String carinfo = "iscar&";  //标记一个tag

    String[] from={"name","id"};              //这里是ListView显示内容每一列的列名
    int[] to={R.id.user_name,R.id.user_id};   //这里是ListView显示每一列对应的list_item中控件的id

    String[] userName={"CarName","License_plate_number","Engine_no","mileage","Amount_of_gasoline",
            "Engine_situation","CarLight","transmission"};  //这里第一列所要显示的人名


    private ArrayList<HashMap<String,String>> list=null;
    private HashMap<String,String> map=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_item);

        listView = (ListView) findViewById(R.id.car_info_elem);
        car_qr_btn = (Button) findViewById(R.id.car_qr_btn);


        list=new ArrayList<>();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("carlist_elem");

        for(int i=0; i<bundle.size(); i++){
            carinfo+=bundle.getString(userName[i])+(i == (bundle.size()-1) ? "":"&");
            map=new HashMap<>();
            map.put("name", userName[i]);
            map.put("id", bundle.getString(userName[i])+((i == 3 ? "km":"")+(i == 4 ? "%":"")));
            list.add(map);
        }

        //创建一个SimpleAdapter对象
        SimpleAdapter adapter=new SimpleAdapter(this,list,R.layout.list_car_info_elem,from,to);
        //调用ListActivity的setListAdapter方法，为ListView设置适配器
        listView.setAdapter(adapter);

        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        car_qr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap bitmap = EncodingHandler.createQRCode(carinfo, 900);
                    final ImageView imageView = new ImageView(CarItemActivity.this);
                    imageView.setImageBitmap(bitmap);
                    builder = new AlertDialog.Builder(CarItemActivity.this);
                    alert = builder.setView(imageView).create();
                    alert.show();
                }catch(WriterException e){
                    ToastUtil.show(CarItemActivity.this,"生成二维码失败");
                }
            }
        });
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
}
