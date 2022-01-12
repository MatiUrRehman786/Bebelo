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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.buzzware.bebelo.databinding.ActivityAddBarBinding;
import com.buzzware.bebelo.databinding.DialogSelectCameraOrGalleryBinding;
import com.buzzware.bebelo.interfaces.EditPricesCallback;
import com.buzzware.bebelo.interfaces.HoursCallback;
import com.buzzware.bebelo.retrofit.DetailModel.BarBottleItem;
import com.buzzware.bebelo.retrofit.DetailModel.BarHasItem;
import com.buzzware.bebelo.retrofit.DetailModel.BarSupliment;
import com.buzzware.bebelo.retrofit.Controller;
import com.buzzware.bebelo.retrofit.DetailModel.BarWeekDayItem;
import com.buzzware.bebelo.retrofit.DetailModel.DetailModelForAddBar;
import com.buzzware.bebelo.retrofit.response.AppRegisterResponse.AppRegisterResponse;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
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

public class AddBar extends AppCompatActivity implements EditPricesCallback,HoursCallback {

    ActivityAddBarBinding binding;

    Context context;

    public static LatLng selectedLocationLatLng = null;

    private int REQUEST_IMAGE_CAPTURE = 1;

    private int REQUEST_PICK_IMAGE = 2;


    List<HourModel> hoursList = new ArrayList<>();

    HoursAdapter adapter;

    Uri imageUri = null;

    String base64Image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddBarBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Init();

        loadDataInConstantList();

        addDataInList();

        setListener();

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


