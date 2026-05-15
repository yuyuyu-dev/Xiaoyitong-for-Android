package com.example.schooltrade.ui.message;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.entity.Message;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.Holder> {
    private final Context context;
    private final List<Message> list;
    private final int currentUserId;

    public ChatMessageAdapter(Context context, List<Message> list, int currentUserId) {
        this.context = context;
        this.list = list;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Message message = list.get(position);
        holder.tvMessage.setText(message.getContent());
        holder.tvTime.setText(formatTime(message.getCreateTime()));

        // 设置消息气泡位置和背景
        if (message.getSenderId() == currentUserId) {
            // 我发送的消息：靠右对齐，蓝色背景
            holder.llContainer.setGravity(Gravity.END);
            holder.tvMessage.setBackgroundResource(R.drawable.bg_message_sent);
            holder.tvMessage.setTextColor(android.graphics.Color.WHITE);
        } else {
            // 对方发送的消息：靠左对齐，白色背景
            holder.llContainer.setGravity(Gravity.START);
            holder.tvMessage.setBackgroundResource(R.drawable.bg_message_received);
            holder.tvMessage.setTextColor(android.graphics.Color.parseColor("#333333"));
        }
    }

    private String formatTime(String time) {
        if (time == null || time.isEmpty()) return "";
        if (time.length() > 16) {
            return time.substring(11, 16);
        }
        return time;
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class Holder extends RecyclerView.ViewHolder {
        LinearLayout llContainer;
        TextView tvMessage, tvTime;

        public Holder(@NonNull View itemView) {
            super(itemView);
            llContainer = itemView.findViewById(R.id.ll_container);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
