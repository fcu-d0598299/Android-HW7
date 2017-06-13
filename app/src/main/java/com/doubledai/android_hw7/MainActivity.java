package com.doubledai.android_hw7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HotelArrayAdapter adapter = null;

    private static final int LIST_HOTEL = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_HOTEL: {
                    List<Hotel> Hotels = (List<Hotel>)msg.obj;
                    refreshPetList(Hotels);
                    break;
                }
            }
        }
    };

    private void refreshPetList(List<Hotel> Hotels) {
        adapter.clear();
        adapter.addAll(Hotels);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lvHotel = (ListView)findViewById(R.id.ls_hotel) ;
        adapter = new HotelArrayAdapter(this, new ArrayList<Hotel>());
        lvHotel.setAdapter(adapter);
        getHotelFromFirebase();
    }

    class FirebaseThread extends  Thread {
        private DataSnapshot dataSnapshot;
        public FirebaseThread(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }
        public void run(){
            List<Hotel> lsHotel=new ArrayList<>();
            for(DataSnapshot ds: dataSnapshot.getChildren()){
                DataSnapshot dsSName = ds.child("Name");
                DataSnapshot dsSPhone = ds.child("Tel");
                DataSnapshot dsSAddress = ds.child("Add");

                String Name = (String)dsSName.getValue();
                String Phone = (String)dsSPhone.getValue();
                String Address = (String)dsSAddress.getValue();

                DataSnapshot dsSImg = ds.child("Picture1");
                String imgUrl = (String)dsSImg.getValue();
                Bitmap Img =  getImgBitmap(imgUrl);

                Hotel aHotel = new Hotel();
                aHotel.setImgUrl(Img);
                aHotel.setName(Name);
                aHotel.setPhone(Phone);
                aHotel.setAddress(Address);
                lsHotel.add(aHotel);

                Log.v("Hotel",Name+";"+Phone+";"+Address);

            }

            Message msg = new Message();
            msg.what = LIST_HOTEL;
            msg.obj = lsHotel;
            handler.sendMessage(msg);

        }
    }

    private void getHotelFromFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new FirebaseThread(dataSnapshot).start();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("Hotel",databaseError.getMessage());
            }
        });
    }


    private  Bitmap getImgBitmap (String imgUrl){
        try {
            URL url = new URL(imgUrl);
            Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bm;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    class HotelArrayAdapter extends ArrayAdapter<Hotel> {
        Context context;

        public HotelArrayAdapter(Context context, List<Hotel> items) {
            super(context, 0, items);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout itemlayout = null;
            if (convertView == null) {
                itemlayout = (LinearLayout) inflater.inflate(R.layout.hotel_item, null);
            } else {
                itemlayout = (LinearLayout) convertView;
            }
            Hotel item = (Hotel) getItem(position);
            TextView tvName = (TextView) itemlayout.findViewById(R.id.tv_name);
            tvName.setText(item.getName());
            TextView tvPhone = (TextView) itemlayout.findViewById(R.id.tv_phone);
            tvPhone.setText(item.getPhone());
            TextView tvAddress = (TextView) itemlayout.findViewById(R.id.tv_address);
            tvAddress.setText(item.getAddress());
            ImageView ivHotel = (ImageView) itemlayout.findViewById(R.id.iv_hotel);
            ivHotel.setImageBitmap(item.getImgUrl());
            return itemlayout;
        }
    }
}
