package orthos.fyzicke_zatazenie;

import android.database.Cursor;

/**
 * Created by Elzoido on 13-Mar-16.
 */
public class Vypocet_vzdialenosti {

    DatabaseHelper myDb;


    public Vypocet_vzdialenosti(DatabaseHelper databaza){

        this.myDb = databaza;
        
    }

    public double getDistanceTrenuj(double lat1, double lon1, double lat2, double lon2) {

        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang * 6371;
        return dist*1000;
    }

    public double getDistanceHistoria(String Trening) {

        double vzdialenost = 0;
        Cursor res = myDb.getGPSdataID(Trening);

        while (res.moveToNext()){
            double lat1,lon1,lat2,lon2;
            lat2=0;
            lon2=0;
            lat1 = res.getDouble(2);
            lon1 = res.getDouble(3);

            if (res.moveToNext()){
                lat2 = res.getDouble(2);
                lon2 = res.getDouble(3);

                res.moveToPrevious();
            }else return vzdialenost;

           vzdialenost = vzdialenost + getDistanceTrenuj(lat1,lon1,lat2,lon2);


        }


        return vzdialenost;


    }

}
