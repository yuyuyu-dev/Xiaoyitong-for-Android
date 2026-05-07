package com.example.schooltrade.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseFragment;
import com.example.schooltrade.entity.User;
import com.example.schooltrade.ui.goods.MyPublishActivity;
import com.example.schooltrade.ui.login.LoginActivity;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

public class MineFragment extends BaseFragment {
    private TextView tvUsername, tvAccount;
    private LinearLayout llMyPublish;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        tvUsername = view.findViewById(R.id.tv_username);
        tvAccount = view.findViewById(R.id.tv_account);
        llMyPublish = view.findViewById(R.id.ll_my_publish);
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
        }
    }

    private void initClick() {
        llMyPublish.setOnClickListener(v -> {
            if (UserSession.isLogin()) {
                startActivity(new Intent(mContext, MyPublishActivity.class));
            } else {
                ToastUtil.show(mContext, "请先登录");
            }
        });

        btnLogout.setOnClickListener(v -> {
            UserSession.clear();
            ToastUtil.show(mContext, "退出成功");
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });
    }
}