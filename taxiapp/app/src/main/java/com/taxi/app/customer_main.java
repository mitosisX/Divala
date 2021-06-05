package com.taxi.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

public class customer_main extends AppCompatActivity implements NetworkInterface, NavigationView.OnNavigationItemSelectedListener {
    RequestQueue requestQue;
    TextView cusExitTxt, cusWelcome, cusWelcomeName, cusInfo, cusFromTxt, cusDestinationTxt, carsFoundTxt, drawerUser, cusCarsFound;
    ListView driversList;
    Spinner spinnerFrom, spinnerDestinations;
    ImageView openDrawer, userImg;

    DrawerLayout drawer;

    ArrayList<JsonModel> filteredModelsList;

    ArrayList<JsonModel> modelsList;
    ArrayAdapter<String> adapter;
    ArrayList<String> fromList; //From where the driver is coming from
    ArrayList<String> toList;   //keeps all routes for destinations

    int listPosition = 0;

    int modelPosition = 0; //When dataList has been clicked, store psoiton in ArrayList<JsonModel>

    //String[] routes = {"Mzuzu", "Lilongwe", "Karonga", "Mzimba", "Blantyre", "Thyolo", "Chitipa", "Dedza"};

    /*
                Mechanism

        - Query for routes first and let user search from the routes.
            * Below variable determines whether to ignore showing the list or not
     */
    boolean routesFirst = true;

    String calledURL = ""; //Know the URL called from an anonymous class

