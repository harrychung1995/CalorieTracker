package com.example.calorietracker;

import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import static android.content.ContentValues.TAG;


public class AppMapFragment extends Fragment implements OnMapReadyCallback {
    View vMap;
    int userId;
    private GoogleMap mMap;
    private LatLng LOCATION;
    String userAddress, userPostCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMap = inflater.inflate(R.layout.fragment_map, container, false);
        userId = MainActivity.getUserId();

        GetAddress getAddress = new GetAddress();
        getAddress.execute();

        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        List<Address> addresses;
        while(userAddress == null || userPostCode == null) {}
        try {
            addresses = geocoder.getFromLocationName(userAddress + " " + userPostCode, 1);
            if(addresses.size() > 0) {
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                LOCATION = new LatLng(latitude, longitude);
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("locationName == null");
        }

        MapFragment mapFragment =
                (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return vMap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(LOCATION).title("Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LOCATION));
        mMap.setMinZoomPreference(12.5f);

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName("park", 5);
        }
        catch (IOException e) {
            Log.e(TAG, "Geolocate Exception: " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(address.getLatitude(), address.getLongitude())).title("Park")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }


        LatLng test = new LatLng(-37.8774, 145.042);
        mMap.addMarker(new MarkerOptions()
                .position(test)
                .title("Monash Park")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


    }

    private class GetAddress extends AsyncTask< Void, Void, Void > {
        @Override
        protected Void doInBackground(Void...params) {
            userAddress = RestClient.findByUserIdToFetchAddress(String.valueOf(userId));
            userPostCode = RestClient.findByUserIdToFetchPostcode(String.valueOf(userId));
            return  null;
        }
    }
}