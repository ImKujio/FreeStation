package com.dxys.demo.bingo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dxys.demo.bingo.TabPage.ImageGroupFg;
import com.dxys.demo.bingo.TabPage.ViewPagerAdapter;
import com.dxys.demo.bingo.altaotu.TabIfoManager;
import com.dxys.demo.bingo.setting.SettingActivity;

import java.util.ArrayList;

import static com.dxys.demo.bingo.Utlis.log;

public class BasePage extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean backPressKey = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_base_page);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViewpager();
        initViewTabLayout();
    }

    private void initViewpager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < TabIfoManager.ALL_TAB_URLS.length; i++) {
            fragments.add(ImageGroupFg.newInstance(BasePage.this, i));
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new ViewPagerAdapter(fragmentManager, fragments));
    }
    private void initViewTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, false);
        for (int i = 0; i < TabIfoManager.ALL_TAB_NAMES.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setText(TabIfoManager.ALL_TAB_NAMES[i]);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.ic_setting:
            {
                startActivity(new Intent(BasePage.this, SettingActivity.class));
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_items,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (backPressKey)
        {
            super.onBackPressed();
        }
        else
        {
            backPressKey = true;
            Toast.makeText(this, "再按一次退出！", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressKey = false;
                }
            },1500);
        }
    }

}
