package com.example.eaham.data;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    SharedPreferences sharedPreferences;
    TextView textView;
    Button button,button2,button3;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        final String name = sharedPreferences.getString("name","User Name");
        final String userid = sharedPreferences.getString("userid","id");
        mDatabase = FirebaseDatabase.getInstance().getReference("User/"+userid);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer2);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        button = (Button) findViewById(R.id.button3);
        button2 = (Button) findViewById(R.id.button4);
        button3 = (Button) findViewById(R.id.button5);


        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        textView = (TextView) headerView.findViewById(R.id.textview);
        textView.setText(name);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this,SelectBloodGroup.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this,BloodCompatibility.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, Registration.class);
                startActivity(intent);
                finish();
            }
        });

        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "getInstanceId failed", task.getException());
                            Toast.makeText(Main2Activity.this, "Token Error", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                       // String msg = getString(R.string.msg_token_fmt, token);
                        //Log.d(TAG, msg);
                        mDatabase.child("fcmid").setValue(token);
                        Toast.makeText(Main2Activity.this, userid+token, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END retrieve_current_token]

        //request1();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mActionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.item1: {
                Intent intent = new Intent(Main2Activity.this,Profile.class);
                startActivity(intent);
                break;
            }
            case R.id.item2: {
                Intent intent = new Intent(Main2Activity.this,SelectBloodGroup.class);
                startActivity(intent);
                break;
            }
            case R.id.item3: {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
                Toast.makeText(Main2Activity.this,"Successfully Logged Out..",Toast.LENGTH_LONG).show();
                break;
            }

        }
        return true;
    }

    public void request(){

// ...

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://projectuscreativity.com/paypal/firebase/gistfile1.php";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Toast.makeText(Main2Activity.this, response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Main2Activity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void request1(){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://projectuscreativity.com/paypal/firebase/gistfile1.php";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("1", "fVYkrlcy_wA:APA91bFViOFpcMqxeXiXMr-TtgVs4mE8LXN0P6s3xiThtCHIJavdXOC4WwWGkZeUGu-aTuRRBpWV33vSgQFmtA2DeyC5rpAnfu5ixGQJgkYEYPrnmQJSfPa5nBHlXvmuFI9oqRXYfoIi");
            jsonBody.put("2", "djltGWnpRTA:APA91bGCyylP_1rha50M9qohWaUieUyJze7zq_hVJvd0sfCj9itooXdr--ff4-ErPcYN6l8AImZw5BGOvj8u8uHf6_LTYY3EaZfllkBlfnAW97E5hOO8aFd1sduyYQ86CD4vgVXZ2Kk_");

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
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
