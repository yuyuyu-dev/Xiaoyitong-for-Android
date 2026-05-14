package com.example.schooltrade.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseFragment;
import com.example.schooltrade.entity.Conversation;
import com.example.schooltrade.model.db.MessageDao;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.util.List;

public class MessageFragment extends BaseFragment {
    private RecyclerView recyclerMessage;
    private ConversationAdapter adapter;
    private TextView tvEmpty;
    private List<Conversation> conversationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        
        recyclerMessage = view.findViewById(R.id.recycler_message);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        recyclerMessage.setLayoutManager(new LinearLayoutManager(mContext));
        
        loadConversations();
        
        return view;
    }

    private void loadConversations() {
        if (!UserSession.isLogin()) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ToastUtil.show(mContext, "请先登录");
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("请先登录后查看消息");
                    recyclerMessage.setVisibility(View.GONE);
                });
            }
            return;
        }
        
        showLoading();
        new Thread(() -> {
            try {
                int userId = UserSession.getCurrentUser().getUserId();
                conversationList = MessageDao.getConversations(userId);
                
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    hideLoading();
                    
                    if (conversationList == null || conversationList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("暂无消息\n快去和买家卖家聊聊吧~");
                        recyclerMessage.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerMessage.setVisibility(View.VISIBLE);
                        
                        adapter = new ConversationAdapter(mContext, conversationList);
                        recyclerMessage.setAdapter(adapter);
                        
                        adapter.setOnItemClickListener(position -> {
                            Conversation conv = conversationList.get(position);
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            intent.putExtra("otherUserId", conv.getOtherUserId());
                            intent.putExtra("otherUserName", conv.getOtherUserName());
                            intent.putExtra("goodsId", conv.getGoodsId());
                            intent.putExtra("goodsTitle", conv.getGoodsTitle());
                            startActivity(intent);
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        hideLoading();
                        ToastUtil.show(mContext, "加载消息失败：" + e.getMessage());
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("加载失败，请重试");
                        recyclerMessage.setVisibility(View.GONE);
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversations();
    }
}