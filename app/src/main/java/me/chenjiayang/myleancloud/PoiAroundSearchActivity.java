package me.chenjiayang.myleancloud;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.chenjiayang.myleancloud.Gas.AroundGasActivity;
import me.chenjiayang.myleancloud.util.ToastUtil;


public class PoiAroundSearchActivity extends Activity implements View.OnClickListener,
        AMap.OnMapClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener,
        PoiSearch.OnPoiSearchListener {

    private MapView mapview;
    private AMap mAMap;
    //定位
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp = null;
    //private LatLonPoint lp = new LatLonPoint(31.022371, 121.442491);
    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker mlastMarker;
    private PoiSearch poiSearch;
    private myPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private PoiItem mPoi;

    private RelativeLayout mPoiDetail;
    private TextView mPoiName, mPoiAddress;
    private String keyWord = "";
    private EditText mSearchText;
    private BootstrapButton mButton;
    private BootstrapButton Poi_gas_item;
    private BootstrapButton Poi_around_gas;
    private Bundle bundle = new Bundle();
    private Intent intent;

    private android.support.v7.app.AlertDialog alert = null;
    private android.support.v7.app.AlertDialog.Builder builder = null;

    private String title = null;
    private String address = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.poiaroundsearch_activity);

        mapview = (MapView) findViewById(R.id.mapView);
        mapview.onCreate(savedInstanceState);

        intent = getIntent();
        bundle = intent.getBundleExtra("location");

        //lp = new LatLonPoint(bundle.getDouble("lat"),bundle.getDouble("lon"));
        lp = new LatLonPoint(31.022371, 121.442491);

        init();

        BottomMenu();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void BottomMenu(){
        Poi_gas_item = (BootstrapButton) findViewById(R.id.poi_gas_item);
        Poi_around_gas = (BootstrapButton) findViewById(R.id.poi_around_gas);
        Poi_gas_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.poi_item, null);

                final TextView item_name = (TextView) layout.findViewById(R.id.poi_item_name);
                final TextView item_addr = (TextView) layout.findViewById(R.id.poi_item_address);
                final TextView item_price = (TextView) layout.findViewById(R.id.poi_item_price);
                final TextView item_province = (TextView) layout.findViewById(R.id.poi_item_province);

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

                                    item_name.setText(title);
                                    item_addr.setText(address);
                                    item_price.setText("90#：5.16元"+"   "+"93#：5.53元"+"\n"+"97#：5.88元"+"   "+"0#：5.11元");
                                    item_province.setText(province);

                                    builder = new android.support.v7.app.AlertDialog.Builder(PoiAroundSearchActivity.this);
                                    alert = builder.setView(layout).setPositiveButton("Cancel", new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                                    alert.show();

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
                                ToastUtil.show(PoiAroundSearchActivity.this,throwable.getMessage());
                            }
                        });
            }
        });
        Poi_around_gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PoiAroundSearchActivity.this, AroundGasActivity.class));
            }
        });
    }


    /**
     * 初始化AMap对象
     */
    private void init() {

        if (mAMap == null) {
            mAMap = mapview.getMap();
            mAMap.setOnMapClickListener(this);
            mAMap.setOnMarkerClickListener(this);
            mAMap.setOnInfoWindowClickListener(this);
            mAMap.setInfoWindowAdapter(this);
            TextView searchButton = (TextView) findViewById(R.id.btn_search);
            searchButton.setOnClickListener(this);
            locationMarker = mAMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_marker)))
                    .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
            locationMarker.showInfoWindow();

        }
        setup();
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lp.getLatitude(), lp.getLongitude()), 14));
    }

    private void setup() {
        mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);
        mPoiDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        mPoiName = (TextView) findViewById(R.id.poi_name);
        mPoiAddress = (TextView) findViewById(R.id.poi_address);
        mSearchText = (EditText) findViewById(R.id.input_edittext);
        mButton = (BootstrapButton) findViewById(R.id.poi_button);
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        keyWord = mSearchText.getText().toString().trim();
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "上海市");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
        whetherToShowDetailInfo(false);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onPoiItemSearched(PoiItem arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {          // 搜索poi的结果
                if (result.getQuery().equals(query)) {                  // 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();                     // 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();                // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay != null) {
                            poiOverlay.removeFromMap();
                        }
                        //还原地图
                        mAMap.clear();
                        //新建poiOverlay
                        poiOverlay = new myPoiOverlay(mAMap, poiItems);
                        poiOverlay.addToMap();   //显示在地图上
                        poiOverlay.zoomToSpan(); //调整视野全部显示poi

                        mAMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.drawable.location_marker)))
                                .position(new LatLng(lp.getLatitude(), lp.getLongitude())));

                        mAMap.addCircle(new CircleOptions()
                                .center(new LatLng(lp.getLatitude(),
                                        lp.getLongitude())).radius(5000)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 1, 1, 1))
                                .strokeWidth(2));

                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(PoiAroundSearchActivity.this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(PoiAroundSearchActivity.this, R.string.no_result);
            }
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.poi_marker_pressed)));

                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }


        return true;
    }

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        } else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

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

    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        title = mCurrentPoi.getTitle();
        address = mCurrentPoi.getSnippet();
        mPoiName.setText(title);
        mPoiAddress.setText(address);

        bundle.putString("pName",title);
        bundle.putString("pAddr",address);
        bundle.putDouble("90#",5.16);
        bundle.putDouble("93#",5.53);
        bundle.putDouble("97#",5.88);
        bundle.putDouble("0#",5.11);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(PoiAroundSearchActivity.this,"请选择预约时间");
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(PoiAroundSearchActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                //ToastUtil.show(PoiAroundSearchActivity.this,"choose"+year+"年"+(monthOfYear+1)+ "月"+dayOfMonth+"日");
                                final int yy = year;
                                final int mm = monthOfYear;
                                final int dd = dayOfMonth;
                                final Calendar c = Calendar.getInstance();
                                new TimePickerDialog(PoiAroundSearchActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {

                                                if(hourOfDay>17 || hourOfDay <12){
                                                    ToastUtil.show(PoiAroundSearchActivity.this,"12:00之前与17:00之后不开放预订");
                                                }else if(yy>c.get(Calendar.YEAR)){
                                                    ToastUtil.show(PoiAroundSearchActivity.this,"仅本年预订，请重选");
                                                }else if(mm+1>c.get(Calendar.MONTH)+2){
                                                    ToastUtil.show(PoiAroundSearchActivity.this,"仅能预订至下一月，请重选");
                                                }else if(mm+1<c.get(Calendar.MONTH)+1){
                                                    ToastUtil.show(PoiAroundSearchActivity.this,"月份出错，请重选");
                                                }else if(dd<c.get(Calendar.DAY_OF_MONTH)){
                                                    ToastUtil.show(PoiAroundSearchActivity.this,"日期出错，请重选");
                                                }else if(hourOfDay<c.get(Calendar.HOUR_OF_DAY)){
                                                    ToastUtil.show(PoiAroundSearchActivity.this,"时钟出错，请重选");
                                                }else if(minute< c.get(Calendar.MINUTE)){
                                                    ToastUtil.show(PoiAroundSearchActivity.this,"分钟出错，请重选");
                                                }
                                                else {
                                                    final String ordertime = yy + "年" + (mm + 1) + "月" + dd + "日" + hourOfDay + "时" + minute + "分";
                                                    final String cmpTool = yy + "年" + (mm + 1) + "月" + dd + "日";

                                                    final List<String>ans = new ArrayList<>();

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
                                                                    Intent intent = new Intent(PoiAroundSearchActivity.this, WriteOrderActivity.class);
                                                                    intent.putExtra("bundle", bundle);
                                                                    startActivity(intent);
                                                                }
                                                            else{
                                                                ToastUtil.show(PoiAroundSearchActivity.this, "此时间段已被预约");
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


    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                doSearchQuery();
                break;

            default:
                break;
        }

    }

    private int[] markers = {R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);
        } else {
            mPoiDetail.setVisibility(View.GONE);
        }
    }


    @Override
    public void onMapClick(LatLng arg0) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetlastmarker();
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(this, infomation);

    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PoiAroundSearch Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://me.chenjiayang.myleancloud/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PoiAroundSearch Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://me.chenjiayang.myleancloud/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * 自定义PoiOverlay
     */
    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

        public myPoiOverlay(AMap amap, List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         *
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         *
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         *
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            } else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }
    }

}

