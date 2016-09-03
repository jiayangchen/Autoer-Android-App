package me.chenjiayang.myleancloud.Gas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class AroundGasActivity extends AppCompatActivity {

    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_gas);


        init();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void init(){
        tv = (TextView) findViewById(R.id.around_gas);
        JuheSDKInitializer.initialize(getApplicationContext());
        Parameters params = new Parameters();
        params.add("lon","116.403119");
        params.add("lat","39.916042");
        params.add("r",3000);
        params.add("page",1);
        params.add("format",1);
        JuheData.executeWithAPI(getApplicationContext(), 7, "http://apis.juhe.cn/oil/local",
                JuheData.GET, params, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            JSONObject result = object.getJSONObject("result");
                            String sk = result.getString("data");
                            //String tmp = sk.getString("city");
                            JSONArray jsonArray = new JSONArray(sk);
                            for(int j=0; j<jsonArray.length(); j++){
                                JSONObject object1 = jsonArray.getJSONObject(j);
                                tv.append(object1.getString("name")+"\n");
                            }
                            //tv.setText(sk);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                        //tv.setText(s);
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(getApplicationContext(),"finish",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {
                        tv.setText(throwable.getMessage());
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tool_refresh:
                ToastUtil.show(AroundGasActivity.this,"下拉刷新");
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
