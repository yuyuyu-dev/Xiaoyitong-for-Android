package com.example.schooltrade.ui.message;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Message;
import com.example.schooltrade.model.db.MessageDao;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.util.List;

public class ChatActivity extends BaseActivity {
    private RecyclerView recyclerChat;
    private EditText etInput;
    private TextView tvTitle;
    private ChatMessageAdapter adapter;
    private List<Message> messageList;

    private int otherUserId;
    private String otherUserName;
    private int goodsId;
    private String goodsTitle;
    private int currentUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        try {
            ImageView btnBack = findViewById(R.id.btn_back);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            tvTitle = findViewById(R.id.tv_title);
            recyclerChat = findViewById(R.id.recycler_chat);
            etInput = findViewById(R.id.et_input);
            ImageView btnSend = findViewById(R.id.btn_send);

            if (recyclerChat == null) {
                ToastUtil.show(this, "页面初始化失败");
                finish();
                return;
            }

            recyclerChat.setLayoutManager(new LinearLayoutManager(this));

            otherUserId = getIntent().getIntExtra("otherUserId", 0);
            otherUserName = getIntent().getStringExtra("otherUserName");
            goodsId = getIntent().getIntExtra("goodsId", 0);
            goodsTitle = getIntent().getStringExtra("goodsTitle");
            
            if (UserSession.getCurrentUser() == null) {
                ToastUtil.show(this, "请先登录");
                finish();
                return;
            }
            currentUserId = UserSession.getCurrentUser().getUserId();

            tvTitle.setText(otherUserName != null ? otherUserName : "聊天");

            if (btnSend != null) {
                btnSend.setOnClickListener(v -> sendMessage());
            }

            loadMessages();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "页面加载失败：" + e.getMessage());
            finish();
        }
    }

    private void loadMessages() {
        showLoading();
        new Thread(() -> {
            try {
                messageList = MessageDao.getConversationMessages(currentUserId, otherUserId, goodsId);
                if (messageList == null) {
                    messageList = new java.util.ArrayList<>();
                }
                MessageDao.markAsRead(currentUserId, otherUserId, goodsId);

                runOnUiThread(() -> {
                    try {
                        hideLoading();
                        adapter = new ChatMessageAdapter(this, messageList, currentUserId);
                        recyclerChat.setAdapter(adapter);

                        if (!messageList.isEmpty()) {
                            recyclerChat.scrollToPosition(messageList.size() - 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.show(this, "显示消息失败");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(this, "加载消息失败：" + e.getMessage());
                });
            }
        }).start();
    }

    private void sendMessage() {
        String content = etInput.getText().toString().trim();
        if (content.isEmpty()) {
            ToastUtil.show(this, "请输入消息内容");
            return;
        }

        Message message = new Message();
        message.setSenderId(currentUserId);
        message.setReceiverId(otherUserId);
        message.setGoodsId(goodsId);
        message.setContent(content);
        message.setMessageType("text");

        new Thread(() -> {
            boolean success = MessageDao.sendMessage(message);
            runOnUiThread(() -> {
                if (success) {
                    etInput.setText("");
                    loadMessages();
                } else {
                    ToastUtil.show(this, "发送失败");
                }
            });
        }).start();
    }

    @Override
    protected void initData() {}
}
