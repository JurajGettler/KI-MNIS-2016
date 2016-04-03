package orthos.fyzicke_zatazenie;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Nastavenia extends AppCompatActivity {

    DatabaseHelper myDb;
    Spinner pohlavie_edit;
    String[] zozmam_pohlavi = {"MUZSKE","ZENSKE"};
    int [] image_pohlavie = {R.drawable.male,R.drawable.female};
    int vybrate_pohlavie;
    Button uprav;
    TextView meno,pohlavie,vaha,max_tep;
    EditText meno_edit,vaha_edit,max_tep_edit;
    Cursor res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nastavenia);
        myDb = new DatabaseHelper(this);

        res = myDb.getAllDataUSER();
        uprav = (Button) findViewById(R.id.user_uprav);
        meno = (TextView) findViewById(R.id.user_meno);
        pohlavie = (TextView) findViewById(R.id.user_pohlavie);
        vaha = (TextView) findViewById(R.id.user_vaha);
        max_tep = (TextView) findViewById(R.id.user_max_tep);
        meno_edit = (EditText) findViewById(R.id.user_meno_edit);
        vaha_edit = (EditText) findViewById(R.id.user_vaha_edit);
        max_tep_edit = (EditText) findViewById(R.id.user_max_tep_edit);

        if (res.moveToFirst()){
            meno.setText("VEK :                                               " + res.getString(1) + " rokov");
            pohlavie.setText("POHLAVIE :                                   " + res.getString(2));
            vaha.setText(   "VAHA :                                            " + res.getString(3) + " Kg");
            max_tep.setText("MAX TEPOVA FREKVENCIA :    " + res.getString(4));

            meno_edit.setText(res.getString(1));
            vaha_edit.setText(res.getString(3));
            max_tep_edit.setText(res.getString(4));
        }

        pohlavie_edit = (Spinner) findViewById(R.id.user_pohlavie_edit);
        SpinerAdapter spinerAdapter = new SpinerAdapter(this,zozmam_pohlavi,image_pohlavie);
        pohlavie_edit.setAdapter(spinerAdapter);
        pohlavie_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                vybrate_pohlavie = position;


                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        }
        );


    }

    public void UPRAV (View view){

        if (!res.moveToNext()){
            String m =meno_edit.getText().toString();
            String p ;
            if (vybrate_pohlavie == 0){
                p = "MUZSKE";
            }else {p = "ZENSKE";}
            String v = vaha_edit.getText().toString();
            String t = max_tep_edit.getText().toString();
            myDb.insertDataUser(m,p,v,t);
            Intent intent = new Intent(getApplicationContext(),Uvod.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(Nastavenia.this,"UDAJE ZMENENE",Toast.LENGTH_LONG).show();

        }else {
            String m =meno_edit.getText().toString();
            String p ;
            if (vybrate_pohlavie == 0){
                p = "MUZSKE";
            }else {p = "ZENSKE";}
            String v = vaha_edit.getText().toString();
            String t = max_tep_edit.getText().toString();
            myDb.ubdateDataUser(m,p,v,t);
            Intent intent = new Intent(getApplicationContext(),Uvod.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(Nastavenia.this,"UDAJE ZMENENE",Toast.LENGTH_LONG).show();}



    }

}
