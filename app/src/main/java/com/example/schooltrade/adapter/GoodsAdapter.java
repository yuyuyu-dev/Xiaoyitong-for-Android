package com.example.schooltrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.schooltrade.R;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.entity.Goods;
import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.Holder> {
    private final Context context;
    private final List<Goods> list;
    private OnGoodsClickListener listener;
    private OnItemClickListener itemClickListener;
    private boolean isMyPublishMode = false; // 是否为「我的发布」模式

    // 商品操作监听（仅管理模式用）
    public interface OnGoodsClickListener {
        void onEdit(int position);
        void onDelete(int position);
    }

    // 条目点击监听（跳转详情）
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // 构造方法：普通模式（首页，无按钮）
    public GoodsAdapter(Context context, List<Goods> list) {
        this.context = context;
        this.list = list;
        this.isMyPublishMode = false;
    }

    // 构造方法：管理模式（我的发布，有按钮）
    public GoodsAdapter(Context context, List<Goods> list, boolean isMyPublishMode) {
        this.context = context;
        this.list = list;
        this.isMyPublishMode = isMyPublishMode;
    }

    public void setOnGoodsClickListener(OnGoodsClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods, parent, false);
        return new Holder(view);
    }

    private static final String[] CATEGORY_NAMES = {
        "", "教材书籍", "数码产品", "生活用品", "服饰鞋包", "运动器材", "其他"
    };

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Goods g = list.get(position);
        holder.title.setText(g.getTitle());
        holder.content.setText(g.getContent());

        // 设置价格
        if (g.getPublishType() == 0) {
            holder.type.setText("出售");
            holder.price.setText("￥" + String.format("%.2f", g.getPrice()));
        } else {
            holder.type.setText("置换");
            holder.price.setText("置换");
        }

        // 分类标签
        if (g.getCategory() > 0 && g.getCategory() < CATEGORY_NAMES.length) {
            holder.tvCategory.setText(CATEGORY_NAMES[g.getCategory()]);
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }

        // 已卖出印章 (status != 1 即为已售出/已下架)
        boolean isSold = (g.getStatus() != 1);
        if (holder.soldOverlay != null) {
            holder.soldOverlay.setVisibility(isSold ? View.VISIBLE : View.GONE);
        }
        if (holder.tvSoldStamp != null) {
            holder.tvSoldStamp.setVisibility(isSold ? View.VISIBLE : View.GONE);
        }

        // 加载商品图片
        loadImage(holder.img, g.getImgUrl());

        // 条目点击跳转详情
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) itemClickListener.onItemClick(position);
        });

        // 管理模式：显示编辑和删除按钮
        if (isMyPublishMode) {
            holder.divider.setVisibility(View.VISIBLE);
            holder.llButtons.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(position);
            });
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(position);
            });
        } else {
            holder.divider.setVisibility(View.GONE);
            holder.llButtons.setVisibility(View.GONE);
        }
    }

    /**
     * 加载图片（网络URL，使用Glide）
     */
    private void loadImage(ImageView imageView, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context)
                .load(RetrofitClient.fullUrl(imagePath))
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.bg_image_placeholder);
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, content, type, price, tvCategory;
        View soldOverlay;
        TextView tvSoldStamp;
        TextView btnEdit, btnDelete;
        View divider;
        android.view.ViewGroup llButtons;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_img);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            type = itemView.findViewById(R.id.tv_type);
            price = itemView.findViewById(R.id.tv_price);
            tvCategory = itemView.findViewById(R.id.tv_category);
            soldOverlay = itemView.findViewById(R.id.sold_overlay);
            tvSoldStamp = itemView.findViewById(R.id.tv_sold_stamp);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            divider = itemView.findViewById(R.id.divider);
            llButtons = itemView.findViewById(R.id.ll_buttons);
        }
    }
}