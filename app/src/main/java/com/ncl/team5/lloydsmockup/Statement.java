package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Statement extends Activity {

    ArrayList<String> statementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);
        Intent i = getIntent();

        String result = i.getStringExtra("com.example.ListViewTest.MESSAGE");

        TextView t = (TextView) findViewById(R.id.txtChange);

        t.setText(result);


        ListView accountsList=(ListView)findViewById(R.id.listView);


        statementList = new ArrayList<String>();
        getStatement();
        // Create The Adapter with passing ArrayList as 3rd parameter
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, statementList);
        // Set The Adapter
        accountsList.setAdapter(arrayAdapter);

    }

    void getStatement()
    {
        statementList.add("StarBucks : -£3.67");
        statementList.add("Rent : -£350.00");
        statementList.add("Pay : +£60.00");
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
