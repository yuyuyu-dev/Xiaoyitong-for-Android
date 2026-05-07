package com.example.schooltrade.ui.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.User;
import com.example.schooltrade.model.db.UserDao;
import com.example.schooltrade.utils.ToastUtil;

public class RegisterActivity extends BaseActivity {
    private EditText etAccount, etPwd, etName;
    private Button btnRegister;

    @Override
    protected int getLayoutId() { return R.layout.activity_register; }

    @Override
    protected void initView() {
        etAccount = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        etName = findViewById(R.id.et_name);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String account = etAccount.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        String name = etName.getText().toString().trim();
        if (account.isEmpty() || pwd.isEmpty() || name.isEmpty()) {
            ToastUtil.show(this, "信息不能为空");
            return;
        }
        User user = new User();
        user.setAccount(account);
        user.setPassword(pwd);
        user.setRealName(name);

        showLoading();
        new Thread(() -> {
            boolean res = UserDao.register(user);
            runOnUiThread(() -> {
                hideLoading();
                if (res) {
                    ToastUtil.show(this, "注册成功");
                    finish();
                } else {
                    ToastUtil.show(this, "注册失败");
                }
            });
        }).start();
    }

    @Override
    protected void initData() {}
}