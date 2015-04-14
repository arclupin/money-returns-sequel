package com.ncl.team5.lloydsmockup;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import HTTPConnect.Connection;

//References: http://code.tutsplus.com/tutorials/android-sdk-working-with-google-maps-displaying-places-of-interest--mobile-16145
//https://developers.google.com/places/documentation/display_search
public class Locations extends Activity implements LocationListener  {
    private String username;
    GoogleMap gMap;
    private LocationManager lManager;
    private Marker userMarkerLoc;
    UiSettings uiSet;
    boolean disabled =false;
    boolean enabled=false;
    //here will be stored all the places near by
    private Marker[] bankMarkers;
    //maximum number of places returned by google
    private final int MAX = 20;
   private MarkerOptions[] places;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        super.onCreate(savedInstanceState);

        username = i.getStringExtra(IntentConstants.USERNAME);
        date = i.getStringExtra(IntentConstants.DATE);

        lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        enabled =lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(enabled==false){
            AlertDialog.Builder errorBox = new AlertDialog.Builder(this);
            errorBox.setMessage("You must turn on the location services on your phone to use this feature")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            autoLogout();
                        }
                    });
            AlertDialog alert = errorBox.create();
            alert.show();

        }else if(enabled==true){
            setContentView(R.layout.activity_locations);
            if (gMap == null) {

                gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                if (gMap != null) {
                    //sets the type of the map
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    //creates the array to store the banks and atms
                    bankMarkers = new Marker[MAX];
                    // Enables MyLocation
                    gMap.setMyLocationEnabled(true);
                    uiSet = gMap.getUiSettings();
                    //To disable the toolbar when I click on a marker
                    uiSet.setMapToolbarEnabled(disabled);
                    Location lastLoc = lManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    updateLocation(lastLoc);
                }

            }
        }

    }

    //location listener methods
    @Override
    public void onLocationChanged(Location location) {
        Log.v("Location Activity", "location changed");
        updateLocation(location);
    }
    @Override
    public void onProviderDisabled(String provider){
        Log.v("Location Activity", "provider disabled");
    }
    @Override
    public void onProviderEnabled(String provider) {
        Log.v("Location Activity", "provider enabled");
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v("Location Activity", "status changed");
    }

    private void updateLocation(Location location){
      /*  lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
      Location lastLoc = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);*/
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        LatLng lastLatLng = new LatLng(lat, lng);
        //removes old marker every time the users change their location
        if(userMarkerLoc!=null){
            userMarkerLoc.remove();        }

        //create and set marker properties for the users location
        userMarkerLoc = gMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here"));
        gMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng),5000,null);


        String types = "atm|bank";
        String name = "Lloyds";
        try {
            types = URLEncoder.encode(types, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+lat+","+lng+
                "&radius=4000&sensor=true" +
                "&types=" + types +
                "&name="+ name +
                "&key=AIzaSyCAXBj3NALN6N1axPkGdHbK7m9_ENYdO9I";

        //execute the query
        new GetPlaces().execute(placesSearchStr);
        lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,30000,250,this);
    }



