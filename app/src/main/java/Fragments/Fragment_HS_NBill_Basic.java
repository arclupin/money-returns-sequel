//package Fragments;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.TableLayout;
//import android.widget.TextView;
//
//import com.ncl.team5.lloydsmockup.CustomMessageBox;
//import com.ncl.team5.lloydsmockup.Houseshares.Member;
//import com.ncl.team5.lloydsmockup.IntentConstants;
//import com.ncl.team5.lloydsmockup.MainActivity;
//import com.ncl.team5.lloydsmockup.NewBillManual;
//import com.ncl.team5.lloydsmockup.R;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import HTTPConnect.ConcurrentConnection;
//import HTTPConnect.Connection;
//import HTTPConnect.Response;
//import HTTPConnect.Responses_Format;
//
///**
// * Created by Thanh on 08-Apr-15.
// */
//public class Fragment_HS_NBill_Basic extends Fragment {
//
//    private RelativeLayout layout;
//    private EditText billName_view;
//    private EditText dueDate_view;
//    private EditText amount_view;
//    private CheckBox addAll_view;
//    private TableLayout members_table_view;
//
//    private Map<String, Member> members;
//
//
//    public Fragment_HS_NBill_Basic() {
//        super();
//    }
//
//
//
//    public static Fragment_HS_NBill_Basic newInstance(String username, String housename,Member[] members) {
//        Fragment_HS_NBill_Basic f = new Fragment_HS_NBill_Basic();
//        Bundle b = new Bundle();
//        b.putParcelableArray(IntentConstants.MEMBERS, members);
//        b.putString(IntentConstants.USERNAME, username);
//        b.putString(IntentConstants.HOUSE_NAME, housename);
//        return f;
//    }
//
//    public class BillCreator_Worker extends ConcurrentConnection {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        public BillCreator_Worker(Activity a) {
//            super(a);
//        }
//
//        @Override
//        protected void onPostExecute(List<Response> responses) {
//            super.onPostExecute(responses);
//            filterMembers(responses.get(0).getRaw_response());
//            showMembers();
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        members = getHostBillActivity().getMembers();
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_hs_bill_manual_basic, container, false);
//        members_table_view = (TableLayout) layout.findViewById(R.id.table_users);
//        billName_view = (EditText) layout.findViewById(R.id.bill_name_value);
//        dueDate_view = (EditText) layout.findViewById(R.id.due_date_value);
//        amount_view = (EditText) layout.findViewById(R.id.total_amount_value);
//
//        billName_view.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                getHostBillActivity().setBillName(s.toString());
//            }
//        });
//
//        dueDate_view.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                getHostBillActivity().setDueDate(s.toString());
//            }
//        });
//
//        amount_view.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                getHostBillActivity().setTotalAmount(s.toString());
//            }
//        });
//
//
//
//        return layout;
//    }
//
//    private void filterMembers(String r) {
//        try {
//            JSONObject j = new JSONObject(r);
//            if (j.getString("expired").equals("true")) {
//
//                 /* Display message box and auto logout user */
//                final Connection temp_connect = new Connection(getActivity());
//                // experimenting a new message box builder
//                CustomMessageBox.MessageBoxBuilder builder = new CustomMessageBox.MessageBoxBuilder(getActivity(), "Your session has been timed out, please login again");
//                builder.setTitle("Expired")
//                        .setActionOnClick(new CustomMessageBox.ToClick() {
//                            @Override
//                            public void DoOnClick() {
//                                temp_connect.autoLogout(getHostBillActivity().getUsername());
//                            }
//                        }).build();
//                return;
//            }
//            JSONArray arr_out = j.getJSONArray(Responses_Format.RESPONSE_MEMBERS);
//            for (int i = 0; i < arr_out.length(); i++) {
//                JSONArray arr_in = arr_out.getJSONArray(i);
//               members.put(arr_in.getString(1), new Member(arr_in.getString(0), arr_in.getString(1), arr_in.getString(2)));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void showMembers() {
//        clearViews();
//        int i = 0;
//        for (String memberName : members.keySet())
//        {
//            View v = members.get(memberName).craftView(getActivity().getLayoutInflater());
//            members_table_view.addView(v, i++);
//            ((CheckBox) v.findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    Member m = members.get(
//                            ((TextView) ((RelativeLayout) buttonView.getParent()).findViewById(R.id.username_select)).getText().toString());
//
//                    if (isChecked)
//                            getHostBillActivity().getInvolved_members().add(m);
//                    else
//                            getHostBillActivity().getInvolved_members().remove(m);
//
//                }
//            });
//        }
//    }
//
//    private void clearViews() {
//        for (int i = 0; i < members_table_view.getChildCount() - 1; i++)
//            members_table_view.removeViewAt(i);
//    }
//
//    public NewBillManual getHostBillActivity() {
//        //todo auto bill also needs this
//       return ((NewBillManual) getActivity());
//    }
//}
