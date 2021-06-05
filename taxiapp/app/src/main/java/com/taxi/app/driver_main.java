package com.taxi.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class driver_main extends AppCompatActivity implements NetworkInterface, NavigationView.OnNavigationItemSelectedListener {
    /*
    Upon successful registration, a url is received like, https://divala.herokuapp.com/users/24/
    but upon login, only an id is received.
        - This can be concatenated to the url /users/ to filter out journeys for current driver
     */
    RequestQueue requestQue;
    TextView welcome, yourAcc, yourName, countCars, countBook, yourCars, bookedTxt, booked, moreTxt, refreshTxt, drawerUser, logoutTxt;
    ListView myJourneysList;
    ImageView openDrawer, userImg;

    ArrayList<JsonModel> modelsList;

    int modelPosition = 0; //When dataList has been clicked, store position in ArrayList<JsonModel>
    String driver_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_driver);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_lay_cus);

        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar,R.string.drawer_open,R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
         */

        NavigationView nav = findViewById(R.id.nav_view_driver);
        nav.setNavigationItemSelectedListener(this);

        requestQue = Volley.newRequestQueue(getApplicationContext());//Obtain item from the navBar
        View header = nav.getHeaderView(0);

        byte[] bit = Base64Image.decode(GlobalIdentity.base_64);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bit, 0, bit.length);

        userImg = header.findViewById(R.id.userImg);
        userImg.setImageBitmap(decodedByte);

        myJourneysList = findViewById(R.id.customersList);



        //TextViews START
        welcome = findViewById(R.id.driverWelcome);
        yourAcc = findViewById(R.id.driverAcc);
        yourName = findViewById(R.id.driverWelcomeName);
        countCars = findViewById(R.id.driverTotalCars);
        yourCars = findViewById(R.id.driverCarsCount);
        booked = findViewById(R.id.driverBooked);
        bookedTxt = findViewById(R.id.driverBookedTxt);
        moreTxt = findViewById(R.id.driverMoreTxt);
        refreshTxt = findViewById(R.id.driverRefreshTxt);
        logoutTxt = findViewById(R.id.driverExitTxt);

        drawerUser = header.findViewById(R.id.drawerUsername); //the name available when you open drawer
        //END TextViews

        yourName.setText(GlobalIdentity.name);

        //Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");

        welcome.setTypeface(font);
        yourAcc.setTypeface(font);
        yourName.setTypeface(font);
        countCars.setTypeface(font);
        yourCars.setTypeface(font);
        booked.setTypeface(font);
        moreTxt.setTypeface(font);
        bookedTxt.setTypeface(font);
        refreshTxt.setTypeface(font);
        logoutTxt.setTypeface(font);
        drawerUser.setTypeface(font);

        drawerUser.setText(GlobalIdentity.name);

        openDrawer = findViewById(R.id.driver_menu_icon);
        openDrawer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawer.openDrawer(GravityCompat.START);
            }
        });

        /*more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(driver_main.this, more);
                popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent inte = new Intent(driver_main.this, driver_add_journey.class);
                        inte.putExtra("token", driver_token);
                        startActivity(inte);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
         */
        ShowSnack("Loading data");
        sending(Divala.myJourneys);
        showHELP();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.menu_driver_refresh){
            ShowSnack("Loading data");
            sending(Divala.myJourneys);
        }else if(id == R.id.menu_add_journey){
            startActivity(new Intent(getApplicationContext(), driver_add_journey.class));
        }else if(id == R.id.menu_driver_exit){
            finish();
        }else if(id == R.id.menu_driver_upload_profile){
            startActivity(new Intent(getApplicationContext(), activity_submit_details.class));
        }

        return true;
    }

    void showHELP(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);
        sequence.addSequenceItem(openDrawer, "Use this button for more options.","GO IT!");
        sequence.start();
    }

    void ShowSnack(String msg){
        Snackbar snack = Snackbar.make(findViewById(R.id.driverLay), msg, Toast.LENGTH_LONG);
        snack.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //This Activity was called from login Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == 5) {
            if (resultCode == Activity.RESULT_OK) {
                String[] details = data.getStringExtra("details").split(",");
            }
        }else if(requestCode == 6){
            driver_token = data.getStringExtra("token");
        }
         */
    }

    //This inflates the required data to the custom adapter with the JsonModel objects from Server
    public void JsonToListView(ArrayList<JsonModel> processedJSON) {
        //spinnerRoutes.setAdapter(null);
        //spinnerRoutes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, toList));

        DriverAdapter adapter = new DriverAdapter(getApplicationContext(), R.layout.driver_custom_list, processedJSON);
        myJourneysList.setAdapter(adapter);
    }

    int journeysFound = 0;
    //Handles the data received from server and populates it in an ArrayLst<JsonModel>
    private ArrayList<JsonModel> processJson(String json) throws JSONException {
        try {
            //int carsFound = 0;

            ArrayList<JsonModel> jsonModelList = new ArrayList<>();

            //JSONObject jsonStartingObject = new JSONObject(json);
            JSONArray jsonStudentArray = new JSONArray(json);

            for (int i = 0; i < jsonStudentArray.length(); i++) {

                JSONObject jsonUnderArrayObject = jsonStudentArray.getJSONObject(i);

                String tempJourney = jsonUnderArrayObject.getString("route");
                String tempID = jsonUnderArrayObject.getString("driver");
                int tempNumSeats = Integer.parseInt(jsonUnderArrayObject.getString("number_of_seats_available"));
                boolean tempBooked = jsonUnderArrayObject.getBoolean("is_booked");

                //Show(String.format("%s\n%s",tempID,GlobalIdentity.driverURL));

                JsonModel jsonModel = new JsonModel();
                jsonModel.setJourneyDestination(tempJourney);

                jsonModel.setDriverID(tempID);
                jsonModel.setNumSeats(tempNumSeats);
                jsonModelList.add(jsonModel);
                journeysFound++;
            }

            //Not used, the compiler was complaining about the method not throwing IOException error,
            //throws was used on the method but another error caught within the method was thrown
            {
                int tempo = 0;
                if (tempo == 2) throw new IOException();
            }

            return jsonModelList;

        } catch (JSONException | IOException e) {
            //Show(e.getMessage());
        }

        return null;
    }

    void Show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    int recievedStatusCode = 0;
    @Override
    public void sending(String url) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        String resp = response.toString();

                        try {
                            modelsList = processJson(resp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonToListView(modelsList);
                        yourCars.setText(journeysFound + "");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Show("An error occured");
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
}
