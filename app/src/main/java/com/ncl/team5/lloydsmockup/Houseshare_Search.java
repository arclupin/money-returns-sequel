package com.ncl.team5.lloydsmockup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import HTTPConnect.Connection;
import HTTPConnect.House_Search_Result;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;


public class Houseshare_Search extends Activity {
    private String username;
    private boolean isRoomMade = false;

    private ScrollView result_scroll_container;
    private TableLayout result_layout;
    private View container;
    private TextView button_create;
    private EditText input_edittext;
    private String input;

    private final float DISTANCE_INPUT_SCROLL = 15;
    private final long ANIMATION_DURATION = 500;

    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare_search);
        Intent intent = getIntent();

        ActionBar a = getActionBar();
        if (a != null) a.hide();

        username = intent.getExtras().getString("ACCOUNT_USERNAME");

        button_create = (TextView) findViewById(R.id.HS_Search_CreateHouse);
        result_layout = (TableLayout) findViewById(R.id.table_result);
        result_scroll_container = (ScrollView) findViewById(R.id.house_search_scroll_container);
        input_edittext = (EditText) findViewById(R.id.HS_Search_TextInput);

        input_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input = input_edittext.getText().toString();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_houseshare__search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createHouse(View v) {
        Intent i = new Intent(this, Houseshare_Create_House.class);
        i.putExtra("ACCOUNT_USERNAME", username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void button_hs_search(View v) {

//        // testing input
//        List<String> result = new ArrayList<String>();
//        result.add("The Incredible");
//        result.add("102 Witch Road, Mars");
//        result.add("We are the incredible!");
//
//        List<String> result2 = new ArrayList<String>();
//        result2.add("Seireitei");
//        result2.add("Unit 1, Soul Society");
//        result2.add("We are soul reapers!");

        if (!StringUtils.isFieldEmpty(input)) {
//            makeHouseResultRow(result);
//            makeHouseResultRow(result2);
           Connection c = new Connection(this);
           c.setMode(Connection.MODE.SMALL_TASK);
           JSONObject j =  c.connect_js(username, Request_Params.PARAM_TYPE,
                   Request_Params.VAL_HS_SEARCH_HOUSE,
                   Request_Params.PARAM_USR, username,
                   Request_Params.HS_SEARCH_HOUSE_KEY,
                   input);

            if (j != null) {
                List<House_Search_Result> result_objects =new ArrayList<House_Search_Result>();
                try {
                    JSONArray result_arr_out = j.getJSONArray(Responses_Format.RESPONSE_HS_CONTENT);
                    //                Log.d("Search json arr", result_arr.toString());
                    for (int i = 0; i < result_arr_out.length(); i ++) {
                        JSONArray result_arr_in = result_arr_out.getJSONArray(i);
                        result_objects.add( new House_Search_Result(result_arr_in.getString(0), result_arr_in.getString(1), result_arr_in.getString(2)));
                    }
               }
               catch (JSONException e) {
                   new CustomMessageBox(this, "There was an error in the server response");
                   Log.e("JSON parsing exception", e.getMessage() + "End of JSON parsing exception msg");
               }

               if (result_objects.size() > 0)
               for (int i = 0; i < result_objects.size(); i++) {
                   makeHouseResultRow(result_objects.get(i).getInfo());
               }
               else  Toast.makeText(this, "Sorry. Your key word does not match any of our records.", Toast.LENGTH_SHORT).show();
           }
            else {
                Toast.makeText(this, "Sorry. Could not process your request now.", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Please supply the name of the house", Toast.LENGTH_SHORT).show();

    }


    // display a search result
    public void makeHouseResultRow(List<String> house_result) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final TableRow newRow = (TableRow) inflater.inflate(R.layout.hs_search_result_row, null);
        LinearLayout l = (LinearLayout) newRow.findViewById(R.id.house_result_container);
       for (int i = 0; i < house_result.size(); i++) {
           TextView text_view = (TextView) l.getChildAt(i);
           text_view.setText(house_result.get(i));
       }

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.hs_welcome_start_fade);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setStartTime(System.currentTimeMillis());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                newRow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        newRow.setVisibility(View.INVISIBLE);
        result_layout.addView(newRow);
        newRow.startAnimation(animation);


    }

//    // reposition the search result scroll view to be under the search input after its animation
//    private void initialiseSearchResultScrollView() {
////        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        params.addRule(RelativeLayout.BELOW, R.id.HS_Search_Input_Container);
////        result_scroll_container.setLayoutParams(params);
//        result_scroll_container.setY(container.getY() + container.getHeight() - findViewById(R.id.HS_Welcome_Container).getHeight() * 0.35f  + DISTANCE_INPUT_SCROLL );
//        result_scroll_container.setSmoothScrollingEnabled(true);
//
//    }
}
