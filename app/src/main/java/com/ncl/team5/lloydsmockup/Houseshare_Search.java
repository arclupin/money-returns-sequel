package com.ncl.team5.lloydsmockup;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Fragment.HS_Cancel_Request_Dialog_Fragment;
import Fragment.HS_Join_Dialog_Fragment;
import HTTPConnect.Connection;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;

/**
 * For performace optimisation, the search wont be performed automatically as the text changes.
 * The search request will only be sent when the user press the search button
 */

public class Houseshare_Search extends FragmentActivity implements HS_Join_Dialog_Fragment.JoinDialogListener, HS_Cancel_Request_Dialog_Fragment.CancelRequestDialogListener {
    private String username;
    private boolean isRoomMade = false;

    private ScrollView result_scroll_container;
    private TableLayout result_layout;
    private View container;
    private TextView button_create;
    private EditText input_edittext;
    private String input;
    private SearchView search_view;


    private final float DISTANCE_INPUT_SCROLL = 15;
    private final long ANIMATION_DURATION = 500;

    private Map<String, Boolean> Houses_Status;
    private LayoutTransition transition;


    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare_search);
        Intent intent = getIntent();

//        ActionBar a = getActionBar();
//        if (a != null) a.hide();

        username = intent.getExtras().getString("ACCOUNT_USERNAME");
        Houses_Status = new HashMap<String, Boolean>();

        button_create = (TextView) findViewById(R.id.HS_Search_CreateHouse);

        result_layout = (TableLayout) findViewById(R.id.table_result);
        result_layout.setLayoutTransition(transition);
        result_scroll_container = (ScrollView) findViewById(R.id.house_search_scroll_container);
        setEmpty(true);
