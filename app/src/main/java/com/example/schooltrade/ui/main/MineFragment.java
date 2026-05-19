package com.example.schooltrade.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.schooltrade.R;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.base.BaseFragment;
import com.example.schooltrade.entity.User;
import com.example.schooltrade.ui.goods.MyCollectActivity;
import com.example.schooltrade.ui.goods.MyPublishActivity;
import com.example.schooltrade.ui.goods.MyTransactionActivity;
import com.example.schooltrade.ui.login.EditProfileActivity;
import com.example.schooltrade.ui.login.LoginActivity;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

public class MineFragment extends BaseFragment {
    private ImageView ivAvatar;
    private TextView tvUsername, tvAccount;
    private LinearLayout llEditProfile, llMyTransaction, llMyPublish, llMyCollect;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        tvAccount = view.findViewById(R.id.tv_account);
        llEditProfile = view.findViewById(R.id.ll_edit_profile);
        llMyTransaction = view.findViewById(R.id.ll_my_transaction);
        llMyPublish = view.findViewById(R.id.ll_my_publish);
        llMyCollect = view.findViewById(R.id.ll_my_collect);
        btnLogout = view.findViewById(R.id.btn_logout);

        showUserInfo();
        initClick();
        return view;
    }

    private void showUserInfo() {
        User user = UserSession.getCurrentUser();
        if (user != null) {
            tvUsername.setText(user.getRealName());
            tvAccount.setText("学号：" + user.getAccount());
            // 加载头像
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                Glide.with(mContext).load(RetrofitClient.fullUrl(user.getAvatarUrl()))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(ivAvatar);
            }
        }
    }

    private void initClick() {
        // 编辑资料
        llEditProfile.setOnClickListener(v -> {
            if (UserSession.isLogin()) {
                startActivity(new Intent(mContext, EditProfileActivity.class));
            } else {
                ToastUtil.show(mContext, "请先登录");
            }
        });

        // 我的交易
        llMyTransaction.setOnClickListener(v -> {
            if (UserSession.isLogin()) {
                startActivity(new Intent(mContext, MyTransactionActivity.class));
            } else {
                ToastUtil.show(mContext, "请先登录");
            }
        });

        // 我的发布
        llMyPublish.setOnClickListener(v -> {
            if (UserSession.isLogin()) {
                startActivity(new Intent(mContext, MyPublishActivity.class));
            } else {
                ToastUtil.show(mContext, "请先登录");
            }
        });

        // 我的收藏
        llMyCollect.setOnClickListener(v -> {
            if (UserSession.isLogin()) {
                startActivity(new Intent(mContext, MyCollectActivity.class));
            } else {
                ToastUtil.show(mContext, "请先登录");
            }
        });

        // 退出登录
        btnLogout.setOnClickListener(v -> {
            UserSession.clear();
            ToastUtil.show(mContext, "退出成功");
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showUserInfo();
    }
}