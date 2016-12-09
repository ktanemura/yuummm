package businesses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kyle on 10/30/2016.
 */

public class Category {
    @SerializedName("alias")
    private String alias;
    @SerializedName("title")
    private String title;

    public Category(String alias, String title) {
        this.alias = alias;
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public String getTitle() {
        return title;
    }
}
