package orthos.fyzicke_zatazenie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PODROBNOSTI_TRENING extends AppCompatActivity {

    public String ID_trening;
    public DatabaseHelper myDb;
    public TextView colm1;
    public TextView colm2;
    public TextView colm3;
    public TextView colm4;
    public TextView colm5;
    public TextView colm6;
    public TextView colm7;
    public Button zmazat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.podrobnosti__trening);

        myDb = new DatabaseHelper(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ID_trening = bundle.getString("id");

       // VZDIALENOST
        Vypocet_vzdialenosti v = new Vypocet_vzdialenosti(myDb);
        double vzdialenostD =  v.getDistanceHistoria(ID_trening);
        int r = (int) Math.round(vzdialenostD);
        String vzdialenost = Integer.toString(r) + " m";



        colm1 = (TextView) findViewById(R.id.DATUM_TRENINGU);
        colm2 = (TextView) findViewById(R.id.VZDIALENOST);
        colm3 = (TextView) findViewById(R.id.DLZKA_TRENINGU);
        colm4 = (TextView) findViewById(R.id.PRIEMERNY_TEP);
        colm5 = (TextView) findViewById(R.id.PRIEMERNA_RYCHLOST);
        colm6 = (TextView) findViewById(R.id.KCAL);
        colm7 = (TextView) findViewById(R.id.CAS_TRENINGU);
        zmazat = (Button) findViewById(R.id.ZMAZAT);


        Cursor res = myDb.getTreningDetail(ID_trening);

        res.moveToNext();

        colm1.setText(res.getString(1));
        colm2.setText(vzdialenost);
        colm3.setText(res.getString(3));
        colm4.setText(res.getString(4));
        colm5.setText(res.getString(5) + " km/h");
        colm6.setText(res.getString(6));
        colm7.setText(res.getString(7));



    }

    public void ZMAZAT_TRENING (View view){

        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("ZMAZAT TRENING")
                .setMessage("Naozaj si prajete odstranit zaznam?")
                .setIcon(R.drawable.delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        myDb.deleteData(ID_trening);
                        myDb.deleteGPS(ID_trening);
                        Intent intent = new Intent("HistoriaN");
                        startActivity(intent);
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(),Historia.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }
}
