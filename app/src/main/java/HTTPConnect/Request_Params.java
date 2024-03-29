package HTTPConnect;

/**
 * Created by Thanh on 16-Mar-15.
 */
public class Request_Params {
    //TODO needed to change to hex in the end

    public static final String PARAM_TYPE = "TYPE";

    public static final String VAL_HS_INIT = "HS_INIT";

    public static final String VAL_HS_REGISTER = "HS_REG";

    public static final String VAL_HS_2_FETCH_HOUSE_DETAIL = "HS_2_FTCH_1"; // 2: status code for having joined service, 1: ini.

    public static final String VAL_HS_SEARCH_HOUSE = "HS_SRCH_HOUSE";// request type name

    public static final String HS_SEARCH_HOUSE_KEY = "HS_SRCH_HOUSE_KEY"; // key for display_search house (house name or part of it)

    public static final String VAL_HS_JOIN_GROUP = "HS_JOIN_GRP";

    public static final String HS_JOIN_GROUP_GRPNAME = "HS_JOIN_GRP_NAME";

    public static final String VAL_HS_CANCEL_REQUEST_GROUP = "HS_CANCEL_REQ";

    public static final String VAL_HS_CREATE_HOUSE = "HS_CH";

    public static final String HS_CREATE_HOUSE_NAME = "HS_CH_NAME";


    public static final String HS_CREATE_HOUSE_HSNO = "HS_CH_HSNO";

    public static final String HS_CREATE_HOUSE_STREET = "HS_CH_STRT";

    public static final String HS_CREATE_HOUSE_CITY = "HS_CH_CITY";

    public static final String HS_CREATE_HOUSE_POSTCODE = "HS_CH_PC";

    public static final String HS_CREATE_HOUSE_DESCRIPTION = "HS_CH_DSC";


    public static final String HS_CREATE_BILL = "HS_CRT_BILL"; // request type for creating a bill

    public static final String HS_CREATE_BILL_NAME = "BILL_NAME"; // name of the bill

    public static final String HS_CREATE_BILL_DUE_DATE = "DUE_DATE"; // due date

    public static final String HS_CREATE_BILL_AMOUNT = "AMOUNT"; // amount of the bill

    public static final String HS_CREATE_BILL_MESSAGE = "MSG"; // message to members

    public static final String HS_CREATE_BILL_MEMBERS = "members[]"; // array storing involved users


    public static final String REQUEST_HS_GET_A_BILL = "HS_GET_A_BILL";

    public static final String REQUEST_HS_GET_MY_BILLS = "HS_GET_MY_BILLS";

    public static final String REQUEST_HS_GET_SUB_BILLS = "HS_GET_SBS";

    public static final String REQUEST_HS_BILL_ID = "HS_BILL_ID";


    public static final String REQUEST_HS_GET_PAYMENTS = "HS_GET_BPAYS";

    public static final String REQUEST_HS_CONFIRM_SUB_BILL = "HS_CONFIRM_MY_SUB_BILL";

    public static final String REQUEST_HS_ACTIVATE_BILL = "HS_ACTIVATE_BILL";


    public static final String REQUEST_HS_CONFIRM_PAYMENT = "HS_PAY";

    public static final String REQUEST_HS_DATE_PAID = "DATE_PAYED";

    public static final String REQUEST_HS_AMOUNT = "AMOUNT";

    public static final String REQUEST_HS_PAY_METHOD = "PAY_METHOD";

    public static final String REQUEST_HS_PAY_MSG = "PAY_MSG";


    public static final String REQUEST_HS_BILL_FETCH_EVENTS = "HS_BILL_FETCH_EVENTS";

    public static final String VAL_REF_NOTI = "REF_NOTI";

    public static final String VAL_FETCH_NOTI = "HS_NOTI_FTCH";


    public static final String MARK_NOTI_AS_SEEN_NOT_READ = "NOTI_ASNR";

    public static final String MARK_NOTI_AS_SEEN_NOT_READ_PARAM = "notifications[]";

    public static final String MARK_NOTI_AS_READ= "NOTI_R";

    public static final String NOTI_ID = "NOTI_ID";


    public static final String VAL_APPROVE_MEMBER = "HS_MEMBER_IN";

    public static final String VAL_APPROVE_MEMBER_PARAM = "HS_MEMBER_IN_NAME";

    public static final String VAL_REFUSE_MEMBER = "HS_MEMBER_RFS";

    public static final String VAL_REFUSE_MEMBER_PARAM = "MEMBER_RFS_NAME";

    public static final String PARAM_USR = "USR";

    public static final String HS_ALL_MEMBERS = "MEMBERS";

    public static final String PARAM_HOUSESHAREID = "HS_ID";

    public static final String PARAM_APPROVE_PAYMENT = "HS_CONF_PAY";

    public static final String PARAM_REJECT_PAYMENT = "HS_REJ_PAY";

    public static final String PARAM_CONCLUDE_BILL = "HS_CONC_BILL";

    public static final String PARAM_REMOVE_BILL = "HS_REMOVE_BILL";

    public static final String PARAM_LEAVE_HOUSE = "HS_LEAVE";

    public static final String PARAM_ASSIGN_ADMIN = "HS_ASSIGN_ADMIN";

}
