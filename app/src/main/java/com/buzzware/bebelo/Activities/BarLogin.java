package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.buzzware.bebelo.R;
import com.buzzware.bebelo.classes.CustomProgressDialog;
import com.buzzware.bebelo.classes.SessionManager;
import com.buzzware.bebelo.databinding.ActivityBarLoginBinding;
import com.buzzware.bebelo.databinding.AlertDialogContactUsBinding;
import com.buzzware.bebelo.retrofit.Controller;
import com.buzzware.bebelo.retrofit.Login.LoginResponse;
import com.google.gson.Gson;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarLogin extends AppCompatActivity {

    ActivityBarLoginBinding binding;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBarLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Init();
    }

    private void Init() {

        context = this;
        SetListener();

    }

    private void SetListener() {

        binding.btnAddBar.setOnClickListener(v -> startActivity(new Intent(context, AddBar.class)));

        binding.appBar.backIV.setOnClickListener(v -> {

            Intent intent = new Intent(context, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

        binding.btnLogin.setOnClickListener(v -> {
            if (isValid()) {
                loginNow();
            }
        });

        binding.forgotPasswordTV.setOnClickListener(v->{
            showContactUsDialog();
        });

    }

    private void showContactUsDialog() {

        final Dialog dialog = new Dialog(this, R.style.DialogTheme);

        dialog.setCancelable(true);

        AlertDialogContactUsBinding contactUsBinding = AlertDialogContactUsBinding.inflate(getLayoutInflater());

        dialog.setContentView(contactUsBinding.getRoot());

        contactUsBinding.exitIconIV.setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();

    }


    private void loginNow() {

        CustomProgressDialog.getInstance(BarLogin.this).showProgressDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_email", binding.emailET.getText().toString())
                .addFormDataPart("user_password", binding.passwordET.getText().toString())
                .build();

        Controller.getApi().appLogin(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        CustomProgressDialog.getInstance(BarLogin.this).dismissProgressDialog();

                        if (response.body() != null) {

                            try {

                                LoginResponse loginResponse = new Gson().fromJson(response.body(), LoginResponse.class);

                                if (loginResponse.getSuccess() == 1) {

                                    SessionManager.getInstance().setUser(BarLogin.this, loginResponse);

                                    Toast.makeText(BarLogin.this, "Login Successfully!", Toast.LENGTH_SHORT).show();

                                    finish();

                                    Intent intent = new Intent(context, Home.class);

                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(intent);

                                } else {

                                    Toast.makeText(BarLogin.this, "Login Failed! "+loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                }

                                Gson gson = new Gson();

                                String jsonData = gson.toJson(loginResponse);

                                Log.d("appRegisterResponse", jsonData);

                            } catch (Exception e) {

                                e.printStackTrace();

                                Toast.makeText(BarLogin.this, "Login Failed! Invalid User", Toast.LENGTH_SHORT).show();

                                Log.d("appRegisterResponse", "catch exception" + e.getLocalizedMessage());

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("appRegisterResponse", "onFailure exception" + t.getLocalizedMessage());

                        CustomProgressDialog.getInstance(BarLogin.this).dismissProgressDialog();

                    }
                });


    }

    private boolean isValid() {

        if (binding.emailET.getText().toString().isEmpty()) {

            Toast.makeText(BarLogin.this, "Please enter your email and password!", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (binding.passwordET.getText().toString().isEmpty()) {

            Toast.makeText(BarLogin.this, "Please enter your email and password!", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (!binding.emailET.getText().toString().contains("@")) {

            Toast.makeText(BarLogin.this, "Please enter enter valid email!", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

        Intent intent = new Intent(context, Home.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }

}