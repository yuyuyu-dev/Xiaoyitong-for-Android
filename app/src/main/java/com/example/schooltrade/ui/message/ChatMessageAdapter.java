package com.example.schooltrade.ui.message;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.schooltrade.R;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.entity.Message;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.Holder> {
    private final Context context;
    private final List<Message> list;
    private final int currentUserId;
    private final String myAvatarUrl;
    private final String otherAvatarUrl;

    public ChatMessageAdapter(Context context, List<Message> list, int currentUserId,
                              String myAvatarUrl, String otherAvatarUrl) {
        this.context = context;
        this.list = list;
        this.currentUserId = currentUserId;
        this.myAvatarUrl = myAvatarUrl;
        this.otherAvatarUrl = otherAvatarUrl;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_chat_message, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Message message = list.get(position);
        holder.tvMessage.setText(message.getContent());
        holder.tvTime.setText(formatTime(message.getCreateTime()));

        if (message.getSenderId() == currentUserId) {
            // 自己发的：消息靠右，蓝色背景，右边头像
            holder.llMsgArea.setGravity(Gravity.END);
            holder.tvMessage.setBackgroundResource(R.drawable.bg_message_sent);
            holder.tvMessage.setTextColor(android.graphics.Color.WHITE);
            holder.ivAvatarLeft.setVisibility(View.GONE);
            holder.ivAvatarRight.setVisibility(View.VISIBLE);
            loadAvatar(holder.ivAvatarRight, myAvatarUrl);
        } else {
            // 对方发的：消息靠左，白色背景，左边头像
            holder.llMsgArea.setGravity(Gravity.START);
            holder.tvMessage.setBackgroundResource(R.drawable.bg_message_received);
            holder.tvMessage.setTextColor(android.graphics.Color.parseColor("#333333"));
            holder.ivAvatarLeft.setVisibility(View.VISIBLE);
            holder.ivAvatarRight.setVisibility(View.GONE);
            loadAvatar(holder.ivAvatarLeft, otherAvatarUrl);
        }
    }

    private void loadAvatar(ImageView iv, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(context).load(RetrofitClient.fullUrl(url))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .into(iv);
        }
    }

    private String formatTime(String time) {
        if (time == null || time.isEmpty()) return "";
        if (time.length() > 16) return time.substring(11, 16);
        return time;
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class Holder extends RecyclerView.ViewHolder {
        LinearLayout llMsgArea;
        ImageView ivAvatarLeft, ivAvatarRight;
        TextView tvMessage, tvTime;

        public Holder(@NonNull View itemView) {
            super(itemView);
            llMsgArea = (LinearLayout) ((android.view.ViewGroup) itemView).getChildAt(1);
            ivAvatarLeft = itemView.findViewById(R.id.iv_avatar_left);
            ivAvatarRight = itemView.findViewById(R.id.iv_avatar_right);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
