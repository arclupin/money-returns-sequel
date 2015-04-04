package HTTPConnect;

/**
 * Created by Thanh on 16-Mar-15.
 */
/**
 * Class storing the format of responses from the server
 */
public class Responses_Format {

    public static final String RESPONSE_EXPIRED = "expired";
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_HS_CONTENT = "content";


    public static final String RESPONSE_HOUSESHARE_NOT_JOINED = "0"; // user is new to the service
    public static final String RESPONSE_HOUSESHARE_JOINED_SERVICE= "1"; // user has joined service but yet to join any house
    public static final String RESPONSE_HOUSESHARE_JOINED_HOUSE = "2"; // user has fully joined a house
    public static final String RESPONSE_HOUSESHARE_SENT_REQ = "3"; // user has send a join request

    public static final String RESPONSE_FAILED_NAME_NOT_UNQ = "x_unq";
    public static final String RESPONSE_FAILED_UNKNOWN = "x_x";


}
