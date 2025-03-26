package com.thien.smart_planner_project.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.MapMaker;
import com.thien.smart_planner_project.MainActivity;
import com.thien.smart_planner_project.R;

import java.io.IOException;
import java.util.List;

public class GMap extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap ggMap;
    private Marker currentMarker;
    private Toolbar toolbar;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gg_map);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GMap.this, MainActivity.class);
                startActivity(intent);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapError", "Map Fragment is null");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        ggMap = googleMap;
        ggMap.getUiSettings().setCompassEnabled(true);
        ggMap.getUiSettings().setZoomControlsEnabled(true);

        // Kiểm tra và yêu cầu quyền vị trí
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();

        }
        ggMap.setOnMapClickListener(latLng -> {
            // Lấy địa chỉ từ LatLng
            if (currentMarker != null) {
                currentMarker.remove();
            }
            getAddressFromLocation(latLng);
        });

    }
    private void getAddressFromLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        try {
            // Lấy danh sách địa chỉ từ tọa độ
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                // Lấy địa chỉ đầu tiên
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);

                showConfirmDialog(latLng, fullAddress);

                // Thêm marker tại vị trí được nhấn
             currentMarker = ggMap.addMarker(new MarkerOptions()
                    .position(latLng).title(fullAddress));
            } else {
                Toast.makeText(this, "Không tìm thấy địa chỉ!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lấy địa chỉ!", Toast.LENGTH_SHORT).show();
        }
    }


    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            ggMap.setMyLocationEnabled(true);

            // Lấy vị trí hiện tại của người dùng
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Di chuyển camera tới vị trí hiện tại
                    ggMap.addMarker(new MarkerOptions().position(currentLocation).title("Vị trí của bạn"));
                    ggMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                } else {
                    Toast.makeText(this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e("LocationError", "Không thể lấy vị trí", e);
            });
        }
    }
    private void showConfirmDialog(LatLng latLng, String fullAddress) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận vị trí")
                .setMessage("Bạn có muốn chọn địa chỉ này?\n" + fullAddress)
                .setPositiveButton("Xác nhận", (dialog, which) -> {

                    Intent intent=new Intent(this,MainActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("fullAddress",fullAddress);
                    intent.putExtras(bundle);
                    startActivity(intent);

                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Quyền truy cập vị trí bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //nút back có id là android.R.id.home
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
