package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.buzzware.bebelo.classes.Constant;
import com.buzzware.bebelo.databinding.ActivityClaimBarBinding;

public class ClaimBar extends AppCompatActivity {

    ActivityClaimBarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClaimBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();

    }

    private void setListener() {

        binding.appBar.backIV.setOnClickListener(v -> {

            finish();

        });

        binding.goToSettingTV.setOnClickListener(v -> {

            Constant.loadSettingFragment=true;

            finish();

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}