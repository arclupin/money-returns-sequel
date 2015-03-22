package HTTPConnect;

import com.ncl.team5.lloydsmockup.Houseshare_Search;

import java.util.ArrayList;
import java.util.List;

import Utils.StringUtils;

/**
 * Created by Thanh on 22-Mar-15.
 */
public class House_Search_Result {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public House_Search_Result(String arg_name, String arg_address, String arg_description) {
        this.name = StringUtils.isFieldEmpty(arg_name) ? "Missing name" : arg_name;
        this.address = StringUtils.isFieldEmpty(arg_address) ? "Missing address" : arg_address;
        this.description = StringUtils.isFieldEmpty(arg_description) ? "Missing description" : arg_description;
    }

    public House_Search_Result() {
        name = "Missing name";
        address = "Missing address";
        description = "Missing description";
    }

    public List<String> getInfo() {
       List<String> l = new ArrayList<String>();
        l.add(name);
        l.add(address);
        l.add(description);
        return l;
    }



}
