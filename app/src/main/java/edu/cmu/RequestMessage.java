/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: Nov 18, 2022
 *
 * This class declares the parameters associated with the request
 * message to be sent to the web server for carbon footprint calculation
 */

package edu.cmu;

import android.widget.EditText;

public class RequestMessage {
    String start;
    String end;
    String transport_mode;
    String vehicle_make;
    String vehicle_model;
    String vehicle_year;
    String vehicle_engine_size;
    String vehicle_fuel_type;
    String vehicle_submodel;
    String vehicle_transmission_type;
    String num_passengers;

    //constructor
    public RequestMessage(String start, String end, String transport_mode) {
        this.start = start;
        this.end = end;
        this.transport_mode = transport_mode;
    }
}
