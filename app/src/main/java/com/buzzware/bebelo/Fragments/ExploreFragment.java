package com.buzzware.bebelo.Fragments;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.bebelo.Activities.ClaimBar;
import com.buzzware.bebelo.Activities.Home;
import com.buzzware.bebelo.Addapter.AdapterSectionRecycler;
import com.buzzware.bebelo.Addapter.HoursAdapter;
import com.buzzware.bebelo.Addapter.TimeScheduleForDialogAdapter;
import com.buzzware.bebelo.Model.HourModel;
import com.buzzware.bebelo.Model.SearchPriceModel;
import com.buzzware.bebelo.Model.SectionHeader;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.RelativeLayoutTouchListener;
import com.buzzware.bebelo.classes.Constant;
import com.buzzware.bebelo.classes.SessionManager;
import com.buzzware.bebelo.databinding.AlertDialogDeleteBarBinding;
import com.buzzware.bebelo.databinding.AlertDialogTimeScheduleBinding;
import com.buzzware.bebelo.databinding.BottomSheetExploreDialogBinding;
import com.buzzware.bebelo.databinding.FragmentExploreBinding;
import com.buzzware.bebelo.eventBusModel.MessageEvent;
import com.buzzware.bebelo.retrofit.Controller;
import com.buzzware.bebelo.retrofit.DetailModel.BarBottleItem;
import com.buzzware.bebelo.retrofit.DetailModel.DetailModelForAddBar;
import com.buzzware.bebelo.retrofit.GetAllStore.GetAllStoreResponse;
import com.buzzware.bebelo.retrofit.GetAllStore.ResultItem;
import com.buzzware.bebelo.retrofit.Login.LoginResponse;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment implements
        OnMapReadyCallback,
        PermissionsListener,
        MapboxMap.OnMarkerClickListener {



    FragmentExploreBinding binding;

    String jsonData;

    Context context;

    public static boolean checkOpen = false;

    private PermissionsManager permissionsManager;

    private MapboxMap mapboxMap;

    Style mapStyle;

    Location lastKnownLocation;

    Dialog detailDialog;

    BottomSheetExploreDialogBinding dialogBinding;

    BottomSheetBehavior bottomSheetBehavior;

    AdapterSectionRecycler adapterRecycler;

    List<SectionHeader> sectionHeaders;

    String markerTitleValue = "1";

    GetAllStoreResponse getAllStoreResponse = null;

    ResultItem currentSelectedMarkerResultItem = null;

    List<BarBottleItem> normalBottleList = new ArrayList<>();

    List<BarBottleItem> highRollerBottleList = new ArrayList<>();

    List<BarBottleItem> warTimeBottleList = new ArrayList<>();

    boolean isLiveData = true;

    CountDownTimer toastCountDown;

    boolean timerReady = false;

    public ExploreFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        binding = FragmentExploreBinding.inflate(inflater);

        Init();

        bottomSheet();

        setListener();

        SetRecyclerView();

        Home.binding.bottomSheetInclude.dataRL.setOnTouchListener(new RelativeLayoutTouchListener(ExploreFragment.this));

        binding.mapView.onCreate(savedInstanceState);

        binding.mapView.getMapAsync(this);

        getCurrentUserData();

        return binding.getRoot();

    }

    private void getCurrentUserData() {

        if (SessionManager.getInstance().getUser(context) != null) {

            LoginResponse loginResponse = SessionManager.getInstance().getUser(context);

            DetailModelForAddBar detailModel = new Gson().fromJson(loginResponse.getResult().getBdetail(), DetailModelForAddBar.class);

            Long tsLong = System.currentTimeMillis();

            String ts = tsLong.toString();

            if (detailModel.getBarAnounce() != null) {

                if (isAnnouncementExp(detailModel.getBarAnounce().getDate())) {

                    detailModel.setBarAnounce(null);
                    Gson gson = new Gson();
                    String json = gson.toJson(detailModel);
                    loginResponse.getResult().setBdetail(json);
                    updateBarNow(loginResponse);

                }

            }

            if (detailModel.getFreeTable() != null) {

                if (detailModel.getFreeTable().isFreeTable()) {

                    if (isFreeTableExp(detailModel.getFreeTable().getDate())) {

                        detailModel.setFreeTable(null);
                        Gson gson = new Gson();
                        String json = gson.toJson(detailModel);
                        loginResponse.getResult().setBdetail(json);
                        updateBarNow(loginResponse);

                    }

                }

            }

        }

    }

    private void updateBarNow(LoginResponse loginResponse) {


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


                        if (response.body() != null) {

                            try {

                                LoginResponse loginResponse = new Gson().fromJson(response.body(), LoginResponse.class);

                                if (loginResponse.getSuccess() == 1) {

                                    Gson gson = new Gson();

                                    String jsonData = gson.toJson(loginResponse);

                                    SessionManager.getInstance().setUser(context, loginResponse);

                                    Log.d("updateResponse", jsonData);


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


                    }
                });

    }

    public boolean isAnnouncementExp(long date) {

        boolean result = false;

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        String announcementDate = df.format(new Date(date));

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date(date));

        calendar.add(Calendar.HOUR, 4);

        Long tsLong = calendar.getTimeInMillis();

        String newDate = df.format(new Date(tsLong));

        Long tsLongCurrent = System.currentTimeMillis();

        if (tsLongCurrent > tsLong) {

            result = true;
            Log.d("currentTimeWith", "curr date time wit 4 hour/" + newDate + "  current date time" + announcementDate + "/ true");

        } else {

            result = false;
            Log.d("currentTimeWith", "curr date time wit 4 hour/" + newDate + "  current date time" + announcementDate + "/ false");

        }

        return result;
    }

    public boolean isFreeTableExp(long date) {

        boolean result = false;

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        String announcementDate = df.format(new Date(date));

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date(date));

        calendar.add(Calendar.MINUTE, 5);

        Long tsLong = calendar.getTimeInMillis();

        String newDate = df.format(new Date(tsLong));

        Long tsLongCurrent = System.currentTimeMillis();

        if (tsLongCurrent > tsLong) {

            result = true;
            Log.d("currentTimeWith", "curr date time wit 4 hour/" + newDate + "  current date time" + announcementDate + "/ true");

        } else {

            result = false;
            Log.d("currentTimeWith", "curr date time wit 4 hour/" + newDate + "  current date time" + announcementDate + "/ false");

        }

        return result;
    }

    private void Init() {

        context = getContext();

    }

    private void setListener() {

        Home.binding.bottomSheetInclude.bottomLay.setOnClickListener(v -> {

            Home.binding.bottomSheetInclude.rvDrinks.setVisibility(View.VISIBLE);

            Home.binding.bottomSheetInclude.bottomLay.setVisibility(View.GONE);

            Home.binding.bottomSheetInclude.bottomLayLess.setVisibility(View.VISIBLE);

        });

        Home.binding.bottomSheetInclude.appBarLine.mainCL.setOnClickListener(v -> {

            openDetailDialog();

        });

        Home.binding.bottomSheetInclude.bottomLayLess.setOnClickListener(v -> {

            Home.binding.bottomSheetInclude.rvDrinks.setVisibility(View.GONE);

            Home.binding.bottomSheetInclude.bottomLay.setVisibility(View.VISIBLE);

            Home.binding.bottomSheetInclude.bottomLayLess.setVisibility(View.GONE);

        });

        binding.liveBtn.setOnClickListener(v -> {

            isLiveData = false;

            mapboxMap.clear();

            binding.liveBtn.setVisibility(View.GONE);

            binding.goLiveBtn.setVisibility(View.VISIBLE);

            if (getAllStoreResponse != null) {

                if (getAllStoreResponse.getResult().size() > 0) {

                    for (ResultItem resultItem : getAllStoreResponse.getResult()) {

                        calculateAndAddAllMarker(resultItem);


                    }
                }

            }


        });

        binding.goLiveBtn.setOnClickListener(v -> {

            isLiveData = true;

            mapboxMap.clear();

            binding.liveBtn.setVisibility(View.VISIBLE);

            binding.goLiveBtn.setVisibility(View.GONE);

            if (getAllStoreResponse != null) {

                if (getAllStoreResponse.getResult().size() > 0) {

                    for (ResultItem resultItem : getAllStoreResponse.getResult()) {

                        calculateAndAddLiveMarkers(resultItem);

                    }
                }

            }

        });

        binding.currentLocationBtn.setOnClickListener(v -> {

            lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();

            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())) // Sets the new camera position
                    .zoom(12) // Sets the zoom
                    .bearing(0) // Rotate the camera
                    .tilt(1) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 4000);

        });

    }

    public void setSmallBottomSheetView(String title) {

        ResultItem resultItem = new Gson().fromJson(title, ResultItem.class);

        currentSelectedMarkerResultItem = resultItem;

        DetailModelForAddBar detail = new Gson().fromJson(resultItem.getBdetail(), DetailModelForAddBar.class);

        Home.binding.bottomSheetInclude.barNameTV.setText(resultItem.getBname());

        if (resultItem.getImgeUrl() != null) {

            Picasso.with(context).load(resultItem.getImgeUrl()).error(R.drawable.thumbnail_image)
                    .into(Home.binding.bottomSheetInclude.mainIV);

        }

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String eTime = "0";
        String[] data = eTime.split("#");

        switch (day) {
            case Calendar.MONDAY:

                eTime = todayEndTime(detail, "Monday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    Home.binding.bottomSheetInclude.statusTV.setText("Open");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.GREEN);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Close at " + eTime);

                } else {

                    Home.binding.bottomSheetInclude.statusTV.setText("Close");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.RED);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Open at " + eTime);

                }

                break;
            case Calendar.TUESDAY:

                eTime = todayEndTime(detail, "Tuesday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    Home.binding.bottomSheetInclude.statusTV.setText("Open");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.GREEN);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Close at " + eTime);

                } else {

                    Home.binding.bottomSheetInclude.statusTV.setText("Close");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.RED);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Open at " + eTime);

                }


                break;
            case Calendar.WEDNESDAY:

                eTime = todayEndTime(detail, "Wednesday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    Home.binding.bottomSheetInclude.statusTV.setText("Open");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.GREEN);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    Home.binding.bottomSheetInclude.statusTV.setText("Close");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.RED);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Open at " + data[0]);

                }


                break;
            case Calendar.THURSDAY:

                eTime = todayEndTime(detail, "Thursday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    Home.binding.bottomSheetInclude.statusTV.setText("Open");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.GREEN);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    Home.binding.bottomSheetInclude.statusTV.setText("Close");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.RED);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Open at " + data[0]);

                }


                break;
            case Calendar.FRIDAY:

                eTime = todayEndTime(detail, "Friday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    Home.binding.bottomSheetInclude.statusTV.setText("Open");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.GREEN);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    Home.binding.bottomSheetInclude.statusTV.setText("Close");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.RED);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Open at " + data[0]);

                }

                break;
            case Calendar.SATURDAY:

                eTime = todayEndTime(detail, "Saturday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    Home.binding.bottomSheetInclude.statusTV.setText("Open");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.GREEN);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    Home.binding.bottomSheetInclude.statusTV.setText("Close");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.RED);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Open at " + data[0]);

                }


                break;
            case Calendar.SUNDAY:

                eTime = todayEndTime(detail, "Sunday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    Home.binding.bottomSheetInclude.statusTV.setText("Open");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.GREEN);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    Home.binding.bottomSheetInclude.statusTV.setText("Close");
                    Home.binding.bottomSheetInclude.statusTV.setTextColor(Color.RED);
                    Home.binding.bottomSheetInclude.closingTimeTV.setText(" - Open at " + data[0]);

                }

                break;
        }

