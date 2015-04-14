package Utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.ncl.team5.lloydsmockup.IntentConstants;

import HTTPConnect.Responses_Format;

/**
 * Created by Thanh on 05-Apr-15.
 */
public class Houseshares {


    public static void hs_intents(Activity callingActivity, Class startingActivity, String username) {
        Intent i = new Intent(callingActivity, startingActivity);
        i.putExtra(IntentConstants.USERNAME, username);
        callingActivity.startActivity(i);
    }

    /**
     * called for the welcome page to be activated.
     *
     * @param startingActivity activity class to be started
     * @param house_name       the name of the house (optional)
     * @param callingActivity  the activity starting the home_view
     * @param type             the type of the home view (for the layout)
     * @param username         the username
     */
    public static void hs_intents_home_view(Activity callingActivity, Class startingActivity, String house_name, String username, String hsid, String type) {
        if (type.equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_HOUSE) || type.equals(Responses_Format.RESPONSE_HOUSESHARE_JOINED_SERVICE) || type.equals(Responses_Format.RESPONSE_HOUSESHARE_SENT_REQ)) {
            Intent i = new Intent(callingActivity, startingActivity);
            i.putExtra(IntentConstants.USERNAME, username);
            i.putExtra(IntentConstants.HOME_VIEW_TYPE, type);
            i.putExtra(IntentConstants.HOUSE_NAME, house_name);
            i.putExtra(IntentConstants.HOUSESHARE_ID, hsid);
            callingActivity.startActivity(i);
        } else {
            Toast.makeText(callingActivity, "Some unknown error on the houseshare service on the server", Toast.LENGTH_SHORT).show();
        }
    }
}
