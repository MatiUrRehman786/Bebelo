package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buzzware.bebelo.Addapter.AdapterSectionRecycler;
import com.buzzware.bebelo.Model.Child;
import com.buzzware.bebelo.Model.SectionHeader;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.databinding.ActivityBarProfileBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

public class BarProfile extends AppCompatActivity {

    ActivityBarProfileBinding binding;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityBarProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Init();

    }

    private void Init() {

        context= this;

        binding.secItem.bottleIV.setImageResource(R.drawable.bombay_drink);

        binding.thirdItem.bottleIV.setImageResource(R.drawable.bombay_drink_three);

        binding.rvDrinks.setLayoutManager(new LinearLayoutManager(this));

        binding.rvDrinks.setHasFixedSize(true);

        SetListener();

    }

    private void SetListener() {

        binding.AnunciarET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    binding.anunciarLay.setVisibility(View.VISIBLE);

                    return true;

                }

                return false;

            }
        });

        binding.anunciarLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetAnunciarView();
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDeleteView();
            }
        });

        binding.switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SetSecLayView(isChecked);
            }
        });

        binding.appBar.editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarProfile.this, EditBar.class));
            }
        });

    }

    private void SetSecLayView(boolean isChecked) {

        if (isChecked){

            binding.secRectLay.setBackground(getResources().getDrawable(R.drawable.rectangle_red));

        } else {

            binding.secRectLay.setBackground(getResources().getDrawable(R.drawable.rectangle_white));

        }
    }

    private void SetDeleteView() {

        binding.anunciarLay.setVisibility(View.INVISIBLE);

        binding.btnDelete.setVisibility(View.GONE);

        binding.AnunciarET.setText("");

    }

    private void SetAnunciarView() {

        binding.anunciarLay.setVisibility(View.GONE);

        binding.btnDelete.setVisibility(View.VISIBLE);

    }
}