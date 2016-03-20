package orthos.fyzicke_zatazenie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.zip.Inflater;

/**
 * Created by Elzoido on 17-Mar-16.
 */
public class SpinerAdapter extends ArrayAdapter <String> {

    Context c;
    String[] sporty;
    int[] images;

    public SpinerAdapter(Context ctx,String[] sporty,int[] images){

        super(ctx,R.layout.spiner_sport,sporty);
        this.c = ctx;
        this.sporty = sporty;
        this.images = images;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            LayoutInflater inflater =(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spiner_sport,null);
        }

        TextView sport = (TextView) convertView.findViewById(R.id.text_spiner);
        ImageView img = (ImageView) convertView.findViewById(R.id.obrazok_spiner);

        sport.setText(sporty[position]);
        img.setImageResource(images[position]);

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            LayoutInflater inflater =(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spiner_sport,null);
        }

        TextView sport = (TextView) convertView.findViewById(R.id.text_spiner);
        ImageView img = (ImageView) convertView.findViewById(R.id.obrazok_spiner);

        sport.setText(sporty[position]);
        img.setImageResource(images[position]);

        return convertView;
    }



}
