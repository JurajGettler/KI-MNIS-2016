package orthos.fyzicke_zatazenie;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import com.google.ads.mediation.customevent.CustomEventAdapter;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Trenuj extends AppCompatActivity{

    public Calendar dnes;
    public SimpleDateFormat format_den,format_hodiny;
    public String datum = "";
    public int vzdialenost;
    public String cas = "";
    public int avg_tep;
    public int aktualna_rychlost,cas_rychlost;
    public int priemerna_rychlost;
    public int kcal;
    public String hodiny;
    public String TRENING;
    public double aktualna_vzdialenost;


    DatabaseHelper myDb;
    Button butnstart, butnstop;
    TextView time,trenuj_vzdialenost,trenuj_rychlost;
    long starttime = 0L;
    long timeInTenthseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int t = 1;
    int minuta = 0;
    int hodina = 0;
    int sekunda = 0;
    int ZAPISUJ_INDIKATOR;
    Handler handler = new Handler();

    //GPS SLEDOVANIE
    //GPS SLEDOVANIE
    //GPS SLEDOVANIE
    //GPS SLEDOVANIE

    public LocationManager locationManager;
    public LocationListener locationListener;
    public Vypocet_vzdialenosti vypocet;

 //SPINER VYBER SPORTU
    public int vybraty_sport;
    Spinner sport;
    String[] zozmam_sportov = {"BEH","CHODZA","CYKLISTIKA","KORCULOVANIE"};
    int [] image_sport = {R.drawable.beh,R.drawable.chodza,R.drawable.cyklistika,R.drawable.korculovanie};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trenuj);

        vzdialenost = 0;
        avg_tep = 0;
        aktualna_rychlost = 0;
        kcal = 0;
        aktualna_vzdialenost = 0;
        priemerna_rychlost = 0;
        cas_rychlost = 1;
        ZAPISUJ_INDIKATOR = 0;

        myDb = new DatabaseHelper(Trenuj.this);
        dnes = Calendar.getInstance();
        format_hodiny = new SimpleDateFormat("HH:mm");
        format_den = new SimpleDateFormat("dd.MM.yyyy");
        butnstart = (Button) findViewById(R.id.start);
        butnstop = (Button) findViewById(R.id.stop);
        time = (TextView) findViewById(R.id.timer);
        trenuj_vzdialenost = (TextView) findViewById(R.id.trenuj_vzdialenost);
        trenuj_rychlost = (TextView) findViewById(R.id.trenuj_rychlost);
        vypocet = new Vypocet_vzdialenosti(myDb);


     //VYBER SPORTU
        sport = (Spinner) findViewById(R.id.sport);
        SpinerAdapter spinerAdapter = new SpinerAdapter(this,zozmam_sportov,image_sport);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.zoznam_sportov, R.layout.support_simple_spinner_dropdown_item);
        sport.setAdapter(spinerAdapter);
        sport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            vybraty_sport = position;


          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {

        }
         }
        );

        //GPS SLEDOVANIE
        //GPS SLEDOVANIE
        //GPS SLEDOVANIE
        //GPS SLEDOVANIE
        //GPS SLEDOVANIE


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (ZAPISUJ_INDIKATOR == 1) {
                    write(TRENING, location.getLatitude(), location.getLongitude());
                    aktualna_vzdialenost = vypocet.getDistanceHistoria(TRENING);
                    vzdialenost = (int) Math.round(aktualna_vzdialenost);
                    String v = Integer.toString(vzdialenost) + " m";
                    trenuj_vzdialenost.setText(v);
                    rychlost(location);
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }
        } else {
            Sledovat();
        }

        //GPS SLEDOVANIE
        //GPS SLEDOVANIE
        //GPS SLEDOVANIE
        //GPS SLEDOVANIE
        //GPS SLEDOVANIE


        butnstart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (t == 1) {
                    butnstart.setText("Pause");
                    starttime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimer, 0);
                    t = 0;
                } else {
                    butnstart.setText("Start");
                    time.setTextColor(Color.BLUE);
                    timeSwapBuff += timeInTenthseconds;
                    handler.removeCallbacks(updateTimer);
                    t = 1;
                }



            if (ZAPISUJ_INDIKATOR == 0) {

                myDb.insertData("",0,"",0,0,0,"",0);
                TRENING = myDb.getID();
                ZAPISUJ_INDIKATOR = 1;
                hodiny = format_hodiny.format(dnes.getTime());
                datum =  format_den.format(dnes.getTime());

            }

            }
        });

        butnstop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                starttime = 0L;
                timeInTenthseconds = 0L;
                timeSwapBuff = 0L;
                updatedtime = 0L;
                t = 1;
                minuta = 0;
                hodina = 0;
                sekunda = 0;
                butnstart.setText("Start");
                handler.removeCallbacks(updateTimer);
                cas = time.getText().toString();
                time.setText("00:00:00");

       if (ZAPISUJ_INDIKATOR == 1) {


           // ZAPIS DO DATABAZY

           priemerna_rychlost = Priemerna_rychlost_zapis();
           vzdialenost = Vzdialenost_zapis();
           myDb.ubdateData(TRENING,datum, vzdialenost, cas, avg_tep, priemerna_rychlost, kcal, hodiny,vybraty_sport);

           StopGPS();
           ZAPISUJ_INDIKATOR = 0;

           // ZOBRAZI DETAILI TRENINGU

           Context context = Trenuj.this ;
           Intent intent = new Intent(context,PODROBNOSTI_TRENING.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           intent.putExtra("id", myDb.getMaxID());
           startActivity(intent);

       }

            }
        });

    }

    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInTenthseconds = (SystemClock.uptimeMillis() - starttime) / (1000);
            updatedtime = timeSwapBuff + timeInTenthseconds;

            sekunda = (int) (updatedtime);
            minuta = sekunda / 60;
            hodina = minuta / 60;
            sekunda = sekunda %60;
            minuta = minuta %60;


            time.setText("" + String.format("%02d", hodina) + ":" + String.format("%02d", minuta) + ":"
                    + String.format("%02d", sekunda));
            time.setTextColor(Color.RED);
            handler.postDelayed(this, 0);
            cas_rychlost =(int) (updatedtime);
        }
    };


    // GPS SLEDOVANIE
    // GPS SLEDOVANIE
    // GPS SLEDOVANIE
    // GPS SLEDOVANIE
    // GPS SLEDOVANIE
    // GPS SLEDOVANIE


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    return;
        }
    }

    private void Sledovat() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 5, locationListener);

    }

    public void StopGPS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.removeUpdates(locationListener);

    }


    public void write(String trening, double latitude, double longitude ) {

        //ZAPIS DO DATABAZI
        myDb.insertGPSdata(trening,latitude,longitude);

    }


    // Vypocet rychlosti pohybu.


    public void rychlost (Location location){

        aktualna_rychlost = Math.round(location.getSpeed() * 3.6F );
        String r = Integer.toString(aktualna_rychlost);
        trenuj_rychlost.setText(r + " km/h");
    }


    public Integer Priemerna_rychlost_zapis (){

        double v = vypocet.getDistanceHistoria(TRENING);
        int r = (int) Math.round( (v/cas_rychlost) * 3.6F);
        return r;

    }

    public Integer Vzdialenost_zapis(){

        return (int) Math.round(vypocet.getDistanceHistoria(TRENING));

    }


}


