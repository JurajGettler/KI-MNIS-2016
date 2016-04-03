package orthos.fyzicke_zatazenie;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class Uvod extends Activity {

    Button historia,nastavenia,ukoncit,trenuj;
    private BluetoothAdapter mBluetoothAdapter;
    int BR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uvod);

        trenuj = (Button) findViewById(R.id.trenuj);
        historia=(Button) findViewById(R.id.historia);
        nastavenia=(Button)findViewById(R.id.nastavenia);
        ukoncit=(Button) findViewById(R.id.ukoncit);

        trenuj_activity();
        Ukoncit();


    }

    public void trenuj_activity () {

        trenuj.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent("Trenovanie");
                        startActivity(intent);
                    }
                }
        );
    }

    public void Ukoncit () {
        ukoncit.setOnClickListener(

        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                finish();
            }
        }
      );
    }

    public void historiaM (View view){
        Intent intent = new Intent("HistoriaN");
        startActivity(intent);
    }

    public void nastaveniaM(View view){

        Intent intent = new Intent("NastaveniaN");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {}

}
