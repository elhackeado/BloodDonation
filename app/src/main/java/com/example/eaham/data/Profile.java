package com.example.eaham.data;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    TextView textview1, textview2, textview3, textview4, textview5;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        final String userid = sharedPreferences.getString("userid", "hello");
        textview1 = (TextView) findViewById(R.id.textView6);
        textview2 = (TextView) findViewById(R.id.textView7);
        textview3 = (TextView) findViewById(R.id.textView8);
        textview4 = (TextView) findViewById(R.id.textView9);
        textview5 = (TextView) findViewById(R.id.textView10);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User/" + userid);


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    textview1.setText(dataSnapshot.child("name").getValue().toString().trim());
                    textview2.setText(dataSnapshot.child("email").getValue().toString().trim());
                    textview3.setText(dataSnapshot.child("phone").getValue().toString().trim());
                    textview4.setText(dataSnapshot.child("bgroup").getValue().toString().trim());
                    textview5.setText(dataSnapshot.child("address").getValue().toString().trim());


                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }
}
