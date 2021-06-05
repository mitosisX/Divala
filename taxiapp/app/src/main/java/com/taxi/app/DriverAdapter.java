package com.taxi.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

class DriverAdapter extends BaseAdapter {

    private Context applicationContext;
    private int sample;
    private List<JsonModel> jsonModels;


    DriverAdapter(Context applicationContext, int sample, List<JsonModel> jsonModels) {

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

            view = layoutInflater.inflate(R.layout.driver_custom_list, viewGroup, false);
        }

        TextView journeyRoute, numSeats;

        journeyRoute = view.findViewById(R.id.receivedJourneyRoute);
        numSeats = view.findViewById(R.id.receivedNumSeat);

        //Set Fonts
        Typeface font = Typeface.createFromAsset(applicationContext.getAssets(), "fonts/LatoRegular.ttf");
        journeyRoute.setTypeface(font);
        numSeats.setTypeface(font);

        journeyRoute.setText("Route: "+jsonModels.get(i).getJourneyDestination());
        numSeats.setText("Seats: "+jsonModels.get(i).getNumSeats());

        LinearLayout lay = view.findViewById(R.id.customListLayout);
        lay.setBackgroundColor(Color.rgb(255, 255, 255)); //white

        return view;
    }
}

