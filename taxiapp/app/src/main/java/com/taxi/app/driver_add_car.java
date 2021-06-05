package com.taxi.app;

import android.content.Intent;
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

public class driver_add_car extends AppCompatActivity implements NetworkInterface {
    RequestQueue requestQue;
    TextView addCarNow, payModelTxt;
    EditText addNewModel, addNewPlate;

    String driver_token = "";

    RegModel mainRegModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_add_car);

        requestQue = Volley.newRequestQueue(getApplicationContext());

        driver_token = GlobalIdentity.userToken;
        addNewModel = findViewById(R.id.addNewModel);
        addNewPlate = findViewById(R.id.addNewPlate);

        addCarNow = findViewById(R.id.driverAddCarNow);
        addCarNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String tempModel = addNewModel.getText().toString();
                String tempPlate = addNewPlate.getText().toString();

                if(tempModel.length()>=1
                    && tempPlate.length() >=1) {
                    Show();

                    RegModel js = new RegModel();
                    js.setModel(tempModel);
                    js.setPlate(tempPlate);
                    mainRegModel = js;
                    sending(Divala.driverQueryURL);
                }else{
                    Show("Some details missing.");
                }
            }
        });

        //Set Fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        addNewModel.setTypeface(font);
        addNewPlate.setTypeface(font);
        //payModelTxt.setTypeface(font);
    }

    @Override
    public void onBackPressed() {

    }

    void Show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    int recievedStatusCode = 0;
    @Override
    public void sending(String url) {
        JSONObject params = new JSONObject();
        try {
            params.put("number_plate", mainRegModel.getPlate());
            params.put("model", mainRegModel.getModel());

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
                            Show("Done");
                            startActivity(new Intent(driver_add_car.this, driver_main.class));
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int respCode = error.networkResponse.statusCode;
                        //if(respCode == 400) Show("Email already exists");
                        //Show("An error occurred");
                        Show("error: "+error.getMessage()+"\ncode"+error.networkResponse.statusCode);
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
