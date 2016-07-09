package me.chenjiayang.myleancloud.cardlayout;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;

import me.chenjiayang.myleancloud.Main2Activity;
import me.chenjiayang.myleancloud.R;
import me.chenjiayang.myleancloud.util.ToastUtil;

public class CardLayoutFragment extends Fragment {

    private ListView cardsList;
    private ArrayList<String> items = new ArrayList<String>();
    private List<AVObject> ans;

    public CardLayoutFragment() {
        // nop
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_layout, container, false);
        cardsList = (ListView) rootView.findViewById(R.id.cards_list);

        AVQuery<AVObject> query = new AVQuery<>("Order");
        // 按时间，升序排列
        query.orderByDescending("createdAt").findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                ans = list;       // 符合 priority = 0 的 Todo 数组
                if(ans.size()==0){
                    ToastUtil.show(getActivity(),"您还没有订单");
                }else {
                    setupList();
                }
            }
        });

        //setupList();
        return rootView;
    }

    private void setupList() {
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new ListItemClickListener());
    }

    private CardsAdapter createAdapter() {

        for (int i = 0; i < ans.size(); i++) {
            items.add(i, "序号"+(i+1)+"\n加油站名称："+ans.get(i).get("pName").toString()+"\n加油站地址:"+
                    ans.get(i).get("pAddr").toString()+"\n数量："+ans.get(i).get("pQuantity").toString()+"升     金额："+ans.get(i).get("pPrice").toString()+"元");
        }
        return new CardsAdapter(getActivity(), items, new ListItemButtonClickListener());
    }


    /**
     * 点击按钮取消或者获取二维码
     */
    private final class ListItemButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = cardsList.getFirstVisiblePosition(); i <= cardsList.getLastVisiblePosition(); i++) {
                if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_1)) {
                    // PERFORM AN ACTION WITH THE ITEM AT POSITION i
                    //Toast.makeText(getActivity(), "Clicked on Left Action Button of List Item " + i, Toast.LENGTH_SHORT).show();
                    AVQuery<AVObject> avQuery = new AVQuery<>("Order");
                    avQuery.getInBackground(ans.get(i).getObjectId(), new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            avObject.deleteInBackground();
                            Toast.makeText(getActivity(), "成功取消预约", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_2)) {
                    // PERFORM ANOTHER ACTION WITH THE ITEM AT POSITION i
                    //Toast.makeText(getActivity(), "获取二维码" , Toast.LENGTH_SHORT).show();

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
