package businesses;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Yelp {
    @POST("oauth2/token")
    Call<Token> getAuthToken(@Query("grant_type") String grant,
                             @Query("client_id") String id,
                             @Query("client_secret") String secret);

    @GET("v3/businesses/search")
    Call<BusinessResponse> getBusinessList(@Query("term") String term,
                                           @Query("latitude") double latitude,
                                           @Query("longitude") double longitude,
                                           @Query("radius") int radius,
                                           @Query("categories") String categories,
                                           @Query("price") String price,
                                           @Query("open_now") boolean open_now,
                                           @Header("Authorization") String authorization);
}
