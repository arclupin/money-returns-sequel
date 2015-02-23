package com.ncl.team5.lloydsmockup;
import android.content.Context;
import android.content.Intent;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Locations extends FragmentActivity {
    GoogleMap gMap;
    UiSettings uiSet;
    boolean disabled =false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        // Enables MyLocation
        gMap.setMyLocationEnabled(true);
        uiSet=gMap.getUiSettings();
        /*uiSet.setZoomControlsEnabled(true);*/
        //To disable the toolbar when I click on a marker
        uiSet.setMapToolbarEnabled(disabled);
        //get Your Current Location
        getLocCurrent();

    }

   public void getLocCurrent(){
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = lManager.getBestProvider(c, true);
       LocationListener lListener = new LocationListener() {
           @Override
           public void onLocationChanged(Location changedLocation) {
               showMapCurrent(changedLocation);
           }

           @Override
           public void onStatusChanged(String s, int i, Bundle bundle) {

           }

           @Override
           public void onProviderEnabled(String s) {

           }

           @Override
           public void onProviderDisabled(String s) {

           }
       };

       lManager.requestLocationUpdates(provider, 500, 0, lListener);

       // Get initial Location
       Location initLocation = lManager.getLastKnownLocation(provider);
       // Shows the initial location
       if(initLocation != null)
       {
           showMapCurrent(initLocation);
       }

    }

    private void showMapCurrent(Location location){
        gMap.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());

        gMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .title("YOU"));

        // Zoom in
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        } else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
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



}
