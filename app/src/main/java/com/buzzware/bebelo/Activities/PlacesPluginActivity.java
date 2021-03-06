package com.buzzware.bebelo.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.buzzware.bebelo.R;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
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
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import java.util.List;
public class PlacesPluginActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        PermissionsListener {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private MapView mapView;

    private MapboxMap mapboxMap;

    private CarmenFeature home;

    private CarmenFeature work;

    private String geojsonSourceLayerId = "geojsonSourceLayerId";

    private String symbolIconId = "symbolIconId";

    Location lastKnownLocation;

    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_places_plugin);

        mapView = findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

            initSearchFab();

            style.addImage(symbolIconId, BitmapFactory.decodeResource(
                    PlacesPluginActivity.this.getResources(), R.drawable.marker_light_blue));

            enableLocationComponent(style);

            setUpSource(style);

            setupLayer(style);

        });

    }

    private void initSearchFab() {

        findViewById(R.id.searchBtn).setOnClickListener(view -> {

            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor(getString(R.color.white)))
                            .limit(10)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(PlacesPluginActivity.this);

            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

        });

        //to add static search location or favorite
        //.addInjectedFeature(home)
        //.addInjectedFeature(work)

    }

    private void setUpSource(@NonNull Style loadedMapStyle) {

        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));

    }

    private void setupLayer(@NonNull Style loadedMapStyle) {

        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(

                iconImage(symbolIconId),

                iconOffset(new Float[] {0f, -8f})

        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap != null) {

                Style style = mapboxMap.getStyle();

                if (style != null) {

                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);

                    if (source != null) {

                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));

                    }

                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);

                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mapView.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

        mapView.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

        mapView.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mapView.onLowMemory();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle){

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            SharedPreferences pref1 = getSharedPreferences("MyPref", 0);

            SharedPreferences.Editor editor = pref1.edit();

            editor.putString("checkLocation", "allow");

            editor.commit();

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

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

            Marker myDestinationMarker = mapboxMap.addMarker(new MarkerOptions().title("Current Location!").icon(IconFactory.getInstance(this).fromResource(R.drawable.marker_light_blue))
                    .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));

        } else {

            SharedPreferences pref1 = this.getSharedPreferences("MyPref", 0);

            SharedPreferences.Editor editor = pref1.edit();

            editor.putString("checkLocation", "cancel");

            editor.commit();

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(this);

        }
    }

    @Override
    public void onExplanationNeeded(List<String> list) {

        Toast.makeText(this, "Location permission require", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) {

            mapboxMap.getStyle(style -> enableLocationComponent(style));

        } else {

            Toast.makeText(this, "Location permission require", Toast.LENGTH_LONG).show();

        }

    }
}