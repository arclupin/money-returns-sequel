package Fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.CustomMessageBox;
import com.ncl.team5.lloydsmockup.HouseShare_Bill;
import com.ncl.team5.lloydsmockup.Houseshare_HomeView;
import com.ncl.team5.lloydsmockup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import HTTPConnect.ConcurrentConnection;
import HTTPConnect.Connection;
import HTTPConnect.Request;
import HTTPConnect.RequestQueue;
import HTTPConnect.Request_Params;
import HTTPConnect.Response;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;

/**
 * Fragment for displaying the home view in the home view activity <br/>
 * (another fragment that would be invoked in this activity is the notification fragment
 *
 */
public class Fragment_HS_Home extends Fragment_HS_Abstract {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String response_content;
    private TextView viewName;
    private TextView viewAddressText;
    private TextView viewDescription;

    private LinearLayout l;
    private RelativeLayout main_view_container;
    private ProgressBar loadingIcon;
    /* Used for the list view */
    private ArrayList<String> testData;
    private String username;
    private String hs_name;
    ListView billList;


    // TODO: For now it will just display the plain response from the server
    // need udating later
    private static String data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("USR");
        hs_name = getArguments().getString("HS_NAME");

    }


    private OnFragmentInteractionListener_HomeView mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_HS_Home newInstance(String username, String house_name) {
        Fragment_HS_Home fragment = new Fragment_HS_Home();
        Bundle args = new Bundle();
        args.putString("USR", username);
        args.putString("HS_NAME", house_name);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_HS_Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        l  = (LinearLayout) inflater.inflate(R.layout.fragment_home_view, container, false);

         main_view_container = (RelativeLayout) l.findViewById(R.id.home_view_main_container);
         viewName = (TextView) l.findViewById(R.id.viewName);
         viewAddressText = (TextView) l.findViewById(R.id.viewAddressText);
         viewDescription = (TextView) l.findViewById(R.id.viewAddress);
         loadingIcon = (ProgressBar) l.findViewById(R.id.progressBar);

        /* Get the list view */
        billList = (ListView) l.findViewById(R.id.listBills);
        List<String> testData = new ArrayList<String>();
        testData.add("Bill 1");
        testData.add("Bill 2");
        testData.add("Bill 3");
        testData.add("Bill 4");
        testData.add("Bill 5");
        testData.add("Bill 6");
        testData.add("Bill 7");
        testData.add("Bill 8");
        testData.add("Bill 9");

        billList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), HouseShare_Bill.class);
                startActivity(i);
            }
        });

        // Create The Adapter with passing ArrayList as 3rd parameter
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, testData);
        // Set The Adapter
        billList.setAdapter(arrayAdapter);

        initialiseData();

        return l;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener_HomeView) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener_HomeView");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update() {
        initialiseData();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener_HomeView {
        // TODO: Update argument type and name

        /**
         * method called when the home view fragment is created (fetch data from server) <br/>
         * NOTE: there are various solutions to this.
         *
         * @param f the fragment
         */
        public void onHomeViewCreated(Fragment_HS_Home f);
    }

    public class HomeViewWorker extends ConcurrentConnection {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public HomeViewWorker(Activity a, boolean showDialog) {
            super(a, showDialog);
        }

        @Override
        protected void onPostExecute(List<Response> responses) {
            super.onPostExecute(responses);
            loadingIcon.setVisibility(View.GONE);
            response_content = processInfo(responses.get(0).getRaw_response()); // do the processing in the home view
            checkNewNotification(responses.get(1).getRaw_response()); // check for new notification and reflect it on the noti icon
            displayContent();

           }
    }

    // method called when the fragment is called.
    protected String processInfo(String response) {
        String content = "DEFAULT";
        try {

            JSONObject jo = new JSONObject(response);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(getActivity());
                final Connection temp_connect = new Connection(getActivity());
                final String temp_usr = username;
                errorBox.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                temp_connect.autoLogout(temp_usr);
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            } else {
//               TextView tv = (TextView) findViewById(R.id.hs_hv_response);
                content = jo.getString(Responses_Format.RESPONSE_HS_CONTENT); //TODO
            }

        }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(getActivity(), "There was an error in the server response");
            jse.printStackTrace();
        } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(getActivity(), "An unknown error occurred");
            e.printStackTrace();
        }
        return content;
    }

    public void displayContent() {
        Log.d("response", response_content);
        try {
            JSONArray house_Array = new JSONObject(response_content).getJSONArray("basic_info");
            viewName.setText(house_Array.getString(0));
            viewAddressText.setText(StringUtils.implode(" ", house_Array.getString(1), house_Array.getString(2), house_Array.getString(3)));
            viewDescription.setText(house_Array.getString(house_Array.length() - 1));

            //TODO Set up the bill
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise data
     */
    private void initialiseData() {
        Request home_feed_request = new Request(Request.TYPE.POST);
        home_feed_request.addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_2_FETCH_HOUSE_DETAIL)
                .addParam(Request_Params.PARAM_USR, username);

        Request new_noti_check_request = new Request(Request.TYPE.POST);
        new_noti_check_request.addParam(Request_Params.PARAM_TYPE, Request_Params.VAL_REF_NOTI)
                .addParam(Request_Params.PARAM_USR, username);

        new HomeViewWorker(getActivity(), false)
                .execute(new RequestQueue().addRequest(home_feed_request).addRequest(new_noti_check_request).toList());

    }

    public void checkNewNotification(String r) {
        //TODO complete
        //Just need to send a simple request asking for the arrival of any new noti, the server will take care of the search

        try {
            /* Command required to make a payment, takes username, to account, from account, both sort codes and amount
             * Returns: JSON String */
//            result = connect.execute(Request_Params.PARAM_TYPE, Request_Params.VAL_REF_NOTI, Request_Params.PARAM_USR, this.username).get();
//            /* Turns String into JSON object, can throw JSON Exception */
            JSONObject jo = new JSONObject(r);

            /* Check if the user has timed out */
            if (jo.getString("expired").equals("true")) {

                /* Display message box and auto logout user */
                AlertDialog.Builder errorBox = new AlertDialog.Builder(getActivity());
                final Connection temp_connect = new Connection(getActivity());
                final String temp_usr = username;
                errorBox.setMessage("Your session has been timed out, please login again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                temp_connect.autoLogout(temp_usr);
                            }
                        });
                AlertDialog alert = errorBox.create();
                alert.show();
            } else if (jo.getString("status").equals("true")) {
                ((Fragment_HS_Notification.OnNotificationInteraction) getActivity()).onNewNotiReceived(); // risky ~
            }

        }
        /* Catch the exceptions */ catch (JSONException jse) {
            /* Error in the JSON response */
            new CustomMessageBox(getActivity(), "There was an error in the server response");
            jse.printStackTrace();
        } catch (Exception e) {
            /* Failsafe if something goes utterly wrong */
            new CustomMessageBox(getActivity(), "An unknown error occurred");
            e.printStackTrace();
        }
    }



}
