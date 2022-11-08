package edu.cmu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //a list of transportation modes to
    String[] modes = {"driving","cycling","e-bike","walking"};

    //This will be the main UI thread
    MainActivity current = this;

    //Create GSON object for marshalling
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main);

        //this is for the callback for the submit button. After calculating the carbon footprint,
        //the click listener will call "this" object and call the responseReady method
        final MainActivity onClick = this;

        //Assign transportation modes to spinner.
        //Adapted from: https://www.geeksforgeeks.org/spinner-in-android-using-java-with-example/
        Spinner mode_drop_down = findViewById(R.id.spinner_modes);
        ArrayAdapter ad = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item,modes);
        mode_drop_down.setAdapter(ad);
        mode_drop_down.setOnItemSelectedListener(this);

        //Find the "submit" button, and add a listener to it
        Button submitButton = (Button)findViewById(R.id.calculate);

        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                //Extract the parameters from the user input
                RequestMessage params = getUserParams();

                //Call the calculate method from the CalculateFootPrint method to start another thread and retrieve the response
                CalculateFootprint cf = new CalculateFootprint();
                cf.calculate(params,current, onClick); // Done asynchronously in another thread.  It calls cf.calculate() in this thread when complete.
            }
        });
    }

    private RequestMessage getUserParams()
    {
        //Instantiate an object to hold the parameters from the user
        RequestMessage jsonParams = null;
        try {
            String start = ((EditText) findViewById(R.id.startTrip)).getText().toString();
            String end = ((EditText) findViewById(R.id.endTrip)).getText().toString();

            //Throw exception if start and end locations are not inputted
            if((start.isEmpty() || start.equals("Start")) || end.isEmpty() || end.equals("End"))
            {
                throw new RequiredParamException("missing start");
            }

            //Extract the transport mode
            String transport_mode = ((Spinner) findViewById(R.id.spinner_modes)).getSelectedItem().toString();

            //Create the initial request with start, end and the transport mode
            jsonParams = new RequestMessage(start, end, transport_mode);

            //Loop through each EditText widget and get their values
            //Code adapted from https://stackoverflow.com/questions/38721741/android-loop-to-collect-all-edittext-values
            int[] drive_ids = new int[]{R.id.vehicle_make, R.id.vehicle_model, R.id.vehicle_year,
                    R.id.vehicle_engine_size, R.id.vehicle_fuel_type, R.id.vehicle_submodel,
                    R.id.vehicle_transmission_type, R.id.num_passengers};
            int index = 0;

            //Only when the user selects 'driving' we extract the other variables
            if (transport_mode.equals("driving")) {

                String year = ((EditText) findViewById(R.id.vehicle_year)).getText().toString();
                String make = ((EditText) findViewById(R.id.vehicle_make)).getText().toString();
                String model = ((EditText) findViewById(R.id.vehicle_model)).getText().toString();

                //If either make, model or year is inputted then all three should be there
                if
                (
                        (!year.isEmpty() & (make.isEmpty() ||model.isEmpty())) ||
                                (!make.isEmpty() & (year.isEmpty() ||model.isEmpty())) ||
                                (!model.isEmpty() & (make.isEmpty() ||year.isEmpty()))
                )
                {
                    //Throw exception if one is missing
                    throw new RequiredParamException("year,make,model");
                }

                //For all the driving text boxes, get the user inputs and store them as requests
                for (int id : drive_ids) {
                    EditText editText = (EditText) findViewById(id);
                    String value;

                    //add the driving parameters accordingly to the request
                    switch(index)
                    {
                        case 0:
                            value = editText.getText().toString();
                            jsonParams.vehicle_make = value;
                            break;
                        case 1:
                            value = editText.getText().toString();
                            jsonParams.vehicle_model = value;
                            break;
                        case 2:
                            value = editText.getText().toString();
                            jsonParams.vehicle_year = value;
                            break;
                        case 3:
                            value = editText.getText().toString();
                            jsonParams.vehicle_engine_size = value;
                            break;
                        case 4:
                            value = editText.getText().toString();
                            jsonParams.vehicle_fuel_type = value;
                            break;
                        case 5:
                            value = editText.getText().toString();
                            jsonParams.vehicle_submodel = value;
                            break;
                        case 6:
                            value = editText.getText().toString();
                            jsonParams.vehicle_transmission_type = value;
                            break;
                        case 7:
                            value = editText.getText().toString();
                            jsonParams.num_passengers = value;
                            break;
                    }
                }
            }
        }
        catch (RequiredParamException ex)
        {

            //Display message if missing start or end locations
            if (ex.getMessage().equals("missing start"))
            {
                //Displaying Toast with Hello Javatpoint message
                //Adapted from https://www.javatpoint.com/android-toast-example
                Toast.makeText(getApplicationContext(),"Required start or end location is missing",Toast.LENGTH_SHORT).show();
            }
            //Display message if missing year, make or model
            else if (ex.getMessage().equals("year,make,model"))
            {
                //Displaying Toast with Hello Javatpoint message
                //Adapted from https://www.javatpoint.com/android-toast-example
                Toast.makeText(getApplicationContext(),"Year, Make and Model are required inputs",Toast.LENGTH_SHORT).show();
            }
        }

        return jsonParams;
    }

    /*
     * This is called by the CalculateFootprint object when the response is ready.
     */
    public void responseReady(String response)
    {
        //Make table layout for response available
        TableLayout tb = findViewById(R.id.resp_table);

        //code adapted from user Debanjan at https://stackoverflow.com/questions/50596820/android-constraint-layout-set-constraint-top-programmatically
        tb.setVisibility(View.VISIBLE);

        ReceiveMessage apiRecieve = gson.fromJson(response, ReceiveMessage.class);

        //Get the textviews to display response to user
        TextView distance = (TextView) findViewById(R.id.distance_response);
        TextView total_carbon_footprint_grams = (TextView) findViewById(R.id.carbon_grams_response);
        TextView carbon_footprint_permile_grams = (TextView) findViewById(R.id.carbon_grams_permile_response);
        TextView total_carbon_footprint_tons = (TextView) findViewById(R.id.carbon_tons_response);
        TextView carbon_footprint_permile_tons = (TextView) findViewById(R.id.carbon_tons_permile_response);

        //Set textviews to data returned from web service
        distance.setText(apiRecieve.distance);
        total_carbon_footprint_grams.setText(apiRecieve.total_carbon_footprint_grams);
        carbon_footprint_permile_grams.setText(apiRecieve.carbon_footprint_permile_grams);
        total_carbon_footprint_tons.setText(apiRecieve.total_carbon_footprint_tons);
        carbon_footprint_permile_tons.setText(apiRecieve.carbon_footprint_permile_tons);

    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View v, int position, long id)
    {
        //Get the widgets for every edit text for driving
        EditText vehicle_make = findViewById(R.id.vehicle_make);
        EditText vehicle_model = findViewById(R.id.vehicle_model);
        EditText vehicle_year = findViewById(R.id.vehicle_year);
        EditText vehicle_engine_size = findViewById(R.id.vehicle_engine_size);
        EditText vehicle_fuel_type = findViewById(R.id.vehicle_fuel_type);
        EditText vehicle_submodel = findViewById(R.id.vehicle_submodel);
        EditText vehicle_transmission_type = findViewById(R.id.vehicle_transmission_type);
        EditText num_passengers  = findViewById(R.id.num_passengers);

       //Set visibility of widgets depending on what is selected
        switch(position)
        {
            //If driving is selected, set all the driving edit text widgets visible
            case 0:
                //code adapted from user Debanjan at https://stackoverflow.com/questions/50596820/android-constraint-layout-set-constraint-top-programmatically
                vehicle_make.setVisibility(View.VISIBLE);
                vehicle_model.setVisibility(View.VISIBLE);
                vehicle_year.setVisibility(View.VISIBLE);
                vehicle_engine_size.setVisibility(View.VISIBLE);
                vehicle_fuel_type.setVisibility(View.VISIBLE);
                vehicle_submodel.setVisibility(View.VISIBLE);
                vehicle_transmission_type.setVisibility(View.VISIBLE);
                num_passengers.setVisibility(View.VISIBLE);
                break;
            //Otherwise, keep them invsisble
            default:
                //code adapted from user Debanjan at https://stackoverflow.com/questions/50596820/android-constraint-layout-set-constraint-top-programmatically
                vehicle_make.setVisibility(View.GONE);
                vehicle_model.setVisibility(View.GONE);
                vehicle_year.setVisibility(View.GONE);
                vehicle_engine_size.setVisibility(View.GONE);
                vehicle_fuel_type.setVisibility(View.GONE);
                vehicle_submodel.setVisibility(View.GONE);
                vehicle_transmission_type.setVisibility(View.GONE);
                num_passengers.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}