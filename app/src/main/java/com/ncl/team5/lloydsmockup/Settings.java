package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Settings extends Activity {

   ArrayList<String> optionsList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ListView optionList=(ListView)findViewById(R.id.listOptions);
        optionsList=  new ArrayList<String>();
        getOptions();
        ArrayAdapter<String> arrayAdapterO =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, optionsList);
        optionList.setAdapter(arrayAdapterO);

        optionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (position==0) {
                    Intent intent = new Intent(Settings.this, Prefer.class);
                    String message = optionsList.get(position);
                    intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                    startActivity(intent);
                }else if (position==1) {
                    Intent intent = new Intent(Settings.this, ChangeAccountName.class);
                    String message = optionsList.get(position);
                    intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                    startActivity(intent);
                }else if(position==2){
                    Intent intent = new Intent(Settings.this, Analysis.class);
                    String message = optionsList.get(position);
                    intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                    startActivity(intent);
                }
            }
        });

    }

       void getOptions(){
           optionsList.add("Preferences");
           optionsList.add("Change Account Name");
           optionsList.add("Change Password");
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
