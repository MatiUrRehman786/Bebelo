package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.buzzware.bebelo.Fragments.ExploreFragment;
import com.buzzware.bebelo.Fragments.ProfileFragment;
import com.buzzware.bebelo.Fragments.SettingsFragment;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    public static ActivityHomeBinding binding;

    SharedPreferences pref;

    boolean checkExploreLoaded=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkExploreLoaded=true;

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        if(pref.getString("checkLogin",null)!=null){

            if(pref.getString("checkLogin",null).equals("login")){

                binding.btnProfile.setVisibility(View.VISIBLE);

                binding.btnDummy.setVisibility(View.GONE);

            } else {

                binding.btnProfile.setVisibility(View.GONE);

                binding.btnDummy.setVisibility(View.VISIBLE);

            }
        } else {

            binding.btnProfile.setVisibility(View.GONE);

            binding.btnDummy.setVisibility(View.VISIBLE);

        }

        Init();

        SetListeners();

    }

    private void SetListeners() {

        binding.btnExplore.setOnClickListener(v -> {

            checkExploreLoaded=true;

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ExploreFragment()).commit();

            SetSelectedTab(0);

        });

        binding.btnSettings.setOnClickListener(v -> {

            checkExploreLoaded=false;

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

            SetSelectedTab(1);

        });
        binding.btnProfile.setOnClickListener(v -> {

            checkExploreLoaded=false;

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();

            SetSelectedTab(2);

        });

    }

    @Override
    public void onBackPressed() {

      //  super.onBackPressed();

        ExploreFragment.checkOpen=false;

        if(checkExploreLoaded){

            finish();

        } else {
            
            checkExploreLoaded=true;

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ExploreFragment()).commit();

            SetSelectedTab(0);

        }

    }

    private void Init() {
        SetDefualtFragment();
    }

    private void SetDefualtFragment() {

        checkExploreLoaded=true;

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ExploreFragment()).commit();

        SetSelectedTab(0);

    }

    public void SetSelectedTab(int position){

        if(position == 0){

            binding.btnExplore.setBackgroundColor(getResources().getColor(R.color.white));

            binding.btnSettings.setBackgroundColor(getResources().getColor(R.color.white));

            binding.firstTabIV.setImageResource(R.drawable.new_explore_icon_fill);

            binding.secTabIV.setImageResource(R.drawable.new_setting_icon);

            binding.firstTabTV.setTextColor(getResources().getColor(R.color.greenish_blue_dark));

            binding.secTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

            binding.thirdTabIV.setImageResource(R.drawable.new_profile_icon);

            binding.thirdTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

        } else if (position == 1) {

            binding.btnExplore.setBackgroundColor(getResources().getColor(R.color.white));

            binding.btnSettings.setBackgroundColor(getResources().getColor(R.color.white));

            binding.firstTabIV.setImageResource(R.drawable.new_explore_icon);

            binding.secTabIV.setImageResource(R.drawable.new_setting_icon_fill);

            binding.firstTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

            binding.secTabTV.setTextColor(getResources().getColor(R.color.greenish_blue_dark));

            binding.thirdTabIV.setImageResource(R.drawable.new_profile_icon);

            binding.thirdTabTV.setTextColor(getResources().getColor(R.color.gray_dark2));

        } else if (position == 2) {

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

        checkExploreLoaded=true;

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        if (pref.getString("checkLogin",null)!=null) {

            if (pref.getString("checkLogin",null).equals("login")) {

                binding.btnProfile.setVisibility(View.VISIBLE);

                binding.btnDummy.setVisibility(View.GONE);

            } else {

                binding.btnProfile.setVisibility(View.GONE);

                binding.btnDummy.setVisibility(View.VISIBLE);

            }
        } else {

            binding.btnProfile.setVisibility(View.GONE);

            binding.btnDummy.setVisibility(View.VISIBLE);

        }
    }
}