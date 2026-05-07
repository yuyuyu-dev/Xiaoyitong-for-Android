package com.example.schooltrade.ui.main;

import androidx.fragment.app.Fragment;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
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