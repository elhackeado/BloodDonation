package com.example.eaham.data;

import android.app.Activity;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class UserList extends ArrayAdapter<User> {


    private Activity context;
    List<User> userList;
    double lat1;
    double lon1;
    static ArrayList<String> near = new ArrayList<String>();

    public UserList(Activity context, List<User> userList, double latitude, double longitude){
        super(context, R.layout.list_item, userList);
        this.context = context;
        this.userList = userList;
        lat1 = latitude;
        lon1 = longitude;
    }






    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        User user = userList.get(position);

            View listViewItem = inflater.inflate(R.layout.list_item, null, true);
            TextView textView1 = (TextView) listViewItem.findViewById(R.id.textView1);
            TextView textView2 = (TextView) listViewItem.findViewById(R.id.textView2);
            TextView textView3 = (TextView) listViewItem.findViewById(R.id.textView3);
            TextView textView4 = (TextView) listViewItem.findViewById(R.id.textView4);

            textView1.setText(user.getName());
            textView2.setText("+91"+user.getPhone());
            textView3.setText(user.getBgroup());
            int distance = distance(lat1,lon1,user.getLatitude(),user.getLongitude());
            if (distance<=10)
                near.add(user.getFcmid());

            textView4.setText(String.valueOf(distance) + " KM");

            return listViewItem;





    }

    public int distance(double lat1,double lon1,double lat2,double lon2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        return (int)loc1.distanceTo(loc2)/1000;
    }

}
