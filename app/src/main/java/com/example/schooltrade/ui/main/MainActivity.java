package com.example.schooltrade.ui.main;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.ui.publish.PublishActivity;
import com.example.schooltrade.ui.publish.PublishActivityWithImage;
import com.example.schooltrade.ui.publish.PublishActivitySimple;
import com.example.schooltrade.ui.publish.PublishActivityFinal;
import com.example.schooltrade.ui.publish.TestPublishActivity;
import com.example.schooltrade.ui.publish.SimplePublishActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {
    private Fragment homeFragment, mineFragment;

    @Override
    protected int getLayoutId() { return R.layout.activity_main; }

    @Override
    protected void initView() {
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        homeFragment = new HomeFragment();
        mineFragment = new MineFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.nav_publish) {
                // 打开发布商品页面 - 使用带图片功能的版本
                startActivity(new Intent(MainActivity.this, PublishActivityWithImage.class));
                // 保持首页选中状态
                nav.setSelectedItemId(R.id.nav_home);
                return true;
            } else if (item.getItemId() == R.id.nav_mine) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mineFragment).commit();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void initData() {}
}