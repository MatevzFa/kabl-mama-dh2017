package kbmm.makeme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private ArrayList<Marker> markers;
    private ArrayList<Circle> circles;
    private SharedPreferences saves;
    private Marker currentPosition;
    private boolean checkingPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markers = new ArrayList<>();
        circles = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Button buttonShrani = (Button) findViewById(R.id.buttonShrani);
        buttonShrani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder s = new StringBuilder();
                for(Marker m : markers) {
                    s.append(m.getPosition().latitude).append(",").append(m.getPosition().longitude).append("\t");
                }
                for(Circle c : circles) c.setVisible(false);
                for(Marker m : markers) m.setVisible(true);
                saves.edit().putString("locations", s.toString()).apply();
            }
        });

        Button buttonCheck = (Button) findViewById(R.id.buttonCheck);
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkingPosition = true;
                try {
                    locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                } catch (SecurityException e) {
                    Log.d("MY APP", e.getMessage());
                }
                for(Marker m : markers) m.setVisible(false);
                for(Circle c : circles) c.setVisible(true);
            }
        });

        ImageButton myLocation = (ImageButton) findViewById(R.id.imageButton);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                } catch (SecurityException e) {
                    Log.d("MY APP", e.getMessage());
                }
            }
        });

    }

    @Override
    protected void onResume() {

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        saves = getSharedPreferences("mm", Context.MODE_PRIVATE);
        String locs = saves.getString("locations", "");
        if(!locs.equals("")) {
            for(String loc : locs.split("\t")) {
                String[] ll = loc.split(",");
                LatLng pos = new LatLng(Double.parseDouble(ll[0]), Double.parseDouble(ll[1]));
                markers.add(mMap.addMarker(new MarkerOptions().position(pos)));
                circles.add(mMap.addCircle(new CircleOptions().center(pos).radius(100).strokeColor(R.color.colorPrimaryDark).fillColor(R.color.colorPrimary)));
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.0236862,14.6028584), 7.5f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(markers.size() < 5) {
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLng)));
                    circles.add(mMap.addCircle(new CircleOptions().center(latLng).radius(100).strokeColor(R.color.colorPrimaryDark).fillColor(R.color.colorPrimary).visible(false)));
                } else {
                    markers.get(4).setPosition(latLng);
                    circles.get(4).setCenter(latLng);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markers.remove(marker);
                marker.remove();
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMap != null) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));

            if(currentPosition == null) {
                currentPosition = mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                if(!checkingPosition) currentPosition.setVisible(false);
                else currentPosition.setVisible(true);

            } else {

                if(!checkingPosition) currentPosition.setVisible(false);
                else currentPosition.setVisible(true);

                currentPosition.setPosition(loc);
            }
        }
        if(checkingPosition) {
            float[] distance = new float[2];
            boolean anyInside = false;
            for(Circle circle : circles) {
                Location.distanceBetween(currentPosition.getPosition().latitude, currentPosition.getPosition().longitude,
                        circle.getCenter().latitude, circle.getCenter().longitude, distance);

                if (distance[0] > circle.getRadius()) {
                    //Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_LONG).show();
                } else {
                    anyInside = true;
                    //Toast.makeText(getBaseContext(), "Inside", Toast.LENGTH_LONG).show();
                }
            }
            if (anyInside) {
                Toast.makeText(getBaseContext(), "Inside", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "Outside", Toast.LENGTH_LONG).show();
            }

        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