//        boolean timeFoundAreNot=false;
//        while (timeFoundAreNot!=true){
//
//        }

        if (detail.getFreeTable() != null) {
            if (detail.getFreeTable().isFreeTable()) {

                Home.binding.bottomSheetInclude.freeTableView.setVisibility(View.VISIBLE);

            } else {

                Home.binding.bottomSheetInclude.freeTableView.setVisibility(View.INVISIBLE);

            }
        } else {

            Home.binding.bottomSheetInclude.freeTableView.setVisibility(View.INVISIBLE);

        }


        if (detail.getBarAnounce() != null) {

            Home.binding.bottomSheetInclude.announceView.setVisibility(View.VISIBLE);

            Home.binding.bottomSheetInclude.announceTV.setText(detail.getBarAnounce().getName());

        } else {

            Home.binding.bottomSheetInclude.announceView.setVisibility(View.INVISIBLE);

        }

        //Home.binding.bottomSheetInclude.mainIV.setImageResource(R.drawable.bar_image1);
        //Home.binding.bottomSheetInclude.redView.setVisibility(View.VISIBLE);
        //Home.binding.bottomSheetInclude.blueLineRL.setVisibility(View.VISIBLE);
        //Home.binding.bottomSheetInclude.micIV.setVisibility(View.VISIBLE);
        //Home.binding.bottomSheetInclude.colorIV.setImageResource(R.drawable.blue_glow);
        //Home.binding.bottomSheetInclude.djTV.setVisibility(View.VISIBLE);
        //Home.binding.bottomSheetInclude.pinkBackTV.setVisibility(View.INVISIBLE);

    }

    public void openDetailDialog() {

        detailDialog = new Dialog(getContext(), android.R.style.Theme_NoTitleBar_Fullscreen);

        detailDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        detailDialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation; //style id

        detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        detailDialog.setCancelable(true);

        dialogBinding = BottomSheetExploreDialogBinding.inflate(LayoutInflater.from(getContext()));

        detailDialog.setContentView(dialogBinding.getRoot());

        setDetailDialogView(markerTitleValue);

        dialogBinding.appBarMenu.menuIV.setOnClickListener(v -> {

            OpenBottomDialog();

        });

        dialogBinding.timeLL.setOnClickListener(v -> {

            OpenTimeScheduleDialog();

        });

        SetRecyclerViewDialog();

        dialogBinding.bottomLay.setOnClickListener(v -> {

            dialogBinding.rvDrinks.setVisibility(View.VISIBLE);

            dialogBinding.bottomLay.setVisibility(View.GONE);

            dialogBinding.bottomLayLess.setVisibility(View.VISIBLE);

        });

        dialogBinding.appBarMenu.backIV.setOnClickListener(v -> {

            detailDialog.dismiss();

        });

        dialogBinding.bottomLayLess.setOnClickListener(v -> {

            dialogBinding.appBarMenu.menuIV.requestFocus();

            dialogBinding.rvDrinks.setVisibility(View.GONE);

            dialogBinding.bottomLay.setVisibility(View.VISIBLE);

            dialogBinding.bottomLayLess.setVisibility(View.GONE);

        });

        detailDialog.show();

    }

    private void OpenTimeScheduleDialog() {

        DetailModelForAddBar detail = new Gson().fromJson(currentSelectedMarkerResultItem.getBdetail(), DetailModelForAddBar.class);

        final Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);

        dialog.setCancelable(true);

        AlertDialogTimeScheduleBinding timeDialogBinding = AlertDialogTimeScheduleBinding.inflate(getLayoutInflater());

        dialog.setContentView(timeDialogBinding.getRoot());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        timeDialogBinding.hoursRV.setLayoutManager(layoutManager);

        List<HourModel> hoursList = new ArrayList<>();

        if (detail.getBarWeekDay() != null) {

            for (int i = 0; i < detail.getBarWeekDay().size(); i++) {

                String name = "";

                if (detail.getBarWeekDay().get(i).getWeekDay() != null) {

                    name = detail.getBarWeekDay().get(i).getName();

                }

                String sTime = getTime(detail.getBarWeekDay().get(i).getSvalue());

                String eTime = getTime(detail.getBarWeekDay().get(i).getEvalue());

                boolean isChild = false;

                HourModel hourModel = new HourModel(name, sTime, eTime, isChild);

                hoursList.add(hourModel);

            }
        }

        TimeScheduleForDialogAdapter adapter = new TimeScheduleForDialogAdapter(getContext(), hoursList);

        timeDialogBinding.hoursRV.setItemAnimator(new DefaultItemAnimator());

        timeDialogBinding.hoursRV.setAdapter(adapter);

        timeDialogBinding.exitIconIV.setOnClickListener(v -> {

            dialog.dismiss();

        });

        dialog.show();


    }

    private void OpenBottomDialog() {

        TextView btnClose;

        LinearLayout flag;

        RelativeLayout btnYourClaim;

        ImageView flagIV;

        TextView flagTV;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.SheetDialogMargin);

        View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null);

        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.show();

        btnClose = view.findViewById(R.id.btnClose);

        btnYourClaim = view.findViewById(R.id.myBarCalinBtn);

        flag = view.findViewById(R.id.flagDataBtn);

        flagIV = view.findViewById(R.id.flagIV);

        flagTV = view.findViewById(R.id.flagTV);

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        flag.setOnClickListener(v -> {

            flagIV.setImageResource(R.drawable.flag_red);

            flagTV.setText("Unflag that data is wrong");

        });

        btnYourClaim.setOnClickListener(v -> {

            bottomSheetDialog.dismiss();

            detailDialog.dismiss();

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            startActivity(new Intent(getContext(), ClaimBar.class));

        });

    }

    public void SetRecyclerView() {

//        //Create a List of Child DataModel
//        List<Child> childList = new ArrayList<>();
//
//        childList.add(new Child("Caña", "\u20ac7", R.drawable.list_image1));
//
//        childList.add(new Child("Doble", "\u20ac7", R.drawable.list_image2));
//
//        //Create a List of SectionHeader DataModel implements SectionHeader
//        sectionHeaders = new ArrayList<>();
//
//        sectionHeaders.add(new SectionHeader(childList, "Cerveza", 4));
//
//        childList = new ArrayList<>();
//
//        childList.add(new Child("Absolut Vodka", "\u20ac7", R.drawable.list_image3));
//        childList.add(new Child("Havana Club", "\u20ac7", R.drawable.list_image4));
//        childList.add(new Child("Jameson", "\u20ac7", R.drawable.list_image5));
//        childList.add(new Child("Bombay Sapphire", "\u20ac7", R.drawable.list_image6));
//        childList.add(new Child("Tanqueray", "\u20ac7", R.drawable.list_image7));
//
//        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 2));
//
//        childList = new ArrayList<>();
//
//        childList.add(new Child("Brugal Anejo", "\u20ac7", R.drawable.list_image8));
//        childList.add(new Child("Nordés", "\u20ac7", R.drawable.list_image9));
//
//        sectionHeaders.add(new SectionHeader(childList, "High roller", 1));
//
//        childList = new ArrayList<>();
//
//        childList.add(new Child("Larios", "\u20ac7", R.drawable.list_image10));
//        childList.add(new Child("Captain Morgan", "\u20ac7", R.drawable.list_image11));
//
//        sectionHeaders.add(new SectionHeader(childList, "Copas subnormales", 3));
//
//
//        adapterRecycler = new AdapterSectionRecycler(getContext(), sectionHeaders);
//
//        Home.binding.bottomSheetInclude.rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        Home.binding.bottomSheetInclude.rvDrinks.setAdapter(adapterRecycler);
//
//        Home.binding.bottomSheetInclude.rvDrinks.setHasFixedSize(true);

    }

    public void SetRecyclerViewDialog() {

        //Create a List of Child DataModel
//        List<Child> childList = new ArrayList<>();
//
//        childList.add(new Child("Caña", "\u20ac7", R.drawable.list_image1));
//        childList.add(new Child("Doble", "\u20ac7", R.drawable.list_image2));
//        //Create a List of SectionHeader DataModel implements SectionHeader
//        sectionHeaders = new ArrayList<>();
//        sectionHeaders.add(new SectionHeader(childList, "Cerveza", 4));
//
//        childList = new ArrayList<>();
//        childList.add(new Child("Absolut Vodka", "\u20ac7", R.drawable.list_image3));
//        childList.add(new Child("Havana Club", "\u20ac7", R.drawable.list_image4));
//        childList.add(new Child("Jameson", "\u20ac7", R.drawable.list_image5));
//        childList.add(new Child("Bombay Sapphire", "\u20ac7", R.drawable.list_image6));
//        childList.add(new Child("Tanqueray", "\u20ac7", R.drawable.list_image7));
//        sectionHeaders.add(new SectionHeader(childList, "Capas Normales", 2));
//
//        childList = new ArrayList<>();
//        childList.add(new Child("Brugal Anejo", "\u20ac7", R.drawable.list_image8));
//        childList.add(new Child("Nordés", "\u20ac7", R.drawable.list_image9));
//        sectionHeaders.add(new SectionHeader(childList, "High roller", 1));
//
//        childList = new ArrayList<>();
//        childList.add(new Child("Larios", "\u20ac7", R.drawable.list_image10));
//        childList.add(new Child("Captain Morgan", "\u20ac7", R.drawable.list_image11));
//        sectionHeaders.add(new SectionHeader(childList, "Copas subnormales", 3));
//
//
//        dialogBinding.rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        dialogBinding.rvDrinks.setAdapter(adapterRecycler);
//
//        dialogBinding.rvDrinks.setHasFixedSize(true);

    }

    private void bottomSheet() {

        Home.binding.bottomSheetInclude.secItem.bottleIV.setImageResource(R.drawable.bombay_drink);

        Home.binding.bottomSheetInclude.thirdItem.bottleIV.setImageResource(R.drawable.bombay_drink_three);

        bottomSheetBehavior = BottomSheetBehavior.from(Home.binding.bottomSheet);

        bottomSheetBehavior.setDraggable(false);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {

                    case BottomSheetBehavior.STATE_COLLAPSED:

                        setViewOnCollapse();

                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:

                        openDetailDialog();

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:

                        setViewOnCollapse();

                        break;

                    case BottomSheetBehavior.STATE_SETTLING:

                        setViewOnCollapse();

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    public void setViewOnCollapse() {

        Home.binding.bottomSheet.setBackground(getResources().getDrawable(R.drawable.rounder_corner_white_two_top));

        Home.binding.bottomSheetInclude.appBarLine.mainCL.setVisibility(View.VISIBLE);

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        this.mapboxMap.addOnMapClickListener(point -> {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            return false;

        });

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

            mapStyle = style;

            enableLocationComponent(style);

        });

        mapboxMap.getUiSettings().setCompassEnabled(false);

        mapboxMap.getUiSettings().setAttributionEnabled(false);

        mapboxMap.getUiSettings().setLogoEnabled(false);

        mapboxMap.cancelAllVelocityAnimations();


    }

    //@SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {

        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            SharedPreferences pref1 = getContext().getSharedPreferences("MyPref", 0);

            SharedPreferences.Editor editor = pref1.edit();

            editor.putString("checkLocation", "allow");

            editor.commit();

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);


            if (SessionManager.getInstance().getLastLoc(context) != null) {

                String[] data = SessionManager.getInstance().getLastLoc(context).split("#");

                LatLng location = new LatLng(Double.parseDouble(data[0]), Double.parseDouble(data[1]));

                CameraPosition position = new CameraPosition.Builder()
                        .target(location) // Sets the new camera position
                        .zoom(Double.parseDouble(data[2])) // Sets the zoom
                        .bearing(0) // Rotate the camera
                        .tilt(Double.parseDouble(data[3])) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

//                mapboxMap.animateCamera(CameraUpdateFactory
//                        .newCameraPosition(position), 1000);

                mapboxMap.setCameraPosition(position);

            } else {

                lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();

                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())) // Sets the new camera position
                        .zoom(12) // Sets the zoom
                        .bearing(0) // Rotate the camera
                        .tilt(1) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 4000);
            }

            getAllStore();
            startTimer();


            mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {


                    CameraPosition d = mapboxMap.getCameraPosition();

                    double zoom = d.zoom;

                    double tilt = d.tilt;

                    SessionManager.getInstance().setLastLoc(context, d.target.getLatitude() + "#" + d.target.getLongitude() + "#" + zoom + "#" + tilt);

                }
            });



            mapboxMap.setOnMarkerClickListener(this);


        } else {

            SharedPreferences pref1 = getContext().getSharedPreferences("MyPref", 0);

            SharedPreferences.Editor editor = pref1.edit();

            editor.putString("checkLocation", "cancel");

            editor.commit();

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(getActivity());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

        Toast.makeText(getContext(), "Location permission required", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) {

            mapboxMap.getStyle(style -> enableLocationComponent(style));

        } else {

            Toast.makeText(getContext(), "Location permission required", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {

        super.onStart();

        binding.mapView.onStart();

        EventBus.getDefault().register(this);

    }

    @Override
    public void onResume() {

        super.onResume();

        binding.mapView.onResume();

//        getAllStore();

    }

    @Override
    public void onPause() {

        super.onPause();

        binding.mapView.onPause();

    }

    @Override
    public void onStop() {

        super.onStop();

        binding.mapView.onStop();

        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        openDetailDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        binding.mapView.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        binding.mapView.onDestroy();

    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();

        binding.mapView.onLowMemory();

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        ExploreFragment.checkOpen = false;

        setSmallBottomSheetView(marker.getTitle());

        markerTitleValue = marker.getTitle();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        return true;

    }

    public void setDetailDialogView(String title) {

        DetailModelForAddBar detail = new Gson().fromJson(currentSelectedMarkerResultItem.getBdetail(), DetailModelForAddBar.class);

        dialogBinding.barNameTV.setText(currentSelectedMarkerResultItem.getBname());

        if (currentSelectedMarkerResultItem.getImgeUrl() != null) {

            Picasso.with(context).load(currentSelectedMarkerResultItem.getImgeUrl()).error(R.drawable.thumbnail_image)
                    .into(dialogBinding.mainIV);

        }

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String eTime = "0";
        String[] data = eTime.split("#");

        switch (day) {
            case Calendar.MONDAY:

                eTime = todayEndTime(detail, "Monday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    dialogBinding.statusTV.setText("Open");
                    dialogBinding.statusTV.setTextColor(Color.GREEN);
                    dialogBinding.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    dialogBinding.statusTV.setText("Close");
                    dialogBinding.statusTV.setTextColor(Color.RED);
                    dialogBinding.closingTimeTV.setText(" - Open at " + data[0]);

                }

                break;
            case Calendar.TUESDAY:

                eTime = todayEndTime(detail, "Tuesday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    dialogBinding.statusTV.setText("Open");
                    dialogBinding.statusTV.setTextColor(Color.GREEN);
                    dialogBinding.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    dialogBinding.statusTV.setText("Close");
                    dialogBinding.statusTV.setTextColor(Color.RED);
                    dialogBinding.closingTimeTV.setText(" - Open at " + data[0]);

                }

                break;
            case Calendar.WEDNESDAY:

                eTime = todayEndTime(detail, "Wednesday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    dialogBinding.statusTV.setText("Open");
                    dialogBinding.statusTV.setTextColor(Color.GREEN);
                    dialogBinding.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    dialogBinding.statusTV.setText("Close");
                    dialogBinding.statusTV.setTextColor(Color.RED);
                    dialogBinding.closingTimeTV.setText(" - Open at " + data[0]);

                }

                break;
            case Calendar.THURSDAY:

                eTime = todayEndTime(detail, "Thursday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    dialogBinding.statusTV.setText("Open");
                    dialogBinding.statusTV.setTextColor(Color.GREEN);
                    dialogBinding.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    dialogBinding.statusTV.setText("Close");
                    dialogBinding.statusTV.setTextColor(Color.RED);
                    dialogBinding.closingTimeTV.setText(" - Open at " + data[0]);

                }

                break;
            case Calendar.FRIDAY:

                eTime = todayEndTime(detail, "Friday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    dialogBinding.statusTV.setText("Open");
                    dialogBinding.statusTV.setTextColor(Color.GREEN);
                    dialogBinding.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    dialogBinding.statusTV.setText("Close");
                    dialogBinding.statusTV.setTextColor(Color.RED);
                    dialogBinding.closingTimeTV.setText(" - Open at " + data[0]);

                }
                break;
            case Calendar.SATURDAY:

                eTime = todayEndTime(detail, "Saturday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    dialogBinding.statusTV.setText("Open");
                    dialogBinding.statusTV.setTextColor(Color.GREEN);
                    dialogBinding.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    dialogBinding.statusTV.setText("Close");
                    dialogBinding.statusTV.setTextColor(Color.RED);
                    dialogBinding.closingTimeTV.setText(" - Open at " + data[0]);

                }

                break;
            case Calendar.SUNDAY:

                eTime = todayEndTime(detail, "Sunday");

                data = eTime.split("#");
                if (data[1].equals("Open")) {

                    dialogBinding.statusTV.setText("Open");
                    dialogBinding.statusTV.setTextColor(Color.GREEN);
                    dialogBinding.closingTimeTV.setText(" - Close at " + data[0]);

                } else {

                    dialogBinding.statusTV.setText("Close");
                    dialogBinding.statusTV.setTextColor(Color.RED);
                    dialogBinding.closingTimeTV.setText(" - Open at " + data[0]);

                }
                break;
        }

        if (detail.getFreeTable() != null) {
            if (detail.getFreeTable().isFreeTable()) {

                dialogBinding.freeTableView.setVisibility(View.VISIBLE);

            } else {

                dialogBinding.freeTableView.setVisibility(View.INVISIBLE);

            }
        } else {

            dialogBinding.freeTableView.setVisibility(View.INVISIBLE);

        }


        if (detail.getBarAnounce() != null) {

            dialogBinding.announceView.setVisibility(View.VISIBLE);

            dialogBinding.announceTV.setText(detail.getBarAnounce().getName());

        } else {

            dialogBinding.announceView.setVisibility(View.INVISIBLE);

        }

        for (int i = 0; i < detail.getBarBottle().size(); i++) {

            if (detail.getBarBottle().get(i).getDrinkName().equals("Tanqueray")) {

                dialogBinding.tanquerayItem.bottleIV.setImageResource(R.drawable.d1);

                dialogBinding.tanquerayItem.nameTV.setText("Tanqueray");

                if (detail.getBarBottle().get(i).getDrinkPrice().contains("-"))
                    dialogBinding.tanquerayItem.amountTV.setText("-");
                else
                    dialogBinding.tanquerayItem.amountTV.setText(detail.getBarBottle().get(i).getDrinkPrice() + "\u20ac");

            } else if (detail.getBarBottle().get(i).getDrinkName().equals("Beefeater")) {

                dialogBinding.beefeaterItem.bottleIV.setImageResource(R.drawable.d2);

                dialogBinding.beefeaterItem.nameTV.setText("Beefeater");

                if (detail.getBarBottle().get(i).getDrinkPrice().contains("-"))
                    dialogBinding.beefeaterItem.amountTV.setText("-");
                else
                    dialogBinding.beefeaterItem.amountTV.setText(detail.getBarBottle().get(i).getDrinkPrice() + "\u20ac");


            } else if (detail.getBarBottle().get(i).getDrinkName().equals("Brugal Añejo")) {

                dialogBinding.brugalItem.bottleIV.setImageResource(R.drawable.d3);

                dialogBinding.brugalItem.nameTV.setText("Brugal Añejo");

                if (detail.getBarBottle().get(i).getDrinkPrice().contains("-"))
                    dialogBinding.brugalItem.amountTV.setText("-");
                else
                    dialogBinding.brugalItem.amountTV.setText(detail.getBarBottle().get(i).getDrinkPrice() + "\u20ac");


            } else if (detail.getBarBottle().get(i).getDrinkName().equals("Caña")) {

                if (detail.getBarBottle().get(i).getDrinkPrice().contains("-"))
                    dialogBinding.canaPriceTV.setText("-");
                else
                    dialogBinding.canaPriceTV.setText(detail.getBarBottle().get(i).getDrinkPrice() + "\u20ac");


            } else if (detail.getBarBottle().get(i).getDrinkName().equals("Doble")) {

                if (detail.getBarBottle().get(i).getDrinkPrice().contains("-"))
                    dialogBinding.doblePriceTV.setText("-");
                else
                    dialogBinding.doblePriceTV.setText(detail.getBarBottle().get(i).getDrinkPrice() + "\u20ac");


            }

            getBottleList(detail);

        }

    }

    private void getBottleList(DetailModelForAddBar detail) {

        if (detail.getBarBottle() != null) {

            String normal = Constant.normals;
            String highRoller = Constant.highRoller;
            String warTime = Constant.wartime;

            normalBottleList.clear();
            highRollerBottleList.clear();
            warTimeBottleList.clear();

            for (BarBottleItem barBottleItem : detail.getBarBottle()) {

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

    public void setRecyclerView() {

        dialogBinding.rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));

        dialogBinding.rvDrinks.setHasFixedSize(true);

        sectionHeaders = new ArrayList<>();

        if (normalBottleList.size() > 0) {

            sectionHeaders.add(new SectionHeader(normalBottleList, "Normales", 2));

        }

        if (highRollerBottleList.size() > 0) {

            sectionHeaders.add(new SectionHeader(highRollerBottleList, "High roller", 1));

        }

        if (warTimeBottleList.size() > 0) {

            sectionHeaders.add(new SectionHeader(warTimeBottleList, "War time", 3));

        }

        adapterRecycler = new AdapterSectionRecycler(getContext(), sectionHeaders);

        dialogBinding.rvDrinks.setAdapter(adapterRecycler);
    }

    String TAG = "Explore";

    private void getAllStore() {

        mapboxMap.clear();


//
//        Marker currentLocation = mapboxMap.addMarker(new MarkerOptions().title("00")
//                .icon(IconFactory.getInstance(getContext()).fromResource(R.drawable.marker_light_blue))
//                .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));


        int random = new Random().nextInt(61) + 20;
        Controller.getApi().getAllStore(random)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.d(TAG, "onResponse: getAllStore");

                        if (response.body() != null) {

                            getAllStoreResponse = new Gson().fromJson(response.body(), GetAllStoreResponse.class);

                            jsonData = response.body();

                            if (getAllStoreResponse.getResult().size() > 0) {

                                for (ResultItem resultItem : getAllStoreResponse.getResult()) {


                                    if (isLiveData) {

                                        Log.d(TAG, "onResponse: isLiveData");

                                        calculateAndAddLiveMarkers(resultItem);

                                    } else {

                                        Log.d(TAG, "onResponse: Not live");

                                        calculateAndAddAllMarker(resultItem);

                                    }


                                }
                            }

                            Gson gson = new Gson();

                            String jsonData = gson.toJson(getAllStoreResponse);

                            Log.d("getAllStoreResponse", jsonData);

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("appRegisterResponse", "onFailure exception" + t.getLocalizedMessage());


                    }
                });
    }

    private void calculateAndAddLiveMarkers(ResultItem resultItem) {

        try {

            Double lat = Double.parseDouble(resultItem.getBlat().toString());

            Double lng = Double.parseDouble(resultItem.getBlng().toString());

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;

            Bitmap bmp = Bitmap.createBitmap(220, 220, conf);

            Canvas canvas1 = new Canvas(bmp);

            Paint color = new Paint();

            color.setTextSize(25);

            color.setColor(Color.BLACK);

            Log.d("jsonId", resultItem.getId());

            DetailModelForAddBar detail = new Gson().fromJson(resultItem.getBdetail(), DetailModelForAddBar.class);

            String markerPrice = getPrice(detail);

            if (detail.getBarHas() != null && detail.getBarHas().get(1).isIsSelected()) {

                if (detail.getBarAnounce() != null) {

                    canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_light_blue_rooftop), 0, 0, color);

                } else if (detail.getFreeTable() != null) {
                    if (detail.getFreeTable().isFreeTable()) {
                        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_light_red_rooftop), 0, 0, color);

                    } else {
                        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_light_simple_rooftop), 0, 0, color);
                    }

                } else {

                    canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_light_simple_rooftop), 0, 0, color);

                }

            } else {

                if (detail.getBarAnounce() != null) {

                    canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_light_blue), 0, 0, color);

                } else if (detail.getFreeTable() != null) {

                    if (detail.getFreeTable().isFreeTable()) {

                        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_light_red), 0, 0, color);

                    } else {

                        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_light_simple), 0, 0, color);

                    }


                } else {

                    canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_light_simple), 0, 0, color);

                }

                if (markerPrice.length() == 1) {

                    canvas1.drawText(markerPrice + "\u20ac", 105, 117, color);

                } else if (markerPrice.length() == 2) {

                    canvas1.drawText(markerPrice + "\u20ac", 98, 117, color);

                } else if (markerPrice.length() == 3) {

                    canvas1.drawText(markerPrice + "\u20ac", 90, 117, color);

                }


            }

            Calendar calendar = Calendar.getInstance();

            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (day) {
                case Calendar.MONDAY:

                    if (isValidTime(detail, "Monday")) {

                        Gson gson = new Gson();

                        String json = gson.toJson(resultItem);

                        addMarkerAsSelectedFilter(resultItem, detail, lat, lng, bmp);

                    }

                    break;
                case Calendar.TUESDAY:

                    if (isValidTime(detail, "Tuesday")) {

                        Gson gson = new Gson();

                        String json = gson.toJson(resultItem);

                        addMarkerAsSelectedFilter(resultItem, detail, lat, lng, bmp);
                    }

                    break;
                case Calendar.WEDNESDAY:

                    if (isValidTime(detail, "Wednesday")) {

                        Gson gson = new Gson();

                        String json = gson.toJson(resultItem);

                        addMarkerAsSelectedFilter(resultItem, detail, lat, lng, bmp);

                    }

                    break;
                case Calendar.THURSDAY:

                    if (isValidTime(detail, "Thursday")) {

                        Gson gson = new Gson();

                        String json = gson.toJson(resultItem);

                        addMarkerAsSelectedFilter(resultItem, detail, lat, lng, bmp);

                    }

                    break;
                case Calendar.FRIDAY:

                    if (isValidTime(detail, "Friday")) {

                        Gson gson = new Gson();

                        String json = gson.toJson(resultItem);

                        addMarkerAsSelectedFilter(resultItem, detail, lat, lng, bmp);

                    }

                    break;
                case Calendar.SATURDAY:


                    if (isValidTime(detail, "Saturday")) {

                        Log.d("callMarker", "call");

                        Gson gson = new Gson();

                        String json = gson.toJson(resultItem);

                        addMarkerAsSelectedFilter(resultItem, detail, lat, lng, bmp);

                    }

                    break;
                case Calendar.SUNDAY:

                    if (isValidTime(detail, "Sunday")) {

                        Gson gson = new Gson();

                        String json = gson.toJson(resultItem);

                        addMarkerAsSelectedFilter(resultItem, detail, lat, lng, bmp);

                    }

                    break;
            }

