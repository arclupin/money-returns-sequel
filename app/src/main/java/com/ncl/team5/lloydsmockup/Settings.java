package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class Settings extends Activity implements AdapterView.OnItemSelectedListener{
    private TextView changeFontSize;
    private static final String[] fontSizes={"Size 12", "Size 14", "Size 16"};
    private static final String[] appColours={"Blue", "Red","Yellow"};
    private static final String[] timeInter={"10 Minutes","15 Minutes", "30 Minutes"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changeFontSize=(TextView)findViewById(R.id.changeFontSize);

        Spinner spin=(Spinner)findViewById(R.id.spinnerFont);
        Spinner spinCol=(Spinner)findViewById(R.id.spinnerColour);
        Spinner spinTime=(Spinner)findViewById(R.id.spinnerTime);

        spin.setOnItemSelectedListener(this);
        ArrayAdapter<String> cFontSize=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                fontSizes);
        ArrayAdapter<String> colour= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,appColours);
        ArrayAdapter<String> timer=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,timeInter);
        cFontSize.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        colour.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        timer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCol.setAdapter(colour);
        spin.setAdapter(cFontSize);
        spinTime.setAdapter(timer);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent,
                               View v, int position, long id) {
        //selection.setText(items[position]);
        if(fontSizes[position].compareTo("Size 12")==0){
            changeFontSize.setTextSize(12);
        }else if(fontSizes[position].compareTo("Size 14")==0){
            changeFontSize.setTextSize(14);
        }else{
            changeFontSize.setTextSize(16);
        }


    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        changeFontSize.setText("");
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
            this.finish();
            //Intent intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }
        else if (id == R.id.action_notifications) {
            Intent intent = new Intent(this, Notifications.class);
            startActivity(intent);

        }
        else if (id == R.id.action_location) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
