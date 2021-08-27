package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.buzzware.bebelo.R;
import com.buzzware.bebelo.databinding.ActivityAddBarBinding;

public class AddBar extends AppCompatActivity {

    ActivityAddBarBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddBarBinding.inflate(getLayoutInflater());
        Init();
        setContentView(binding.getRoot());
    }

    private void Init() {
        context= AddBar.this;
        SetListener();
    }

    private void SetListener() {
        binding.appBar.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("checkLogin", "login");
                editor.commit();


//                Intent intent=new Intent(context,Home.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
                finish();
            }
        });
        binding.appBar.backIV.setOnClickListener(v->{
            finish();
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
    }
}