package com.example.schooltrade.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.example.schooltrade.R;
import com.example.schooltrade.api.LoginRequest;
import com.example.schooltrade.api.LoginResponse;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.User;
import com.example.schooltrade.ui.main.MainActivity;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.io.IOException;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private EditText etAccount, etPwd;
    private Button btnLogin, btnRegister;

    @Override
    protected int getLayoutId() { return R.layout.activity_login; }

    @Override
    protected void initView() {
        etAccount = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String account = etAccount.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        if (account.isEmpty() || pwd.isEmpty()) {
            ToastUtil.show(this, "账号密码不能为空");
            return;
        }
        showLoading();
        new Thread(() -> {
            try {
                Response<Result<LoginResponse>> response = RetrofitClient.getInstance()
                    .getApiService()
                    .login(new LoginRequest(account, pwd))
                    .execute();
                runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        LoginResponse data = response.body().getData();
                        User user = new User();
                        user.setUserId(data.getUserId());
                        user.setAccount(data.getAccount());
                        user.setRealName(data.getRealName());
                        user.setAvatarUrl(data.getAvatarUrl());
                        UserSession.saveLogin(user, data.getToken());
                        ToastUtil.show(this, "登录成功");
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        String msg = response.body() != null ? response.body().getMessage() : "登录失败";
                        ToastUtil.show(this, msg);
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(this, "网络连接失败，请检查服务器是否启动");
                });
            }
        }).start();
    }

    @Override
    protected void initData() {}
}