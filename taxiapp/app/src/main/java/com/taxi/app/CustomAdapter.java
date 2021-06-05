package com.taxi.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

class CustomAdapter extends BaseAdapter {

    private Context applicationContext;
    private int sample;
    private List<JsonModel> jsonModels;


    CustomAdapter(Context applicationContext, int sample, List<JsonModel> jsonModels) {

        this.applicationContext = applicationContext;
        this.sample = sample;
        this.jsonModels = jsonModels;
    }

    @Override
    public int getCount() {
        return jsonModels.size();
    }

    @Override
    public Object getItem(int i) {
        return jsonModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(R.layout.custom_list, viewGroup, false);
        }

        TextView carModel, numSeats;

        carModel = view.findViewById(R.id.receivedCarModel);
        numSeats = view.findViewById(R.id.receivedNumSeats);

        //Set Fonts
        Typeface font = Typeface.createFromAsset(applicationContext.getAssets(), "fonts/LatoRegular.ttf");
        carModel.setTypeface(font);
        numSeats.setTypeface(font);

        carModel.setText("Model: "+jsonModels.get(i).getCarModel());
        numSeats.setText("Seats: "+jsonModels.get(i).getNumSeats());

        LinearLayout lay = view.findViewById(R.id.customListLayout);

        //Apply an indication colour to signify book status
        if(jsonModels.get(i).getBookStatus())
        {
            lay.setBackgroundColor(Color.rgb(31, 212, 175)); //Kind of blue
        }else{
            lay.setBackgroundColor(Color.rgb(255, 255, 255)); //white
        }

        return view;
    }
}

