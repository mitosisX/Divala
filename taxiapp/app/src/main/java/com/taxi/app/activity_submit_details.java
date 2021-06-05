package com.taxi.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class activity_submit_details extends AppCompatActivity implements NetworkInterface {
    RequestQueue requestQue;
    TextView submitTheDetails, uploadID, detailsTxt;
    EditText uploadDOB, uploadLocation, uploadNumber;
    ImageView thumbID;

    RegModel mainRegModel;
    String filePath;
    String base64Image;
    Bitmap bitmap;
    private int recievedStatusCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_details);

        mainRegModel = new RegModel();

        requestQue = Volley.newRequestQueue(getApplicationContext());

        thumbID = findViewById(R.id.profileThumbID);

        uploadDOB = findViewById(R.id.uploadDOB);
        uploadLocation = findViewById(R.id.uploadLocation);
        uploadNumber = findViewById(R.id.uploadNumber);

        detailsTxt = findViewById(R.id.submitText);

        //Set Fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        uploadDOB.setTypeface(font);
        uploadLocation.setTypeface(font);
        uploadNumber.setTypeface(font);
        detailsTxt.setTypeface(font);

        uploadID = findViewById(R.id.uploadPickID);
        uploadID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAnImage();
            }
        });

        submitTheDetails = findViewById(R.id.uploadDetailsNow);
        submitTheDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainRegModel.setUploadDOB(uploadDOB.getText().toString());
                mainRegModel.setUploadLocation(uploadLocation.getText().toString());
                mainRegModel.setUploadNumber(uploadNumber.getText().toString());

                Show("Uploading details.");
                sending(Divala.detailsURL);
            }
        });
    }

    void Show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    void ShowSnack(String msg) {
        Snackbar snack = Snackbar.make(findViewById(R.id.relLayout), msg, Toast.LENGTH_SHORT);
        snack.show();
    }

    void pickAnImage() {
        if (ActivityCompat.checkSelfPermission(activity_submit_details.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity_submit_details.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent inte = new Intent();
            inte.setType("image/*");
            inte.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(inte, "Select Image"), 1);
        }
    }

    @Override
    public void sending(String url) {
        String holdDOB = mainRegModel.getUploadDOB();
        String holdLocation = mainRegModel.getUploadLocation();
        String holdNumber = mainRegModel.getUploadNumber();
        String holdBase64 = base64Image;

        JSONObject obj = new JSONObject();
        try {
            obj.put("national_id_image", holdBase64);
            obj.put("date_of_birth", holdDOB);
            obj.put("location", holdLocation);
            obj.put("phone_number", holdNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (recievedStatusCode == 201) {
                            Show("Details Uploaded!");
                        } else {
                            Show("Received code: " + recievedStatusCode);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int respCode = error.networkResponse.statusCode;
                        if (respCode == 401) {
                            Show("Uploading Failed!");
                        } else {
                            Show(error.getMessage());
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
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQue.add(stringRequest);
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
                    thumbID.setImageBitmap(bitmap);
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
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
