package com.taxi.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class customer_book_car extends AppCompatActivity implements NetworkInterface {
    RequestQueue requestQue;
    TextView payCarModel, payDriverNow;
    EditText payingAmount, payingNumber;

    String customer_token = "";
    String journeyURL = "";
    private int recievedStatusCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_pay_driver);

        requestQue = Volley.newRequestQueue(getApplicationContext());

        String[] details = getIntent().getStringExtra("details").split(",");

        journeyURL = details[1];

        customer_token = GlobalIdentity.userToken;

        payCarModel = findViewById(R.id.payCarModel);
        payCarModel.setText(details[0]);
        payingNumber = findViewById(R.id.payPhoneNumber);
        payingAmount = findViewById(R.id.payAmount);

        payDriverNow = findViewById(R.id.payDriver);

        //Set Fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        payCarModel.setTypeface(font);
        payingAmount.setTypeface(font);
        payingNumber.setTypeface(font);

        payDriverNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Show("Booking car");
                    sending(Divala.billingURL);
                } catch (JSONException e) {
                }
            }
        });
    }

    void Show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sending(String url) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("journey", journeyURL);

        requestQue.add(new JsonObjectRequest(
                Request.Method.POST,
                url,
                o,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        if (recievedStatusCode == 201) {
                            Show("Booked Successfully!");
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int respCode = error.networkResponse.statusCode;
                        if (respCode == 401) {
                            Show("Booking Failed!");
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", String.format("Token %s", GlobalIdentity.userToken));
                return headers;
            }

            @Override
            public Response parseNetworkResponse(NetworkResponse response) {
                recievedStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        });
    }
}
