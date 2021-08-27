package com.buzzware.bebelo.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.buzzware.bebelo.Activities.AddBar;
import com.buzzware.bebelo.Activities.BarLogin;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.databinding.FragmentSettingsBinding;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;
    Context context;
    SharedPreferences pref;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= FragmentSettingsBinding.inflate(inflater);
        pref = getContext().getSharedPreferences("MyPref", 0);
        if(pref.getString("checkLocation",null)!=null){
            if(pref.getString("checkLocation",null).equals("allow")){
                binding.locationRL.setVisibility(View.GONE);
            }else{
                binding.locationRL.setVisibility(View.VISIBLE);
            }
        }
        Init();
        return binding.getRoot();
    }

    private void Init() {
        context= getContext();
        SetListener();
    }

    private void SetListener() {
        binding.locationAllowSetting.setOnClickListener(v->{
            SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("checkLocation", "allow");
            editor.commit();
            binding.locationRL.setVisibility(View.GONE);
        });
        binding.addBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddBar.class));
            }
        });
        binding.loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BarLogin.class));
            }
        });
        binding.roofTopCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));
                    binding.roofTopTV.setTextColor(getResources().getColor(R.color.white));
                   // binding.roofTopTV.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.white));
                }else{
                    binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.white));
                    binding.roofTopTV.setTextColor(getResources().getColor(R.color.light_black));
                 //   binding.roofTopTV.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.light_black));
                }
            }
        });
        binding.barWithTerraceCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));
                    binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.white));
                }else{
                    binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.white));
                    binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.light_black));
                }
            }
        });
        binding.instagramRL.setOnClickListener(v->{
            Uri uri = Uri.parse("https://www.instagram.com/bebelo.es/");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/bebelo.es/")));
            }
        });
    }
}