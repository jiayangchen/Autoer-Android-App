package me.chenjiayang.myleancloud;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.chenjiayang.myleancloud.util.ToastUtil;

public class CarInfoActivity extends AppCompatActivity {

    private ListView mListView;
    private SimpleAdapter simpleAdapter;
    private Button LookCarInfoBtn;
    private Button GetQRCodeBtn;
    private List<AVObject> carlist = null;
    private ArrayList<HashMap<String, Object>> item = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carinfo);

        AVQuery<AVObject> query = new AVQuery<>("Car");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                carlist = list;
                for (int i = 0; i <carlist.size(); i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    String carname = carlist.get(i).get("CarName").toString();
                    String carsummary = "车牌号："+carlist.get(i).get("License_plate_number").toString()+"\n" +"发动机号："+ carlist.get(i).get("Engine_no").toString()+"\n" +
                            "里程数："+ carlist.get(i).get("mileage").toString() +"\n"+"汽油量："+
                            carlist.get(i).get("Amount_of_gasoline").toString() +"%\n"+"变速器情况："+ carlist.get(i).get("transmission").toString();

                    map.put("itemTitle", carname);
                    map.put("itemPhoto", R.drawable.audilogo);
                    map.put("itemSummary", carsummary);
                    item.add(map);
                }
                setAdapter();
            }
        });


        /**
         * 显示返回箭头
         * 隐藏Logo图标，id默认为R.id.home
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void setAdapter(){
        mListView = (ListView)this.findViewById(R.id.listview);

        //创建简单适配器SimpleAdapter
        simpleAdapter = new SimpleAdapter(this,item, R.layout.activity_car_info,
                new String[] {"itemTitle","itemPhoto", "itemSummary"},
                new int[] {R.id.title, R.id.photograph, R.id.summary});

        //加载SimpleAdapter到ListView中
        mListView.setAdapter(simpleAdapter);
    }

    /*
     * Function     :    获取所有的列表内容
     * Author       :    博客园-依旧淡然
     */
    public ArrayList<HashMap<String, Object>> getItem() {
        return item;
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
            case R.id.look_car_info_btn:
                ToastUtil.show(CarInfoActivity.this,"详情");
            case R.id.get_qrcode_btn:
                ToastUtil.show(CarInfoActivity.this,"二维码");
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
