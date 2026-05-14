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

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.llContainer.getLayoutParams();

        if (message.getSenderId() == currentUserId) {
            params.gravity = Gravity.END;
            holder.tvMessage.setBackgroundResource(R.drawable.bg_message_sent);
        } else {
            params.gravity = Gravity.START;
            holder.tvMessage.setBackgroundResource(R.drawable.bg_message_received);
        }
        holder.llContainer.setLayoutParams(params);
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
