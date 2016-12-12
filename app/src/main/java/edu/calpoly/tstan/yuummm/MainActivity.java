package edu.calpoly.tstan.yuummm;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import businesses.BusinessResponse;
import businesses.Coordinate;
import businesses.Token;
import businesses.Yelp;
import businesses.YelpApi;
import businesses.YelpManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The class that contains the main activity for the yuummm app.
 */
public class MainActivity extends AppCompatActivity {
    private static final int MY_PERM_CODE = 31;
    private static final int MAX_RADIUS = 39999;
    private static final String TAG = "MainActivity";
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    private ImageButton suggest_button;
    private ImageButton settings_button;

    //API Credentials
    private String client_id = "ALFgfNr50IKVMU5p8E3NrQ";
    private String client_secret = "B3mBJLXe6GSKdDb0Bw7rG9c8UWJ0XsyeQvys3fknN9y8VMc3bHyJ5qleHOgrIHMe";

    //Parameters, move to shared preferences?
    private String term = "food";
    private int radius;
    private String categories;
    private String price;
    private boolean open_now;

    //location variables
    private Coordinate mCor;
    private Location mLoc;
    private LocationManager mLocMan;

    private final LocationListener mLocLis = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCor = new Coordinate(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERM_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "permission wasn't granted");

                        return;
                    }
                    mLocMan.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            mLocLis);

                    mLoc = mLocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mCor = new Coordinate(mLoc.getLatitude(), mLoc.getLongitude());
                    Log.d("MainActivity", "coordinates were set");
                }

                break;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getApplicationContext().getSharedPreferences("yuummm.pref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // first run, should only happen once
        if (sharedPreferences.getAll().isEmpty()) {
            Log.d(TAG, "Initializing Shared Preferences");
            String defaultCategories = "chinese,newamerican,italian,japanese,mexican,mediterranean,indpak,comfortfood,breakfast_brunch";
            Set<String> categories = new HashSet<>();
            categories.addAll(Arrays.asList(defaultCategories.split(",")));
            editor.putStringSet("categories", categories);

            editor.putInt("radius", MAX_RADIUS);

            Set<String> prices = new HashSet<>();
            prices.add("1");
            prices.add("2");
            editor.putStringSet("prices", prices);
            editor.apply();
        }

        getParameters();

        //Setup auth capability
        Call<Token> authResponse;
        mLocMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Yelp yelpService = YelpApi.getClient().create(Yelp.class);

        if (YelpManager.Auth.isEmpty()) {
            authResponse = yelpService.getAuthToken("client_credentials", client_id, client_secret);
            authResponse.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    YelpManager.Auth = response.body().getToken();
                    Log.d(TAG, "Successful token got " + YelpManager.Auth);
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    Log.d(TAG, "Token failure");
                }
            });
        }

        //settings button setup
        settings_button = (ImageButton) findViewById(R.id.settings_button);
        settings_button.setOnTouchListener(buttonOnTouchDefault());
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettings();
            }
        });

        //suggestion button setup
        suggest_button = (ImageButton) findViewById(R.id.suggest_button);
        delaySuggestButton(1);
        suggest_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Animation shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.wobble);
                suggest_button.startAnimation(shake);
                return false;
            }
        });
        suggest_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View v2 = v;

                Log.d(TAG, "Go Button Pressed");

                //don't allow spamming of button
                delaySuggestButton(2);

                // check for permission again
                if (!needsPermission()) {
                    Log.d(TAG, "onCreate: Permissions are fine");

                    //setup location
                    try {
                        mLocMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocLis);
                        mLoc = mLocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        mCor = new Coordinate(mLoc.getLatitude(), mLoc.getLongitude());
                    } catch (SecurityException e) {
                        Log.e("ERROR", e.getMessage());
                    }

                    Call<BusinessResponse> searchResponse = yelpService.getBusinessList(
                            term,
                            mCor.getLatitude(),
                            mCor.getLongitude(),
                            radius,
                            categories,
                            price,
                            open_now,
                            "Bearer " + YelpManager.Auth);

                    searchResponse.enqueue(new Callback<BusinessResponse>() {
                        @Override
                        public void onResponse(Call<BusinessResponse> call, Response<BusinessResponse> response) {
                            int total = response.body().getTotal();
                            if (total > 0) {
                                Log.d(TAG, "total was: " + response.body().getTotal());

                                Random r = new Random();
                                int i = r.nextInt((total < 20 ? total : 20));
                                YelpManager.entry = response.body().getBusinesses().get(i);

                                Log.d(TAG, "Successful business got " + YelpManager.entry.getName());

                                Intent mIntent = new Intent(v2.getContext(), EntryActivity.class);
                                v2.getContext().startActivity(mIntent);
                            } else {
                                Log.e(TAG, "No results");
                                Toast.makeText(getApplicationContext(), "No results were found. Please help us by adjusting your settings.", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<BusinessResponse> call, Throwable t) {
                            Log.d(TAG, "Business get failure");
                        }
                    });
                }
            }
        });

        //ask for permission on startup
        needsPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void changeSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void getParameters() {
        radius = sharedPreferences.getInt("radius", MAX_RADIUS);
        Set<String> categorySet = sharedPreferences.getStringSet("categories", null);
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : categorySet) {
            stringBuilder.append(string + ",");
        }
        stringBuilder.setLength(stringBuilder.length() - 1); //trim final comma

        categories = stringBuilder.toString();

        Set<String> prices = sharedPreferences.getStringSet("prices", null);
        stringBuilder = new StringBuilder();

        for (String string : prices) {
            stringBuilder.append(string + ",");
        }
        stringBuilder.setLength(stringBuilder.length() - 1); //trim final comma

        price = stringBuilder.toString();

        open_now = sharedPreferences.getBoolean("open_now", false);
    }

    public boolean needsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            final Activity a = this;

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(a);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Help Yuummm Help You");
                alertBuilder.setMessage("We need your device location to find food near you!");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(a,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERM_CODE);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                Log.d(TAG, "Request permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERM_CODE);
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * used to let application load necessary data in background
     *
     * @param seconds the # of seconds to delay
     */
    public void delaySuggestButton(int seconds) {
        suggest_button.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        suggest_button.setEnabled(true);
                    }
                });
            }
        }, seconds * 1000);
    }

    public static View.OnTouchListener buttonOnTouchDefault() {
        return new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        };
    }
}
