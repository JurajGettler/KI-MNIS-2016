package orthos.fyzicke_zatazenie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Elzoido on 17-Feb-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "trening.db";
    public static final String TABLE_NAME = "Hlavne_data";
    public static final String TABLE_NAME_GPS = "GPS";
    public static final String TABLE_NAME_USER = "POUZIVATEL";
    public static final String COLM_1  = "ID";
    public static final String COLM_2  = "DATUM";
    public static final String COLM_3  = "VZDIALENOST";
    public static final String COLM_4 = "CAS";
    public static final String COLM_5 = "AVG_TEP";
    public static final String COLM_6 = "MAX_TEP";
    public static final String COLM_7 = "KCAL";
    public static final String COLM_8 = "HODINY";
    public static final String COLM_9 = "SPORT";
    public static final String COLM_1_GPS = "TRENING";
    public static final String COLM_2_GPS = "LATITUDE";
    public static final String COLM_3_GPS = "LONGITUDE";
    public static final String COLM_0_user = "ID";
    public static final String COLM_1_user = "MENO";
    public static final String COLM_2_user = "POHLAVIE";
    public static final String COLM_3_user = "VAHA";
    public static final String COLM_4_user = "MAX_FREKVENICA";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,DATUM TEXT," +
                "VZDIALENOST INTEGER,CAS TEXT,AVG_TEP INTEGER,MAX_TEP INTEGER,KCAL TEXT, HODINY TEXT,SPORT INTEGER)");

        db.execSQL("create table if not exists " + TABLE_NAME_GPS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ",TRENING TEXT,LATITUDE TEXT, LONGITUDE TEXT)");

        db.execSQL("create table if not exists " + TABLE_NAME_USER + " (ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ",MENO TEXT,POHLAVIE TEXT,VAHA TEXT,MAX_FREKVENICA TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
        onCreate(db);
    }

    public boolean insertData(String DATUM,Integer VZDIALENOST, String CAS,Integer AVG_TEP,Integer MAX_TEP,
                              String KCAL, String HODINY, Integer SPORT){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLM_2,DATUM);
        contentValues.put(COLM_3,VZDIALENOST);
        contentValues.put(COLM_4,CAS);
        contentValues.put(COLM_5,AVG_TEP);
        contentValues.put(COLM_6,MAX_TEP);
        contentValues.put(COLM_7, KCAL);
        contentValues.put(COLM_8, HODINY);
        contentValues.put(COLM_9, SPORT);
        long resolt = db.insert(TABLE_NAME,null,contentValues);
        if(resolt == -1)
          return false;
        else
            return true;

    }

    public Cursor getAllData(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME,null);
        return  res;

    }

    public Cursor getPotomokData(String datum){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {datum};
        Cursor res_potomok = db.query(TABLE_NAME, null, COLM_2 + "=?", selectionArgs, null, null, null);

        return res_potomok;
    }

    public Cursor getDatum(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor datum = db.rawQuery("SELECT DISTINCT * from " + TABLE_NAME + " GROUP BY " + COLM_2, null);
        return datum;

    }

    public boolean ubdateData(String ID, String DATUM,Integer VZDIALENOST, String CAS,
                              Integer AVG_TEP,Integer AVG_RYCHLOST,String KCAL, String HODINY, Integer SPORT){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLM_1,ID);
        contentValues.put(COLM_2,DATUM);
        contentValues.put(COLM_3,VZDIALENOST);
        contentValues.put(COLM_4,CAS);
        contentValues.put(COLM_5,AVG_TEP);
        contentValues.put(COLM_6,AVG_RYCHLOST);
        contentValues.put(COLM_7, KCAL);
        contentValues.put(COLM_8, HODINY);
        contentValues.put(COLM_9, SPORT);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{ID});
        return true;
    }

    public Integer deleteData(String ID){

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLM_1 + " = ?", new String[]{ID});
    }

    public Integer deleteGPS(String ID){

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_GPS, COLM_1_GPS + " = ?", new String[]{ID});

    }

    public Cursor getAllGPSdata (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_GPS, null);
        return  res;
    }

    public boolean insertGPSdata(String TRENING, double LATITUDE, double LONGITUDE){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String LATITUDEs = String.valueOf(LATITUDE);
        String LONGITUDEs = String.valueOf(LONGITUDE);
        contentValues.put(COLM_1_GPS,TRENING);
        contentValues.put(COLM_2_GPS,LATITUDEs);
        contentValues.put(COLM_3_GPS,LONGITUDEs);
        long resolt = db.insert(TABLE_NAME_GPS,null,contentValues);
        if(resolt == -1)
            return false;
        else
            return true;

    }

    public String getMaxID(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        if (res.moveToNext()){
            res.moveToLast();
            String LID = res.getString(0);
            return LID;

        }else return "1";

    }

    public String getLastId (){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        if (res.moveToNext()){
            res.moveToLast();
            String LID = res.getString(0);
            int id = Integer.parseInt(LID);
            id++;
            LID =String.valueOf(id);
            return LID;

        }else return "1";
    }

    public Cursor getGPSdataID (String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {ID};
        Cursor res = db.query(TABLE_NAME_GPS, null, COLM_1_GPS + "=?", selectionArgs, null, null, null);
        return  res;
    }


    public Cursor getTreningDetail (String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {ID};
        Cursor res = db.query(TABLE_NAME, null, COLM_1 + "=?", selectionArgs, null, null, null);
        return  res;
    }


    public String getID (){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToLast();
        return res.getString(0);

    }

  //POUZIVATEL

    public Cursor getAllDataUSER(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_USER,null);
        return  res;

    }

    public boolean ubdateDataUser(String MENO,String POHLAVIE, String VAHA,String MAX_TEP){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLM_1_user,MENO);
        contentValues.put(COLM_2_user,POHLAVIE);
        contentValues.put(COLM_3_user,VAHA);
        contentValues.put(COLM_4_user,MAX_TEP);
        db.update(TABLE_NAME_USER, contentValues, "ID = ?", new String[]{"1"});
        return true;
    }

    public boolean insertDataUser(String MENO,String POHLAVIE, String VAHA,String MAX_TEP){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLM_1_user,MENO);
        contentValues.put(COLM_2_user,POHLAVIE);
        contentValues.put(COLM_3_user,VAHA);
        contentValues.put(COLM_4_user,MAX_TEP);
        long resolt = db.insert(TABLE_NAME_USER,null,contentValues);
        if(resolt == -1)
            return false;
        else
            return true;

    }



}
