package com.example.schooltrade.ui.message;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.api.MessageRequest;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Message;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Response;

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
    private String autoMessage;
    private String otherUserAvatar;
    private String myAvatarUrl;

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
            autoMessage = getIntent().getStringExtra("autoMessage");
            otherUserAvatar = getIntent().getStringExtra("otherUserAvatar");
            myAvatarUrl = UserSession.getCurrentUser() != null
                ? UserSession.getCurrentUser().getAvatarUrl() : null;

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
                Response<Result<List<Message>>> response = RetrofitClient.getInstance()
                    .getApiService()
                    .getConversationMessages(currentUserId, otherUserId, goodsId)
                    .execute();
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    messageList = response.body().getData();
                } else {
                    messageList = new java.util.ArrayList<>();
                }
                // 标记已读
                Map<String, Integer> readBody = new HashMap<>();
                readBody.put("userId", currentUserId);
                readBody.put("otherUserId", otherUserId);
                readBody.put("goodsId", goodsId);
                RetrofitClient.getInstance().getApiService().markAsRead(readBody).execute();

                runOnUiThread(() -> {
                    try {
                        hideLoading();
                        adapter = new ChatMessageAdapter(ChatActivity.this, messageList,
                            currentUserId, myAvatarUrl, otherUserAvatar);
                        recyclerChat.setAdapter(adapter);

                        if (!messageList.isEmpty()) {
                            recyclerChat.scrollToPosition(messageList.size() - 1);
                        }

                        // 自动发送消息（从购买按钮跳转时）
                        if (autoMessage != null && !autoMessage.isEmpty()) {
                            sendAutoMessage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.show(ChatActivity.this, "显示消息失败");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(ChatActivity.this, "加载消息失败");
                });
            }
        }).start();
    }

    private void sendAutoMessage() {
        String msg = autoMessage;
        autoMessage = null; // 只发一次
        new Thread(() -> {
            try {
                RetrofitClient.getInstance().getApiService()
                    .sendMessage(new MessageRequest(otherUserId, goodsId, msg))
                    .execute();
                runOnUiThread(() -> loadMessages());
            } catch (IOException e) {
                runOnUiThread(() ->
                    ToastUtil.show(ChatActivity.this, "自动消息发送失败"));
            }
        }).start();
    }

    private void sendMessage() {
        String content = etInput.getText().toString().trim();
        if (content.isEmpty()) {
            ToastUtil.show(this, "请输入消息内容");
            return;
        }

        new Thread(() -> {
            try {
                Response<Result<Message>> response = RetrofitClient.getInstance()
                    .getApiService()
                    .sendMessage(new MessageRequest(otherUserId, goodsId, content))
                    .execute();
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        etInput.setText("");
                        loadMessages();
                    } else {
                        ToastUtil.show(ChatActivity.this, "发送失败");
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    ToastUtil.show(ChatActivity.this, "网络连接失败");
                });
            }
        }).start();
    }

    @Override
    protected void initData() {}
}
