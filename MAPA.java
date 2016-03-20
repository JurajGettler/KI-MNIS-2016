package orthos.fyzicke_zatazenie;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MAPA extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public String ID_trening ;
    public DatabaseHelper myDb;
    private ArrayList<LatLng> zoznamGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);

        myDb = new DatabaseHelper(this);
        zoznamGPS = new ArrayList<LatLng>();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ID_trening = bundle.getString("id");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        trasa();

    }



    public void trasa () {

        PolylineOptions trasa = new PolylineOptions();
        Cursor res = myDb.getGPSdataID(ID_trening);
        LatLng pozicia;

        while (res.moveToNext()) {

            pozicia = new LatLng(Double.parseDouble(res.getString(2)), Double.parseDouble(res.getString(3)));
            zoznamGPS.add(pozicia);
        }


        if (!zoznamGPS.isEmpty()){

            trasa.addAll(zoznamGPS).width(7).color(Color.BLUE).geodesic(true);

        mMap.addMarker(new MarkerOptions()
                .position(zoznamGPS.get(0))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
                .title("START"));

        mMap.addMarker(new MarkerOptions()
                .position(zoznamGPS.get(zoznamGPS.size() - 1))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish))
                .title("CIEL"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoznamGPS.get(0),14));
    }

        mMap.addPolyline(trasa);

    }





}
