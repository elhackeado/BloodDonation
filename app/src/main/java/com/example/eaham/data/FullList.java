package com.example.eaham.data;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FullList extends AppCompatActivity {
    DatabaseReference profileRef;
    List<User> userList;
    ListView listViewUsers;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    UserList adapter;
    Button button;
    Bundle c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_list);
        c = getIntent().getExtras();
        final String bgroup = c.getString("bgroup");
        getSupportActionBar().setTitle("Donor List");
        getSupportActionBar().setSubtitle(bgroup + " Donors");
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        final double latitude = sharedPreferences.getFloat("latitude", 0);
        final double longitude = sharedPreferences.getFloat("longitude", 0);
        button = (Button) findViewById(R.id.sendnotification);
        listViewUsers = (ListView) findViewById(R.id.list_view);
        userList = new ArrayList<>();
        progressDialog = new ProgressDialog(FullList.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching list of donors..");
        progressDialog.show();
        profileRef = FirebaseDatabase.getInstance().getReference("User");
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //addNotification();
                userList.clear();
                ;


                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getBgroup().equals(bgroup))
                        userList.add(user);
                }

                adapter = new UserList(FullList.this, userList, latitude, longitude);
                listViewUsers.setAdapter(adapter);
                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });


        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                final double maplat = adapter.getItem(i).getLatitude();
                final double maplng = adapter.getItem(i).getLongitude();
                final String mapname = adapter.getItem(i).getName();
                final String mapaddress = adapter.getItem(i).getAddress();
                final long mapphone = Long.parseLong(adapter.getItem(i).getPhone());
                final AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(FullList.this);
                alertdialogbuilder.setTitle(mapname);
                alertdialogbuilder.setMessage("What you want to do ?");
                alertdialogbuilder.setCancelable(true);
                alertdialogbuilder.setPositiveButton("Call " + mapname, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + mapphone));//change the number
                        if (ActivityCompat.checkSelfPermission(FullList.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            if (ActivityCompat.shouldShowRequestPermissionRationale(FullList.this,
                                    Manifest.permission.CALL_PHONE)) {
                            } else {
                                ActivityCompat.requestPermissions(FullList.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        0);
                            }
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);
                        Toast.makeText(FullList.this,"calling " + mapphone,Toast.LENGTH_LONG).show();
                    }
                });
                alertdialogbuilder.setNegativeButton("Get Address", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        Intent intent  = new Intent(getApplicationContext(),MapsActivity.class);
                        Bundle b = new Bundle();
                        b.putString("mapname",mapname);
                        b.putString("mapaddress",mapaddress);
                        b.putDouble("maplat",maplat);
                        b.putDouble("maplng",maplng);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });

                AlertDialog alertDialog = alertdialogbuilder.create();
                alertDialog.show();;






            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(FullList.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Notifying donors within 10KM..");
                progressDialog.show();
                sendNotification();

            }
        });
    }

    public void sendNotification(){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://projectuscreativity.com/paypal/firebase/gistfile1.php";
            JSONObject jsonBody = new JSONObject();
            int nearsize = UserList.near.size();

            for(int i=0;i<nearsize;i++)
            jsonBody.put(String.valueOf(i), UserList.near.get(i));
            UserList.near.clear();
            jsonBody.put("msg", "Urgently need Blood Group " + c.getString("bgroup") );



            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    progressDialog.dismiss();
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));

                }
            };

            requestQueue.add(stringRequest);

            button.setVisibility(View.GONE);
            Toast.makeText(FullList.this,"Sent notification to " + nearsize + " donors.",Toast.LENGTH_LONG).show();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
