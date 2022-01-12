package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.buzzware.bebelo.Fragments.ExploreFragment;
import com.buzzware.bebelo.Fragments.ProfileFragment;
import com.buzzware.bebelo.Fragments.SettingsFragment;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.classes.Constant;
import com.buzzware.bebelo.classes.SessionManager;
import com.buzzware.bebelo.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    public static ActivityHomeBinding binding;

    ExploreFragment fragment;

    boolean checkExploreLoaded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkExploreLoaded = true;

        checkUserLoginOrNot();

        Init();

        SetListeners();

    }

    private void checkUserLoginOrNot() {

        if (SessionManager.getInstance().getUser(Home.this) != null) {

            binding.btnProfile.setVisibility(View.VISIBLE);

            binding.btnDummy.setVisibility(View.GONE);

        } else {

            binding.btnProfile.setVisibility(View.GONE);

            binding.btnDummy.setVisibility(View.VISIBLE);

        }

    }

    private void SetListeners() {

        binding.btnExplore.setOnClickListener(v -> {

            if (fragment == null)
                fragment = new ExploreFragment();
            checkExploreLoaded = true;

           // getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();


            SetSelectedTab(0);

        });

        binding.btnSettings.setOnClickListener(v -> {

            checkExploreLoaded = false;

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

            SetSelectedTab(1);

        });
        binding.btnProfile.setOnClickListener(v -> {

            checkExploreLoaded = false;

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();



            SetSelectedTab(2);

        });

    }

    @Override
    public void onBackPressed() {

        //  super.onBackPressed();

        ExploreFragment.checkOpen = false;

        if (checkExploreLoaded) {

            finish();

        } else {

            checkExploreLoaded = true;

           // getSupportFragmentManager().beginTransaction().replace(R.id.container, new ExploreFragment()).commit();

            SetSelectedTab(0);

        }

    }

    private void Init() {

        SetDefaultFragment();

    }

    private void SetDefaultFragment() {

        checkExploreLoaded = true;

        fragment = new ExploreFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.containerExplore, fragment).commit();

        SetSelectedTab(0);

    }

    private void setSettingFragment() {

        checkExploreLoaded = true;

        Constant.loadSettingFragment = false;

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

        SetSelectedTab(0);

    }

    public void SetSelectedTab(int position) {

        if (position == 0) {

            binding.containerExplore.setVisibility(View.VISIBLE);
            binding.container.setVisibility(View.INVISIBLE);

            binding.btnExplore.setBackgroundColor(getResources().getColor(R.color.white));

            binding.btnSettings.setBackgroundColor(getResources().getColor(R.color.white));

            binding.firstTabIV.setImageResource(R.drawable.new_explore_icon_fill);

            binding.secTabIV.setImageResource(R.drawable.new_setting_icon);

            binding.firstTabTV.setTextColor(getResources().getColor(R.color.greenish_blue_dark));

            binding.secTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

            binding.thirdTabIV.setImageResource(R.drawable.new_profile_icon);

            binding.thirdTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

        } else if (position == 1) {

            binding.containerExplore.setVisibility(View.INVISIBLE);
            binding.container.setVisibility(View.VISIBLE);

            binding.btnExplore.setBackgroundColor(getResources().getColor(R.color.white));

            binding.btnSettings.setBackgroundColor(getResources().getColor(R.color.white));

            binding.firstTabIV.setImageResource(R.drawable.new_explore_icon);

            binding.secTabIV.setImageResource(R.drawable.new_setting_icon_fill);

            binding.firstTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

            binding.secTabTV.setTextColor(getResources().getColor(R.color.greenish_blue_dark));

            binding.thirdTabIV.setImageResource(R.drawable.new_profile_icon);

            binding.thirdTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

        } else if (position == 2) {

            binding.containerExplore.setVisibility(View.INVISIBLE);
            binding.container.setVisibility(View.VISIBLE);

            binding.btnExplore.setBackgroundColor(getResources().getColor(R.color.white));

            binding.btnSettings.setBackgroundColor(getResources().getColor(R.color.white));

            binding.firstTabIV.setImageResource(R.drawable.new_explore_icon);

            binding.secTabIV.setImageResource(R.drawable.new_setting_icon);

            binding.firstTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

            binding.secTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

            binding.thirdTabIV.setImageResource(R.drawable.new_profile_icon_fill);

            binding.thirdTabTV.setTextColor(getResources().getColor(R.color.greenish_blue_dark));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkExploreLoaded = false;

        checkUserLoginOrNot();

        if (Constant.loadSettingFragment) {

            setSettingFragment();

        }

    }
}