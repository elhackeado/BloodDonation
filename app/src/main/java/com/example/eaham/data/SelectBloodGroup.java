package com.example.eaham.data;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectBloodGroup extends AppCompatActivity {

    String bloodGroup[] = {"A+", "A-", "B+", "B-","O+","O-","AB+","AB-"};
    ArrayAdapter<String> adpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_blood_group);
        getSupportActionBar().setTitle("Select Blood Group");
        adpater = new ArrayAdapter<String>(this,R.layout.activity_listview,bloodGroup);
        ListView listView = (ListView) findViewById(R.id.bgroup_listview);
        listView.setAdapter(adpater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent  = new Intent(getApplicationContext(),FullList.class);
                Bundle b = new Bundle();
                b.putString("bgroup",adpater.getItem(i).toString());
                intent.putExtras(b);
                startActivity(intent);
                Toast.makeText(SelectBloodGroup.this,adpater.getItem(i).toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
}

