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
    MainActivity ma = null;   // for callback
    String params = null;       // RequestMessage object
    String response = null;

    // search( )
    // Parameters:
    // String searchTerm: the thing to search for on flickr
    // Activity activity: the UI thread activity
    // InterestingPicture ip: the callback method's class; here, it will be ip.pictureReady( )
    public void calculate(RequestMessage params, Activity activity, MainActivity ma) {
        this.ma = ma;
        Gson gson = new Gson();
        this.params = gson.toJson(params);
        new BackgroundTask(activity).execute();
    }



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
            System.out.println("doInBackground");
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
//            System.out.println("BackgroundTask.search");
            String endpoint = "http://10.0.2.2:8080//Project4Task2WebService-1.0-SNAPSHOT/calculateCarbonFootprint";
//            endpoint = endpoint + "?params=" + params;
            String s = "";
            try
            {
                URL url = new URL(endpoint);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");

                //Code adapted from Lab8: https://github.com/CMU-Heinz-95702/lab7-Android
                con.setRequestProperty("Content-Type", "text/plain");
                con.setRequestProperty("Accept", "text/plain");

                //Code adapted from https://www.baeldung.com/httpurlconnection-post
                con.setDoOutput(true);
                con.setDoInput(true);

                //Writing params adapted from user itsraja: https://stackoverflow.com/questions/36647210/servlet-reading-inputstream-for-a-post-value-gives-null
                PrintWriter pw = new PrintWriter(con.getOutputStream());
                pw.println(params);
                pw.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    s = inputLine;
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            return s;

        }

        /*
         * Given a url that will request XML, return a Document with that XML, else null
         */
        private Document getRemoteXML(String url) {
            System.out.println("getRemoteXML");
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource(url);
                return db.parse(is);
            } catch (Exception e) {
                System.out.print("Yikes, hit the error: "+e);
                return null;
            }
        }

        /*
         * Given a URL referring to an image, return a bitmap of that image
         */
        @RequiresApi(api = Build.VERSION_CODES.P)
        private Bitmap getRemoteImage(final URL url) {
            System.out.println("getRemoteImage");
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
