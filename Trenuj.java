package orthos.fyzicke_zatazenie;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
    public int avg_tep = 0;
    public int aktualna_rychlost,cas_rychlost;
    public int priemerna_rychlost;
    public String kcal;
    public String hodiny;
    public String TRENING;
    public double aktualna_vzdialenost;
    public String pohlavie;
  //MERANIE TEPU
    public boolean BluetoothOn = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private String s;
    Handler sHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            s =  msg.obj.toString();
            tep.setText(s);
            int t = Integer.parseInt(s);
            pocitadlo++;
            avg_tep = avg_tep + t;
        }
    };
    int BR = 1;
    int sk ;
    private ListView list_device;
    private ArrayAdapter<String> adapter;
    private BluetoothDevice zariadenie;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGatt mGatt;
    private int pocitadlo = 1;
    private LinearLayout tep_layout;


    DatabaseHelper myDb;
    Button butnstart, butnstop,skenuj;
    TextView time,trenuj_vzdialenost,trenuj_rychlost,tep;
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
        myDb = new DatabaseHelper(Trenuj.this);

  //KONTROLA UDAJOV POUZIVATELA
        Cursor user = myDb.getAllDataUSER();
        if (user.moveToFirst()) {
            int vaha = user.getInt(3);
            int max_tep = user.getInt(4);
            int vek = user.getInt(1);
            pohlavie = user.getString(2);
            if (vaha < 30 || max_tep < 60 || vek < 5 ) {
                Intent intent = new Intent("NastaveniaN");
                startActivity(intent);

                Toast.makeText(Trenuj.this, "PROSIM NASTAVTE SVOJE OSOBNE UDAJE", Toast.LENGTH_LONG).show();
            }
        }else{Intent intent = new Intent("NastaveniaN");
            startActivity(intent);

            Toast.makeText(Trenuj.this, "PROSIM NASTAVTE SVOJE OSOBNE UDAJE", Toast.LENGTH_LONG).show();}

        vzdialenost = 0;
        avg_tep = 0;
        aktualna_rychlost = 0;
        kcal = "0";
        aktualna_vzdialenost = 0;
        priemerna_rychlost = 0;
        cas_rychlost = 1;
        ZAPISUJ_INDIKATOR = 0;


        dnes = Calendar.getInstance();
        format_hodiny = new SimpleDateFormat("HH:mm");
        format_den = new SimpleDateFormat("dd.MM.yyyy");
        skenuj = (Button) findViewById(R.id.skenuj);
        butnstart = (Button) findViewById(R.id.start);
        butnstop = (Button) findViewById(R.id.stop);
        time = (TextView) findViewById(R.id.timer);
        trenuj_vzdialenost = (TextView) findViewById(R.id.trenuj_vzdialenost);
        trenuj_rychlost = (TextView) findViewById(R.id.trenuj_rychlost);
        tep = (TextView) findViewById(R.id.trenuj_tep);
        vypocet = new Vypocet_vzdialenosti(myDb);
        list_device = (ListView) findViewById(R.id.list_device);
        tep_layout = (LinearLayout) findViewById(R.id.tep_layout);


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

        //MERANIE TEPU


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sk = 0;






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
                    sport.setVisibility(View.INVISIBLE);
                    tep_layout.setVisibility(View.INVISIBLE);
                } else {
                    butnstart.setText("Start");
                    time.setTextColor(Color.BLUE);
                    timeSwapBuff += timeInTenthseconds;
                    handler.removeCallbacks(updateTimer);
                    t = 1;
                }



            if (ZAPISUJ_INDIKATOR == 0) {

                myDb.insertData("",0,"",0,0,"","",0);
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
           avg_tep = avg_tep/pocitadlo;
           if (pohlavie.equals("MUZSKE")) {
               kcal = kcalMuz();
               Toast.makeText(Trenuj.this,"MUZ",Toast.LENGTH_LONG).show();
           }else {
               kcal = kcalZena();
               Toast.makeText(Trenuj.this,"ZENA",Toast.LENGTH_LONG).show();
           }
           myDb.ubdateData(TRENING,datum, vzdialenost, cas, avg_tep, priemerna_rychlost, kcal, hodiny,vybraty_sport);

           StopGPS();
           stopBluetooth();
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

 //MERANIE TEPU

    public void SKEN (View view){



        if (sk==0){
            skenuj.setText("SKENUJ");
            sk = 1;
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,BR);

            }
            return;
        }

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {}
        else {


            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(Trenuj.this, "JE NAM LUTO, ALE VASE ZARIADENIE NEMA PODPORU BLUETOOTH SMART", Toast.LENGTH_LONG).show();

            } else {

                final BluetoothManager manager =
                        (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                mBluetoothAdapter = manager.getAdapter();
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                startScan();



            }
        }

    }

    private void startScan(){

        ScanFilter beaconFilter = new ScanFilter.Builder().build();

        ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
        filters.add(beaconFilter);

        ScanSettings settings = new ScanSettings.Builder().build();

        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);


    }

    private void CreteListView(final String[] device){


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, device);
        list_device.setAdapter(adapter);
        list_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mBluetoothLeScanner.stopScan(mScanCallback);

                mBluetoothGatt = zariadenie.connectGatt(Trenuj.this, true, mGattCallback);

                BluetoothOn = true;

                tep_layout.setVisibility(View.INVISIBLE);





            }
        });

    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String[] device = new String [1];
            device [0] = result.getDevice().getAddress();
            CreteListView(device);
            zariadenie = result.getDevice();


        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            String[] device = new String[results.size()];
            for (int i = 0;i<results.size();i++){
                device[i] = results.get(i).getDevice().getAddress();
            }
            CreteListView(device);
            zariadenie = results.get(0).getDevice();

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(Trenuj.this, "SKENOVANIE ZLYHALO", Toast.LENGTH_LONG).show();

        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);


            int t = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,1);
            String te =Integer.toString(t);
            Log.i("TEP", te);
            Message msg = Message.obtain();
            msg.obj = te;
            sHandler.sendMessage(msg);


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            /**  byte[] value=characteristic.getValue();
             String v = new String(value);
             setText(v);
             **/

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);

        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {


            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    gatt.discoverServices();
                    break;

            }

        }




        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            mGatt = gatt;
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("SLUZBY", services.get(2).getCharacteristics().get(0).getUuid().toString());
            BluetoothGattCharacteristic therm_char = services.get(2).getCharacteristics().get(0);

            for (BluetoothGattDescriptor descriptor : therm_char.getDescriptors()) {
                descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mGatt.writeDescriptor(descriptor);
            }

            //gatt.readCharacteristic(therm_char);
            gatt.setCharacteristicNotification(therm_char, true);



        }
    };

 //VYPOCET KCAL

    public String kcalMuz(){

        Cursor user = myDb.getAllDataUSER();
        int vaha;
        int vek;
        String muz_vysledok = "0";
        if (user.moveToFirst()) {
            vaha = user.getInt(3);
            vek = user.getInt(1);


            int vysledok = Math.round((((-55.0969f) + (0.6309f * avg_tep) + (0.1988f * vaha) +
                    (0.2017f * vek)) / 4.184f) * (cas_rychlost / 60));
            muz_vysledok = Integer.toString(vysledok);
        }
        return muz_vysledok;


    }

    public String kcalZena(){

        Cursor user = myDb.getAllDataUSER();
        int vaha;
        int vek;
        String zena_vysledok = "0";
        if (user.moveToFirst()) {
            vaha = user.getInt(3);
            vek = user.getInt(1);


            int vysledok = Math.round((((-20.4022f) + (0.4472f * avg_tep) + (0.1263f * vaha) +
                    (0.074f * vek)) / 4.184f) * (cas_rychlost / 60));
            zena_vysledok = Integer.toString(vysledok);
        }
        return zena_vysledok;

    }

    public void stopBluetooth(){
        if(BluetoothOn){

            mBluetoothGatt.disconnect();
            BluetoothOn = false;

        }
    }


}


