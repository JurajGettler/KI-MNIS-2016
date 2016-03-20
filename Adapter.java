package orthos.fyzicke_zatazenie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Elzoido on 01-Mar-16.
 */
public class Adapter extends BaseExpandableListAdapter {

    private Context ctx;
    private DatabaseHelper myDb;


    Adapter(Context ctx, DatabaseHelper myDb){

        this.ctx = ctx;
        this.myDb = myDb;



    }

    public String mojaHlavicka (int groupPosition){
        Cursor prd = myDb.getDatum();
        prd.moveToPosition(groupPosition);
        return prd.getString(1);
    }

    @Override
    public int getGroupCount() {
        return myDb.getDatum().getCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return myDb.getPotomokData(mojaHlavicka(groupPosition)).getCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mojaHlavicka(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
       Cursor prd =  myDb.getPotomokData(mojaHlavicka(groupPosition));
        prd.moveToPosition(childPosition);
        return  prd.getString(1);
    }



    public Object getChildVzdialenost(int groupPosition, int childPosition) {
        Cursor prd =  myDb.getPotomokData(mojaHlavicka(groupPosition));
        prd.moveToPosition(childPosition);
        return  prd.getString(2);
    }

    public Object getChildID(int groupPosition, int childPosition) {
        Cursor prd =  myDb.getPotomokData(mojaHlavicka(groupPosition));
        prd.moveToPosition(childPosition);
        return  prd.getString(0);
    }

    public Object getChildHodiny(int groupPosition, int childPosition) {
        Cursor prd =  myDb.getPotomokData(mojaHlavicka(groupPosition));
        prd.moveToPosition(childPosition);
        return  prd.getString(7);
    }

    public Object getChildCas(int groupPosition, int childPosition) {
        Cursor prd =  myDb.getPotomokData(mojaHlavicka(groupPosition));
        prd.moveToPosition(childPosition);
        return  prd.getString(3);
    }

    public Object getChildKcal(int groupPosition, int childPosition) {
        Cursor prd =  myDb.getPotomokData(mojaHlavicka(groupPosition));
        prd.moveToPosition(childPosition);
        return  prd.getString(6);
    }

    public Object getChildSport(int groupPosition, int childPosition) {
        Cursor prd =  myDb.getPotomokData(mojaHlavicka(groupPosition));
        prd.moveToPosition(childPosition);
        return  prd.getString(8);
    }



    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String title = (String) this.getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater layoutInflater =(LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.perent_layout,null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.hlavicka);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(title);




        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {

        String title = (String) this.getChild(groupPosition, childPosition);
        String title_hodiny = (String) this.getChildHodiny(groupPosition, childPosition);
        String title_cas = (String) this.getChildCas(groupPosition, childPosition);
        String title_kcal = (String) this.getChildKcal(groupPosition, childPosition);
        String title_vzdialenost = (String) this.getChildVzdialenost(groupPosition, childPosition);
        String title_sport = (String) this.getChildSport(groupPosition,childPosition);
        final String id = (String) this.getChildID(groupPosition, childPosition);


        if(convertView == null){
            LayoutInflater layoutInflater =(LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_layout,null);
        }


        TextView textViewV = (TextView) convertView.findViewById(R.id.potomok_text_vzdialenost);
        textViewV.setTypeface(null, Typeface.BOLD);
        textViewV.setText(title_vzdialenost);

        TextView textViewCas = (TextView) convertView.findViewById(R.id.potomok_text_cas);
        textViewCas.setTypeface(null, Typeface.BOLD);
        textViewCas.setText(title_cas);

        TextView textViewKcal = (TextView) convertView.findViewById(R.id.potomok_kcal);
        textViewKcal.setTypeface(null, Typeface.BOLD);
        textViewKcal.setText(title_kcal);

    // NASTAV OBRAZOK

        ImageView imageViewSport = (ImageView) convertView.findViewById(R.id.potomok_sport);
        if (title_sport.equals("0")){imageViewSport.setImageResource(R.drawable.beh);};
        if (title_sport.equals("1")){imageViewSport.setImageResource(R.drawable.chodza);};
        if (title_sport.equals("2")){imageViewSport.setImageResource(R.drawable.cyklistika);};
        if (title_sport.equals("3")){imageViewSport.setImageResource(R.drawable.korculovanie);};



        Button gps = (Button) convertView.findViewById(R.id.GPS);

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), MAPA.class);
                intent.putExtra("id", id);
                parent.getContext().startActivity(intent);

            }
        });

        Button viac = (Button) convertView.findViewById(R.id.podrobnosti);

        viac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), PODROBNOSTI_TRENING.class);
                intent.putExtra("id", id);
                parent.getContext().startActivity(intent);

            }
        });




        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