//Inner class to parse and execute the query. All the fun happens here(not really)
    private class GetPlaces extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... placesURL) {

            //build result as string
            StringBuilder pBuilder = new StringBuilder();

            for (String placeSearchURL : placesURL) {
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    //HTTP Get receives the URL string
                    HttpGet placesGet = new HttpGet(placeSearchURL);
                    //execute GET with Client - return response
                    HttpResponse pResponse = placesClient.execute(placesGet);
                    //check response status only carry on if response is OK at the end
                    StatusLine status = pResponse.getStatusLine();
                    if (status.getStatusCode() == 200) {
                        //get response entity
                        HttpEntity placesEntity = pResponse.getEntity();
                        //get input stream setup
                        InputStream locContent = placesEntity.getContent();
                        //create reader
                        InputStreamReader input = new InputStreamReader(locContent);
                        //use buffered reader to process
                        BufferedReader placesReader = new BufferedReader(input);
                        //read a line at a time, append to string builder
                        String line;
                        while ((line = placesReader.readLine()) != null) {
                            pBuilder.append(line + "\n");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return pBuilder.toString();
        }

        //process data retrieved from doInBackground
        protected void onPostExecute(String result) {
            //remove existing markers
            if(bankMarkers !=null){
                for(int pm=0; pm< bankMarkers.length; pm++){
                    if(bankMarkers[pm]!=null)
                        bankMarkers[pm].remove();
                }
            }
            try {
                //creates JSONObject
                JSONObject resultObject = new JSONObject(result);
                //get "results" array
                JSONArray placesArray = resultObject.getJSONArray("results");
                //create the marker options for each place returned
                places = new MarkerOptions[placesArray.length()];
                //loop through the places get the details of each place
                for (int p=0; p<placesArray.length(); p++) {
                    //if any values are missing we won't show the marker
                    boolean missingValue = false;
                    LatLng placeLL = null;
                    String placeName = "";
                    String vicinity = "";

                    try {
                        missingValue = false;
                        //get place at this index and all the relevant details
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        JSONObject loc = placeObject.getJSONObject("geometry")
                                .getJSONObject("location");
                        placeLL = new LatLng(Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        vicinity = placeObject.getString("vicinity");
                        placeName = placeObject.getString("name");

                    } catch (JSONException jse) {
                        Log.v("PLACES", "missing value");
                        missingValue = true;
                        jse.printStackTrace();
                    }
                    //if values missing we don't display
                    if (missingValue) {
                        places[p] = null;
                    } else {
                        places[p] = new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
                                .snippet(vicinity);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(places!=null && bankMarkers !=null){
                for(int p=0; p<places.length && p< bankMarkers.length; p++){
                    if(places[p]!=null)
                        bankMarkers[p]=gMap.addMarker(places[p]);
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Show notification icon in menu bar */
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.getItem(1);
        GetNotification notif = new GetNotification();


        if(notif.getNotifications(this, username, date)) {
            Log.d("Notif Change", "IN HERE");
            item.setIcon(R.drawable.ic_action_notify);
        }
        else
        {
            Log.d("Notif Change", "IN There");
            item.setIcon(R.drawable.globe);
        }

        return true;
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();


            if (id == R.id.action_backHome) {
                ((KillApp) this.getApplication()).setStatus(false);
                this.finish();
            } else if (id == R.id.action_notifications) {
                Intent intent = new Intent(this, Notifications.class);
                intent.putExtra(IntentConstants.USERNAME, username);
                intent.putExtra(IntentConstants.DATE, date);
                startActivity(intent);
                ((KillApp) this.getApplication()).setStatus(false);
            }
	    return super.onOptionsItemSelected(item);
	}

    /* This is how the application knows if it has been stopped by an intent or by an
     * external source (i.e. home button, phone call etc). Each time an intent is called, it
     * sets an application global variable denoted as KillApp to false. This means that when a new
     * activity is opened, it does not want to restart the application. However if no intent is
     * fired (i.e. phonecall, home button pressed) KillApp will have the value true so it will
     * restart back to the login activity.
     */

        /* This is where the test is done to see whether the KillApp variable is true, and if it is, to call
         * the login class. It also clears the activity stack so the back button cannot be used to go back */
        @Override
        protected void onResume() {
            getActionBar().setBackgroundDrawable(new ColorDrawable(MainActivity.getColour(this)));
            if (((KillApp) this.getApplication()).getStatus()) {
                //only finish is needed for all other apps apart from the main screen
                //as the login screen only needs to be called once, and by calling finish
                //it creates a domino affect to all of the other activities
                finish();
            } else {
                //each time the app resumes and it wasnt killed, the variable needs to be reset
                ((KillApp) this.getApplication()).setStatus(true);
            }

            super.onResume();


        }

        //overriding the on back pressed method (for the built in back button) so
        //its status can be set to false, so it doesnt launch the login on on resume
        @Override
        public void onBackPressed() {
            ((KillApp) this.getApplication()).setStatus(false);
            finish();

        }


    private void autoLogout()
    {
        Connection hc = new Connection(this);
        try
        {
            hc.execute("TYPE","LOGOUT", IntentConstants.USERNAME, username);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        ((KillApp) this.getApplication()).setStatus(false);
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }



}
