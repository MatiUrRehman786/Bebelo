package com.buzzware.bebelo.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.buzzware.bebelo.Addapter.HighRollerBottleAdapter;
import com.buzzware.bebelo.Addapter.HoursAdapter;
import com.buzzware.bebelo.Addapter.NormalBottleAdapter;
import com.buzzware.bebelo.Addapter.WarTimeBottleAdapter;
import com.buzzware.bebelo.Model.BottleModelForEdit;
import com.buzzware.bebelo.Model.HourModel;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.classes.Constant;
import com.buzzware.bebelo.classes.CustomProgressDialog;
import com.buzzware.bebelo.classes.SessionManager;
import com.buzzware.bebelo.databinding.ActivityEditBarBinding;
import com.buzzware.bebelo.databinding.AlertDialogContactUsBinding;
import com.buzzware.bebelo.databinding.AlertDialogDeleteBarBinding;
import com.buzzware.bebelo.databinding.DialogSelectCameraOrGalleryBinding;
import com.buzzware.bebelo.interfaces.EditPricesCallback;
import com.buzzware.bebelo.interfaces.HoursCallback;
import com.buzzware.bebelo.retrofit.Controller;
import com.buzzware.bebelo.retrofit.Delete.DeleteBarResponse;
import com.buzzware.bebelo.retrofit.DetailModel.BarBottleItem;
import com.buzzware.bebelo.retrofit.DetailModel.BarHasItem;
import com.buzzware.bebelo.retrofit.DetailModel.BarSupliment;
import com.buzzware.bebelo.retrofit.DetailModel.BarWeekDayItem;
import com.buzzware.bebelo.retrofit.DetailModel.DetailModelForAddBar;

