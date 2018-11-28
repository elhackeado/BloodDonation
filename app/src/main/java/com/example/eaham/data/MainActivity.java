package com.example.eaham.data;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.sip.SipErrorCode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

   private Button login;
   private EditText name,pass;
   private TextView tv1;
   private DatabaseReference mdatabase;
   SharedPreferences sharedPreferences;
   private ProgressDialog progressDialog;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        login = (Button) findViewById(R.id.button);
        name = (EditText) findViewById(R.id.editText);
        sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        progressDialog = new ProgressDialog(MainActivity.this);



        pass = (EditText) findViewById(R.id.editText2);
        tv1 = (TextView) findViewById(R.id.textView4);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MainActivity.this,Registration.class);
                startActivity(in);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                final String result1=name.getText().toString();
                final String result2=pass.getText().toString();

                if((result1.isEmpty())||(result2.isEmpty()))
                {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Sorry Please fill All Details ",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    mdatabase=FirebaseDatabase.getInstance().getReference("User/"+result1);

                    mdatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String password = dataSnapshot.child("pass").getValue().toString().trim();
                            String name = dataSnapshot.child("name").getValue().toString().trim();
                            float latitude =  Float.parseFloat(dataSnapshot.child("latitude").getValue().toString());
                            float longitude = Float.parseFloat(dataSnapshot.child("longitude").getValue().toString());
                            if(result2.equals(password)){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userid",result1);
                                editor.putString("name",name);
                                editor.putBoolean("isLoggedIn",true);
                                editor.putFloat("latitude",latitude);
                                editor.putFloat("longitude",longitude);
                                editor.commit();
                                Intent  intent = new Intent(MainActivity.this,Main2Activity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this,"Invalid username or password",Toast.LENGTH_LONG).show();
                            }





                            }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });















                    //validate(name.getText().toString(), pass.getText().toString());
                }


            }
        });


    }





    }

