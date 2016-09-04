package me.chenjiayang.myleancloud.Gas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.WriteOrderActivity;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class GasItemActivity extends AppCompatActivity {

    private TextView GasName;
    private TextView GasAddress;
    private TextView GasPrice;
    private TextView GasProvince;
    private BootstrapButton gas_order;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_item);

        init();

        set();

        GasCommitBtn();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void init(){
        GasName = (TextView) findViewById(R.id.gas_item_name);
        GasAddress = (TextView) findViewById(R.id.gas_item_address);
        GasPrice = (TextView) findViewById(R.id.gas_item_price);
        GasProvince = (TextView) findViewById(R.id.gas_item_province);
        gas_order = (BootstrapButton) findViewById(R.id.gas_item_commit);
    }

    private void set(){

        JuheSDKInitializer.initialize(getApplicationContext());
        Parameters params = new Parameters();
        params.add("dtype","json");
        JuheData.executeWithAPI(getApplicationContext(), 48, "http://apis.juhe.cn/cnoil/oil_city",
                JuheData.GET, params, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            JSONArray jsonArray = object.getJSONArray("result");
                            JSONObject temp = (JSONObject) jsonArray.get(2);
                            String province = "90#："+temp.getString("b90")+"元"+"   "+"93#："+temp.getString("b93")+"\n"
                                    +"97#："+temp.getString("b97")+"元"+"   "+"0#："+temp.getString("b0")+"元";

                            Intent intent = getIntent();
                            bundle = intent.getBundleExtra("gasitem");
                            GasName.setText(bundle.getString("GasName"));
                            GasAddress.setText(bundle.getString("GasAddress"));
                            GasPrice.setText(bundle.getString("GasPrice"));
                            GasProvince.setText(province);

                            String title = bundle.getString("GasName");
                            String address = bundle.getString("GasAddress");

                            bundle.putString("pName",title);
                            bundle.putString("pAddr",address);
                            bundle.putDouble("90#",5.16);
                            bundle.putDouble("93#",5.53);
                            bundle.putDouble("97#",5.88);
                            bundle.putDouble("0#",5.11);

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(getApplicationContext(),"获取成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {
                        ToastUtil.show(GasItemActivity.this,throwable.getMessage());
                    }
                });
    }

    private void GasCommitBtn(){
        gas_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(GasItemActivity.this,"请选择预约时间");
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(GasItemActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                //ToastUtil.show(PoiAroundSearchActivity.this,"choose"+year+"年"+(monthOfYear+1)+ "月"+dayOfMonth+"日");
                                final int yy = year;
                                final int mm = monthOfYear;
                                final int dd = dayOfMonth;
                                final Calendar c = Calendar.getInstance();
                                new TimePickerDialog(GasItemActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {

                                                if(hourOfDay>17 || hourOfDay <12){
                                                    ToastUtil.show(GasItemActivity.this,"12:00之前与17:00之后不开放预订");
                                                }else if(yy>c.get(Calendar.YEAR)){
                                                    ToastUtil.show(GasItemActivity.this,"仅本年预订，请重选");
                                                }else if(mm+1>c.get(Calendar.MONTH)+2){
                                                    ToastUtil.show(GasItemActivity.this,"仅能预订至下一月，请重选");
                                                }else if(mm+1<c.get(Calendar.MONTH)+1){
                                                    ToastUtil.show(GasItemActivity.this,"月份出错，请重选");
                                                }else if(dd<c.get(Calendar.DAY_OF_MONTH)){
                                                    ToastUtil.show(GasItemActivity.this,"日期出错，请重选");
                                                }else if(hourOfDay<c.get(Calendar.HOUR_OF_DAY)){
                                                    ToastUtil.show(GasItemActivity.this,"时钟出错，请重选");
                                                }else if(minute< c.get(Calendar.MINUTE)){
                                                    ToastUtil.show(GasItemActivity.this,"分钟出错，请重选");
                                                }
                                                else {
                                                    final String ordertime = yy + "年" + (mm + 1) + "月" + dd + "日" + hourOfDay + "时" + minute + "分";
                                                    final String cmpTool = yy + "年" + (mm + 1) + "月" + dd + "日";

                                                    final List<String> ans = new ArrayList<>();

                                                    AVQuery<AVObject> query = new AVQuery<>("Order");
                                                    query.findInBackground(new FindCallback<AVObject>() {
                                                        @Override
                                                        public void done(List<AVObject> list, AVException e) {
                                                            Boolean Tag = true;

                                                            for(int i=0; i<list.size();i++) {
                                                                Boolean tag = compare(cmpTool, list.get(i).get("pTime").toString());
                                                                if(tag){
                                                                    ans.add(list.get(i).get("pTime").toString());
                                                                }
                                                            }

                                                            if(minute<10) {
                                                                for (int i = 0; i < ans.size(); i++) {
                                                                    String hh = ans.get(i).substring(ans.get(i).indexOf("日") + 1, ans.get(i).indexOf("时") - 1);
                                                                    String mm = ans.get(i).substring(ans.get(i).indexOf("时") + 1, ans.get(i).indexOf("分") - 1);

                                                                    if (Integer.parseInt(hh) == hourOfDay - 1) {
                                                                        if (Integer.parseInt(mm) > minute + 50 && Integer.parseInt(mm) <= 59) {
                                                                            //ToastUtil.show(PoiAroundSearchActivity.this, "此时间段已被预约");
                                                                            Tag = false;
                                                                            break;
                                                                        }
                                                                    }else if(Integer.parseInt(hh) == hourOfDay){
                                                                        if(Integer.parseInt(mm)>=0 && Integer.parseInt(mm)<=minute) {
                                                                            //ToastUtil.show(PoiAroundSearchActivity.this, "此时间段已被预约");
                                                                            Tag = false;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            else if (minute>=10){
                                                                for (int i = 0; i < ans.size(); i++) {
                                                                    String hh = ans.get(i).substring(ans.get(i).indexOf("日")+1,ans.get(i).indexOf("时")-1);
                                                                    String mm = ans.get(i).substring(ans.get(i).indexOf("时")+1,ans.get(i).indexOf("分")-1);

                                                                    if(Integer.parseInt(hh) == hourOfDay){
                                                                        if(Integer.parseInt(mm)>minute-10 && Integer.parseInt(mm)<=minute) {
                                                                            //ToastUtil.show(PoiAroundSearchActivity.this, "此时间段已被预约");
                                                                            Tag = false;
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            if (Tag){
                                                                bundle.putString("pTime", ordertime);
                                                                Intent intent = new Intent(GasItemActivity.this, WriteOrderActivity.class);
                                                                intent.putExtra("bundle", bundle);
                                                                startActivity(intent);
                                                            }
                                                            else{
                                                                ToastUtil.show(GasItemActivity.this, "此时间段已被预约");
                                                            }
                                                        }
                                                    });


                                                }
                                            }
                                        }
                                        , c.get(Calendar.HOUR_OF_DAY)
                                        , c.get(Calendar.MINUTE)
                                        , true).show();
                            }
                        }
                        , c.get(Calendar.YEAR)
                        , c.get(Calendar.MONTH)
                        , c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private Boolean compare(String s1,String s2){
        char c1,c2;
        for (int i=0; i<s1.length(); i++){
            c1 = s1.charAt(i);
            c2 = s2.charAt(i);
            if(c1 != c2){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gas_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gas_route:

                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
