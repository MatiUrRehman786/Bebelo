package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.buzzware.bebelo.databinding.ActivityAddBarBinding;
import com.buzzware.bebelo.databinding.ActivityBarLoginBinding;

public class BarLogin extends AppCompatActivity {

    ActivityBarLoginBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityBarLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Init();
    }

    private void Init() {
        context= this;
        SetListener();
    }

    private void SetListener() {
        binding.btnAddBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddBar.class));
            }
        });
        binding.appBar.backIV.setOnClickListener(v->{
            Intent intent=new Intent(context,Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        binding.btnLogin.setOnClickListener(v->{
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("checkLogin", "login");
            editor.commit();

            Intent intent=new Intent(context,Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(context,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}