    private void setListener() {

        binding.appBar.btnDone.setOnClickListener(v -> {

            if (isValid()) {

                addBarNow();

            }

        });

        binding.noCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {

                if(check){

                    binding.yesCB.setChecked(false);

                } else {

                    binding.yesCB.setChecked(true);

                }

            }

        });

        binding.yesCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {

                if(check){

                    binding.noCB.setChecked(false);

                } else {

                    binding.noCB.setChecked(true);

                }
            }
        });

    }

    private boolean isValid() {

        boolean check = false;

        if (binding.barNameET.getText().toString().isEmpty()) {

            Toast.makeText(AddBar.this, "Bar Name Required!", Toast.LENGTH_SHORT).show();

            return check;

        }

        if (binding.barLocationET.getText().toString().equals("Select Location")) {

            Toast.makeText(AddBar.this, "Bar Location Required!", Toast.LENGTH_SHORT).show();

            return check;

        }
//
//        if (binding.canaPriceET.getText().toString().isEmpty()) {
//
//            Toast.makeText(AddBar.this, "Cana Price Required!", Toast.LENGTH_SHORT).show();
//
//            return check;
//
//        }
//
//        if (binding.doblePriceET.getText().toString().isEmpty()) {
//
//            Toast.makeText(AddBar.this, "Doble Price Required!", Toast.LENGTH_SHORT).show();
//
//            return check;
//
//        }
//
//        if (binding.tanquerayPriceET.getText().toString().isEmpty()) {
//
//            Toast.makeText(AddBar.this, "Tanqueray Price Required!", Toast.LENGTH_SHORT).show();
//
//            return check;
//
//        }
//
//        if (binding.beefeaterPriceET.getText().toString().isEmpty()) {
//
//            Toast.makeText(AddBar.this, "Beefeater Price Required!", Toast.LENGTH_SHORT).show();
//
//            return check;
//
//        }
//
//        if (binding.brugalAnejoPriceET.getText().toString().isEmpty()) {
//
//            Toast.makeText(AddBar.this, "BrugalAnejo Price Required!", Toast.LENGTH_SHORT).show();
//
//            return check;
//
//        }

        if (imageUri == null) {

            Toast.makeText(AddBar.this, "Bar Image Required!", Toast.LENGTH_SHORT).show();

            return check;

        }


        if (binding.customerNameET.getText().toString().isEmpty()) {

            Toast.makeText(AddBar.this, "Customer Name Required!", Toast.LENGTH_SHORT).show();

            return check;

        }

        if (binding.customerPhoneET.getText().toString().isEmpty()) {

            Toast.makeText(AddBar.this, "Customer Phone Number Required!", Toast.LENGTH_SHORT).show();

            return check;

        }

        if (binding.customerEmailET.getText().toString().isEmpty()) {

            Toast.makeText(AddBar.this, "Customer Email Required!", Toast.LENGTH_SHORT).show();

            return check;

        }

        if (binding.customerPasswordET.getText().toString().isEmpty()) {

            Toast.makeText(AddBar.this, "Customer Password Required!", Toast.LENGTH_SHORT).show();

            return check;

        }

        check = true;

        return check;

    }

    private void addBarNow() {

        CustomProgressDialog.getInstance(AddBar.this).showProgressDialog();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("cname", binding.customerNameET.getText().toString())
                .addFormDataPart("cemail", binding.customerEmailET.getText().toString())
                .addFormDataPart("cphone", binding.customerPhoneET.getText().toString())
                .addFormDataPart("cpassword", binding.customerPasswordET.getText().toString())
                .addFormDataPart("image", base64Image)
                .addFormDataPart("bname", binding.barNameET.getText().toString())
                .addFormDataPart("baddress", binding.barLocationET.getText().toString())
                .addFormDataPart("blat", selectedLocationLatLng.getLatitude() + "")
                .addFormDataPart("blng", selectedLocationLatLng.getLongitude() + "")
                .addFormDataPart("bdetail", createJsonFromData())
                .build();

        Controller.getApi().addBar(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        CustomProgressDialog.getInstance(AddBar.this).dismissProgressDialog();

                        if (response.body() != null) {

                            try {

                                AppRegisterResponse appRegisterResponse = new Gson().fromJson(response.body(), AppRegisterResponse.class);

                                Gson gson = new Gson();
                                String jsonData = gson.toJson(appRegisterResponse);

                                Log.d("appRegisterResponse", jsonData);

                                Toast.makeText(AddBar.this, "Bar has been added Successfully", Toast.LENGTH_SHORT).show();

                                finish();

                            } catch (Exception e) {

                                e.printStackTrace();

                                Log.d("appRegisterResponse", "catch exception");

                                Toast.makeText(AddBar.this, "Bar added failed", Toast.LENGTH_SHORT).show();

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("appRegisterResponse", "onFailure exception" + t.getLocalizedMessage());

                        CustomProgressDialog.getInstance(AddBar.this).dismissProgressDialog();

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
         if(binding.tanquerayPriceET.getText().toString().isEmpty()){
            barBottleItem.setDrinkPrice("-");
        }else {
             barBottleItem.setDrinkPrice(binding.tanquerayPriceET.getText().toString());
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


        for(int i=0;i<Constant.normalArray.size();i++){

            barBottleItem=new BarBottleItem();

            if(!Constant.normalArray.get(i).getPrice().equals("")){

                barBottleItem.setDrinkImage(Constant.normalArray.get(i).getImage());

                barBottleItem.setDrinkName(Constant.normalArray.get(i).getName());

                barBottleItem.setDrinkPrice(Constant.normalArray.get(i).getPrice());

                barBottleItemsList.add(barBottleItem);

            }

        }

        for(int i=0;i<Constant.highRollerArray.size();i++){

            barBottleItem=new BarBottleItem();

            if(!Constant.highRollerArray.get(i).getPrice().equals("")){

                barBottleItem.setDrinkImage(Constant.highRollerArray.get(i).getImage());

                barBottleItem.setDrinkName(Constant.highRollerArray.get(i).getName());

                barBottleItem.setDrinkPrice(Constant.highRollerArray.get(i).getPrice());

                barBottleItemsList.add(barBottleItem);

            }

        }

        for(int i=0;i<Constant.warTimeArray.size();i++){

            barBottleItem=new BarBottleItem();

            if(!Constant.warTimeArray.get(i).getPrice().equals("")){

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
            barSupliment.setSupliment(false);
        } else if(binding.yesCB.isChecked()) {
            barSupliment.setSupliment(true);
        }
        detailModel.setBarSupliment(barSupliment);


        //Add Day Data

        List<BarWeekDayItem> barWeekDayItemList = new ArrayList<>();
        BarWeekDayItem barWeekDayItem = new BarWeekDayItem();

        for (int i = 0; i < hoursList.size(); i++) {

            barWeekDayItem = new BarWeekDayItem();

            barWeekDayItem.setWeekDay(hoursList.get(i).getDayName());

            barWeekDayItem.setName(hoursList.get(i).getDayName());

            if (hoursList.get(i).getStartTime().equals("") && hoursList.get(i).getEndTime().equals("")) {

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

        }
        long output = date.getTime();
        String str = Long.toString(output);
        long timestamp = Long.parseLong(str);
        return timestamp;
    }

    private void addDataInList() {

        hoursList.add(new HourModel("Monday", "", "", false));
        hoursList.add(new HourModel("Tuesday", "", "", false));
        hoursList.add(new HourModel("Wednesday", "", "", false));
        hoursList.add(new HourModel("Thursday", "", "", false));
        hoursList.add(new HourModel("Friday", "", "", false));
        hoursList.add(new HourModel("Saturday", "", "", false));
        hoursList.add(new HourModel("Sunday", "", "", false));

        setHoursRecycler();

    }

    private void setHoursRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        binding.hoursRV.setLayoutManager(layoutManager);

        adapter = new HoursAdapter(this, hoursList, this);

        binding.hoursRV.setItemAnimator(new DefaultItemAnimator());

        binding.hoursRV.setAdapter(adapter);

    }

    private void Init() {

        context = AddBar.this;

        SetListener();

    }

    private void SetListener() {

        binding.appBar.btnDone.setOnClickListener(v -> {

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

            SharedPreferences.Editor editor = pref.edit();

            editor.putString("checkLogin", "login");

            editor.commit();

            finish();

        });

        binding.appBar.backIV.setOnClickListener(v -> {

            finish();

        });

        binding.roofTopCB.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                binding.roofTopTV.setTextColor(getResources().getColor(R.color.white));

            } else {

                binding.roofTopRL.setBackgroundColor(getResources().getColor(R.color.white));

                binding.roofTopTV.setTextColor(getResources().getColor(R.color.light_black));

            }

        });

        binding.barWithTerraceCB.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.greenish_blue));

                binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.white));

            } else {

                binding.barWithTerraceRL.setBackgroundColor(getResources().getColor(R.color.white));

                binding.barWithTerraceTV.setTextColor(getResources().getColor(R.color.light_black));

            }
        });

        binding.barLocationET.setOnClickListener(v -> {

            startActivity(new Intent(AddBar.this, PlacesPluginActivity.class).putExtra("type","AddBar"));

        });

        binding.selectImageBtn.setOnClickListener(v -> openSelectOperationDialog());


    }

    public void openSelectOperationDialog() {

        Dialog dialog = new Dialog(AddBar.this, android.R.style.Theme_NoTitleBar_Fullscreen);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        DialogSelectCameraOrGalleryBinding dialogBinding = DialogSelectCameraOrGalleryBinding.inflate((AddBar.this).getLayoutInflater());

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

        Permissions.check(AddBar.this, permission, "All permissions Required", null, new PermissionHandler() {
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

    Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        String dateTime = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, dateTime, null);

        return Uri.parse(path);
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

            binding.barLocationET.setText(address);

        }

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

            binding.selectedImageIV.setImageURI(uri);

            imageUri = uri;

            getBase64String(bitmap);

        } else if (requestCode == REQUEST_PICK_IMAGE) {

            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();

                binding.selectedImageIV.setImageURI(uri);

                imageUri = uri;

                Uri imageUriData = data.getData();

                try {

                    Bitmap bitmap = getBitmap(this.getContentResolver(),imageUriData);

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

    private void getBase64String(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.d("base64ImageData", base64Image);

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

        } else {

            hourModel.setEndTime(value);

        }

        hoursList.add(index, hourModel);

        setHoursRecycler();

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
    }
}