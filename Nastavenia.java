package orthos.fyzicke_zatazenie;

import android.app.AlertDialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Nastavenia extends AppCompatActivity {

    DatabaseHelper myDb;
    Button zobraz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nastavenia);
        myDb = new DatabaseHelper(this);
        zobraz = (Button) findViewById(R.id.zobraz);

       }

    public void zobrazM (View view){
        Cursor res = myDb.getAllGPSdata();
        if (res.getCount() == 0){

            showMessage("Error","NENASLI SA DATA");

            return;
        }else{
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()){
                buffer.append("ID : " + res.getString(0) + "\n");
                buffer.append("ID TRENINGU : " + res.getString(1) + "\n");
                buffer.append("LATITUDE : " + res.getString(2) + "\n");
                buffer.append("LONGITUDE : " + res.getString(3) + "\n\n");
            }

            showMessage("Data",buffer.toString());
        }
    }

    public void showMessage (String title,String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
}
