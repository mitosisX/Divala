package com.taxi.app;

import android.app.Activity;
import android.app.AlertDialog;
//import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.support.design.widget.*;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity implements NetworkInterface {
    RequestQueue requestQue;

    EditText pswd, userEmail;
    TextView registerUser, loginNow, inf;
    ProgressBar progress;

    RegModel mainRegModel;
    private int recievedStatusCode = 0; //The status response code received through volley

    String filePath;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQue = Volley.newRequestQueue(getApplicationContext());
        progress = findViewById(R.id.progressIndicator);

        loginNow = findViewById(R.id.loginAccount);
        userEmail = findViewById(R.id.loginEmail);
        pswd = findViewById(R.id.pswrdd);
        registerUser = findViewById(R.id.sup);
        inf = findViewById(R.id.info_login);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");

        loginNow.setTypeface(custom_font1);
        registerUser.setTypeface(custom_font);
        userEmail.setTypeface(custom_font);
        pswd.setTypeface(custom_font);
        inf.setTypeface(custom_font1);
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent it = new Intent(getApplicationContext(), driver_main.class);
                //startActivity(it);
                processLogin();
                ShowSnack("Loading. Please wait!");
            }
        });

        showHELP();
        showDEMO();
    }

    void showHELP(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);
        sequence.addSequenceItem(loginNow, "Use this button to login.","GOT IT!");
        sequence.addSequenceItem(registerUser, "Use this button to register an account.","GOT IT!");
        sequence.start();
    }


    void showDEMO() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("DEMO DEMO DEMO DEMO!")
                .setMessage("This is an intentional message! CLIENT - DEVELOPER AGREEMENT NOT FULLY MET!");
        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        builder.create().show();
    }

    void ShowSnack(String msg){
        Snackbar snack = Snackbar.make(findViewById(R.id.relLayout), msg, Toast.LENGTH_SHORT);
        snack.show();
    }

    void processLogin() {
        String holdEmail = userEmail.getText().toString();
        String holdPassword = pswd.getText().toString();

        int regLength = 1;

        //Check if all is okay
        if (holdEmail.length() >= regLength && holdPassword.length() >= regLength) {

            RegModel mod = new RegModel();
            mod.setEmail(holdEmail);
            mod.setPassword(holdPassword);

            progress.setVisibility(View.VISIBLE);

            mainRegModel = mod;
            //ShowSnack("Processing. Please Wait!");
            sending(Divala.loginURL);
        } else {
            ShowSnack("Sorry, some details are missing.");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void sending(String url) {
        JSONObject params = new JSONObject();
        try {
            params.put("username", mainRegModel.getEmail());
            params.put("password", mainRegModel.getPassword());
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
                        if (recievedStatusCode == 200) {
                            try {
                                progress.setVisibility(View.INVISIBLE);

                                //Show(response.toString());
                                JSONObject obj = new JSONObject(response.toString());
                                int holdID = obj.getInt("user_id");
                                String holdToken = obj.getString("token");
                                String holdBase64 = obj.getString("base_64");
                                String holdName = obj.getString("name");

                                GlobalIdentity.userToken = holdToken;
                                GlobalIdentity.name = holdName;
                                GlobalIdentity.base_64 = holdBase64;

                                boolean isDriver = obj.getBoolean("is_driver");

                                //Let's check if the user is a regular customer or a driver
                                if (!isDriver) {
                                    startActivity(new Intent(MainActivity.this, customer_main.class));
                                } else {
                                    //GlobalIdentity.driverURL = "http://divala.herokuapp.com/drivers/" + holdID + "/";
                                    startActivity(new Intent(MainActivity.this, driver_main.class));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int respCode = error.networkResponse.statusCode;
                        //Show(String.format("error: %s\ncode: %s", error.getMessage(), respCode));
                        if (respCode == 400) {
                            progress.setVisibility(View.INVISIBLE);
                            Show("Login failed");
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public Response parseNetworkResponse(NetworkResponse response) {
                recievedStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQue.add(stringRequest);
    }

    void Show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Register As")
                .setMessage("How do you want to register?");
        builder.setPositiveButton("Driver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent it = new Intent(MainActivity.this, signup_driver.class);
                startActivityForResult(it, 5);
            }
        });
        builder.setNegativeButton("Customer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent it = new Intent(MainActivity.this, signup_customer.class);
                startActivity(it);
            }
        });
        builder.create().show();
    }
}