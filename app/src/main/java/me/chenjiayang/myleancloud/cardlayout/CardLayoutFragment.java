package me.chenjiayang.myleancloud.cardlayout;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.chenjiayang.myleancloud.Main2Activity;
import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class CardLayoutFragment extends Fragment {

    private ListView cardsList;
    private ArrayList<String> items = new ArrayList<>();
    private List<AVObject> ans;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    public CardLayoutFragment() {
        // nop
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsList = (ListView) rootView.findViewById(R.id.cards_list);

        AVQuery<AVObject> query = new AVQuery<>("Order");
        // 按时间，升序排列
        query.orderByDescending("createdAt")
                .whereEqualTo("currUserID", AVUser.getCurrentUser().getObjectId())
                .findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                ans = list;
                if(ans.size()==0){
                    ToastUtil.show(getActivity(),"您还没有订单");
                }else {
                    setupList();
                }
            }
        });
        return rootView;
    }

    private void setupList() {
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new ListItemClickListener());
    }

    private CardsAdapter createAdapter() {

        //Text文本，拼接字符串
        for (int i = 0; i < ans.size(); i++) {
            items.add(i, "序号"+(i+1)+"\n加油站名称："+ans.get(i).get("pName").toString()+"\n加油站地址:"+
                    ans.get(i).get("pAddr").toString()+"\n数量："+ans.get(i).get("pQuantity").toString()+"升\n金额："+ans.get(i).get("pPrice").toString()+"元\n"
                    +"预约时间:"+ ans.get(i).get("pTime").toString());
        }
        return new CardsAdapter(getActivity(), items, new ListItemButtonClickListener());
    }

    private void refresh() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), CardLayoutActivity.class);
        startActivity(intent);
    }

    /**
     * 点击按钮取消或者获取二维码
     */
    private final class ListItemButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = cardsList.getFirstVisiblePosition(); i <= cardsList.getLastVisiblePosition(); i++) {
                if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_1)) {
                    final int x = i;

                    //创建sweet dialog
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Won't be able to recover this file!")
                            .setConfirmText("Yes,delete it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog
                                            .setTitleText("Deleted!")
                                            .setContentText("Your imaginary file has been deleted!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    refresh(); //重新加载activity
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                    //取消预约
                                    AVQuery<AVObject> avQuery = new AVQuery<>("Order");
                                    avQuery.getInBackground(ans.get(x).getObjectId(), new GetCallback<AVObject>() {
                                        @Override
                                        public void done(AVObject avObject, AVException e) {
                                            avObject.deleteInBackground();
                                        }
                                    });

                                }
                            })
                            .show();

                }
                //生成二维码
                else if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_2)) {
                    try{
                        Bitmap bitmap = EncodingHandler.createQRCode(ans.get(i).getObjectId().toString(),900);
                        final ImageView imageView = new ImageView(getActivity());
                        imageView.setImageBitmap(bitmap);
                        builder=new AlertDialog.Builder(getActivity());
                        alert = builder.setView(imageView).create();
                        alert.show();

                    }catch (WriterException e){
                        ToastUtil.show(getActivity(),"二维码生成失败");
                    }
                }
            }
        }
    }

    /**
     * 点击对应订单调转到详情
     */
    private final class ListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT).show();
        }
    }
}
