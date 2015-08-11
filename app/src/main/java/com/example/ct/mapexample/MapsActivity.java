package com.example.ct.mapexample;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ShareDialog shareDialog;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FacebookSdk.sdkInitialize(getApplicationContext());
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public String loadJsonFromAsset() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("loc.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        try {
            JSONObject obj = new JSONObject(loadJsonFromAsset());
            JSONArray jsonArray = obj.getJSONArray("locations");
            for (int i = 0; i < jsonArray.length(); i ++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                double latValue = (double) jsonObject.get("lat");
                double lngValue = (double) jsonObject.get("lng");
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latValue, lngValue))
                        .title(String.valueOf(latValue) + ", " + String.valueOf(lngValue)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                            .setContentTitle(marker.getPosition().latitude + "," + marker.getPosition().longitude)
                            .setContentUrl(Uri.parse("http://maps.google.com/maps?q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude))
                            .build();
                    shareDialog.show(shareLinkContent);
                }
                return true;
            }
        });
    }
}
