package com.ncl.team5.lloydsmockup;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import Fragment.HS_Cancel_Confirm_Dialog;
import Fragment.HS_Cancel_Request_Dialog_Fragment;
import Fragment.HS_Join_Confirm_Dialog;
import Fragment.HS_Join_Conflict_Dialog;
import Fragment.HS_Join_Dialog_Fragment;
import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Connection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.Houseshares;
import Utils.StringUtils;

/**
 * For performace optimisation, the display_search wont be performed automatically as the text changes.
 * The display_search request will only be sent when the user press the display_search button
 */

public class Houseshare_Search extends FragmentActivity implements HS_Join_Dialog_Fragment.JoinDialogListener,
        HS_Cancel_Request_Dialog_Fragment.CancelRequestDialogListener, HS_Join_Confirm_Dialog.JoinConfirmedDialogListener
        , HS_Join_Conflict_Dialog.JoinConflictDialogListener, HS_Cancel_Confirm_Dialog.CancelConfirmedDialogListener {
    private String username;
    private String chose_hs_name;
    private String pending_new_hs_name;



    private static enum search_mode {SEARCH, CONFLICT_OK}

    private ScrollView result_scroll_container;
    private TableLayout result_layout;
    private View container;
    private TextView button_create;
    private EditText input_edittext;
    private String input;
    private SearchView search_view;
    private ProgressBar loading_icon;

    private final float DISTANCE_INPUT_SCROLL = 15;
    private final long ANIMATION_DURATION = 500;

    private Map<String, Boolean> Houses_Status;
    private LayoutTransition transition;

    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseshare_search);
        Intent intent = getIntent();

        ActionBar a = getActionBar();
            if (a != null) {
                a.setDisplayShowTitleEnabled(false);
                a.setDisplayShowHomeEnabled(false);
                a.setDisplayUseLogoEnabled(false);
                a.setDisplayHomeAsUpEnabled(false);
            }

        username = intent.getExtras().getString(IntentConstants.USERNAME);
        chose_hs_name = intent.getStringExtra(IntentConstants.HOUSE_NAME);

        Log.d("hs_name_chosen", "/" + chose_hs_name);

        Houses_Status = new HashMap<String, Boolean>();
        button_create = (TextView) findViewById(R.id.HS_Search_CreateHouse);
        result_layout = (TableLayout) findViewById(R.id.table_result);
        result_layout.setLayoutTransition(transition);
        result_scroll_container = (ScrollView) findViewById(R.id.house_search_scroll_container);
        loading_icon = (ProgressBar) findViewById(R.id.progressBar_search);
        setEmpty(true);

        //Auto searching
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

        // get the search widget
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Search a house");

        // set the listner for the search text
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                input = query;
                if (!StringUtils.isFieldEmpty(input))
                    new Search_Worker(Houseshare_Search.this, false).setMode(search_mode.SEARCH).
                        execute(
                                new RequestQueue().addRequest(new Request(Request.TYPE.POST).
                                addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_SEARCH_HOUSE).
                                addParam(Request_Params.PARAM_USR, username).
                                addParam(Request_Params.HS_SEARCH_HOUSE_KEY, input)).toList()
                        );
                else
                    Toast.makeText(Houseshare_Search.this, "Please supply the name of the house", Toast.LENGTH_SHORT).show();

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

    /**
     * See if the user has got any request active after working with the search page. <br/>
     *
     * Set the home page accordingly
     */
    @Override
    public void onBackPressed() {
        if (StringUtils.isFieldEmpty(chose_hs_name)) {
            Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, chose_hs_name, username, Responses_Format.RESPONSE_HOUSESHARE_JOINED_SERVICE);
        }
        else
            Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, chose_hs_name, username, Responses_Format.RESPONSE_HOUSESHARE_SENT_REQ);

    }

    public void createHouse(View v) {
        Intent i = new Intent(this, Houseshare_Create_House.class);
        i.putExtra(IntentConstants.USERNAME, username);
        startActivity(i);
        ((KillApp) this.getApplication()).setStatus(false);
    }

    public void display_search(String response) {

            try {
                JSONObject j = new JSONObject(response);
                if (j != null) {
                    List<ArrayList<String>> result_objects = new ArrayList<ArrayList<String>>();
                    Houses_Status = new HashMap<String, Boolean>();

                    JSONArray result_arr_out = j.getJSONArray(Responses_Format.RESPONSE_HS_CONTENT);
                    //                Log.d("Search json arr", result_arr.toString());
                    for (int i = 0; i < result_arr_out.length(); i++) {

                        JSONArray result_arr_in = result_arr_out.getJSONArray(i);
                        ArrayList<String> l = new ArrayList<String>();

                        Houses_Status.put(result_arr_in.getString(0), result_arr_in.getString(result_arr_in.length() - 1).equalsIgnoreCase("1"));
                        for (int t = 0; t < result_arr_in.length(); t++) {
                            l.add(result_arr_in.getString(t));
                        }
                        result_objects.add(l);
                    }


                    if (result_objects.size() > 0) {
                        result_layout.removeAllViews();
                        for (int i = 0; i < result_objects.size(); i++) {
                            makeHouseResultRow(result_objects.get(i)); // add view to this position
                        }
                    }
                    else {
                        setEmpty(false);
                    }
                }
                else {
                        Toast.makeText(this, "Sorry. Could not process your request now.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                new CustomMessageBox(this, "There was an error in the server response");
                Log.e("JSON parsing exception", e.getMessage() + "End of JSON parsing exception msg", e);
            }
    }

    // display a display_search result
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
                    Log.d("default onlick goes", "here");
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


    // ***************************************JOIN REQUEST DIALOG***********************************************
    // user clicks to join a house (the whole house view is clickable)
    public void showJoinDialog(View v) {
        LinearLayout l = (LinearLayout) v;
        TextView tv = (TextView) l.getChildAt(0);
        String house_name = tv.getText().toString();

        //if user has not joined any house => okay to show the join dialog as normal
        if (StringUtils.isFieldEmpty(chose_hs_name)) {
            HS_Join_Dialog_Fragment dialog = HS_Join_Dialog_Fragment.initialise(house_name, l.getId());
            dialog.show(getFragmentManager(), "JoinDialog");
        }
        else {
            // else if user has already joined a house => we need to show them the conflict dialog to tell them that
            // they have already sent a join request to a different house and that continuing would cancel the previous request
            Log.d("conflict goes", "here");
            HS_Join_Conflict_Dialog dialog = HS_Join_Conflict_Dialog.initialise(chose_hs_name, house_name, username);
            dialog.show(getFragmentManager(), "JoinConflictDialog");
        }
    }

    //user clicks the join button for a normal join (no conflict)
    @Override
    public void onJoinButtonClick(String house_name, HS_Join_Dialog_Fragment f, int view_id) {
        f.dismiss();
        Connection c = new Connection(this);
        JSONObject j = c.connect_js(username,
                Request_Params.PARAM_TYPE, Request_Params.VAL_HS_JOIN_GROUP,
                Request_Params.HS_JOIN_GROUP_GRPNAME, house_name,
                Request_Params.PARAM_USR, username);
        try {
            if (j.getString(Responses_Format.RESPONSE_STATUS).equals("true")) {

                // show the confirmation dialog
                HS_Join_Confirm_Dialog dialog = HS_Join_Confirm_Dialog.initialise(house_name, username);
                dialog.show(getFragmentManager(), "JoinConfirmDialog");

                //mark the result row as request-sent
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
            } else
                new CustomMessageBox(this, "We are sorry that we could not process your request at the moment. \n If you are experiencing this error constantly, please contact our team.");
        } catch (JSONException e) {
            Log.e("JSON error at join", e.getMessage() + "End", e);
        }
    }


    // user clicks onto the text view (Request sent)
    public void showCancelRequestDialog(View v) {

        LinearLayout parent = (LinearLayout) v.getParent();

        TextView tv = (TextView) parent.getChildAt(0);
        String house_name = tv.getText().toString();

        HS_Cancel_Request_Dialog_Fragment dialog = HS_Cancel_Request_Dialog_Fragment.initialise(house_name, parent.getId());
        dialog.show(getFragmentManager(), "CancelRequestDialog");

    }

    /**
     * Cancel clicked from join dialog
     * @param f
     */
    @Override
    public void onCancelButtonClick(HS_Join_Dialog_Fragment f) {
        f.dismiss();
    }


    /**
     *
     * @param f the dialog itself (for dismission)
     * @param username the username
     * @param hs_name the house name that user has sent request to
     */
    @Override
    public void onButtonClick(HS_Join_Confirm_Dialog f, String username, String hs_name) {
        f.dismiss();
        Houseshares.hs_intents_home_view(this, Houseshare_HomeView.class, hs_name, username, Responses_Format.RESPONSE_HOUSESHARE_SENT_REQ);
    }
// ***************************************END JOIN REQUEST DIALOG***********************************************


// ***************************************CANCEL REQUEST DIALOG*************************************************
    /**
     *
     * @param house_name the house user clicked
     * @param f the dialog fragment itself
     * @param view_id the view id of the parent
     */
    @Override
    public void onCancelRequestButtonClick(String house_name, HS_Cancel_Request_Dialog_Fragment f, int view_id) {
        f.dismiss();
        Connection c = new Connection(this);
        JSONObject j = c.connect_js(username,
                Request_Params.PARAM_TYPE, Request_Params.VAL_HS_CANCEL_REQUEST_GROUP,
                Request_Params.HS_JOIN_GROUP_GRPNAME, house_name,
                Request_Params.PARAM_USR, username);
        try {
            if (j.getString(Responses_Format.RESPONSE_STATUS).equals("true")) {
                HS_Cancel_Confirm_Dialog dialog = HS_Cancel_Confirm_Dialog.initialise(house_name, username);
                dialog.show(getFragmentManager(), "cancelRequestDialogConfirm");

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

    /**
     * User confirms that his join request has been cancelled -> redirect the home page
     *
     * @param f        the dialog itself (for dismission)
     * @param username the username
     * @param hs_name  the house name that user has sent request to
     */
    @Override
    public void onButtonClickCancel(HS_Cancel_Confirm_Dialog f, String username, String hs_name) {
        f.dismiss();
        chose_hs_name = null;
    }

    /**
     * Cancel clicked on cancelling join request dialog
     * @param f
     */
    @Override
    public void onCancelButtonClick(HS_Cancel_Request_Dialog_Fragment f) {
            f.dismiss();
    }

// ***************************************END CANCEL REQUEST DIALOG*************************************************


// ***************************************JOIN REQUEST CONFLICT DIALOG**********************************************
    /**
     * see if the new request has taken the old request place. That is, the old request has been cancelled and the new request has been sent.
     *
     * @params old, new_req responses regarding the requests.
     */
    private void resolveConflict(Response old, Response new_req) {
        if (old.getToken("status").equals("true") || new_req.getToken("status").equals("true")) {
            chose_hs_name = pending_new_hs_name; // make the pending name become the chosen name
            pending_new_hs_name = null; // invalidate the pending name variable
            HS_Join_Confirm_Dialog dialog = HS_Join_Confirm_Dialog.initialise(chose_hs_name, username);
            dialog.show(getFragmentManager(), "ConfirmAfterConflictDialog");
        }
    }



    @Override
    public void onPositiveButtonClick(HS_Join_Conflict_Dialog f, String username, String old_hs_name, String new_hs_name) {
        f.dismiss();
        // this will potentially be the name of the house to which the user has sent the newest request
        pending_new_hs_name = new_hs_name;
        // preparing the request for cancelling the old request
        Request cancel_old = new Request(Request.TYPE.POST);
        cancel_old.addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_CANCEL_REQUEST_GROUP)
                .addParam(Request_Params.HS_JOIN_GROUP_GRPNAME, chose_hs_name)
                .addParam( Request_Params.PARAM_USR, username);

        Request join_new = new Request(Request.TYPE.POST);
        join_new.addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_JOIN_GROUP)
                .addParam( Request_Params.HS_JOIN_GROUP_GRPNAME, new_hs_name)
                .addParam(Request_Params.PARAM_USR, username);

        new Search_Worker(this, false).setMode(search_mode.CONFLICT_OK)
                .execute(new RequestQueue().addRequests(cancel_old, join_new).toList());
    }

    @Override
    public void onNegativeButtonClick(HS_Join_Conflict_Dialog f, String username, String old_hs_name, String new_hs_name) {
        f.dismiss();
    }
// ***************************************END JOIN REQUEST CONFLICT DIALOG**********************************************


    /**
     * Class Search_Worker responsible for doing the server connection
     */
    class Search_Worker extends ConcurrentConnection {
        private search_mode mode;

        public Search_Worker setMode(search_mode mode) {
            this.mode = mode;
            return this;
        }

        public Search_Worker(Activity a) {
            super(a, false);
        }

        public Search_Worker(Activity a, boolean showDialog) {
            super(a, showDialog);
        }


        @Override
        public void onPreExecute() {
            loading_icon.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        @Override
        public void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            loading_icon.setVisibility(View.GONE);
            if (mode == search_mode.SEARCH)
                display_search(responses.get(0).getRaw_response());
            else if (mode == search_mode.CONFLICT_OK) {
                assert responses.size() == 2;
                resolveConflict(responses.get(0), responses.get(1));
            }

        }
    }


    public int findIdOfHouseName(String house_name) {
        for (int i = 0; i < result_layout.getChildCount(); i++) {
            if (getHouseNameAtChild(i).equals(house_name))
                return i;
        }
        return -1;
    }

}
