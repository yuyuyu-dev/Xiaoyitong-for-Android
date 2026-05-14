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
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvTitle = findViewById(R.id.tv_title);
        recyclerChat = findViewById(R.id.recycler_chat);
        etInput = findViewById(R.id.et_input);
        ImageView btnSend = findViewById(R.id.btn_send);

        recyclerChat.setLayoutManager(new LinearLayoutManager(this));

        otherUserId = getIntent().getIntExtra("otherUserId", 0);
        otherUserName = getIntent().getStringExtra("otherUserName");
        goodsId = getIntent().getIntExtra("goodsId", 0);
        goodsTitle = getIntent().getStringExtra("goodsTitle");
        currentUserId = UserSession.getCurrentUser().getUserId();

        tvTitle.setText(otherUserName != null ? otherUserName : "聊天");

        btnSend.setOnClickListener(v -> sendMessage());

        loadMessages();
    }

    private void loadMessages() {
        showLoading();
        new Thread(() -> {
            messageList = MessageDao.getConversationMessages(currentUserId, otherUserId, goodsId);
            MessageDao.markAsRead(currentUserId, otherUserId, goodsId);

            runOnUiThread(() -> {
                hideLoading();
                adapter = new ChatMessageAdapter(this, messageList, currentUserId);
                recyclerChat.setAdapter(adapter);

                if (!messageList.isEmpty()) {
                    recyclerChat.scrollToPosition(messageList.size() - 1);
                }
            });
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
