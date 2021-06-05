package com.taxi.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class driver_add_journey extends AppCompatActivity implements NetworkInterface {
    RequestQueue requestQue;
    TextView addJourneyNow, payModelTxt;
    EditText addNewModel, addNewPlate, addNewSeats, addStartingPoint, addDestination;

    String driver_token = "";

    RegModel mainRegModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_add_journey);

        requestQue = Volley.newRequestQueue(getApplicationContext());

        driver_token = GlobalIdentity.userToken;

        //addNewModel = findViewById(R.id.addNewModel);
        //payModelTxt = findViewById(R.id.payCarModel);
        addNewSeats = findViewById(R.id.addJourneySeats);
        //addNewPlate= findViewById(R.id.addNewPlate);

        addStartingPoint = findViewById(R.id.addJourneyLocation);
        addDestination = findViewById(R.id.addJourneyDestination);

        addJourneyNow = findViewById(R.id.driverAddJourneyNow);

        //Set Fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        //addNewModel.setTypeface(font);
        //payModelTxt.setTypeface(font);
        addNewSeats.setTypeface(font);
        //addNewPlate.setTypeface(font);
        addStartingPoint.setTypeface(font);
        addDestination.setTypeface(font);

        addJourneyNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Show();
                processAddJourney();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void processAddJourney(){
        //String holdModel = addNewModel.getText().toString();
        //String holdPlate = addNewPlate.getText().toString();
        String holdSeats = addNewSeats.getText().toString();
        String holdStartLocation = addStartingPoint.getText().toString();
        String holdDestination = addDestination.getText().toString();

        int regLength = 1;

        //Check if all is okay
        if (holdSeats.length() >= regLength
                && holdStartLocation.length() >= regLength
                && holdDestination.length() >= regLength) {

            RegModel mod = new RegModel();
            //mod.setEmail(holdModel);
            //mod.setName(holdPlate);
            mod.setSeats(holdSeats);
            mod.setJourneyLocation(holdStartLocation);
            mod.setJourneyDestination(holdDestination);

            mainRegModel = mod;
            sending(Divala.journeysURL);
        } else {
            Show("Sorry, some details are missing.");
        }
    }

    void Show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    int recievedStatusCode = 0;
    @Override
    public void sending(String url) {
        JSONObject params = new JSONObject();
        try {
            params.put("start", mainRegModel.getJourneyLocation());
            params.put("destination", mainRegModel.getJourneyDestination());
            params.put("number_of_seats_available", mainRegModel.getSeats());
            params.put("price", 4000);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(recievedStatusCode == 201){
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int respCode = error.networkResponse.statusCode;
                        //if(respCode == 400) Show("Email already exists");
                        Show("error: "+error.getMessage());
                        //Show("An error occurred");
                        //when 400, already exists
                    }
                }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization", String.format("Token %s", GlobalIdentity.userToken));
                return headers;
            }

            @Override
            public Response parseNetworkResponse(NetworkResponse response) {
                recievedStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        requestQue.add(stringRequest);
    }

    void Show(){
        LayoutInflater inflater = getLayoutInflater();
        View viewLay = inflater.inflate(R.layout.custom_toast, null);
        Toast toast = Toast.makeText(this, "Toast:Gravity.TOP", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(viewLay);
        toast.show();
    }
}
