package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.buzzware.bebelo.databinding.ActivityClaimBarBinding;

public class ClaimBar extends AppCompatActivity {

    ActivityClaimBarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityClaimBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();

    }

    private void setListener() {
        binding.appBar.backIV.setOnClickListener(v->{
//            Intent intent=new Intent(ClaimBar.this,Home.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent=new Intent(ClaimBar.this,Home.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
        finish();
    }
}