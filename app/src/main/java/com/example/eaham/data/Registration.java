package com.example.eaham.data;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Registration extends AppCompatActivity {

    private DatabaseReference mDatabase;
    EditText editText, editText2, editText3, editText4;
    Button button;
    String name, email, phone, pass, bgroup;
    Spinner spinner;
    public static String location;
    private ProgressDialog progressDialog;
    LocationManager mLocManager;
    boolean GpsStatus;
    Context context;
    public static double latitude;
    public static double longitude;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        context = getApplicationContext();
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        progressDialog = new ProgressDialog(Registration.this);
        button = (Button) findViewById(R.id.button2);
        if (ActivityCompat.checkSelfPermission(Registration.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Registration.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(Registration.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (ActivityCompat.checkSelfPermission(Registration.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Registration.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Registration.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                0);
                    }

                    GPSStatus();
                    if(GpsStatus == false)
                    {
                        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent1);
                        finish();

                    }
                    else {
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Registering new user..");
                        progressDialog.show();
                        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        LocationListener mLocListener = new MyLocationListener();

                        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);



                        name = editText.getText().toString().trim();
                        email = editText2.getText().toString().trim();
                        phone = editText3.getText().toString().trim();
                        pass = editText4.getText().toString().trim();
                        if (name.equals("") || email.equals("") || phone.equals("") || pass.equals("") || bgroup.equals("BLOOD GROUP")) {
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, "FILL ALL FIELDS", Toast.LENGTH_LONG).show();
                        }
                        else if (longitude==0 || latitude==0){
                            Toast.makeText(context,"WAIT..Let us fetch your location....",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                        else {
                            address = getCompleteAddressString(latitude,longitude);
                            User user = new User(name, email, phone, pass, bgroup, latitude, longitude, address);
                            mDatabase.child("User").child(phone).setValue(user);
                            Toast.makeText(Registration.this, "REGISTERED SUCCESSFULLY " + longitude+ " , " + latitude + " " + address, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Registration.this, MainActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                }
                catch (Exception e){
                    progressDialog.dismiss();
                    Toast.makeText(Registration.this, e.toString(),Toast.LENGTH_LONG).show();
                }

            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> categories= new ArrayList<String>();
        categories.add("BLOOD GROUP");
        categories.add("A+");
        categories.add("A-");
        categories.add("B+");
        categories.add("B-");
        categories.add("O+");
        categories.add("O-");
        categories.add("AB+");
        categories.add("AB-");

        //CREATING ADAPTER FOR SPINNER

        ArrayAdapter<String> dataadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categories);
        dataadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataadapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 bgroup = adapterView.getItemAtPosition(i).toString().trim();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(Registration.this,"Please select your blood group.",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void GPSStatus(){
        mLocManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Current loction address", "Canont get Address!");
        }
        return strAdd;
    }



}


class User {

    public String name;
    public String email;
    public String phone;
    public String pass;
    public String bgroup;
    public String address;
    public  double latitude;
    public double longitude;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String phone, String pass, String bgroup, double latitude, double longitude, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.pass = pass;
        this.bgroup = bgroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }



    public String getName() {return name;}

    public String getEmail() {return email; }

    public String getPhone() {return phone; }

    public String getBgroup() { return bgroup; }

    public double getLatitude() {return latitude;}

    public double getLongitude() {return longitude; }

    public String getAddress() {return address; }

}

    class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc) {
            Registration.longitude = loc.getLongitude();
            Registration.latitude = loc.getLatitude();


        }
        public void onProviderDisabled(String arg0) {

        }
        public void onProviderEnabled(String provider) {

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }



