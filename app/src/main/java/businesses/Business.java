package businesses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Describes a yelp business item response from retrofit.
 */
public class Business {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("image_url")
    private String image;

    @SerializedName("is_closed")
    private boolean is_closed;

    @SerializedName("url")
    private String url;

    @SerializedName("price")
    private String price;

    @SerializedName("phone")
    private String phone;

    @SerializedName("rating")
    private double rating;

    @SerializedName("review_count")
    private int review_count;

    @SerializedName("distance")
    private double distance;

    @SerializedName("categories")
    private List<Category> categories;

    @SerializedName("coordinates")
    private Coordinate coordinates;

    @SerializedName("location")
    private Location location;

    @SerializedName("rating_img_url")
    private String image_url;

    public Business(String id, String name, String image, boolean is_closed, String url,
                    String price, String phone, double rating, int review_count, double distance,
                    List<Category> categories, Coordinate coordinates, Location location) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.is_closed = is_closed;
        this.url = url;
        this.price = price;
        this.phone = phone;
        this.rating = rating;
        this.review_count = review_count;
        this.distance = distance;
        this.categories = categories;
        this.coordinates = coordinates;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getImage() {
        return image;
    }

    public String getPhone() {
        return phone;
    }

    public String getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public Location getLocation() {
        return location;
    }

    public double getDistance() {
        return distance;
    }

}
