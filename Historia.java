package orthos.fyzicke_zatazenie;


import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class Historia extends AppCompatActivity {


    DatabaseHelper myDb;
    Adapter adapter;
    ExpandableListView exp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historia);
        myDb = new DatabaseHelper(this);
        adapter = new Adapter(this,myDb);
        exp = (ExpandableListView) findViewById(R.id.exp_listview);
        exp.setAdapter(adapter);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(),Uvod.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}

