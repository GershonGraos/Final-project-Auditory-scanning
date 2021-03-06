package com.graos.auditory_scanning_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SA on 15/06/2017.
 */

// *************** CREATE DATA BASE ****************
public class DBHelper_Requests extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "requests.db";
    public static final String TABLE_NAME = "requests_table";
    public static final String COL_1 = "id_patient";
    public static final String COL_2 = "parent_id";
    public static final String COL_3 = "stage";
    public static final String COL_4 = "request";
    public static final String COL_5 = "id";
    public static final String COL_6 = "last_update";
    public static final String COL_7 = "counter";

    private Context thisContext;

    public DBHelper_Requests(Context context) {
        super(context, DATABASE_NAME, null, 1);
        thisContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( id_patient TEXT, parent_id TEXT, stage TEXT, request TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, last_update TEXT, counter INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public long add_request(String id_pt, String id_parent, String stage_rama, String request, String up, int count){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id_pt);
        contentValues.put(COL_2, id_parent);
        contentValues.put(COL_3, stage_rama);
        contentValues.put(COL_4, request);
        contentValues.put(COL_6, up);
        contentValues.put(COL_7, count);
        long result_add = db.insert(TABLE_NAME, null, contentValues); // add the db
        log_this_action_for_mongo();
        return result_add;
    }

    public Cursor show_requests(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
    public Cursor show_requests_by_patient_id(String id_of_patient){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where id_patient = '"+id_of_patient+"'", null);
        return res;
    }
    public Integer delete_data(String id_counter){
        SQLiteDatabase db = this.getWritableDatabase();
        log_this_action_for_mongo();
        return db.delete(TABLE_NAME, "id = ?", new String[] { id_counter } );
    }

    public boolean update_data(String req, String i){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4, req);
        db.update(TABLE_NAME, contentValues, "id = ?", new String[] { i } );
        log_this_action_for_mongo();
        return true;
    }

    public boolean update_counter(int count, String i){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7, count);
        db.update(TABLE_NAME, contentValues, "id = ?", new String[] { i } );
        log_this_action_for_mongo();
        return true;
    }

    public Cursor show_requests_level_1_sorted_statistically(String patient_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select request from " + TABLE_NAME + " where id_patient = '" + patient_id + "' AND parent_id = '-1' AND stage = '1' order by counter DESC", null);
        return res;
    }

    public Cursor show_requests_level_1(String patient_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select request from " + TABLE_NAME + " where id_patient = '" + patient_id + "' AND parent_id = '-1' AND stage = '1'", null);
        return res;
    }

    public Cursor show_requests_level_2_3_4_sorted_statistically(String patient_id,String parent_id,String stage){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select request from " + TABLE_NAME + " where id_patient = '" + patient_id + "' AND parent_id = '" + parent_id + "' AND stage = '" + stage + "' order by counter DESC", null);
        return res;
    }

    public Cursor show_requests_level_2_3_4(String patient_id,String parent_id,String stage){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select request from " + TABLE_NAME + " where id_patient = '" + patient_id + "' AND parent_id = '" + parent_id + "' AND stage = '" + stage + "'", null);
        return res;
    }
    public Cursor get_req_id_by_req_name(String req){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select id from " + TABLE_NAME + " where request = '"+req+"'", null);
        return res;
    }
    public Cursor get_request_id_by_request_and_patient_id_and_parent_request(String req,String patient_id,String parent_req){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor tmp = get_req_id_by_req_name(parent_req);
        tmp.moveToFirst();
        String p_id = tmp.getString(0);
        Cursor res = db.rawQuery("select id from " + TABLE_NAME + " where request = '"+req+"' and parent_id = '"+p_id+"' and id_patient = '"+patient_id+"'", null);
        return res;
    }
    private void log_this_action_for_mongo(){
        DBHelper_MongoDB_Data dbHelper_mongoDB_data = new DBHelper_MongoDB_Data(thisContext);
        dbHelper_mongoDB_data.update_mongo_data("","","1","");
    }
}