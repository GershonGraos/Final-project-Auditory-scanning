package com.graos.auditory_scanning_final_project;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// *************** CREATE DATA BASE ****************
public class DBHelper_Therapists extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "therapists.db";
    public static final String TABLE_NAME = "therapists_table";
    public static final String COL_1 = "id";
    public static final String COL_2 = "name";
    public static final String COL_3 = "user_name";
    public static final String COL_4 = "password";
    public static final String COL_5 = "stay_connected";

    private Context thisContext;

    public DBHelper_Therapists(Context context) {
        super(context, DATABASE_NAME, null, 1);
        thisContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( id TEXT, name TEXT, user_name TEXT, password TEXT,stay_connected TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public long insert_data_therapist(String id_user, String name, String user_name, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();;
        contentValues.put(COL_1, id_user);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, user_name);
        contentValues.put(COL_4, password);
        contentValues.put(COL_5, "false");
        long result_add = db.insert(TABLE_NAME, null, contentValues); // add the db
        log_this_action_for_mongo();
        return result_add;
    }

    public Cursor show_data_therapists(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
    public Cursor show_therapist_data_by_id_and_user_name(String Therapist_id,String Therapist_username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME+ " where id = '" + Therapist_id + "' AND user_name = '" + Therapist_username + "'", null);
        return res;
    }
    public Cursor show_therapist_data_by_user_name(String Therapist_username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME+ " where user_name = '" + Therapist_username + "'", null);
        return res;
    }
    public boolean update_data(String id_user, String name ,String user_name, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id_user);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, user_name);
        contentValues.put(COL_4, password);
        db.update(TABLE_NAME, contentValues, "id = ?", new String[] { id_user } );
        log_this_action_for_mongo();
        return true;
    }

    public Cursor get_stay_connected_status(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select id from " + TABLE_NAME+ " where stay_connected = 'true'", null);
        return res;
    }
    public boolean update_stay_connected_status(String user_name, String pass, String stay_connected_status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, stay_connected_status);
        db.update(TABLE_NAME, contentValues, "user_name = ? AND password = ?", new String[] { user_name,pass } );
        log_this_action_for_mongo();
        return true;
    }
    public boolean update_stay_connected_status_by_id(String id, String stay_connected_status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, stay_connected_status);
        db.update(TABLE_NAME, contentValues, "id = ?", new String[] { id } );
        log_this_action_for_mongo();
        return true;
    }

    public Integer delete_data(String id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        log_this_action_for_mongo();
        return db.delete(TABLE_NAME, "id = ?", new String[] { id_user } );
    }
    private void log_this_action_for_mongo(){
        DBHelper_MongoDB_Data dbHelper_mongoDB_data = new DBHelper_MongoDB_Data(thisContext);
        dbHelper_mongoDB_data.update_mongo_data("","1","","");
    }
}

