package businesses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kyle on 10/30/2016.
 */

public class BusinessResponse {
    @SerializedName("total")
    private int total;
    @SerializedName("businesses")
    private List<Business> businesses;

    public BusinessResponse(int total, List<Business> businesses) {
        this.total = total;
        this.businesses = businesses;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }
}
