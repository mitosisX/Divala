package com.taxi.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class signup_customer extends AppCompatActivity implements NetworkInterface {
    EditText email, password, username, natID;
    TextView lin, inf, pickImage, signUp;

    ImageView thumbCus;

    RequestQueue requestQue;
    RegModel mainRegModel;
    int recievedStatusCode = 0;

    String filePath;
    String base64Image;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_customer);

        requestQue = Volley.newRequestQueue(getApplicationContext());

        lin = findViewById(R.id.lin);

        username = findViewById(R.id.regUserName);
        password = findViewById(R.id.regUserPassword);
        email = findViewById(R.id.regUserEmail);
        natID = findViewById(R.id.regUserNatID);
        inf = findViewById(R.id.lin);

        thumbCus = findViewById(R.id.customerThumb);

        signUp = findViewById(R.id.signUpCus);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });
        pickImage = findViewById(R.id.regCusPickImage);
        pickImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                pickAnImage();
            }
        });

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        password.setTypeface(custom_font);
        lin.setTypeface(custom_font);
        username.setTypeface(custom_font);
        email.setTypeface(custom_font);
        natID.setTypeface(custom_font);
        pickImage.setTypeface(custom_font);

        inf.setTypeface(custom_font1);
        lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(signup_customer.this, signup_driver.class);
                startActivity(inte);
                finish();
            }
        });
    }

    void pickAnImage(){
        if(ActivityCompat.checkSelfPermission(signup_customer.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(signup_customer.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            Intent inte = new Intent();
            inte.setType("image/*");
            inte.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(inte, "Select Image"), 1);
        }
    }

    void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(!false)
                .setTitle("Confirm")
                .setMessage("Accept to register all specified details?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String holdEmail = email.getText().toString();
                String holdName = username.getText().toString();
                String holdPassword = password.getText().toString();
                String holdNatID = natID.getText().toString();
                String holdBae64 = base64Image;

                int regLength = 1;

                //Check if all is okay
                if (holdEmail.length() > regLength
                        && holdName.length() > regLength
                        && holdPassword.length() > regLength
                        && holdName.length() > regLength) {

                    RegModel mod = new RegModel();
                    mod.setEmail(holdEmail);
                    mod.setName(holdName);
                    mod.setPassword(holdPassword);
                    mod.setNatID(holdNatID);
                    mod.setBase64(holdBae64);

                    mainRegModel = mod;
                    Show("Registering. Please wait!");
                    sending(Divala.registerUserURl);
                } else {
                    Show("Sorry, some details are missing.");
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    public void sending(String url) {
        JSONObject params = new JSONObject();
        try {
            params.put("email", mainRegModel.getEmail());
            params.put("name", mainRegModel.getName());
            params.put("national_id_number",mainRegModel.getNatID());
            params.put("base_64", mainRegModel.getBase64());
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
                        if (recievedStatusCode == 201) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String holdToken = obj.getString("token");
                                String holdName = obj.getString("name");
                                String holdBase64 = obj.getString("base_64");

                                GlobalIdentity.userToken = holdToken;
                                GlobalIdentity.name = holdName;
                                GlobalIdentity.base_64 = holdBase64;

                                Show("Registration Successful");

                                Intent inte = new Intent();
                                inte.putExtra("details", String.format("%s,%s", mainRegModel.getEmail(), mainRegModel.getPassword()));
                                setResult(5, inte);
                                finish();

                            } catch (JSONException e) {

                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int respCode = error.networkResponse.statusCode;
                        if (respCode == 400) Show("Email already exists");
                        //Show("error: " + error.getMessage() + "\ncode" + error.networkResponse.statusCode);
                        //when 400, already exists
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

    //overloaded Show
    void Show(){
        LayoutInflater inflater = getLayoutInflater();
        View viewLay = inflater.inflate(R.layout.custom_toast, null);
        Toast toast = Toast.makeText(this, "Toast:Gravity.TOP", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setView(viewLay);
        toast.show();
    }

    void Show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();
            filePath = getPath(picUri);

            if (filePath != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                    base64Image = Base64Image.encode(filePath);
                    thumbCus.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        this,"You need to select an Image to proceed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
