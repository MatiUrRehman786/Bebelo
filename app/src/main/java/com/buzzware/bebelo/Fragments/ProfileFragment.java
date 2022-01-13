package com.buzzware.bebelo.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.buzzware.bebelo.Activities.ClaimBar;
import com.buzzware.bebelo.Activities.EditBar;
import com.buzzware.bebelo.Addapter.AdapterSectionRecycler;
import com.buzzware.bebelo.Model.Child;
import com.buzzware.bebelo.Model.SectionHeader;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.classes.Constant;
import com.buzzware.bebelo.classes.CustomProgressDialog;
import com.buzzware.bebelo.classes.SessionManager;
import com.buzzware.bebelo.databinding.DialogAddTextBinding;
import com.buzzware.bebelo.databinding.FragmentProfileBinding;
import com.buzzware.bebelo.eventBusModel.GetAllStoreEvent;
import com.buzzware.bebelo.retrofit.Controller;
import com.buzzware.bebelo.retrofit.DetailModel.BarAnounce;
import com.buzzware.bebelo.retrofit.DetailModel.BarBottleItem;
import com.buzzware.bebelo.retrofit.DetailModel.DetailModelForAddBar;
import com.buzzware.bebelo.retrofit.DetailModel.FreeTable;
import com.buzzware.bebelo.retrofit.Login.LoginResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    AdapterSectionRecycler adapterRecycler;

    List<SectionHeader> sectionHeaders;

    Context context;

    Dialog dialogNew;

    LoginResponse loginResponse = null;

    DetailModelForAddBar currentUserDetail = null;

    List<BarBottleItem> normalBottleList = new ArrayList<>();
    List<BarBottleItem> highRollerBottleList = new ArrayList<>();
    List<BarBottleItem> warTimeBottleList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater);

        Init();

        return binding.getRoot();
    }

    private void Init() {

        context = getContext();

        binding.secItem.bottleIV.setImageResource(R.drawable.bombay_drink);

        binding.secItem.nameTV.setText("Havana Club");

        binding.thirdItem.bottleIV.setImageResource(R.drawable.bombay_drink_three);

        binding.rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.rvDrinks.setHasFixedSize(true);

        SetListener();

        getCurrentUserDetail();

    }

    private void getCurrentUserDetail() {

        loginResponse = SessionManager.getInstance().getUser(getContext());

        currentUserDetail = new Gson().fromJson(loginResponse.getResult().getBdetail(), DetailModelForAddBar.class);

        if (currentUserDetail.getFreeTable() != null) {

            if (currentUserDetail.getFreeTable().isFreeTable()) {

                binding.freeTableCB.setChecked(true);
                SetSecLayView(true);

            }

        }

        if (currentUserDetail.getBarAnounce() != null) {

            if (currentUserDetail.getBarAnounce().getName() != null && !currentUserDetail.getBarAnounce().getName().isEmpty()) {

                setAnnouncementLayout(true, currentUserDetail.getBarAnounce().getName());

            }

        }

        if (loginResponse.getResult().getImgeUrl() != null && !loginResponse.getResult().getImgeUrl().equals("")) {

            Picasso.with(getContext()).load(loginResponse.getResult().getImgeUrl()).error(R.drawable.thumbnail_image).into(binding.mainBarIV);

        }

        if (currentUserDetail.getBarBottle() != null) {

            binding.firstItem.bottleIV.setImageResource(R.drawable.tanquaray_small);
            binding.secItem.bottleIV.setImageResource(R.drawable.beefeater_small);
            binding.thirdItem.bottleIV.setImageResource(R.drawable.brugal_small);

            binding.firstItem.nameTV.setText("Tanqueray");
            binding.secItem.nameTV.setText("Beefeater");
            binding.thirdItem.nameTV.setText("Brugal Añejo");

            for (BarBottleItem barBottleItem : currentUserDetail.getBarBottle()) {

                if (barBottleItem.getDrinkName().equals("Tanqueray")) {

                    if (barBottleItem.getDrinkPrice().contains("-"))
                        binding.firstItem.amountTV.setText("-");
                    else
                        binding.firstItem.amountTV.setText("€ " + barBottleItem.getDrinkPrice());

                }
                if (barBottleItem.getDrinkName().equals("Beefeater")) {
                    if (barBottleItem.getDrinkPrice().contains("-"))
                        binding.secItem.amountTV.setText("-");
                    else
                        binding.secItem.amountTV.setText("€ " + barBottleItem.getDrinkPrice());

                }
                if (barBottleItem.getDrinkName().equals("Brugal Añejo")) {
                    if (barBottleItem.getDrinkPrice().contains("-"))
                        binding.thirdItem.amountTV.setText("-");
                    else
                        binding.thirdItem.amountTV.setText("€ " + barBottleItem.getDrinkPrice());

                }
                if (barBottleItem.getDrinkName().equals("Caña")) {
                    if (barBottleItem.getDrinkPrice().contains("-"))
                        binding.canaPriceTV.setText("-");
                    else
                        binding.canaPriceTV.setText("€ " + barBottleItem.getDrinkPrice());

                }
                if (barBottleItem.getDrinkName().equals("Doble")) {
                    if (barBottleItem.getDrinkPrice().contains("-"))
                        binding.doblePriceTV.setText("-");
                    else
                        binding.doblePriceTV.setText("€ " + barBottleItem.getDrinkPrice());

                }

            }

            getBottleList();


        }
    }

    private void getBottleList() {

        if (currentUserDetail.getBarBottle() != null) {

            String normal = Constant.normals;
            String highRoller = Constant.highRoller;
            String warTime = Constant.wartime;

            normalBottleList.clear();
            highRollerBottleList.clear();
            warTimeBottleList.clear();

            for (BarBottleItem barBottleItem : currentUserDetail.getBarBottle()) {

                if (normal.contains(barBottleItem.getDrinkName())) {
                    normalBottleList.add(barBottleItem);
                } else if (highRoller.contains(barBottleItem.getDrinkName())) {
                    highRollerBottleList.add(barBottleItem);
                } else if (warTime.contains(barBottleItem.getDrinkName())) {
                    warTimeBottleList.add(barBottleItem);
                }

            }

            setRecyclerView();

        }

    }

    private void SetListener() {

        binding.AnunciarET.setOnClickListener(v -> {

            showDialogText();

        });

        binding.firstRectLay.setOnClickListener(v -> {

            showDialogText();

        });

        binding.plusBT.setOnClickListener(v -> {

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


                BarAnounce barAnounce = new BarAnounce();
                barAnounce = null;
                currentUserDetail.setBarAnounce(barAnounce);

                Gson gson = new Gson();
                String json = gson.toJson(currentUserDetail);
                loginResponse.getResult().setBdetail(json);
                updateBarNow(loginResponse);

                EventBus.getDefault().post(new GetAllStoreEvent());

            }
        });

        binding.freeTableCB.setOnCheckedChangeListener((view, isChecked) -> {

            FreeTable freeTable = new FreeTable();

            if (isChecked) {

                EventBus.getDefault().post(new GetAllStoreEvent());

                freeTable.setFreeTable(true);

            } else {

                EventBus.getDefault().post(new GetAllStoreEvent());
                freeTable.setFreeTable(false);

            }

            freeTable.setDate(System.currentTimeMillis());
            currentUserDetail.setFreeTable(freeTable);
            Gson gson = new Gson();
            String json = gson.toJson(currentUserDetail);
            loginResponse.getResult().setBdetail(json);
            updateBarNow(loginResponse);


            SetSecLayView(isChecked);

        });

        binding.appBar.editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditBar.class));
            }
        });

    }

    public long getCurrentTimeStamp() {
        long tsLong = System.currentTimeMillis();
        return tsLong;

    }

    private void SetSecLayView(boolean isChecked) {
        if (isChecked) {
            binding.btnRedGlow.setVisibility(View.VISIBLE);
        } else {
            binding.btnRedGlow.setVisibility(View.INVISIBLE);
        }
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.SheetDialogMargin);
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        btnClose = view.findViewById(R.id.btnClose);
        btnYourClaim = view.findViewById(R.id.myBarCalinBtn);
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

    public void setRecyclerView() {

        sectionHeaders = new ArrayList<>();

        if (normalBottleList.size() > 0)
            sectionHeaders.add(new SectionHeader(normalBottleList, "Normales", 2));

        if (highRollerBottleList.size() > 0)
            sectionHeaders.add(new SectionHeader(highRollerBottleList, "High roller", 1));

        if (warTimeBottleList.size() > 0)
            sectionHeaders.add(new SectionHeader(warTimeBottleList, "War time", 3));

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

                if (!dialogBinding.textET.getText().toString().equals("")) {

                    setAnnouncementLayout(true, dialogBinding.textET.getText().toString());

                    BarAnounce barAnounce = new BarAnounce();
                    barAnounce.setName(dialogBinding.textET.getText().toString());
                    barAnounce.setDate(System.currentTimeMillis());
                    currentUserDetail.setBarAnounce(barAnounce);

                    Gson gson = new Gson();
                    String json = gson.toJson(currentUserDetail);
                    loginResponse.getResult().setBdetail(json);
                    updateBarNow(loginResponse);

                    EventBus.getDefault().post(new GetAllStoreEvent());


                } else {

                    setAnnouncementLayout(false, "Announce something!");
                    BarAnounce barAnounce = new BarAnounce();
                    barAnounce = null;
                    currentUserDetail.setBarAnounce(barAnounce);

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
                binding.btnBlueGlow.setVisibility(View.INVISIBLE);
            }
        });

        dialogNew.show();

    }

    public void setAnnouncementLayout(boolean check, String title) {

        if (check) {

            binding.anunciarLay.setVisibility(View.GONE);
            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.plusBT.setVisibility(View.INVISIBLE);
            binding.AnunciarET.setText(title);
            binding.btnBlueGlow.setVisibility(View.VISIBLE);

        } else {

            binding.AnunciarET.setText(title);
            binding.anunciarLay.setVisibility(View.GONE);
            binding.btnDelete.setVisibility(View.GONE);
            binding.plusBT.setVisibility(View.VISIBLE);
            binding.btnBlueGlow.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentUserDetail();
    }

    private void updateBarNow(LoginResponse loginResponse) {
        CustomProgressDialog.getInstance(getActivity()).showProgressDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", loginResponse.getResult().getId())
                .addFormDataPart("cname", loginResponse.getResult().getCname())
                .addFormDataPart("cphone", loginResponse.getResult().getCphone())
                .addFormDataPart("image", loginResponse.getResult().getImgeUrl())
                .addFormDataPart("bname", loginResponse.getResult().getBname())
                .addFormDataPart("baddress", loginResponse.getResult().getBaddress())
                .addFormDataPart("blat", loginResponse.getResult().getBlat())
                .addFormDataPart("blng", loginResponse.getResult().getBlng())
                .addFormDataPart("bdetail", loginResponse.getResult().getBdetail())
                .build();

        Controller.getApi().updateBar(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        CustomProgressDialog.getInstance(getActivity()).dismissProgressDialog();

                        if (response.body() != null) {

                            try {

                                LoginResponse loginResponse = new Gson().fromJson(response.body(), LoginResponse.class);

                                if (loginResponse.getSuccess() == 1) {

                                    Gson gson = new Gson();

                                    String jsonData = gson.toJson(loginResponse);

                                    SessionManager.getInstance().setUser(context, loginResponse);

                                    Log.d("updateResponse", jsonData);

                                    getCurrentUserDetail();


                                } else {

                                    Toast.makeText(context, "updated Failed! " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {

                                e.printStackTrace();

                                Log.d("updateResponse", "catch exception");

                                Toast.makeText(context, "updated Failed! " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("appRegisterResponse", "onFailure exception" + t.getLocalizedMessage());

                        CustomProgressDialog.getInstance(getActivity()).dismissProgressDialog();

                    }
                });

    }


}