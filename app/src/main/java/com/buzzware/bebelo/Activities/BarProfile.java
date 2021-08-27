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
    AdapterSectionRecycler adapterRecycler;
    List<SectionHeader> sectionHeaders;
    Context context;
    boolean isAnunciarLay = false;

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

        SetRecyclerView();

        ///setClick
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
        if(isChecked){
            binding.secRectLay.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
        }else{
            binding.secRectLay.setBackground(getResources().getDrawable(R.drawable.rectangle_white));
        }
    }

    private void SetDeleteView() {
     //   binding.firstRectLay.setBackground(getResources().getDrawable(R.drawable.rectangle_white));
        binding.anunciarLay.setVisibility(View.INVISIBLE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.AnunciarET.setText("");
    }

    private void SetAnunciarView() {
      //  binding.firstRectLay.setBackground(getResources().getDrawable(R.drawable.rectangle_blue));
        binding.anunciarLay.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.VISIBLE);
    }

    private void OpenBottomDialog() {
        AppCompatButton btnClose;
        RelativeLayout btnYourClaim;
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(BarProfile.this, R.style.SheetDialogMargin);
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        btnClose= view.findViewById(R.id.btnClose);
        btnYourClaim= view.findViewById(R.id.myBarCalinBtn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        btnYourClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarProfile.this, ClaimBar.class));
            }
        });
    }

    public void SetRecyclerView(){
        //Create a List of Child DataModel
        List<Child> childList = new ArrayList<>();
        childList.add(new Child("Cerveza", "\u20ac7", R.drawable.bombay_drink_two));
        childList.add(new Child("Capas Normales", "\u20ac7", R.drawable.bombay_drink));
        childList.add(new Child("High Roler", "\u20ac7", R.drawable.bombay_drink_three));
        childList.add(new Child("Capas SubNormales", "\u20ac7", R.drawable.bombay_drink_two));

        //Create a List of SectionHeader DataModel implements SectionHeader
        sectionHeaders = new ArrayList<>();
        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 6));

        childList = new ArrayList<>();
        childList.add(new Child("Bill Gates", "\u20ac7", R.drawable.bombay_drink_two));
        childList.add(new Child("Bob Proctor", "\u20ac7", R.drawable.bombay_drink_two));
        childList.add(new Child("Bryan Tracy", "\u20ac7", R.drawable.bombay_drink_two));
        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 2));

        childList = new ArrayList<>();
        childList.add(new Child("Intruder Shanky", "\u20ac7", R.drawable.bombay_drink_two));
        childList.add(new Child("Invincible Vinod", "\u20ac7", R.drawable.bombay_drink_two));
        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 1));

        childList = new ArrayList<>();
        childList.add(new Child("Jim Carry", "\u20ac7", R.drawable.bombay_drink_two));
        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 4));

        childList = new ArrayList<>();
        childList.add(new Child("Neil Patrick Harris", "\u20ac7", R.drawable.bombay_drink_two));
        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 3));

        childList = new ArrayList<>();
        childList.add(new Child("Orange", "\u20ac7", R.drawable.bombay_drink_two));
        childList.add(new Child("Olive", "\u20ac7", R.drawable.bombay_drink_two));
        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 5));

        adapterRecycler = new AdapterSectionRecycler(this, sectionHeaders);
        binding.rvDrinks.setAdapter(adapterRecycler);
    }
}