//        input_edittext = (EditText) findViewById(R.id.HS_Search_TextInput);
//
//        setEmpty(true);
//
//        input_edittext.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                input = input_edittext.getText().toString();
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                input = input_edittext.getText().toString();
//
//                if (StringUtils.isFieldEmpty(input))
//                     setEmpty(true);
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_houseshare__search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Search a house");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                input = query;
                search(Connection.MODE.LONG_TASK);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                input = newText;
                return true;
            }
        });
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

        search(Connection.MODE.LONG_TASK);

    }

    public void search(Connection.MODE task) {


        if (!StringUtils.isFieldEmpty(input)) {
//            makeHouseResultRow(result);
//            makeHouseResultRow(result2);
            Connection c = new Connection(this);
            c.setMode(task);
            JSONObject j =  c.connect_js(username, Request_Params.PARAM_TYPE,
                    Request_Params.VAL_HS_SEARCH_HOUSE,
                    Request_Params.PARAM_USR, username,
                    Request_Params.HS_SEARCH_HOUSE_KEY,
                    input);

            if (j != null) {
                List<ArrayList<String>> result_objects =new ArrayList<ArrayList<String>>();
                Houses_Status = new HashMap<String, Boolean>();
                try {
                    JSONArray result_arr_out = j.getJSONArray(Responses_Format.RESPONSE_HS_CONTENT);
                    //                Log.d("Search json arr", result_arr.toString());
                    for (int i = 0; i < result_arr_out.length(); i ++) {

                        JSONArray result_arr_in = result_arr_out.getJSONArray(i);
                        ArrayList<String> l = new ArrayList<String>();

                        Houses_Status.put(result_arr_in.getString(0), result_arr_in.getString(result_arr_in.length() - 1).equalsIgnoreCase("1"));
                        for (int t = 0; t < result_arr_in.length(); t++) {
                            l.add(result_arr_in.getString(t));
                        }
                        result_objects.add(l);
                    }
                }
                catch (JSONException e) {
                    new CustomMessageBox(this, "There was an error in the server response");
                    Log.e("JSON parsing exception", e.getMessage() + "End of JSON parsing exception msg", e);

                }
                if (result_objects.size() > 0) {
                    // This is for the option of setting the search to be performed on each text change
//                    for (int i = 0; i < result_objects.size(); i++) {
//                        if (!getHouseNameAtChild(i).equals(result_objects.get(i).get(0)))
//                        // if result row is not the same then clear it and add the new row otherwise keep it.
//                        {
//                            cleanSearchAtChild(i); // clean the child at this position
//                            makeHouseResultRow(result_objects.get(i)); // add view to this position
//                        }
//                    }
//                    clearTail(result_objects.size()); // clear the tail of the result table

                    result_layout.removeAllViews();
                    for (int i = 0; i < result_objects.size(); i++) {
                        makeHouseResultRow(result_objects.get(i)); // add view to this position
                    }

                }
                else {
                    setEmpty(false);
                    if (task == Connection.MODE.LONG_TASK)
                        Toast.makeText(this, "Sorry. Your key word does not match any of our records.", Toast.LENGTH_SHORT).show();

                }

            }
            else {
                if (task == Connection.MODE.LONG_TASK)
                    Toast.makeText(this, "Sorry. Could not process your request now.", Toast.LENGTH_SHORT).show();
            }
        }
        else
        if (task == Connection.MODE.LONG_TASK)
            Toast.makeText(this, "Please supply the name of the house", Toast.LENGTH_SHORT).show();
    }

    // display a search result
    public void makeHouseResultRow(List<String> house_result) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final TableRow newRow = (TableRow) inflater.inflate(R.layout.hs_search_result_row, null);
        LinearLayout l = (LinearLayout) newRow.findViewById(R.id.house_result_container);

        TextView text_view = (TextView) l.getChildAt(0);
        text_view.setText(house_result.get(0));
        text_view = (TextView) l.getChildAt(2);
        text_view.setText(!StringUtils.isFieldEmpty((house_result.get(house_result.size() - 2))) ? house_result.get(house_result.size() - 2) : "No description available" );
        text_view = (TextView) l.getChildAt(1);
        String address = "";
        for (int i = 1; i < house_result.size() - 2; i++) {
            address += house_result.get(i) + " ";
        }
        text_view.setText(address);

        // set the request sent button - 1 means already sent req, 0 otherwise
        if (house_result.get(house_result.size() - 1).equals("1")) {

            l.setClickable(false);
            TextView stv = (TextView) newRow.findViewById(R.id.HS_Search_Result_request_sent);
            stv.setClickable(true);
            stv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCancelRequestDialog(v);
                }
            });
            stv.setVisibility(View.VISIBLE);

        }

       else if (house_result.get(house_result.size() - 1).equals("0")) {
            l.setClickable(true);
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showJoinDialog(v);
                }
            });
            TextView stv = (TextView) newRow.findViewById(R.id.HS_Search_Result_request_sent);
            stv.setClickable(false);
            stv.setVisibility(View.INVISIBLE);

        }
        result_layout.addView(newRow);



    }


    // get house name of a result row
    private String getHouseNameAtChild(int i) {
        if (result_layout.getChildCount() >= (i+1)) {
            TableRow r = (TableRow) result_layout.getChildAt(i);
            LinearLayout l = (LinearLayout) r.getChildAt(0);
            TextView tv = (TextView) l.getChildAt(0);
            return tv.getText().toString();
        }
        return "";
    }

    // clean the child view
    public void cleanSearchAtChild(int child) {
        if (result_layout.getChildCount() > 0 && result_layout.getChildCount() > child) {

            //some animation for better user experience hopefully
//            Utils.Animation.fade_out(result_layout.getChildAt(child), this, Utils.Animation.SHORT, Utils.Animation.POST_EFFECT.PERMANENTLY);
            result_layout.removeViewAt(child);

        }

    }

    // clean the tail
    public void clearTail(int from) {
        // need to delete from tail otherwise it wont work as the childCount get decremented after each child removal
        for (int i =  result_layout.getChildCount() - 1; i >= from ; i--)
            cleanSearchAtChild(i);
        Log.d("Clear tail from", Integer.toString(from));
    }

    // initially empty (true) or empty result (false)
    public void setEmpty(boolean empty_type) {

        LayoutInflater l = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TableRow r = (TableRow) l.inflate(R.layout.hs_search_result_empty, null);
        LinearLayout ll = (LinearLayout) r.getChildAt(0);
        TextView tv = (TextView) ll.getChildAt(0);

        tv.setText(empty_type ? getString(R.string.houseshare_search_empty_ini) : getString(R.string.houseshare_search_empty_result));

        result_layout.removeAllViews();
        result_layout.addView(r);
    }

