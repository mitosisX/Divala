package com.taxi.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class activity_submit_profile extends AppCompatActivity implements NetworkInterface {
    EditText uploadDOB, uploadLocation, uploadNumber;
    TextView pickNatID, text, uploadTheProfile;

    ImageView thumbProfile;

    RequestQueue requestQue;
    RegModel mainRegModel;
    int recievedStatusCode = 0;

    String filePath;
    String base64Image;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_details);

        requestQue = Volley.newRequestQueue(getApplicationContext());

        uploadDOB = findViewById(R.id.uploadDOB);
        uploadLocation = findViewById(R.id.uploadLocation);
        uploadNumber = findViewById(R.id.uploadNumber);
        pickNatID = findViewById(R.id.uploadPickID);

        text = findViewById(R.id.submitText);

        thumbProfile = findViewById(R.id.profileThumbID);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        text.setTypeface(custom_font);

        pickNatID.setTypeface(custom_font1);
        pickNatID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAnImage();
            }
        });

        uploadTheProfile = findViewById(R.id.uploadDetailsNow);
        uploadTheProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });
    }

    //Ask for permission first, for android 5 and above
    void pickAnImage() {
        if (ActivityCompat.checkSelfPermission(activity_submit_profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity_submit_profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
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
                .setMessage("Accept to submit the specified profile?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String holdDOB = uploadDOB.getText().toString();
                String holdLocation = uploadLocation.getText().toString();
                String holdNumber = uploadNumber.getText().toString();
                String holdBase64 = base64Image;

                int regLength = 1;

                //Check if all is okay
                if (holdDOB.length() > regLength
                        && holdLocation.length() > regLength
                        && holdNumber.length() > regLength) {

                    //Create an object holding all register-details
                    RegModel mod = new RegModel();
                    mod.setUploadDOB(holdDOB);
                    mod.setUploadLocation(holdLocation);
                    mod.setUploadNumber(holdNumber);
                    mod.setBase64(holdBase64);

                    mainRegModel = mod;
                    Show("Uploading Details!");
                    sending(Divala.detailsURL);
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
            params.put("national_id_image", mainRegModel.getBase64());
            params.put("date_of_birth", mainRegModel.getUploadDOB());
            params.put("location", mainRegModel.getUploadLocation());
            params.put("phone_number", mainRegModel.getUploadNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        if (recievedStatusCode == 201) {
                            Show("Profile Updated Successfully!");
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int respCode = error.networkResponse.statusCode;
                        Show("Error: " + respCode);
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
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQue.add(stringRequest);
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
                    base64Image = Compress.Compressor(Base64Image.encode(filePath));
                    thumbProfile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(
                        this, "You need to select an Image to proceed.",
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
