package com.buzzware.bebelo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buzzware.bebelo.Addapter.AdapterSectionRecycler;
import com.buzzware.bebelo.Model.Child;
import com.buzzware.bebelo.Model.SectionHeader;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.RelativeLayoutTouchListener;

import com.buzzware.bebelo.databinding.ActivityBarDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import static android.view.DragEvent.ACTION_DRAG_STARTED;
import static android.view.DragEvent.ACTION_DROP;

public class BarDetail extends AppCompatActivity {

    ActivityBarDetailBinding binding;

    String title="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityBarDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Init();

        getDataFromIntent();

    }
    private void getDataFromIntent() {

        Intent intent = getIntent();

        title = intent.getStringExtra("title");

        if (title.equals("1"))

            binding.mainIV.setImageResource(R.drawable.bar_image1);

        if (title.equals("2"))

            binding.mainIV.setImageResource(R.drawable.bar_image2);

        if (title.equals("3"))

            binding.mainIV.setImageResource(R.drawable.bar_image3);
    }

    private void Init() {

        binding.secItem.bottleIV.setImageResource(R.drawable.bombay_drink);

        binding.thirdItem.bottleIV.setImageResource(R.drawable.bombay_drink_three);

        binding.rvDrinks.setLayoutManager(new LinearLayoutManager(this));

        binding.rvDrinks.setHasFixedSize(true);

        SetListeners();

    }

    private void SetListeners() {

        binding.appBar.backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

                overridePendingTransition( R.anim.slide_in_up_2, R.anim.slide_out_up );

            }
        });

        binding.appBar.menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenBottomDialog();
            }
        });

        binding.bottomLay.setOnClickListener(v -> {

            binding.rvDrinks.setVisibility(View.VISIBLE);

            binding.bottomLay.setVisibility(View.GONE);

            binding.bottomLayLess.setVisibility(View.VISIBLE);

        });

        binding.bottomLayLess.setOnClickListener(v -> {

            binding.rvDrinks.setVisibility(View.GONE);

            binding.bottomLay.setVisibility(View.VISIBLE);

            binding.bottomLayLess.setVisibility(View.GONE);

        });
    }

    private void OpenBottomDialog() {

        TextView btnClose;

        LinearLayout flag;

        RelativeLayout btnYourClaim;

        ImageView flagIV;

        TextView flagTV;

        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(BarDetail.this, R.style.SheetDialogMargin);

        View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null);

        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.show();

        btnClose= view.findViewById(R.id.btnClose);

        btnYourClaim= view.findViewById(R.id.myBarCalinBtn);

        flag= view.findViewById(R.id.flagDataBtn);

        flagIV= view.findViewById(R.id.flagIV);

        flagTV= view.findViewById(R.id.flagTV);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flagIV.setImageResource(R.drawable.flag_red);

                flagTV.setText("Unflag that data is wrong");

            }
        });

        btnYourClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarDetail.this, ClaimBar.class));
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition( R.anim.slide_in_up_2, R.anim.slide_out_up );

    }

}