//    public void removeEmpty() {
//       if (result_layout.getChildCount() == 1 && findViewById(R.id.empty_row_msg) != null)
//           result_layout.removeAllViews();
//    }


    @Override
    public void onJoinButtonClick(String house_name, HS_Join_Dialog_Fragment f, int view_id) {
        //TODO send request to the server
        Connection c = new Connection(this);
        JSONObject j = c.connect_js(username,
                Request_Params.PARAM_TYPE, Request_Params.VAL_HS_JOIN_GROUP,
                Request_Params.HS_JOIN_GROUP_GRPNAME, house_name,
                Request_Params.PARAM_USR, username);
        try {
            if (j.getString(Responses_Format.RESPONSE_STATUS).equals("true")) {
                new CustomMessageBox(this, "Your request has been sent to the house admin. \nWe will let you know as soon as he/she makes a response.");
                TableRow r = (TableRow) result_layout.getChildAt(findIdOfHouseName(house_name));
                LinearLayout parent = (LinearLayout) r.getChildAt(0);
                parent.setClickable(false); // the house now should be clickable again
                TextView v = (TextView) parent.findViewById(R.id.HS_Search_Result_request_sent);
                v.setVisibility(View.VISIBLE);
                v.setClickable(true);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCancelRequestDialog(v);
                    }
                });
            }
                    else
                new CustomMessageBox(this, "We are sorry that we could not process your request at the moment. \n If you are experiencing this error constantly, please contact our team.");
        }
        catch (JSONException e)
        {
            Log.e("JSON error at join", e.getMessage() + "End", e);
        }
       }

    @Override
    public void onCancelButtonClick(HS_Join_Dialog_Fragment f) {
        f.dismiss();
    }



    @Override
    public void onCancelRequestButtonClick(String house_name, HS_Cancel_Request_Dialog_Fragment f, int view_id) {
        Connection c = new Connection(this);
        JSONObject j = c.connect_js(username,
                Request_Params.PARAM_TYPE, Request_Params.VAL_HS_CANCEL_REQUEST_GROUP,
                Request_Params.HS_JOIN_GROUP_GRPNAME, house_name,
                Request_Params.PARAM_USR, username);
        try {
            if (j.getString(Responses_Format.RESPONSE_STATUS).equals("true")) {
                new CustomMessageBox(this, "Your request has been cancelled. \nGood bye!");

                TableRow r = (TableRow) result_layout.getChildAt(findIdOfHouseName(house_name));
                LinearLayout parent = (LinearLayout) r.getChildAt(0);
                parent.setClickable(true); // the house now should be clickable again
                parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showJoinDialog(v);
                    }
                });
                TextView v = (TextView) parent.findViewById(R.id.HS_Search_Result_request_sent);
                v.setVisibility(View.INVISIBLE);


            }
            else
                new CustomMessageBox(this, "We are sorry that we could not process your request at the moment. \nIf you are experiencing this error constantly, please contact our team.");
        }
        catch (JSONException e)
        {
            Log.e("JSON error at join", e.getMessage() + "End", e);
        }
    }



    @Override
    public void onCancelButtonClick(HS_Cancel_Request_Dialog_Fragment f) {
            f.dismiss();
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

// user clicks to join a house (the whole house view is clickable)
    public void showJoinDialog(View v) {
        LinearLayout l = (LinearLayout) v;
        Log.d("linear of tv", l.toString());
        TextView tv = (TextView) l.getChildAt(0);
        String house_name = tv.getText().toString();
        HS_Join_Dialog_Fragment dialog = HS_Join_Dialog_Fragment.initialise(house_name, l.getId());
        dialog.show(getFragmentManager(), "JoinDialog");
    }


    // user clicks onto the text view (Request sent)
    public void showCancelRequestDialog(View v) {

        LinearLayout parent = (LinearLayout) v.getParent();

        TextView tv = (TextView) parent.getChildAt(0);
        String house_name = tv.getText().toString();

        HS_Cancel_Request_Dialog_Fragment dialog = HS_Cancel_Request_Dialog_Fragment.initialise(house_name, parent.getId());

        dialog.show(getFragmentManager(), "CancelRequestDialog");

    }

    public int findIdOfHouseName(String house_name) {
        for (int i = 0; i < result_layout.getChildCount(); i++) {
            if (getHouseNameAtChild(i).equals(house_name))
                return i;
        }
        return -1;
    }

}
