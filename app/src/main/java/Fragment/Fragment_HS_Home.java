package Fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ncl.team5.lloydsmockup.CustomMessageBox;
import com.ncl.team5.lloydsmockup.Houseshare_HomeView;
import com.ncl.team5.lloydsmockup.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import HTTPConnect.Connection;
import HTTPConnect.Request_Params;
import HTTPConnect.Responses_Format;
import Utils.StringUtils;

/**
 * Fragment for displaying the home view in the home view activity <br/>
 * (another fragment that would be invoked in this activity is the notification fragment
 *
 */
public class Fragment_HS_Home extends android.support.v4.app.Fragment {
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


    // TODO: For now it will just display the plain response from the server
    // need updating later
    private static String data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = Houseshare_HomeView.username;
        hs_name = Houseshare_HomeView.house_name;
//        username = getArguments().getString("USR");
//        hs_name = getArguments().getString("HS_NAME");

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

         Log.d("a", "a");

        /* Get the list view */
        ListView billList = (ListView) l.findViewById(R.id.listBills);
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

    public class HomeViewWorker extends Connection {
        ProgressDialog p;


        public HomeViewWorker(Activity a) {
            super(a);
        }

        @Override
        protected void onPostExecute(String r) {

            super.onPostExecute(r);
            loadingIcon.setVisibility(View.GONE);
            response_content = processInfo(r); // do the processing
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

        new HomeViewWorker(getActivity()).setMode(Connection.MODE.LONG_TASK)
                .setDialogMessage("Fetching news")
                .execute(Request_Params.PARAM_TYPE, Request_Params.VAL_HS_2_FETCH_HOUSE_DETAIL, Request_Params.PARAM_USR, this.username);

    }



}
