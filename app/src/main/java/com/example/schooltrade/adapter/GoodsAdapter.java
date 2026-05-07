package com.example.schooltrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schooltrade.R;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.utils.ImageUtil;
import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.Holder> {
    private Context context;
    private List<Goods> list;
    private OnGoodsClickListener listener;

    // 回调接口
    public interface OnGoodsClickListener{
        void onEdit(int position);
        void onDelete(int position);
    }

    public void setOnGoodsClickListener(OnGoodsClickListener listener){
        this.listener = listener;
    }

    public GoodsAdapter(Context context, List<Goods> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Goods g = list.get(position);
        holder.title.setText(g.getTitle());
        holder.content.setText(g.getContent());

        if (g.getPublishType() == 0) {
            holder.type.setText("出售：¥" + g.getPrice());
        } else {
            holder.type.setText("置换：想换 " + g.getWantGoods());
        }

        ImageUtil.loadImage(holder.img, g.getImgUrl());

        // 编辑点击
        holder.btnEdit.setOnClickListener(v->{
            if(listener != null){
                listener.onEdit(position);
            }
        });
        // 删除点击
        holder.btnDelete.setOnClickListener(v->{
            if(listener != null){
                listener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, content, type;
        Button btnEdit,btnDelete;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_img);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            type = itemView.findViewById(R.id.tv_type);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}