package com.hc_06.yj.bluetoothapplication.ui.activities;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hc_06.yj.bluetoothapplication.R;
import com.hc_06.yj.bluetoothapplication.data.LocationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.HashMap;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.hc_06.yj.bluetoothapplication.R.id.map;

@RuntimePermissions
public class MapActivity extends GMSLocationActivity implements MapEventsReceiver, Marker.OnMarkerClickListener  {

    private DatabaseReference databaseReference;
    private RelativeLayout dialogMarkerView;
    private Button mBtn;
    private TextView mTitle;
    private TextView mDescription;
    private Marker mMarker;
    private MapView mMapView = null;
    private MapEventsOverlay mapEventsOverlay;
    private MyLocationNewOverlay mLocationOverlay = null;
    private RotationGestureOverlay mRotationGestureOverlay;
    private boolean isMapInit = false;
    private Map<Marker, String> markersId;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("locations");
        markersId = new HashMap<>();

        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mapEventsOverlay = new MapEventsOverlay(this);

        MapActivityPermissionsDispatcher.initMapWithCheck(this);

        mRequestingLocationUpdates = true;
    }

    @Override
    protected void locationUpdated() {
        super.locationUpdated();

        if(!isMapInit) {
            initLocation();
        }
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    @NeedsPermission({ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE })
    public void initMap() {
        mMapView = (MapView) findViewById(map);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);

        mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
        mRotationGestureOverlay.setEnabled(true);

        mMapView.setMultiTouchControls(true);
        mMapView.setClickable(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);
        mMapView.setTilesScaledToDpi(true);

        mMapView.setBuiltInZoomControls(true);

        mLocationOverlay = new MyLocationNewOverlay(mMapView);
        mLocationOverlay.enableMyLocation();

        mMapView.getOverlays().add(0, mapEventsOverlay);
        mMapView.addOnFirstLayoutListener((v, left, top, right, bottom) -> initLocation());
    }

    public void initLocation() {
        if(mCurrentLocation != null) {
            double x = mCurrentLocation.getLatitude();
            double y = mCurrentLocation.getLongitude();

            GeoPoint mGeoPoint = new GeoPoint(x, y);

            this.mMapView.getController().setZoom(15);
            this.mMapView.getController().setCenter(mGeoPoint);

            initMarkers();
            isMapInit = true;
        }
    }

    private void initMarkers() {
        if(isSignedIn()) {
            //clear map
            if(markersId != null) {
                for(Marker m : markersId.keySet()) {
                    m.remove(mMapView);
                }
                markersId.clear();
                mMapView.invalidate();
            } else {
                markersId = new HashMap<>();
            }
            showProgress();

            databaseReference.child(getUid()).addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        LocationItem locationItem = postSnapshot.getValue(LocationItem.class);

                        mMarker = new Marker(mMapView);
                        GeoPoint p = new GeoPoint(locationItem.getLat(), locationItem.getLng());
                        mMarker.setPosition(p);
                        mMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        mMarker.setTitle(locationItem.getTitle());

                        mMarker.setOnMarkerClickListener(MapActivity.this);
                        mMarker.setInfoWindow(new MyMarkerInfoWindow(mMapView));

                        markersId.put(mMarker, postSnapshot.getKey());
                        mMapView.getOverlays().add(mMarker);
                    }
                    hideProgress();
                    mMapView.invalidate();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        if(isSignedIn()) {
            AlertDialog dialog = new AlertDialog.Builder(MapActivity.this).create();

            for (Marker m : markersId.keySet()) {
                if (p != mMarker.getPosition())
                    m.closeInfoWindow();
            }

            dialogMarkerView = (RelativeLayout) getLayoutInflater()
                    .inflate(R.layout.dialog_marker_enter_info, null);

            Button infoSendBtn = (Button) dialogMarkerView.findViewById(R.id.btn_marker_info_send);
            EditText infoTitleEt = (EditText)dialogMarkerView.findViewById(R.id.etMarkerTitle);
            EditText infoGroupName = (EditText)dialogMarkerView.findViewById(R.id.etMarkerGroupName);
            EditText infoOrderNumber = (EditText) dialogMarkerView.findViewById(R.id.etMarkerOrderNumber);
            EditText infoNavigatorNumber = (EditText) dialogMarkerView.findViewById(R.id.etMarkerNavigatorNumber);

            dialog.setView(dialogMarkerView);
            dialog.show();

            infoSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(infoNavigatorNumber.getText())) {
                        dialog.cancel();
                        Toast.makeText(MapActivity.this, R.string.empty_nav_number, Toast.LENGTH_LONG).show();
                        return;
                    }
                    setItemLocationToDB(infoOrderNumber.getText().toString(), infoTitleEt.getText().toString(),
                            infoGroupName.getText().toString(), p.getLatitude(), p.getLongitude(),
                            infoNavigatorNumber.getText().toString(), false);
                    dialogMarkerView.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            });
        }
        return true;
    }

    private void setItemLocationToDB(String orderNumber, String title, String groupName, double lat, double lng,
                                     String navigatorNumber, boolean isChecked) {
        LocationItem item = new LocationItem(orderNumber, title, groupName, lat, lng, navigatorNumber, isChecked);

        databaseReference.child(getUid()).push().setValue(item, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null) {
                    Log.e("Error", "Failed to write message", databaseError.toException());
                } else {
                    initMarkers();
                }
            }
        });
    }

    public void removeMarker(Marker marker) {
        marker.closeInfoWindow();
        String markerId = markersId.get(marker);
        Log.e("TAG", markerId);
        showProgress();
        databaseReference.child(getUid()).child(markerId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgress();
                initMarkers();
            }
        });
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    public boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {
        marker.showInfoWindow();
        return true;
    }

    public class MyMarkerInfoWindow extends MarkerInfoWindow {

        public MyMarkerInfoWindow(MapView mapView) {
            super(R.layout.marker_bubble, mapView);
        }

        @Override
        public void onOpen(Object item) {
            super.onOpen(item);
            mMarker = (Marker) item;

            for (Marker m : markersId.keySet()) {
                if (m.getPosition() != mMarker.getPosition())
                    m.closeInfoWindow();
            }

            mTitle = (TextView) mView.findViewById(R.id.bubble_title);
            mTitle.setText(mMarker.getTitle());
            mDescription = (TextView) mView.findViewById(R.id.bubble_subdescription);
            mDescription.setText(mMarker.getSubDescription());

            mBtn = (Button) mView.findViewById(R.id.bubble_remove);
            mBtn.setOnClickListener(v -> removeMarker(mMarker));
        }
    }
}
