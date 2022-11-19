/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: Nov 18, 2022
 *
 * This program makes a GET request to the web service to
 * get the response from the API
 */

package edu.cmu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CalculateFootprint {
    MainActivity ma = null;     // for callback
    String params = null;       // params to send to web service
    String response = null;     //response from web service

    /*
     *  This method is called by the UI main thread. It will send the params for the API to calculate.
     *  The ma object is a reference back to the UI main thread. We do this because we do not want to
     *  have the UI be unresponsive while its getting a response from the web service.
     *
     * @param RequestMessage params, Activity activity, MainActivity ma
     * @return none
     */
    public void calculate(RequestMessage params, Activity activity, MainActivity ma) {
        this.ma = ma;

        //Convert the params to string to send to web service
        Gson gson = new Gson();
        this.params = gson.toJson(params);

        //Create new thread to execute response
        new BackgroundTask(activity).execute();
    }

    /*
    This class is invoked so that long-running operations don't freeze the UI,
    and run in the background
     */

    private class BackgroundTask {

        private Activity activity; // The UI thread

        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {

                    doInBackground();
                    // This is magic: activity should be set to MainActivity.this
                    //    then this method uses the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }

        private void execute(){
            // There could be more setup here, which is why
            //    startBackground is not called directly
            startBackground();
        }

        // doInBackground( ) implements whatever you need to do on
        //    the background thread.
        // Implement this method to suit your needs
        private void doInBackground() {
            response = calculate(params);

        }

        // onPostExecute( ) will run on the UI thread after the background
        //    thread completes.
        // Implement this method to suit your needs
        public void onPostExecute() {

//            System.out.println("onPostExecute");
            ma.responseReady(response);
        }

        /*
         * Make a GET request to the web service to get the response from the API
         */
        private String calculate(String params) {
            String endpoint = "https://warm-beach-45153.herokuapp.com/calculateCarbonFootprint";

            String s = "";
            try
            {
                //Make POST request to web service
                URL url = new URL(endpoint);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");

                //We set content negotiation so that we know what we will send and will accept
                //Code adapted from Lab8: https://github.com/CMU-Heinz-95702/lab7-Android
                con.setRequestProperty("Content-Type", "text/plain");
                con.setRequestProperty("Accept", "text/plain");

                //Writing params to API
                // adapted from user itsraja: https://stackoverflow.com/questions/36647210/servlet-reading-inputstream-for-a-post-value-gives-null
                PrintWriter pw = new PrintWriter(con.getOutputStream());
                pw.println(params);
                pw.flush();

                //Get response from web service
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    s = inputLine;
                }
            }
            // IO exception
            catch(IOException e)
            {
                s = "Unable to reach server";
            }

            return s;

        }

    }
}