//           .icon(IconFactory.getInstance(getContext()).fromResource(R.drawable.marker_light_blue))

            Log.d("getAllStoreResponse", "" + lat + "/" + lng);

        } catch (Exception e) {

            Log.d("getAllStoreResponse", "Error" + e.getLocalizedMessage());

        }

    }

    public void addMarkerAsSelectedFilter(ResultItem resultItem, DetailModelForAddBar detail, Double lat, Double lng, Bitmap bmp) {

        Gson gson = new Gson();

        String json = gson.toJson(resultItem);

        if (SessionManager.getInstance().getFilter(context).equals("No")) {

            Marker marker = mapboxMap.addMarker(new MarkerOptions().title(resultItem.getBname())
                    .icon(IconFactory.getInstance(getContext()).fromBitmap(bmp))
                    .setTitle(json)
                    .position(new LatLng(lat, lng)));



        } else if (SessionManager.getInstance().getFilter(context).equals("Both")) {

            if (detail.getBarHas() != null) {

                if (detail.getBarHas().get(0).isIsSelected() && detail.getBarHas().get(1).isIsSelected()) {

                    Marker marker = mapboxMap.addMarker(new MarkerOptions().title(resultItem.getBname())
                            .icon(IconFactory.getInstance(getContext()).fromBitmap(bmp))
                            .setTitle(json)
                            .position(new LatLng(lat, lng)));


                }

            }

        } else if (SessionManager.getInstance().getFilter(context).equals("BarWithTerrace")) {


            if (detail.getBarHas() != null) {

                if (detail.getBarHas().get(0).isIsSelected()) {

                    Marker marker = mapboxMap.addMarker(new MarkerOptions().title(resultItem.getBname())
                            .icon(IconFactory.getInstance(getContext()).fromBitmap(bmp))
                            .setTitle(json)
                            .position(new LatLng(lat, lng)));

                }
            }


        } else if (SessionManager.getInstance().getFilter(context).equals("Rooftop")) {

            if (detail.getBarHas() != null) {

                if (detail.getBarHas().get(1).getTitle().equals("Rooftops")) {

                    if (detail.getBarHas().get(1).isIsSelected()) {

                        Marker marker = mapboxMap.addMarker(new MarkerOptions().title(resultItem.getBname())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(bmp))
                                .setTitle(json)
                                .position(new LatLng(lat, lng)));

                    }

                }

            }
        }

    }

    private void calculateAndAddAllMarker(ResultItem resultItem) {

        try {

            Double lat = Double.parseDouble(resultItem.getBlat().toString());

            Double lng = Double.parseDouble(resultItem.getBlng().toString());

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;

            Bitmap bmp = Bitmap.createBitmap(220, 220, conf);

            Canvas canvas1 = new Canvas(bmp);

            Paint color = new Paint();

            color.setTextSize(25);

            color.setColor(Color.BLACK);

            DetailModelForAddBar detail = new Gson().fromJson(resultItem.getBdetail(), DetailModelForAddBar.class);

            String markerPrice = getPrice(detail);

            if (detail.getBarHas() != null && detail.getBarHas().get(1).isIsSelected()) {

                if (detail.getBarAnounce() != null) {

                    canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_light_blue_rooftop), 0, 0, color);

                } else if (detail.getFreeTable() != null) {

                    if (detail.getFreeTable().isFreeTable()) {

                        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_light_red_rooftop), 0, 0, color);

                    } else {

                        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_light_simple_rooftop), 0, 0, color);

                    }


                } else {

                    canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_light_simple_rooftop), 0, 0, color);

                }

            } else {

                if (detail.getBarAnounce() != null) {

                    canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_light_blue), 0, 0, color);

                } else if (detail.getFreeTable() != null) {

                    if (detail.getFreeTable().isFreeTable()) {

                        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_light_red), 0, 0, color);

                    } else {

                        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_light_simple), 0, 0, color);

                    }

                } else {

                    canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_light_simple), 0, 0, color);

                }

                if (markerPrice.length() == 1) {

                    canvas1.drawText(markerPrice + "\u20ac", 105, 117, color);

                } else if (markerPrice.length() == 2) {

                    canvas1.drawText(markerPrice + "\u20ac", 98, 117, color);

                } else if (markerPrice.length() == 3) {

                    canvas1.drawText(markerPrice + "\u20ac", 90, 117, color);

                }


            }

            Gson gson = new Gson();

            String json = gson.toJson(resultItem);

            if (SessionManager.getInstance().getFilter(context).equals("No")) {

                Marker marker = mapboxMap.addMarker(new MarkerOptions().title(resultItem.getBname())
                        .icon(IconFactory.getInstance(getContext()).fromBitmap(bmp))
                        .setTitle(json)
                        .position(new LatLng(lat, lng)));


            } else if (SessionManager.getInstance().getFilter(context).equals("Both")) {

                if (detail.getBarHas() != null) {

                    if (detail.getBarHas().get(0).isIsSelected() && detail.getBarHas().get(1).isIsSelected()) {

                        Marker marker = mapboxMap.addMarker(new MarkerOptions().title(resultItem.getBname())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(bmp))
                                .setTitle(json)
                                .position(new LatLng(lat, lng)));


                    }

                }

            } else if (SessionManager.getInstance().getFilter(context).equals("BarWithTerrace")) {

                if (detail.getBarHas() != null) {

                    if (detail.getBarHas().get(0).isIsSelected()) {

                        Marker marker = mapboxMap.addMarker(new MarkerOptions().title(resultItem.getBname())
                                .icon(IconFactory.getInstance(getContext()).fromBitmap(bmp))
                                .setTitle(json)
                                .position(new LatLng(lat, lng)));

                    }
                }


            } else if (SessionManager.getInstance().getFilter(context).equals("Rooftop")) {

                if (detail.getBarHas() != null) {

                    if (detail.getBarHas().get(1).getTitle().equals("Rooftops")) {

                        if (detail.getBarHas().get(1).isIsSelected()) {

                            Marker marker = mapboxMap.addMarker(new MarkerOptions().title(resultItem.getBname())
                                    .icon(IconFactory.getInstance(getContext()).fromBitmap(bmp))
                                    .setTitle(json)
                                    .position(new LatLng(lat, lng)));

                        }

                    }
                }

            }


