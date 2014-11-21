package com.ncl.team5.lloydsmockup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class Accounts extends Activity {

    String[] items;
    ArrayList<String> accountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        ListView accountsList=(ListView)findViewById(R.id.listView);


        accountList = new ArrayList<String>();
        getAccounts();
        // Create The Adapter with passing ArrayList as 3rd parameter
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, accountList);
        // Set The Adapter
        accountsList.setAdapter(arrayAdapter);

        accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(Accounts.this, Statement.class);
                String message = accountList.get(position);
                intent.putExtra("com.example.ListViewTest.MESSAGE", message);
                startActivity(intent);
            }
        });
    }



    void getAccounts()
    {
        accountList.add("Account 1 : £100.00");
        accountList.add("Account 2 : £607.76");
        accountList.add("Account 3 : £5098.49");
        accountList.add("Account 4 : £0.01");
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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
