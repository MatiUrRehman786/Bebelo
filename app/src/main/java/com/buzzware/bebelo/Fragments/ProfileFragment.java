package com.buzzware.bebelo.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buzzware.bebelo.Activities.BarProfile;
import com.buzzware.bebelo.Activities.ClaimBar;
import com.buzzware.bebelo.Activities.EditBar;
import com.buzzware.bebelo.Addapter.AdapterSectionRecycler;
import com.buzzware.bebelo.Model.Child;
import com.buzzware.bebelo.Model.SectionHeader;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.databinding.DialogAddTextBinding;
import com.buzzware.bebelo.databinding.DialogAllowLocationBinding;
import com.buzzware.bebelo.databinding.FragmentProfileBinding;
import com.buzzware.bebelo.databinding.FragmentSettingsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    AdapterSectionRecycler adapterRecycler;
    List<SectionHeader> sectionHeaders;
    Context context;
    Dialog dialogNew;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentProfileBinding.inflate(inflater);
        Init();
        return binding.getRoot();
    }
    private void Init() {
        context= getContext();
        binding.secItem.bottleIV.setImageResource(R.drawable.bombay_drink);
        binding.secItem.nameTV.setText("Havana Club");
        binding.thirdItem.bottleIV.setImageResource(R.drawable.bombay_drink_three);
        binding.rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDrinks.setHasFixedSize(true);
        SetRecyclerView();

        ///setClick
        SetListener();
    }
    private void SetListener() {
        binding.AnunciarET.setOnClickListener(v->{
            showDialogText();
        });
        binding.firstRectLay.setOnClickListener(v->{
            showDialogText();
        });
        binding.plusBT.setOnClickListener(v->{
            showDialogText();
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
                binding.AnunciarET.setText("Announce something!");
                binding.btnBlueGlow.setVisibility(View.INVISIBLE);
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
                startActivity(new Intent(getContext(), EditBar.class));
            }
        });
    }

    private void SetSecLayView(boolean isChecked) {
        if(isChecked){
            binding.btnRedGlow.setVisibility(View.VISIBLE);
        }else{
            binding.btnRedGlow.setVisibility(View.INVISIBLE);        }
    }

    private void SetDeleteView() {
        binding.firstRectLay.setBackground(getResources().getDrawable(R.drawable.rectangle_white));
        binding.anunciarLay.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.plusBT.setVisibility(View.VISIBLE);
        binding.AnunciarET.setText("");
    }

    private void SetAnunciarView() {
        binding.firstRectLay.setBackground(getResources().getDrawable(R.drawable.rectangle_blue));
        binding.anunciarLay.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.VISIBLE);
    }

    private void OpenBottomDialog() {
        AppCompatButton btnClose;
        RelativeLayout btnYourClaim;
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(getContext(), R.style.SheetDialogMargin);
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
                startActivity(new Intent(getContext(), ClaimBar.class));
            }
        });
    }

    public void SetRecyclerView(){
        //Create a List of Child DataModel
        List<Child> childList = new ArrayList<>();
        childList.add(new Child("Caña", "\u20ac7", R.drawable.list_image1));
        childList.add(new Child("Doble", "\u20ac7", R.drawable.list_image2));
        //Create a List of SectionHeader DataModel implements SectionHeader
        sectionHeaders = new ArrayList<>();
        sectionHeaders.add(new SectionHeader(childList, "Cerveza", 4));

        childList = new ArrayList<>();
        childList.add(new Child("Absolut Vodka", "\u20ac7", R.drawable.list_image3));
        childList.add(new Child("Havana Club", "\u20ac7", R.drawable.list_image4));
        childList.add(new Child("Jameson", "\u20ac7", R.drawable.list_image5));
        childList.add(new Child("Bombay Sapphire", "\u20ac7", R.drawable.list_image6));
        childList.add(new Child("Tanqueray", "\u20ac7", R.drawable.list_image7));
        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 2));

        childList = new ArrayList<>();
        childList.add(new Child("Brugal Anejo", "\u20ac7", R.drawable.list_image8));
        childList.add(new Child("Nordés", "\u20ac7", R.drawable.list_image9));
        sectionHeaders.add(new SectionHeader(childList, "High roller", 1));

        childList = new ArrayList<>();
        childList.add(new Child("Larios", "\u20ac7", R.drawable.list_image10));
        childList.add(new Child("Captain Morgan", "\u20ac7", R.drawable.list_image11));
        sectionHeaders.add(new SectionHeader(childList, "Copas subnormales", 3));


        adapterRecycler = new AdapterSectionRecycler(getContext(), sectionHeaders);
        binding.rvDrinks.setAdapter(adapterRecycler);
    }

    private void showDialogText() {

        dialogNew = new Dialog(getContext(), R.style.Theme_AppCompat_Dialog_Alert);
        dialogNew.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogNew.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNew.setCancelable(true);
        final DialogAddTextBinding dialogBinding = DialogAddTextBinding.inflate(LayoutInflater.from(getContext()));
        dialogNew.setContentView(dialogBinding.getRoot());
        dialogBinding.cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNew.dismiss();
                binding.plusBT.setVisibility(View.VISIBLE);
                binding.anunciarLay.setVisibility(View.INVISIBLE);
            }
        });
        dialogBinding.doneBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNew.dismiss();

                if(!dialogBinding.textET.getText().toString().equals("")){
                    binding.anunciarLay.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.VISIBLE);
                    binding.plusBT.setVisibility(View.INVISIBLE);
                    binding.AnunciarET.setText(dialogBinding.textET.getText().toString());
                    binding.btnBlueGlow.setVisibility(View.VISIBLE);
                }else{
                    binding.AnunciarET.setText("Announce something!");
                    binding.anunciarLay.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.GONE);
                    binding.plusBT.setVisibility(View.VISIBLE);
                    binding.btnBlueGlow.setVisibility(View.INVISIBLE);
                }
            }
        });
        dialogBinding.cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.AnunciarET.setText("Announce something!");
                binding.anunciarLay.setVisibility(View.GONE);
                binding.btnDelete.setVisibility(View.GONE);
                binding.plusBT.setVisibility(View.VISIBLE);
                binding.btnBlueGlow.setVisibility(View.INVISIBLE);            }
        });

        dialogNew.show();

    }

}