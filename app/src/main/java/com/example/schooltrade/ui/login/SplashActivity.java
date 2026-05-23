package com.example.schooltrade.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.schooltrade.R;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.entity.User;
import com.example.schooltrade.ui.main.MainActivity;
import com.example.schooltrade.utils.UserSession;

import java.io.IOException;

import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        UserSession.init(getApplicationContext());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (UserSession.hasSavedLogin()) {
                tryAutoLogin();
            } else {
                goToLogin();
            }
        }, 1200);
    }

    private void tryAutoLogin() {
        UserSession.loadSavedUser();
        new Thread(() -> {
            try {
                Response<Result<User>> response = RetrofitClient.getInstance()
                    .getApiService().getProfile().execute();

                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    // Token 有效，更新用户信息
                    User user = response.body().getData();
                    if (user != null) {
                        UserSession.setCurrentUser(user);
                    }
                    runOnUiThread(() -> {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
                } else {
                    // Token 过期
                    UserSession.clear();
                    runOnUiThread(this::goToLogin);
                }
            } catch (IOException e) {
                // 网络不通，用本地缓存的信息先进主页
                runOnUiThread(() -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
            }
        }).start();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
