package HTTPConnect;

import com.ncl.team5.lloydsmockup.Houseshare_Search;

import java.util.ArrayList;
import java.util.List;

import Utils.StringUtils;

/**
 * Created by Thanh on 22-Mar-15.
 */
public class House_Search_Result {

    /* Returns the name */
    public String getName() {
        return name;
    }

    /* Sets the name */
    public void setName(String name) {
        this.name = name;
    }

    /* name variable */
    private String name;

    /* Gets address */
    public String getAddress() {
        return address;
    }

    /* sets address */
    public void setAddress(String address) {
        this.address = address;
    }

    /* Variable */
    private String address;

    /* gets the description */
    public String getDescription() {
        return description;
    }

    /* sets it */
    public void setDescription(String description) {
        this.description = description;
    }

    /* variable */
    private String description;

    /* sets all of the variables */
    public House_Search_Result(String arg_name, String arg_address, String arg_description) {
        this.name = StringUtils.isFieldEmpty(arg_name) ? "Missing name" : arg_name;
        this.address = StringUtils.isFieldEmpty(arg_address) ? "Missing address" : arg_address;
        this.description = StringUtils.isFieldEmpty(arg_description) ? "Missing description" : arg_description;
    }

    /* default */
    public House_Search_Result() {
        name = "Missing name";
        address = "Missing address";
        description = "Missing description";
    }

    /* Gets all the info */
    public List<String> getInfo() {
       List<String> l = new ArrayList<String>();
        l.add(name);
        l.add(address);
        l.add(description);
        return l;
    }



}