//           .icon(IconFactory.getInstance(getContext()).fromResource(R.drawable.marker_light_blue))

            Log.d("getAllStoreResponse", "" + lat + "/" + lng);

        } catch (Exception e) {

            Log.d("getAllStoreResponse", "Error" + e.getLocalizedMessage());

        }

    }

    private String todayEndTime(DetailModelForAddBar detail, String day) {

        String time = "0";

        boolean isNext = false;

        int size = detail.getBarWeekDay().size();

        for (int i = 0; i < detail.getBarWeekDay().size(); i++) {

            if (!isNext) {

                if (detail.getBarWeekDay().get(i).getWeekDay() != null) {

                    if (detail.getBarWeekDay().get(i).getWeekDay().equals(day)) {

                        time = getTime(detail.getBarWeekDay().get(i).getEvalue());

                        if (time == null || time.equals("") || time.equals("0")) {

                            isNext = true;

                        } else {

                            if (isValidTime(detail, day))
                                return time + "#Open";
                            else
                                isNext = true;

                        }

                    }

                }
            } else {

                time = getTime(detail.getBarWeekDay().get(i).getSvalue());

                if (time == null || time.equals("") || time.equals("0")) {

                    isNext = true;

                } else {

                    return time + "#Close";

                }

            }

        }
        for (int i = 0; i < detail.getBarWeekDay().size(); i++) {

            if (detail.getBarWeekDay().get(i).getWeekDay().equals(day)) {

                return time + "#Close";

            } else {

                time = getTime(detail.getBarWeekDay().get(i).getSvalue());

                if (time == null || time.equals("") || time.equals("0")) {


                } else {

                    return time + "#Close";

                }

            }

        }

        return time + "#Close";

    }

    private boolean isValidTime(DetailModelForAddBar detail, String day) {

        boolean check = false;
        try {

            for (int i = 0; i < detail.getBarWeekDay().size(); i++) {

                if (detail.getBarWeekDay().get(i).getWeekDay().equals(day)) {

                    Long tsLong = System.currentTimeMillis();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

                    String currentTime = getTime(tsLong);

                    String endTime = getTime(detail.getBarWeekDay().get(i).getEvalue());

                    String[] dataCurrent = currentTime.split(":");

                    String[] dataEnd = endTime.split(":");

                    if (Integer.parseInt(dataCurrent[0]) < Integer.parseInt(dataEnd[0])) {

                        Log.d("timeCheck", "Time is: " + tsLong + "/" + detail.getBarWeekDay().get(i).getEvalue());


                        Date date1 = simpleDateFormat.parse(getTime(tsLong));

                        Date date2 = simpleDateFormat.parse(getTime(detail.getBarWeekDay().get(i).getEvalue()));

                        long difference = date2.getTime() - date1.getTime();

                        int days = (int) (difference / (1000 * 60 * 60 * 24));

                        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));

                        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);

                        hours = (hours < 0 ? -hours : hours);
                        min = (min < 0 ? -min : min);

                        Log.d("twoDiffchecktime", " :: " + hours);

                        if (min > 0) {

                            check = true;

                        }

                    } else {

                        check = false;

                    }


                }
            }


        } catch (Exception e) {

            Toast.makeText(context, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        }


        return check;

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

    private String getPrice(DetailModelForAddBar detail) {

        String finalPrice = "0";

        List<String> priceList = new ArrayList<>();

        List<SearchPriceModel> priceModelList = new ArrayList<>();

        for (int i = 0; i < detail.getBarBottle().size(); i++) {

            String price = detail.getBarBottle().get(i).getDrinkPrice();

            if (price.contains(",")) {

                price = price.replace(",", ".");

            }

            String formattedPrice = price;

            if (price.contains(".")) {

                String[] data = price.split("\\.");

                if (Integer.parseInt(data[1]) <= 25) {

                    formattedPrice = data[0];

                }

                if (Integer.parseInt(data[1]) >= 75) {

                    int p = Integer.parseInt(data[0]) + 1;
                    formattedPrice = p + "";

                }

                if (Integer.parseInt(data[1]) > 25 && Integer.parseInt(data[1]) < 75) {

                    formattedPrice = data[0] + ".5";

                }

            }

            if (!formattedPrice.contains("-")) {
                priceList.add(formattedPrice);
            }


        }

        if (priceList.size() <= 0)
            return "0";

        List<String> asList = priceList;

        Set<String> mySet = new HashSet<String>(asList);

        for (String s : mySet) {

            // Log.d("repeted", s + "number / id "+resultItem.getId()+" time /" +Collections.frequency(asList,s));

            SearchPriceModel searchPriceModel = new SearchPriceModel();
            searchPriceModel.setNumber(Double.parseDouble(s));
            searchPriceModel.setCount(Collections.frequency(asList, s));

            priceModelList.add(searchPriceModel);

        }

        SearchPriceModel maxRepeated = priceModelList.get(0);

        int check = 0;

        for (int i = 0; i < priceModelList.size(); i++) {
            if (priceModelList.get(i).getCount() == 1) {
                check++;
            }
        }

        if (check == priceModelList.size()) {

            for (int i = 0; i < priceModelList.size(); i++) {

                if (maxRepeated.getNumber() > priceModelList.get(i).getNumber()) {
                    maxRepeated = priceModelList.get(i);
                }

            }

        } else {

            for (int i = 0; i < priceModelList.size(); i++) {

                if (maxRepeated.getCount() < priceModelList.get(i).getCount()) {
                    maxRepeated = priceModelList.get(i);
                } else if (maxRepeated.getCount() == priceModelList.get(i).getCount()) {
                    if (maxRepeated.getNumber() > priceModelList.get(i).getNumber()) {
                        maxRepeated = priceModelList.get(i);
                    }
                }

            }

        }

//        Log.d("repeted", resultItem.getId()+" value is "+maxRepeated.getNumber());

        finalPrice = String.valueOf(maxRepeated.getNumber());

        if (finalPrice.contains(".")) {
            String[] d = finalPrice.split("\\.");
            finalPrice = d[0];
        }

        return finalPrice;

    }

    public void startTimer() {


        int toastDurationInMilliSeconds = 1200000;


        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 60000 /*Tick duration*/) {

            public void onTick(long millisUntilFinished) {

                getAllStoreUpdated();

                Log.d("tickTimer", " # Tick");

            }

            public void onFinish() {


            }

        };


        timerReady = true;

        toastCountDown.start();


    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (timerReady)
            toastCountDown.cancel();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }


    private void getAllStoreUpdated() {

        int random = new Random().nextInt(61) + 20;
        Controller.getApi().getAllStore(random)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null) {


                            if (getAllStoreResponse != null) {
                                GetAllStoreResponse newGetAllStoreResponse = new Gson().fromJson(response.body(), GetAllStoreResponse.class);




                                if (jsonData == null || !jsonData.equalsIgnoreCase(response.body())) {

                                    jsonData = response.body();

                                    mapboxMap.clear();

                                    getAllStoreResponse = new Gson().fromJson(response.body(), GetAllStoreResponse.class);

                                    if (getAllStoreResponse.getResult().size() > 0) {

                                        for (ResultItem resultItem : getAllStoreResponse.getResult()) {

                                            if (isLiveData) {

                                                calculateAndAddLiveMarkers(resultItem);

                                            } else {

                                                calculateAndAddAllMarker(resultItem);

                                            }

                                        }
                                    }

                                }

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("appRegisterResponse", "onFailure exception" + t.getLocalizedMessage());


                    }
                });
    }

}