import com.buzzware.bebelo.retrofit.GetAllStore.ResultItem;
import com.buzzware.bebelo.retrofit.Login.LoginResponse;
import com.buzzware.bebelo.retrofit.Update.UpdateBarResponse;
import com.buzzware.bebelo.retrofit.response.AppRegisterResponse.AppRegisterResponse;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBar extends AppCompatActivity implements EditPricesCallback, HoursCallback {

    ActivityEditBarBinding binding;

    Context context;

    LoginResponse loginResponse = null;

    DetailModelForAddBar currentUserDetail = null;

    List<HourModel> hoursList = new ArrayList<>();

    HoursAdapter adapter;

    private int REQUEST_IMAGE_CAPTURE = 1;

    private int REQUEST_PICK_IMAGE = 2;

    Uri imageUri = null;

    public static LatLng selectedLocationLatLng = null;

    String base64Image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Init();

    }

    private void Init() {

        context = this;

        loadDataInConstantList();

        SetListener();

        getCurrentUserDetail();

    }

    private void loadDataInConstantList() {

        Constant.normalArray.clear();
        Constant.highRollerArray.clear();
        Constant.warTimeArray.clear();

        List<BottleModelForEdit> array = new ArrayList<>();

        String[] data = Constant.normals.split(",");

        int imageCount = 6;

        for (int i = 0; i < data.length; i++) {

            array.add(new BottleModelForEdit(data[i], "", "d" + imageCount));
            imageCount++;

        }
        Constant.normalArray = array;

        array = new ArrayList<>();
        data = Constant.highRoller.split(",");

        for (int i = 0; i < data.length; i++) {

            array.add(new BottleModelForEdit(data[i], "", "d" + imageCount));
            imageCount++;

        }
        Constant.highRollerArray = array;

        array = new ArrayList<>();
        data = Constant.wartime.split(",");

        for (int i = 0; i < data.length; i++) {

            array.add(new BottleModelForEdit(data[i], "", "d" + imageCount));
            imageCount++;

        }
        Constant.warTimeArray = array;

        setRecyclerView();

    }

    private void getCurrentUserDetail() {

        loginResponse = SessionManager.getInstance().getUser(EditBar.this);

        currentUserDetail = new Gson().fromJson(loginResponse.getResult().getBdetail(), DetailModelForAddBar.class);

        if (loginResponse.getResult().getImgeUrl() != null && !loginResponse.getResult().getImgeUrl().equals("")) {

            Picasso.with(EditBar.this).load(loginResponse.getResult().getImgeUrl()).error(R.drawable.thumbnail_image).into(binding.mainBarIV);

        }

        selectedLocationLatLng = new LatLng(Double.parseDouble(loginResponse.getResult().getBlat()),Double.parseDouble(loginResponse.getResult().getBlng()));

        if (currentUserDetail.getBarBottle() != null) {


            for (BarBottleItem barBottleItem : currentUserDetail.getBarBottle()) {

                if (barBottleItem.getDrinkName().equals("Caña")) {

                    binding.canaPriceET.setText(barBottleItem.getDrinkPrice());

                }
                if (barBottleItem.getDrinkName().equals("Doble")) {

                    binding.doblePriceET.setText(barBottleItem.getDrinkPrice());

                }
                if (barBottleItem.getDrinkName().equals("Tanqueray")) {

                    binding.tanquarayPriceET.setText(barBottleItem.getDrinkPrice());

                }
                if (barBottleItem.getDrinkName().equals("Beefeater")) {

                    binding.beefeaterPriceET.setText(barBottleItem.getDrinkPrice());

                }
                if (barBottleItem.getDrinkName().equals("Brugal Añejo")) {

                    binding.brugalAnejoPriceET.setText(barBottleItem.getDrinkPrice());

                }

            }

            getBottleList();

        }
    }

    public String getTime(long timeStamp) {

        String time = "0";

        if (timeStamp != 0) {

            Date timeD = new Date(timeStamp);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            time = sdf.format(timeD);

        }

        return time;

    }

    private void getBottleList() {

        if (currentUserDetail.getBarBottle() != null) {

            String normal = Constant.normals;
            String highRoller = Constant.highRoller;
            String warTime = Constant.wartime;

            for (BarBottleItem barBottleItem : currentUserDetail.getBarBottle()) {

                if (normal.contains(barBottleItem.getDrinkName())) {

                    for (int i = 0; i < Constant.normalArray.size(); i++) {
                        if (Constant.normalArray.get(i).getName().equals(barBottleItem.getDrinkName()))
                            Constant.normalArray.get(i).setPrice(barBottleItem.getDrinkPrice());
                    }

                } else if (highRoller.contains(barBottleItem.getDrinkName())) {

                    for (int i = 0; i < Constant.highRollerArray.size(); i++) {
                        if (Constant.highRollerArray.get(i).getName().equals(barBottleItem.getDrinkName()))
                            Constant.highRollerArray.get(i).setPrice(barBottleItem.getDrinkPrice());
                    }

                } else if (warTime.contains(barBottleItem.getDrinkName())) {

                    for (int i = 0; i < Constant.warTimeArray.size(); i++) {
                        if (Constant.warTimeArray.get(i).getName().equals(barBottleItem.getDrinkName()))
                            Constant.warTimeArray.get(i).setPrice(barBottleItem.getDrinkPrice());
                    }

                }
            }

            setRecyclerView();

            addDataInHourList();
        }

    }

    private void setRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.normalRV.setLayoutManager(layoutManager);
        NormalBottleAdapter normalBottleAdapter = new NormalBottleAdapter(this, Constant.normalArray, this);
        binding.normalRV.setAdapter(normalBottleAdapter);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.highRollerRV.setLayoutManager(layoutManager1);
        HighRollerBottleAdapter highRollerBottleAdapter = new HighRollerBottleAdapter(this, Constant.highRollerArray, this);
        binding.highRollerRV.setAdapter(highRollerBottleAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.warTimeRV.setLayoutManager(layoutManager2);
        WarTimeBottleAdapter warTimeBottleAdapter = new WarTimeBottleAdapter(this, Constant.warTimeArray, this);
        binding.warTimeRV.setAdapter(warTimeBottleAdapter);


    }

    private void addDataInHourList() {

        if (currentUserDetail.getBarWeekDay() != null) {

            for (int i = 0; i < currentUserDetail.getBarWeekDay().size(); i++) {

                String name = currentUserDetail.getBarWeekDay().get(i).getName();

                String sTime = getTime(currentUserDetail.getBarWeekDay().get(i).getSvalue());

                String eTime = getTime(currentUserDetail.getBarWeekDay().get(i).getEvalue());

                boolean isChild = checkDataExist(name,i);

                HourModel hourModel = new HourModel(name, sTime, eTime, isChild);

                hoursList.add(hourModel);

            }
        }

        setHoursRecycler();

    }

    public boolean checkDataExist(String dayName,int index) {

        if (hoursList != null) {

            for (int i = index+1; i < hoursList.size(); i++) {

                if (hoursList.get(i).getDayName()==null || hoursList.get(i).getDayName().equals("")) {

                    return true;

                }
            }
        }

        return false;

    }



    private void setHoursRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        binding.hoursRV.setLayoutManager(layoutManager);

        adapter = new HoursAdapter(this, hoursList, this);

        binding.hoursRV.setItemAnimator(new DefaultItemAnimator());

        binding.hoursRV.setAdapter(adapter);

        setUserPersonalInfo();

    }

    private void setUserPersonalInfo() {


        if (currentUserDetail.getBarSupliment().isIsSupliment()){

            binding.yesCB.setChecked(true);
            binding.noCB.setChecked(false);

        } else {

            binding.noCB.setChecked(true);
            binding.yesCB.setChecked(false);

        }

        binding.barNameET.setText(loginResponse.getResult().getBname());

        binding.barAddressET.setText(loginResponse.getResult().getBaddress());

        binding.customerNameET.setText(loginResponse.getResult().getCname());

        binding.customerPhoneET.setText(loginResponse.getResult().getCphone());

    }

    private void SetListener() {

        binding.appBar.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.appBar.backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.roofTopCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                    binding.roofTopTV.setTextColor(getResources().getColor(R.color.white));

                } else {

                    binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.white));

                    binding.roofTopTV.setTextColor(getResources().getColor(R.color.light_black));

                }
            }
        });

        binding.barWithTerraceCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                    binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.white));

                } else {

                    binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.white));

                    binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.light_black));

                }
            }
        });

        binding.selectImageBtn.setOnClickListener(v -> openSelectOperationDialog());

        binding.appBar.doneBtn.setOnClickListener(v -> updateData());

        binding.deleteBarBtn.setOnClickListener(v -> showDeleteBarDialog());

        binding.btnContactUsBtn.setOnClickListener(v-> showContactUsDialog());

        binding.noCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if(check){

                    binding.yesCB.setChecked(false);

                }else{

                    binding.yesCB.setChecked(true);

                }
            }
        });
        binding.yesCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if(check){

                    binding.noCB.setChecked(false);

                }else{

                    binding.noCB.setChecked(true);

                }
            }
        });
        binding.btnLogout.setOnClickListener(v->{

            SessionManager.getInstance().setUser(EditBar.this,null);

            finish();

            Intent intent = new Intent(EditBar.this, Home.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

        });

        binding.barAddressET.setOnClickListener(v -> {

            startActivity(new Intent(EditBar.this, PlacesPluginActivity.class).putExtra("type","EditBar"));

        });


    }

    private void showContactUsDialog() {

        final Dialog dialog = new Dialog(this,R.style.DialogTheme);

        dialog.setCancelable(true);

        AlertDialogContactUsBinding contactUsBinding = AlertDialogContactUsBinding.inflate(getLayoutInflater());

        dialog.setContentView(contactUsBinding.getRoot());

        contactUsBinding.exitIconIV.setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (selectedLocationLatLng != null) {

            Geocoder geocoder;

            List<Address> addresses = null;

            geocoder = new Geocoder(this, Locale.getDefault());

            try {

                addresses = geocoder.getFromLocation(selectedLocationLatLng.getLatitude(), selectedLocationLatLng.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            } catch (IOException e) {

                e.printStackTrace();

            }

            String address = addresses.get(0).getAddressLine(0);

            binding.barAddressET.setText(address);

        }

    }

    private void showDeleteBarDialog() {

        final Dialog dialog = new Dialog(this,R.style.DialogTheme);

        dialog.setCancelable(true);

        AlertDialogDeleteBarBinding deleteDialogBinding = AlertDialogDeleteBarBinding.inflate(getLayoutInflater());

        dialog.setContentView(deleteDialogBinding.getRoot());

        deleteDialogBinding.exitIconIV.setOnClickListener(v->{
            dialog.dismiss();
        });

        deleteDialogBinding.cancelBtn.setOnClickListener(v->{
            dialog.dismiss();
        });

        deleteDialogBinding.deleteBarBtn.setOnClickListener(v->{
            deleteBarNow();
            dialog.dismiss();
        });

        dialog.show();

    }


    private void deleteBarNow() {
        CustomProgressDialog.getInstance(EditBar.this).showProgressDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", loginResponse.getResult().getId())
                .build();

        Controller.getApi().deleteBar(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        CustomProgressDialog.getInstance(EditBar.this).dismissProgressDialog();

                        if (response.body() != null) {

                            try {

                                DeleteBarResponse deleteBarResponse = new Gson().fromJson(response.body(), DeleteBarResponse.class);

                                if (deleteBarResponse.getSuccess().equals("1")) {


                                    SessionManager.getInstance().setUser(EditBar.this,null);

                                    Intent intent = new Intent(EditBar.this, FirstScreen.class);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    startActivity(intent);

                                    finish();

                                    Toast.makeText(EditBar.this, "Delete Bar Successful! " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(EditBar.this, "Delete Bar Failed! " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {

                                e.printStackTrace();

                                Log.d("updateResponse", "catch exception");

                                Toast.makeText(EditBar.this, "Delete Bar Failed! " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("appRegisterResponse", "onFailure exception" + t.getLocalizedMessage());

                        CustomProgressDialog.getInstance(EditBar.this).dismissProgressDialog();

                    }
                });
    }

    private void updateData() {

        if (isValid()) {
            updateBarNow();
        }

    }

    private void updateBarNow() {
        CustomProgressDialog.getInstance(EditBar.this).showProgressDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", loginResponse.getResult().getId())
                .addFormDataPart("cname", binding.customerNameET.getText().toString())
                .addFormDataPart("cphone", binding.customerPhoneET.getText().toString())
                .addFormDataPart("image", base64Image)
                .addFormDataPart("bname", binding.barNameET.getText().toString())
                .addFormDataPart("baddress", binding.barAddressET.getText().toString())
                .addFormDataPart("blat", String.valueOf(selectedLocationLatLng.getLatitude()))
                .addFormDataPart("blng", String.valueOf(selectedLocationLatLng.getLongitude()))
                .addFormDataPart("bdetail", createJsonFromData())
                .build();

        Controller.getApi().updateBar(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        CustomProgressDialog.getInstance(EditBar.this).dismissProgressDialog();

                        if (response.body() != null) {

                            try {

                                LoginResponse loginResponse = new Gson().fromJson(response.body(), LoginResponse.class);

                                if (loginResponse.getSuccess() == 1) {

                                    Gson gson = new Gson();

                                    String jsonData = gson.toJson(loginResponse);

                                    SessionManager.getInstance().setUser(EditBar.this, loginResponse);

                                    Log.d("updateResponse", jsonData);

                                    Toast.makeText(EditBar.this, "Bar has been updated Successfully", Toast.LENGTH_SHORT).show();

                                    finish();

                                } else {

                                    Toast.makeText(EditBar.this, "updated Failed! " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            } catch (Exception e) {

                                e.printStackTrace();

                                Log.d("updateResponse", "catch exception");

                                Toast.makeText(EditBar.this, "updated Failed! " + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("appRegisterResponse", "onFailure exception" + t.getLocalizedMessage());

                        CustomProgressDialog.getInstance(EditBar.this).dismissProgressDialog();

                    }
                });

    }

    private String createJsonFromData() {

        DetailModelForAddBar detailModel = new DetailModelForAddBar();

        //Add bottle data
        List<BarBottleItem> barBottleItemsList = new ArrayList<>();

        BarBottleItem barBottleItem = new BarBottleItem();

        barBottleItem.setDrinkImage("d4");
        barBottleItem.setDrinkName("Caña");
        if(binding.canaPriceET.getText().toString().isEmpty()){
            barBottleItem.setDrinkPrice("-");
        }else{
            barBottleItem.setDrinkPrice(binding.canaPriceET.getText().toString());
        }
        barBottleItemsList.add(barBottleItem);

        barBottleItem = new BarBottleItem();
        barBottleItem.setDrinkImage("d5");
        barBottleItem.setDrinkName("Doble");
        if(binding.doblePriceET.getText().toString().isEmpty()){
            barBottleItem.setDrinkPrice("-");
        }else {
            barBottleItem.setDrinkPrice(binding.doblePriceET.getText().toString());
        }
        barBottleItemsList.add(barBottleItem);


        barBottleItem = new BarBottleItem();
        barBottleItem.setDrinkImage("d1");
        barBottleItem.setDrinkName("Tanqueray");
        if(binding.tanquarayPriceET.getText().toString().isEmpty()){
            barBottleItem.setDrinkPrice("-");
        }else {
            barBottleItem.setDrinkPrice(binding.tanquarayPriceET.getText().toString());
        }
        barBottleItemsList.add(barBottleItem);

        barBottleItem = new BarBottleItem();
        barBottleItem.setDrinkImage("d2");
        barBottleItem.setDrinkName("Beefeater");
        if(binding.beefeaterPriceET.getText().toString().isEmpty()){
            barBottleItem.setDrinkPrice("-");
        }else {
            barBottleItem.setDrinkPrice(binding.beefeaterPriceET.getText().toString());
        }
        barBottleItemsList.add(barBottleItem);

        barBottleItem = new BarBottleItem();
        barBottleItem.setDrinkImage("d3");
        barBottleItem.setDrinkName("Brugal Añejo");
        if(binding.brugalAnejoPriceET.getText().toString().isEmpty()){
            barBottleItem.setDrinkPrice("-");
        }else {
            barBottleItem.setDrinkPrice(binding.brugalAnejoPriceET.getText().toString());
        }
        barBottleItemsList.add(barBottleItem);

        for (int i = 0; i < Constant.normalArray.size(); i++) {
            Log.d("listDataRes", "list1" + Constant.normalArray.get(i).getPrice());
            if (!Constant.normalArray.get(i).getPrice().equals("")) {

                barBottleItem = new BarBottleItem();
                barBottleItem.setDrinkImage(Constant.normalArray.get(i).getImage());
                barBottleItem.setDrinkName(Constant.normalArray.get(i).getName());
                barBottleItem.setDrinkPrice(Constant.normalArray.get(i).getPrice());
                barBottleItemsList.add(barBottleItem);

            }

        }
        for (int i = 0; i < Constant.highRollerArray.size(); i++) {
            Log.d("listDataRes", "list2" + Constant.highRollerArray.get(i).getPrice());
            if (!Constant.highRollerArray.get(i).getPrice().equals("")) {

                barBottleItem = new BarBottleItem();
                barBottleItem.setDrinkImage(Constant.highRollerArray.get(i).getImage());
                barBottleItem.setDrinkName(Constant.highRollerArray.get(i).getName());
                barBottleItem.setDrinkPrice(Constant.highRollerArray.get(i).getPrice());
                barBottleItemsList.add(barBottleItem);

            }

        }
        for (int i = 0; i < Constant.warTimeArray.size(); i++) {
            Log.d("listDataRes", "list3" + Constant.warTimeArray.get(i).getPrice());
            if (!Constant.warTimeArray.get(i).getPrice().equals("")) {

                barBottleItem = new BarBottleItem();
                barBottleItem.setDrinkImage(Constant.warTimeArray.get(i).getImage());
                barBottleItem.setDrinkName(Constant.warTimeArray.get(i).getName());
                barBottleItem.setDrinkPrice(Constant.warTimeArray.get(i).getPrice());
                barBottleItemsList.add(barBottleItem);

            }

        }

        detailModel.setBarBottle(barBottleItemsList);

        //Add Bar Supplement

        BarSupliment barSupliment = new BarSupliment();
        if (binding.noCB.isChecked()) {
            barSupliment.setSupliment(true);
        } else {
            barSupliment.setSupliment(false);
        }
        detailModel.setBarSupliment(barSupliment);


        //Add Day Data

        List<BarWeekDayItem> barWeekDayItemList = new ArrayList<>();
        BarWeekDayItem barWeekDayItem = new BarWeekDayItem();

        for (int i = 0; i < hoursList.size(); i++) {

            barWeekDayItem = new BarWeekDayItem();
            barWeekDayItem.setWeekDay(hoursList.get(i).getDayName());
            barWeekDayItem.setName(hoursList.get(i).getDayName());

            if (hoursList.get(i).getStartTime().equals("") || hoursList.get(i).getEndTime().equals("")) {

                barWeekDayItem.setEvalue(0);
                barWeekDayItem.setSvalue(0);

            } else {

                barWeekDayItem.setSvalue(getTimeStamp(hoursList.get(i).getStartTime()));
                barWeekDayItem.setEvalue(getTimeStamp(hoursList.get(i).getEndTime()));

            }

            barWeekDayItemList.add(barWeekDayItem);

        }
        detailModel.setBarWeekDay(barWeekDayItemList);


        //Add BarHas Terraces or Rooftops
        List<BarHasItem> barHasItemsList = new ArrayList<>();
        BarHasItem barHasItem = new BarHasItem();

        if (binding.barWithTerraceCB.isChecked()) {

            barHasItem.setSelected(true);

        } else {

            barHasItem.setSelected(false);

        }

        barHasItem.setTitle("Terraces");

        barHasItemsList.add(barHasItem);

        barHasItem = new BarHasItem();

        if (binding.roofTopCB.isChecked()) {

            barHasItem.setSelected(true);
        } else {
            barHasItem.setSelected(false);
        }
        barHasItem.setTitle("Rooftops");

        barHasItemsList.add(barHasItem);

        detailModel.setBarHas(barHasItemsList);

        Gson gson = new Gson();
        String jsonData = gson.toJson(detailModel);

        Log.d("json data", jsonData);

        return jsonData;

    }

    public long getTimeStamp(String time) {

        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = (Date) formatter.parse(time);
        } catch (Exception e) {
            return 0;
        }
        Log.d("timeResponseData", time);
      //  long output = date.getTime() / 1000L;
        long output = date.getTime();
        String str = Long.toString(output);
        long timestamp = Long.parseLong(str);
       // long timestamp = Long.parseLong(str) * 1000;
        return timestamp;
    }

    private boolean isValid() {

        boolean check = false;

        if (binding.barNameET.getText().toString().isEmpty()) {
            Toast.makeText(EditBar.this, "Bar Name Required!", Toast.LENGTH_SHORT).show();
            return check;
        }

        if (binding.barAddressET.getText().toString().equals("Select Location")) {
            Toast.makeText(EditBar.this, "Bar Location Required!", Toast.LENGTH_SHORT).show();
            return check;
        }

//        if (binding.canaPriceET.getText().toString().isEmpty()) {
//            Toast.makeText(EditBar.this, "Cana Price Required!", Toast.LENGTH_SHORT).show();
//            return check;
//        }
//
//        if (binding.doblePriceET.getText().toString().isEmpty()) {
//            Toast.makeText(EditBar.this, "Doble Price Required!", Toast.LENGTH_SHORT).show();
//            return check;
//        }
//
//        if (binding.tanquarayPriceET.getText().toString().isEmpty()) {
//            Toast.makeText(EditBar.this, "Tanquaray Price Required!", Toast.LENGTH_SHORT).show();
//            return check;
//        }
//
//        if (binding.beefeaterPriceET.getText().toString().isEmpty()) {
//            Toast.makeText(EditBar.this, "Beefeater Price Required!", Toast.LENGTH_SHORT).show();
//            return check;
//        }
//
//        if (binding.brugalAnejoPriceET.getText().toString().isEmpty()) {
//            Toast.makeText(EditBar.this, "BrugalAnejo Price Required!", Toast.LENGTH_SHORT).show();
//            return check;
//        }

//        if (imageUri == null) {
//            Toast.makeText(EditBar.this, "Bar Image Required!", Toast.LENGTH_SHORT).show();
//            return check;
//        }


        if (binding.customerNameET.getText().toString().isEmpty()) {
            Toast.makeText(EditBar.this, "Customer Name Required!", Toast.LENGTH_SHORT).show();
            return check;
        }

        if (binding.customerPhoneET.getText().toString().isEmpty()) {
            Toast.makeText(EditBar.this, "Customer Phone Number Required!", Toast.LENGTH_SHORT).show();
            return check;
        }

        check = true;
        return check;

    }

    public void openSelectOperationDialog() {

        Dialog dialog = new Dialog(EditBar.this, android.R.style.Theme_NoTitleBar_Fullscreen);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        DialogSelectCameraOrGalleryBinding dialogBinding = DialogSelectCameraOrGalleryBinding.inflate((EditBar.this).getLayoutInflater());

        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.cameraBT.setOnClickListener(v -> {

            checkCameraPermission();

            dialog.dismiss();

        });

        dialogBinding.galleryBt.setOnClickListener(v -> {

            checkGalleryPermission();

            dialog.dismiss();

        });

        dialog.show();
    }

    private void checkCameraPermission() {
        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(EditBar.this, permission, "All permissions Required", null, new PermissionHandler() {
            @Override
            public void onGranted() {
                openCamera();
            }
        });
    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

    private void checkGalleryPermission() {
        Permissions.check(this /*context*/, Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                openGallery();

            }
        });
    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*");

        startActivityForResult(intent, REQUEST_PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            if (data == null || data.getExtras() == null) {
                return;
            }

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Uri uri = getImageUri(this, bitmap);

            binding.mainBarIV.setImageURI(uri);

            imageUri = uri;

            getBase64String(bitmap);

        } else if (requestCode == REQUEST_PICK_IMAGE) {

            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();

                binding.mainBarIV.setImageURI(uri);

                imageUri = uri;

                Uri imageUriData = data.getData();

                try {

                    Bitmap bitmap = getBitmap(this.getContentResolver(), imageUriData);

                    getBase64String(bitmap);

                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
        }
    }
    public static final Bitmap getBitmap(ContentResolver cr, Uri url)
            throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }
    Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        String dateTime = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, dateTime, null);

        return Uri.parse(path);
    }

    private void getBase64String(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

        //Toast.makeText(EditBar.this, base64Image, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPriceChange(String type, int index, String priceValue) {

        if (type.equals("normal")) {
            Constant.normalArray.get(index).setPrice(priceValue);
        } else if (type.equals("highRoller")) {
            Constant.highRollerArray.get(index).setPrice(priceValue);
        } else if (type.equals("warTime")) {
            Constant.warTimeArray.get(index).setPrice(priceValue);
        }

        //  setRecyclerView();

    }

    @Override
    public void onItemClick(String dayName, int index, String addChildOrRemove) {

        if (addChildOrRemove.equals("remove")) {

            hoursList.remove(index);

            setHoursRecycler();

        } else {

            HourModel hourModelNew = new HourModel("", "", "", true);

            hoursList.add(index + 1, hourModelNew);

            setHoursRecycler();

        }

    }

    @Override
    public void onDataChange(String value, int index, boolean isStart) {

        HourModel hourModel = hoursList.get(index);

        hoursList.remove(index);

        if (isStart) {

            hourModel.setStartTime(value);

            hoursList.add(index, hourModel);

        } else {

            hourModel.setEndTime(value);

            hoursList.add(index, hourModel);

        }

        //setHoursRecycler();

    }
}