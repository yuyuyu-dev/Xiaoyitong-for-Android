package com.example.schooltrade.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.User;
import com.example.schooltrade.model.db.UserDao;
import com.example.schooltrade.ui.main.MainActivity;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

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
            User user = UserDao.login(account, pwd);
            runOnUiThread(() -> {
                hideLoading();
                if (user != null) {
                    UserSession.setCurrentUser(user);
                    ToastUtil.show(this, "登录成功");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    ToastUtil.show(this, "账号或密码错误");
                }
            });
        }).start();
    }

    @Override
    protected void initData() {}
}