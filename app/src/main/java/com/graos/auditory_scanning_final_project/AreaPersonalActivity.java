package com.graos.auditory_scanning_final_project;
/**
 * Created by GG on 05/01/2017.
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
//public class AreaPersonalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

public class AreaPersonalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextView _user_view;
    Spinner _spinner_patient;
    Spinner _spinner_choose;
    EditText input_patient;

    ArrayList<String> list_patients;
    ArrayAdapter<String> adapter;
    String userName_signIn;
    String userName_register;
    String idNew_patient, nameNew_patient, id_therapist;
    String id_patient;
    String id_therapist_to_displayAct;
    int flag_newUser=0;
    int flag_login=0;
    String s_state;
    int s_state_index;
    int flag_add_one_user = 0;
    DBHelper_Patients my_dbHelper_patients;
    private boolean first_entry = false;

    private global_variables mApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_personal);
        setTitle(R.string.nameActivity_area_personal);

        mApp = ((global_variables)getApplicationContext());
        mApp.is_start_from_display_rama_1 = false;

        first_entry = true;
        my_dbHelper_patients = new DBHelper_Patients(this);

        // -- connect with Xml --
        _spinner_patient = (Spinner) findViewById(R.id.spinner_show);

        // -- get intent --
        Intent i_result = getIntent();
        id_therapist = i_result.getStringExtra("ID_REGISTER");

        if(i_result.getStringExtra("USER_REGISTER") != null){
            userName_register = i_result.getStringExtra("USER_REGISTER");
            flag_newUser = 1;
        }

        else if(i_result.getStringExtra("USER_SIGN_IN") != null){
            userName_signIn = i_result.getStringExtra("USER_SIGN_IN");
            flag_login = 1;
        }

        _spinner_patient.setOnItemSelectedListener(this);

        if(flag_newUser == 1){
            addPatient(_user_view);
        }
        populateSpinnerView();
    }


    // ------------ Select Patient --------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l){
        id_patient = adapterView.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this,"Select/Add Patient",Toast.LENGTH_SHORT).show();
    }



    // ---------------- PRESS NEXT ----------------------
    public void press_next(View view){

        // get audio data
        DBHelper_Patients_Data dbHelper_patients_data = new DBHelper_Patients_Data(AreaPersonalActivity.this);
        Cursor cursor = dbHelper_patients_data.get_patient_data_by_id(id_patient);
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                mApp.setUriYesVideo(Uri.fromFile(new File(cursor.getString(0))));
                mApp.setAudioPath(cursor.getString(1));
                try {
                    ArrayList<String> listdata = new ArrayList<String>();
                    JSONArray jArray = new JSONArray(cursor.getString(2));
                    if (jArray != null)
                        for (int j = 0; j < jArray.length(); j++)
                            listdata.add(jArray.getString(j));
                    mApp.setMatchesList(listdata);
                } catch (JSONException e) {
                }
            }
        }else {
            mApp.setUriYesVideo(null);
            mApp.setAudioPath(null);
            mApp.setMatchesList(null);
        }

        DBHelper_Requests temp_db_request = new DBHelper_Requests(this);
        Cursor c = temp_db_request.show_requests_by_patient_id(id_patient);

        if(c.getCount() == 0){
            Intent it = new Intent(AreaPersonalActivity.this, Edit_Rama_1.class);
            mApp.idPatient = id_patient;
            mApp.idTherapist = id_therapist_to_displayAct;
            it.putExtra("ID_PATIENT",id_patient);
            startActivity(it);
        }
        else{
            Intent i = new Intent(this, Display_Rama_1.class);
            mApp.l = 0;
            mApp.l1="";
            mApp.l2="";
            mApp.l3="";
            mApp.l4="";
            mApp.idPatient = id_patient;
            mApp.idTherapist = id_therapist_to_displayAct;
            i.putExtra("ID_PATIENT", id_patient);
            i.putExtra("ID_ONLY", id_therapist_to_displayAct);
            startActivity(i);
        }
    }



    // ---------------------------------------------------------------------
    // --------------------- Add Patient -----------------------------------
    public void addPatient(View v) {
        View view = LayoutInflater.from(AreaPersonalActivity.this).inflate(R.layout.add_patient_layout, null);
        final EditText user_id = (EditText) view.findViewById(R.id.newPt_id);
        final EditText user_name = (EditText) view.findViewById(R.id.newPt_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(AreaPersonalActivity.this);
        builder.setIcon(android.R.drawable.ic_menu_edit);
        builder.setTitle(R.string.newPtnt_tittle);
        builder.setView(view);
        builder.setPositiveButton(R.string.newPtnt_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if(user_id.getText().toString().isEmpty() || user_name.getText().toString().isEmpty())
                    Toast.makeText(AreaPersonalActivity.this,R.string.error_fields_required_alert,Toast.LENGTH_SHORT).show();

                else{
                    idNew_patient = user_id.getText().toString();
                    nameNew_patient = user_name.getText().toString();
                    //Toast.makeText(AreaPersonalActivity.this,"idPt: " + idNew_patient + "\nnamePt: " + nameNew_patient + "\nidThpt: " + id_therapist,Toast.LENGTH_SHORT).show();

                    if (idNew_patient.matches("\\d+(?:\\.\\d+)?")){  // is a number
                        long b = my_dbHelper_patients.add_patient(idNew_patient, nameNew_patient, id_therapist);
                        if (b == -1)
                            Toast.makeText(AreaPersonalActivity.this, R.string.sign_up_error, Toast.LENGTH_SHORT).show();
                        else{
                            Toast.makeText(AreaPersonalActivity.this, R.string.sign_up_successful, Toast.LENGTH_SHORT).show();
                            populateSpinnerView();
                            mApp.setUriYesVideo(null);
                            mApp.setAudioPath(null);
                            mApp.setMatchesList(null);
                            mApp.l = 0;
                            mApp.l1="";
                            mApp.l2="";
                            mApp.l3="";
                            mApp.l4="";
                            mApp.idPatient = idNew_patient;
                            mApp.idTherapist = id_therapist_to_displayAct;
                            Intent it = new Intent(AreaPersonalActivity.this, Edit_Rama_1.class);
                            it.putExtra("ID_PATIENT",nameNew_patient + " - " + idNew_patient);
                            startActivity(it);
                            flag_newUser = 0;
                        }
                    }
                    else
                        Toast.makeText(AreaPersonalActivity.this, R.string.id_error_no_int, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.newPtnt_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }


    // -- show data //
    public void view_db_patients(View v){
        Cursor res = my_dbHelper_patients.show_patients();
        if(res.getCount() == 0) {
            showMessage("Error", "No data found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()){
            buffer.append("Id_Patient: " + res.getString(0) + "\n");
            buffer.append("Name: " + res.getString(1) + "\n");
            buffer.append("Id_therapist: " + res.getString(2) + "\n\n");
        }
        showMessage("Data", buffer.toString());
    }

    public void showMessage(String tittle, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(tittle);
        builder.setMessage(Message);
        builder.show();
    }
    //--- END View all ----


    // ---------------------------------------------------------------------
    // --------------------- Share Patient --------------------------------
    public void sharePatient(View view){
        //Toast.makeText(this,"Share Demo Patients",Toast.LENGTH_SHORT).show();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {"send_to@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        /*String filename="contacts_sid.vcf";
        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = Uri.fromFile(filelocation);
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);*/
// the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    protected void sendEmail() {
        Log.d("Send email", "");

        String[] TO = {"someone@gmail.com"};
        String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.d("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    // SPINNER VIEW
    public void populateSpinnerView(){
        Cursor cursor = my_dbHelper_patients.show_patients_by_id(id_therapist);
        if(cursor.getCount() != 0) {
            list_patients = new ArrayList<String>();
            //Toast.makeText(this,"id: "+id_therapist, Toast.LENGTH_SHORT).show();

            while (cursor.moveToNext()) {
                id_therapist_to_displayAct = cursor.getString(2);
                list_patients.add(cursor.getString(1) + " - " + cursor.getString(0));
            }
            adapter = new ArrayAdapter<String>(AreaPersonalActivity.this,android.R.layout.simple_spinner_item, list_patients);
            adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
            _spinner_patient.setAdapter(adapter);
        }

    }


    // REMOVE PATIENT
    public void remove_patient(View v){
        final AlertDialog.Builder builder = new AlertDialog.Builder(AreaPersonalActivity.this);
        builder.setTitle(R.string.delete_pt_tittle);
        builder.setIcon(R.mipmap.ic_remove);
        builder.setMessage(R.string.delete_pt_quetion)

                .setPositiveButton(R.string.button_get_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        my_dbHelper_patients = new DBHelper_Patients(AreaPersonalActivity.this);
                        String[] myStrings_ids = id_patient.split(" - ");
                        String id_pti = myStrings_ids[1];
                        Integer delete_row = my_dbHelper_patients.delete_patient(id_pti);
                        if(delete_row > 0){
                            populateSpinnerView();
                            Toast.makeText(getApplicationContext(), R.string.delete_pt_item , Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), R.string.error_delete_pt_item , Toast.LENGTH_LONG).show();
                    }
                })

                .setNegativeButton(R.string.button_get_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.show();
    }


    // HELP ICON
    public void help_personal_area_activity(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(AreaPersonalActivity.this);
        builder.setTitle(R.string.tittle_help_main);
        builder.setIcon(R.mipmap.ic_help3);
        builder.setMessage(R.string.help_personal_area_activity);
        builder.show();
    }
}







