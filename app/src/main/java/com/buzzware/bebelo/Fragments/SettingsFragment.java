package com.buzzware.bebelo.Fragments;

import android.Manifest;
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
import android.widget.Toast;

import com.buzzware.bebelo.Activities.AddBar;
import com.buzzware.bebelo.Activities.BarLogin;
import com.buzzware.bebelo.Activities.EditBar;
import com.buzzware.bebelo.Activities.Home;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.classes.Constant;
import com.buzzware.bebelo.classes.SessionManager;
import com.buzzware.bebelo.databinding.FragmentSettingsBinding;
import com.buzzware.bebelo.retrofit.Login.LoginResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;

    Context context;

    SharedPreferences pref;

    LoginResponse loginResponse=null;

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

            } else {

                binding.locationRL.setVisibility(View.VISIBLE);

            }
        }

        Init();

        setListener();


        return binding.getRoot();
    }

    private void setListener() {

        binding.locationAllowSetting.setOnClickListener(v->{
            checkCameraPermission();
        });

    }

    private void checkCameraPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        Permissions.check(getContext(), permission, "Location permissions Required", null, new PermissionHandler() {
            @Override
            public void onGranted() {
                SharedPreferences pref1 = getContext().getSharedPreferences("MyPref", 0);

                SharedPreferences.Editor editor = pref1.edit();

                editor.putString("checkLocation", "allow");

                editor.commit();

                binding.locationRL.setVisibility(View.GONE);

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                SharedPreferences pref1 = getContext().getSharedPreferences("MyPref", 0);

                SharedPreferences.Editor editor = pref1.edit();

                editor.putString("checkLocation", "not allow");

                editor.commit();

                binding.locationRL.setVisibility(View.VISIBLE);

            }
        });
    }
    private void Init() {

        context= getContext();

        setView();

        SetListener();


    }

    private void setView() {

        loginResponse = SessionManager.getInstance().getUser(context);

        if(loginResponse!=null){

            binding.addBarAndLoginView.setVisibility(View.GONE);

            binding.simpleBarView.setVisibility(View.VISIBLE);

            binding.simpleBaeTV.setText(loginResponse.getResult().getBname());

            if(loginResponse.getResult().getImgeUrl()!=null && !loginResponse.getResult().getImgeUrl().equals("")){
               Picasso.with(context).load(loginResponse.getResult().getImgeUrl()).error(R.drawable.thumbnail_image).into(binding.simpleBarImage);

            }

        } else {

            binding.addBarAndLoginView.setVisibility(View.VISIBLE);

            binding.simpleBarView.setVisibility(View.GONE);

        }

        if(SessionManager.getInstance().getFilter(context)!=null){
            if(SessionManager.getInstance().getFilter(context).equals("Both")){

                binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                binding.roofTopTV.setTextColor(getResources().getColor(R.color.white));

                binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.white));

                binding.roofTopCB.setChecked(true);

                binding.barWithTerraceCB.setChecked(true);


            } else if(SessionManager.getInstance().getFilter(context).equals("Rooftop")){


                binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                binding.roofTopTV.setTextColor(getResources().getColor(R.color.white));

                binding.roofTopCB.setChecked(true);


            }else if(SessionManager.getInstance().getFilter(context).equals("BarWithTerrace")){

                binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.white));

                binding.barWithTerraceCB.setChecked(true);

            }
        }
    }

    private void SetListener() {

        binding.locationAllowSetting.setOnClickListener(v->{

            SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0);

            SharedPreferences.Editor editor = pref.edit();

            editor.putString("checkLocation", "allow");

            editor.commit();

            binding.locationRL.setVisibility(View.GONE);

        });

        binding.addBar.setOnClickListener(v -> startActivity(new Intent(context, AddBar.class)));

        binding.loginTV.setOnClickListener(v -> startActivity(new Intent(context, BarLogin.class)));

        binding.roofTopCB.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){

                binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                binding.roofTopTV.setTextColor(getResources().getColor(R.color.white));

                if(binding.barWithTerraceCB.isChecked()){

                    SessionManager.getInstance().setFilter(context,"Both");

                } else {

                    SessionManager.getInstance().setFilter(context,"Rooftop");

                }


            } else {

                binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.white));

                binding.roofTopTV.setTextColor(getResources().getColor(R.color.light_black));

                if(binding.barWithTerraceCB.isChecked()){

                    SessionManager.getInstance().setFilter(context,"BarWithTerrace");

                } else {

                    SessionManager.getInstance().setFilter(context,"No");

                }

            }
        });

        binding.barWithTerraceCB.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){

                binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.white));

                if(binding.roofTopCB.isChecked()){

                    SessionManager.getInstance().setFilter(context,"Both");


                } else {

                    SessionManager.getInstance().setFilter(context,"BarWithTerrace");


                }


            } else {

                binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.white));

                binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.light_black));

                if(binding.roofTopCB.isChecked()){

                    SessionManager.getInstance().setFilter(context,"Rooftop");


                } else {

                    SessionManager.getInstance().setFilter(context,"No");


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