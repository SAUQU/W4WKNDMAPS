package com.example.segundoauqui.w4wkndmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.segundoauqui.w4wkndmaps.model.AddressResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    public static final String GEO_KEY = "AIzaSyDJtTznAxYQEvbrpSqYuUcDw6_jojEvMcY";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final String TAG = "MainActivity";
    private static final String DISTANCE = "2000";
    private SupportMapFragment mapFragment;
    List<Address> addressList;
    CardView cvPlace;
    RecyclerView rvPlaces;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemAnimator itemAnimator;
    GoogleMap map;
    AutoCompleteTextView etLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        etLocation = (AutoCompleteTextView) findViewById(R.id.etLocation);
        etLocation.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.places));
        layoutManager = new LinearLayoutManager(getApplicationContext());
        itemAnimator = new DefaultItemAnimator();
        rvPlaces = (RecyclerView) findViewById(R.id.rvPlaces);
        cvPlace = (CardView) findViewById(R.id.cvPlace);
        rvPlaces.setLayoutManager(layoutManager);
        rvPlaces.setItemAnimator(itemAnimator);
        checkPermissions();
    }


    public void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {
            getLocation();

        }
    }


    public void getLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        currentLocation = location;
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap map) {
                                    currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                    loadMap(map, currentLatLng, "You are here");
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,100, this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.btnRestaurant:
                map.clear();
                getGeodCodeAddress("restaurant");
                Toast.makeText(this, "Restaurants closed to your location.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnBank:
                map.clear();
                getGeodCodeAddress("bank");
                Toast.makeText(this, "Banks closed to your location.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnATM:
                map.clear();
                getGeodCodeAddress("atm");
                Toast.makeText(this, "ATMs closed to your location", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnBar:
                map.clear();
                getGeodCodeAddress("bar");
                Toast.makeText(this, "Bars closed to your location", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnPark:
                map.clear();
                getGeodCodeAddress("park");
                Toast.makeText(this, "Parks closed to your location", Toast.LENGTH_LONG).show();
                return true;
            case R.id.btnStore:
                map.clear();
                getGeodCodeAddress("store");
                Toast.makeText(this, "Stores closed to your location", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.btnHospital:
                map.clear();
                getGeodCodeAddress("hospital");
                Toast.makeText(this, "Hospitals closed to your location", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getGeodCodeAddress(String nearByPlace) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("place")
                .addPathSegment("nearbysearch")
                .addPathSegment("json")
                .addQueryParameter("location", currentLocation.getLatitude() + "," + currentLocation.getLongitude())
                .addQueryParameter("radius", DISTANCE)
                .addQueryParameter("type", "restaurant")
                .addQueryParameter("type", "true")
                .addQueryParameter("key", GEO_KEY)
                .build();
        Log.d(TAG, "getGeodCodeAddress: " + url.toString());

        final Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Gson gson = new Gson();
                final AddressResponse addressResponse = gson.fromJson(response.body().string(), AddressResponse.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AddressResponseAdapter addressResponseAdapter = new AddressResponseAdapter(addressResponse.getResults());
                        cvPlace.setVisibility(View.VISIBLE);
                        rvPlaces.setAdapter(addressResponseAdapter);
                        addressResponseAdapter.notifyDataSetChanged();
                        for (int i = 0; i < addressResponse.getResults().size(); i++) {
                            getLocationInMap(addressResponse.getResults().get(i).getVicinity(),addressResponse.getResults().get(i).getName(), false);


                        }

                    }
                });

            }
        });
    }


    private void getLocationInMap(String location, String name, boolean isSearch) {
        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 5);
                if (addressList != null) {
                    for (int i = 0; i < addressList.size(); i++) {
                        if (isSearch) {
                            currentLocation.setLatitude(addressList.get(i).getLatitude());
                            currentLocation.setLongitude(addressList.get(i).getLongitude());
                        }
                        currentLatLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                        loadMap(map, currentLatLng, name);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void loadMap(GoogleMap googleMap, LatLng currentLatLng, String location) {
        map = googleMap;
        if (map != null) {
            try {
                map = googleMap;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLatLng).zoom(14).tilt(0).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                map.addMarker(new MarkerOptions().position(currentLatLng).title(location));
                map.setTrafficEnabled(true);
                map.setMyLocationEnabled(true);
            } catch (Exception e) {
                e.getMessage();
                Log.d(TAG, "loadMap: " + e.getMessage());
            }
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }




    }

    public void onSearchDelete(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                map.clear();
                cvPlace.setVisibility(View.GONE);
                getLocationInMap(etLocation.getText().toString(), etLocation.getText().toString(), true);
                break;
            case R.id.btnDelete:
                etLocation.setText("");
                break;
        }
    }
}