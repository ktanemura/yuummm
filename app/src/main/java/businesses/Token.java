package businesses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kyle on 10/30/2016.
 */

public class Token {
    @SerializedName("access_token")
    private String token;
    @SerializedName("expires_in")
    private int expires;
    @SerializedName("token_type")
    private String token_type;

    public Token(String token, int expires, String token_type) {
        this.token = token;
        this.expires = expires;
        this.token_type = token_type;
    }

    public String getToken() {
        return token;
    }
}
