package businesses;

import com.google.gson.annotations.SerializedName;

/**
 * Describes a Location item used for the business response
 */
public class Location {
    @SerializedName("city")
    private String city;
    @SerializedName("country")
    private String country;
    @SerializedName("address2")
    private String address2;
    @SerializedName("address3")
    private String address3;
    @SerializedName("state")
    private String state;
    @SerializedName("address1")
    private String address1;
    @SerializedName("zip_code")
    private String zip;

    public Location(String city, String country, String address2, String address3,
                    String state, String address1, String zip) {
        this.city = city;
        this.country = country;
        this.address2 = address2;
        this.address3 = address3;
        this.state = state;
        this.address1 = address1;
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(address1 + "\n");
        sb.append(city + ", " + state + " " + country + " " + zip);
        return sb.toString();
    }

    public String toSingleLineString() {
        StringBuilder sb = new StringBuilder(address1 + " ");
        sb.append(city + ", " + state + " " + country + " " + zip);
        return sb.toString();
    }
}