    int recievedStatusCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_cus);

        drawer = findViewById(R.id.drawer_lay_cus);

        NavigationView nav = findViewById(R.id.nav_view_cus);
        nav.setNavigationItemSelectedListener(this);
        //Obtain item from the navBar
        View header = nav.getHeaderView(0);

        byte[] bit = Base64Image.decode(GlobalIdentity.base_64);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bit, 0, bit.length);

        userImg = header.findViewById(R.id.userImg);
        userImg.setImageBitmap(decodedByte);

        drawerUser = header.findViewById(R.id.drawerUsername); //the name available when you open drawer

        //filteredModelsList = new ArrayList<>();

        //The lists holding the destination and starting point for journeys
        toList = new ArrayList<>();
        fromList = new ArrayList<>();

        requestQue = Volley.newRequestQueue(getApplicationContext());

        //Spinner, ImageView, ListView
        spinnerFrom = findViewById(R.id.cusFromList);
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filteredModelsList = filterJourneys();

                if(filteredModelsList.size() < 1) {
                    driversList.setAdapter(null);
                }else {
                    JsonToListView(filteredModelsList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerDestinations = findViewById(R.id.cusDestinationList); //All journeys
        spinnerDestinations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filteredModelsList = filterJourneys();

                if(filteredModelsList.size() < 1) {
                    driversList.setAdapter(null);
                }else {
                    JsonToListView(filteredModelsList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //spinnerDestinations.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,routes));

        openDrawer = findViewById(R.id.cus_menu_icon);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        driversList = findViewById(R.id.cusAvailableDrivers);

        //TextViews - intitialization
        cusWelcome = findViewById(R.id.cusWelcome);
        cusWelcomeName = findViewById(R.id.cusWelcomeName);
        cusExitTxt = findViewById(R.id.cusExitText);
        //cusInfo = findViewById(R.id.cusInformation);
        cusFromTxt = findViewById(R.id.cusFromText);
        cusDestinationTxt = findViewById(R.id.cusDestinationText);
        carsFoundTxt = findViewById(R.id.cusCarsFoundTxt);
        cusCarsFound = findViewById(R.id.cusCarsFound);

        cusWelcomeName.setText(GlobalIdentity.name);

        //Set Fonts - decorating them with fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
        cusWelcome.setTypeface(font);
        cusWelcomeName.setTypeface(font);
        cusExitTxt.setTypeface(font);
        drawerUser.setTypeface(font);
        cusFromTxt.setTypeface(font);
        cusDestinationTxt.setTypeface(font);
        carsFoundTxt.setTypeface(font);
        cusCarsFound.setTypeface(font);

        drawerUser.setText(GlobalIdentity.name); //set the drawer name

        driversList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                modelPosition = position;

                Object obj = driversList.getItemAtPosition(position);
                //JsonModel item = (JsonModel) obj;
                showPopupMenu(driversList);
                //Show(String.format("%s, %s and %s",item.getDriverID(),item.getCarModel(),item.getNumSeats()));
            }
        });

        //Show();
        ShowSnack("Loading available routes - please wait");
        calledURL = Divala.journeysURL;
        sending(calledURL);
        showHELP();
    }

    void showHELP(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);
        sequence.addSequenceItem(openDrawer, "Use this button for more options.","GOT IT!");
        sequence.addSequenceItem(spinnerFrom, "This let's you select current location.","GOT IT!");
        sequence.addSequenceItem(spinnerDestinations, "This let's you select your destination.","GOT IT!");
        sequence.start();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_cus_refresh) {
            ShowSnack("Refreshing!");
            sending(Divala.journeysURL);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.menu_as_driver) {
            AlertDialog.Builder ask = new AlertDialog.Builder(this);
            ask.setTitle("Change account type?");
            ask.setMessage("Changing from Customer to Driver is irreversible. Sure to proceed?");
            ask.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            ask.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(getApplicationContext(), driver_add_car.class));
                    finish();
                }
            });
            ask.show();
        } else if (id == R.id.menu_cus_exit) {
            finish();
        }else if(id == R.id.menu_cus_about){
            startActivity(new Intent(getApplicationContext(), activity_about.class));
        }else if(id == R.id.menu_cus_upload_profile){
            startActivity(new Intent(getApplicationContext(), activity_submit_profile.class));
        }

        return true;
    }

    void showPopupMenu(ListView list) {
        JsonModel verify = filteredModelsList.get(modelPosition);

        if (verify.getNumSeats() > 0 || verify.getBookStatus()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Information");
            builder.setMessage(String.format("Select an Option for \"%s\"", verify.getCarModel()));

            /*if (verify.getBookStatus() && (2 == 3)) //fake bracket condition, just didn't want this line to execute
                builder.setNegativeButton("Unbook Driver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JsonModel tempModel = modelsList.get(modelPosition);

                        if (tempModel.getBookStatus()) {
                            int seats = tempModel.getNumSeats();

                            tempModel.setBookStatus(false);
                            tempModel.setNumSeats(seats + 1);
                            Show(String.format("%s unbooked successfully", tempModel.getCarModel()));

                            driversList.setAdapter(null);
                            JsonToListView(modelsList);

                        } else {
                            Show("Car was never booked");
                        }
                    }
                });
             */

            if (!verify.getBookStatus())
                builder.setPositiveButton("Book Driver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JsonModel tempModel = filteredModelsList.get(modelPosition);

                        Intent inte = new Intent(getApplicationContext(), customer_book_car.class);
                        inte.putExtra("details", String.format("%s,%s",tempModel.getCarModel(),tempModel.getJourneyURL()));
                        startActivity(inte);
                    }
                });

            builder.show();

        } else {
            Show(String.format("Sorry, the %s is already full.", verify.getCarModel()));
        }
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    void exitApp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Exit")
                .setMessage("Sure to close the Taxi App?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    void ShowSnack(String msg) {
        Snackbar snack = Snackbar.make(findViewById(R.id.customerLay), msg, Toast.LENGTH_SHORT);
        snack.show();
    }

    private ArrayList<JsonModel> filterJourneys() {
        ArrayList<JsonModel> tempJ = new ArrayList<>();

        String FROM = spinnerFrom.getSelectedItem().toString();
        String TO = spinnerDestinations.getSelectedItem().toString();

        try {
            for (JsonModel mod : modelsList) {
                if (mod.getJourneyLocation().equals(FROM) && mod.getJourneyDestination().equals(TO)) {
                    tempJ.add(mod);
                }
            }
        } catch (Exception e) {
            Show("Error: " + e.getMessage());
        }

        return tempJ;
    }

    //This inflates the required data to the custom adapter with the JsonModel objects from Server
    public void JsonToListView(ArrayList<JsonModel> processedJSON) {
        driversList.setAdapter(null);
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), R.layout.custom_list, processedJSON);
        driversList.setAdapter(adapter);
    }

    //Handles the data received from server and populates it in an ArrayLst<JsonModel>
    private ArrayList<JsonModel> processJson(String json) {
        try {
            //int carsFound = 0;

            ArrayList<JsonModel> jsonModelList = new ArrayList<>();

            //JSONObject jsonStartingObject = new JSONObject(json);
            JSONArray jsonStudentArray = new JSONArray(json);

            for (int i = 0; i < jsonStudentArray.length(); i++) {

                JSONObject jsonUnderArrayObject = jsonStudentArray.getJSONObject(i);

                String tempModel = jsonUnderArrayObject.getString("car_model");
                String tempFrom = jsonUnderArrayObject.getString("start");
                String tempDesti = jsonUnderArrayObject.getString("destination");
                String tempID = jsonUnderArrayObject.getString("driver");
                String tempJourneyURL = jsonUnderArrayObject.getString("url");
                int tempNumSeats = Integer.parseInt(jsonUnderArrayObject.getString("number_of_seats_available"));
                boolean tempBooked = jsonUnderArrayObject.getBoolean("is_booked");

                JsonModel jsonModel = new JsonModel();
                jsonModel.setCarModel(tempModel);

                jsonModel.setJourneyLocation(tempFrom);
                jsonModel.setJourneyDestination(tempDesti);

                jsonModel.setJourneyURL(tempJourneyURL);
                jsonModel.setDriverID(tempID);
                jsonModel.setNumSeats(tempNumSeats);
                jsonModel.setBookStatus(tempBooked);
                jsonModelList.add(jsonModel);

                //carsFound++;
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

    /*void updateCarsFound(int carsFound) {
        cusCarsFound.setText("" + carsFound);
    }*/

    @Override
    public void sending(String url) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {

                        String responseText = response.toString();

                        modelsList = processJson(responseText);

                        if(modelsList.size() >= 1) {

                            try {
                                processJourneys(responseText); //add the routes to their respected
                            } catch (JSONException e) {
                            }

                            filteredModelsList = filterJourneys();
                            JsonToListView(filteredModelsList);
                            routesFirst = false;
                        }else{
                            ShowSnack("No Taxi's available at the moment.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Show(error.getMessage());
                        Show("There is a network problem.");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", String.format("Token %s", GlobalIdentity.userToken));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQue.add(stringRequest);
    }

    void processJourneys(String json) throws JSONException {
        fromList.clear();
        toList.clear(); //let's start from afresh; clear all routes

        JSONArray jsonStudentArray = new JSONArray(json);

        for (int i = 0; i < jsonStudentArray.length(); i++) {

            JSONObject jsonUnderArrayObject = jsonStudentArray.getJSONObject(i);

            String tempFrom = jsonUnderArrayObject.getString("start");
            String tempDesti = jsonUnderArrayObject.getString("destination");

            if (!fromList.contains(tempFrom))
                fromList.add(tempFrom); //check if route isn't available
            if (!toList.contains(tempDesti)) toList.add(tempDesti);
        }

        //Set all destinations for user searching
        spinnerFrom.setAdapter(null);
        spinnerFrom.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fromList));

        spinnerDestinations.setAdapter(null);
        spinnerDestinations.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, toList));
    }

    void Show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    void Show() {
        LayoutInflater inflater = getLayoutInflater();
        View viewLay = inflater.inflate(R.layout.custom_toast, null);
        Toast toast = Toast.makeText(this, "Toast:Gravity.TOP", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(viewLay);
        toast.show();
    }
}
