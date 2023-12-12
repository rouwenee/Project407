package com.cs407.journeydoodle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, StringDataCallback {
    private GoogleMap mMap;
    ArrayList markerPoints = new ArrayList();
    List<Polyline> polylines = new ArrayList();
    List<Marker> markers = new ArrayList();
    private String title = ""; // title to set when storing saved route in database and list

    private int routeId = -1; // default route, when no route is selected
    public static ArrayList<Route> routes = new ArrayList<>(); // list of saved routes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Journey Doodle");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // check if saved route is called
        Intent intent = getIntent();
        routeId = intent.getIntExtra("routeId", routeId);
        Log.i("INFO", "routeId: " + routeId);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: if (routeId != -1) update the route
                showDialog();
            }
        });
        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerPoints.clear();
                mMap.clear();
            }
        });

        Button undoButton = findViewById(R.id.undoButton);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (polylines.size() != 0) {
                    polylines.get(polylines.size()-1).remove();
                    markers.get(markers.size()-1).remove();

                    polylines.remove(polylines.size()-1);
                    markers.remove(markers.size()-1);
                    markerPoints.remove(markerPoints.size()-1);
                }
            }
        });

    }
    /*
     * Method that displays the dialog.
     */
    private void showDialog() {
        SaveDialog saveDialog = new SaveDialog();
        saveDialog.setStringDataCallback(this);
        saveDialog.show(getSupportFragmentManager(), "example dialog");
    }

    private void saveToDBAfterUserInput(String username, DBHelper d) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        String date = dateFormat.format(new Date());
        String content = "";
        // concatenates every marker point in the array list as a string with a comma in
        // between
        for(int i = 0; i < markerPoints.size(); i++) {
            if(i == markerPoints.size() - 1) {
                content = content + markerPoints.get(i).toString();
            } else {
                content = content + markerPoints.get(i).toString() + ", ";
            }
        }
        content.trim(); // removes whitespace from both ends of the string
        // stores username, name of route (user input), date, and array content (of marker
        // locations into database
        d.saveRoute(username, title, date, content);
    }
    /*
     * The callback method that assigns the title of the route
     */
    @Override
    public void onStringDataReceived(String data) {
        title = data;
        Log.i("INFO", "Received title of route: " + title);


        Context context = getApplicationContext();
        Installation installation = new Installation();
        String id = installation.id(context);
        Log.i("Info", "Printing user id from main: " + id);
        SQLiteDatabase sq = openOrCreateDatabase("routes", Context.MODE_PRIVATE, null);
        DBHelper db = new DBHelper(sq);
        saveToDBAfterUserInput(id, db);
        Log.i("INFO", "Printing title after saving route: " + title);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // TODO: if(routeId != -1) load the routes into the map
        if (routeId != -1) {
            // get selected route
            routes = savedRoutes.routes;
            Route route = routes.get(routeId);
            String routeContent = route.getContent();
            Log.i("INFO", "routeId: " + routeId + " content: " + routeContent);

            // convert String content to list of LatLng
            String[] splitedContent = routeContent.split(",");
            Double latitude = 43.075142;
            Double longitude = -89.403419;
            Pattern pattern = Pattern.compile("-?\\d+.\\d+"); // match double using regex
            Matcher matcher;
            LatLng latLng;
            for (int i = 2; i <= splitedContent.length; i += 2) {
                matcher = pattern.matcher(splitedContent[i-2]);
                if (matcher.find()) { // need to find before getting start/end index
                    latitude = Double.parseDouble(splitedContent[i - 2].substring(matcher.start(), matcher.end()));
                } else {
                    Log.i("INFO", "ERROR Latitude not found");
                }

                matcher = pattern.matcher(splitedContent[i-1]);
                if (matcher.find()) {
                    longitude = Double.parseDouble(splitedContent[i - 1].substring(matcher.start(), matcher.end()));
                } else {
                    Log.i("INFO", "ERROR Longitude not found");
                }

                Log.i("INFO", "#" + i / 2 + " lat: " + latitude + " long: " + longitude);

                latLng = new LatLng(latitude, longitude);
                if(i/2 == 1)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                markerPoints.add(latLng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                markers.add(mMap.addMarker(options));

                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(markerPoints.size() - 2);
                    LatLng dest = (LatLng) markerPoints.get(markerPoints.size() - 1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }

        } else {
            LatLng bascom = new LatLng(43.075142, -89.403419);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bascom, 16));
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.i("INFO", "Clicked LatLng: " + latLng.toString());
                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                markers.add(mMap.addMarker(options));

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(markerPoints.size() - 2);
                    LatLng dest = (LatLng) markerPoints.get(markerPoints.size() - 1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.routes) {
            Intent intent = new Intent(this, savedRoutes.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //System.out.println(result);
            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            //System.out.println(result.size());
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            polylines.add(mMap.addPolyline(lineOptions));
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyCCn5wexmw52ZPFvAnQJCvq7XXT_8WCR2I";


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}