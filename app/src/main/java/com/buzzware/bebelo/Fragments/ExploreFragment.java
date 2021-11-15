package com.buzzware.bebelo.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.bebelo.Activities.ClaimBar;
import com.buzzware.bebelo.Activities.Home;
import com.buzzware.bebelo.Addapter.AdapterSectionRecycler;
import com.buzzware.bebelo.Model.Child;
import com.buzzware.bebelo.Model.SectionHeader;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.RelativeLayoutTouchListener;
import com.buzzware.bebelo.databinding.BottomSheetExploreDialogBinding;
import com.buzzware.bebelo.databinding.FragmentExploreBinding;
import com.buzzware.bebelo.eventBusModel.MessageEvent;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment implements
        OnMapReadyCallback,
        PermissionsListener,
        MapboxMap.OnMarkerClickListener {

    FragmentExploreBinding binding;

    Context context;

    public static boolean checkOpen = false;

    SharedPreferences pref;

    private PermissionsManager permissionsManager;

    private MapboxMap mapboxMap;

    Style mapStyle;

    Location lastKnownLocation;

    BottomSheetExploreDialogBinding dialogBinding;

    BottomSheetBehavior bottomSheetBehavior;

    AdapterSectionRecycler adapterRecycler;

    List<SectionHeader> sectionHeaders;

    String markerTitelValue = "1";

    public ExploreFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        binding = FragmentExploreBinding.inflate(inflater);

        Init();

        bottomSheet();

        setClickListener();

        SetRecyclerView();

        Home.binding.bottomSheetInclude.dataRL.setOnTouchListener(new RelativeLayoutTouchListener(ExploreFragment.this));

        pref = getContext().getSharedPreferences("MyPref", 0);

        binding.mapView.onCreate(savedInstanceState);

        binding.mapView.getMapAsync(this);

        return binding.getRoot();

    }

    private void setClickListener() {

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

    }

    public void openDetailDialog() {

        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_NoTitleBar_Fullscreen);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation; //style id

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        dialogBinding = BottomSheetExploreDialogBinding.inflate(LayoutInflater.from(getContext()));

        dialog.setContentView(dialogBinding.getRoot());

        setDetailDialogView(markerTitelValue);

        dialogBinding.secItem.bottleIV.setImageResource(R.drawable.bombay_drink);

        dialogBinding.thirdItem.bottleIV.setImageResource(R.drawable.bombay_drink_three);

        dialogBinding.appBarMenu.menuIV.setOnClickListener(v -> {

            OpenBottomDialog();

        });

        SetRecyclerViewDilog();

        dialogBinding.bottomLay.setOnClickListener(v -> {

            dialogBinding.rvDrinks.setVisibility(View.VISIBLE);

            dialogBinding.bottomLay.setVisibility(View.GONE);

            dialogBinding.bottomLayLess.setVisibility(View.VISIBLE);

        });

        dialogBinding.appBarMenu.backIV.setOnClickListener(v -> {

            dialog.dismiss();

        });

        dialogBinding.bottomLayLess.setOnClickListener(v -> {

            dialogBinding.appBarMenu.menuIV.requestFocus();

            dialogBinding.rvDrinks.setVisibility(View.GONE);

            dialogBinding.bottomLay.setVisibility(View.VISIBLE);

            dialogBinding.bottomLayLess.setVisibility(View.GONE);

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

            startActivity(new Intent(getContext(), ClaimBar.class));

        });

    }

    public void SetRecyclerView() {

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

        Home.binding.bottomSheetInclude.rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));

        Home.binding.bottomSheetInclude.rvDrinks.setAdapter(adapterRecycler);

        Home.binding.bottomSheetInclude.rvDrinks.setHasFixedSize(true);

    }

    public void SetRecyclerViewDilog() {

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


        dialogBinding.rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));

        dialogBinding.rvDrinks.setAdapter(adapterRecycler);

        dialogBinding.rvDrinks.setHasFixedSize(true);

    }


    private void bottomSheet() {

        Home.binding.bottomSheetInclude.secItem.bottleIV.setImageResource(R.drawable.bombay_drink);

        Home.binding.bottomSheetInclude.thirdItem.bottleIV.setImageResource(R.drawable.bombay_drink_three);

        bottomSheetBehavior = BottomSheetBehavior.from(Home.binding.bottomSheet);

        bottomSheetBehavior.setDraggable(false);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
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
            public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    private void Init() {

        context = getContext();

    }


    public void setBottomSheetView(String title) {

        if (title.equals("1")) {

            Home.binding.bottomSheetInclude.mainIV.setImageResource(R.drawable.bar_image1);
            Home.binding.bottomSheetInclude.redView.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.blueLineRL.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.micIV.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.colorIV.setImageResource(R.drawable.blue_glow);
            Home.binding.bottomSheetInclude.djTV.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.pinkBackTV.setVisibility(View.INVISIBLE);

        }
        if (title.equals("2")) {

            Home.binding.bottomSheetInclude.mainIV.setImageResource(R.drawable.bar_image2);
            Home.binding.bottomSheetInclude.redView.setVisibility(View.INVISIBLE);
            Home.binding.bottomSheetInclude.blueLineRL.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.colorIV.setImageResource(R.drawable.blue_glow);
            Home.binding.bottomSheetInclude.djTV.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.micIV.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.pinkBackTV.setVisibility(View.INVISIBLE);


        }
        if (title.equals("3")) {

            Home.binding.bottomSheetInclude.micIV.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.mainIV.setImageResource(R.drawable.bar_image3);
            Home.binding.bottomSheetInclude.redView.setVisibility(View.INVISIBLE);
            Home.binding.bottomSheetInclude.blueLineRL.setVisibility(View.INVISIBLE);
            Home.binding.bottomSheetInclude.pinkBackTV.setVisibility(View.INVISIBLE);


        }
        if (title.equals("4")) {

            Home.binding.bottomSheetInclude.micIV.setVisibility(View.INVISIBLE);
            Home.binding.bottomSheetInclude.mainIV.setImageResource(R.drawable.bar_image3);
            Home.binding.bottomSheetInclude.blueLineRL.setVisibility(View.VISIBLE);
            Home.binding.bottomSheetInclude.redView.setVisibility(View.INVISIBLE);
            Home.binding.bottomSheetInclude.colorIV.setImageResource(R.drawable.red_glow_big);
            Home.binding.bottomSheetInclude.djTV.setVisibility(View.INVISIBLE);
            Home.binding.bottomSheetInclude.pinkBackTV.setVisibility(View.VISIBLE);

        }

    }

    public void setViewOnCollapse() {

        Home.binding.bottomSheet.setBackground(getResources().getDrawable(R.drawable.rounder_corner_white_two_top));

        Home.binding.bottomSheetInclude.appBarLine.mainCL.setVisibility(View.VISIBLE);

    }

    @Override
    public void onMapReady(@NonNull @NotNull MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        this.mapboxMap.addOnMapClickListener(point -> {

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            return false;

        });

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

            mapStyle = style;

            enableLocationComponent(style);

        });

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
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);

            lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();

            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())) // Sets the new camera position
                    .zoom(15) // Sets the zoom
                    .bearing(0) // Rotate the camera
                    .tilt(1) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 4000);

            Marker myDestinationMarker = mapboxMap.addMarker(new MarkerOptions().title("2").icon(IconFactory.getInstance(getContext()).fromResource(R.drawable.marker_light_blue))
                    .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));

            Marker myDestinationMarker1 = mapboxMap.addMarker(new MarkerOptions().title("1").icon(IconFactory.getInstance(getContext()).fromResource(R.drawable.marker_light_red))
                    .position(new LatLng(lastKnownLocation.getLatitude() + 0.002, lastKnownLocation.getLongitude())));

            Marker myDestinationMarker2 = mapboxMap.addMarker(new MarkerOptions().title("3").icon(IconFactory.getInstance(getContext()).fromResource(R.drawable.marker_light_red))
                    .position(new LatLng(lastKnownLocation.getLatitude() - 0.003, lastKnownLocation.getLongitude())));

            Marker myDestinationMarker4 = mapboxMap.addMarker(new MarkerOptions().title("4").icon(IconFactory.getInstance(getContext()).fromResource(R.drawable.marker_light_blue))
                    .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude() - 0.003)));

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

        Toast.makeText(getContext(), "Location permisssion requier", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) {

            mapboxMap.getStyle(style -> enableLocationComponent(style));

        } else {

            Toast.makeText(getContext(), "Location permisssion requier", Toast.LENGTH_LONG).show();

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

        setBottomSheetView(marker.getTitle());

        markerTitelValue = marker.getTitle();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        return true;

    }


    public void setDetailDialogView(String title) {
        if (title.equals("1")) {
            dialogBinding.mainIV.setImageResource(R.drawable.bar_image1);
            dialogBinding.redView.setVisibility(View.VISIBLE);
            dialogBinding.blueLineRL.setVisibility(View.VISIBLE);
            dialogBinding.micIV.setVisibility(View.VISIBLE);
            dialogBinding.colorIV.setImageResource(R.drawable.blue_glow);
            dialogBinding.djTV.setVisibility(View.VISIBLE);
            dialogBinding.pinkBackTV.setVisibility(View.INVISIBLE);

        }
        if (title.equals("2")) {
            dialogBinding.mainIV.setImageResource(R.drawable.bar_image2);
            dialogBinding.redView.setVisibility(View.INVISIBLE);
            dialogBinding.blueLineRL.setVisibility(View.VISIBLE);
            dialogBinding.colorIV.setImageResource(R.drawable.blue_glow);
            dialogBinding.djTV.setVisibility(View.VISIBLE);
            dialogBinding.micIV.setVisibility(View.VISIBLE);
            dialogBinding.pinkBackTV.setVisibility(View.INVISIBLE);

        }
        if (title.equals("3")) {
            dialogBinding.micIV.setVisibility(View.VISIBLE);
            dialogBinding.mainIV.setImageResource(R.drawable.bar_image3);
            dialogBinding.redView.setVisibility(View.INVISIBLE);
            dialogBinding.blueLineRL.setVisibility(View.INVISIBLE);
            dialogBinding.pinkBackTV.setVisibility(View.INVISIBLE);

        }

        if (title.equals("4")) {
            dialogBinding.micIV.setVisibility(View.INVISIBLE);
            dialogBinding.mainIV.setImageResource(R.drawable.bar_image3);
            dialogBinding.blueLineRL.setVisibility(View.VISIBLE);
            dialogBinding.redView.setVisibility(View.INVISIBLE);
            dialogBinding.colorIV.setImageResource(R.drawable.red_glow_big);
            dialogBinding.djTV.setVisibility(View.INVISIBLE);
            dialogBinding.pinkBackTV.setVisibility(View.VISIBLE);
        }

    }

}