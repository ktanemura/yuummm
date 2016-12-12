package edu.calpoly.tstan.yuummm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import businesses.Business;
import businesses.YelpManager;

/**
 * Shows a business entry.
 */
public class EntryActivity extends AppCompatActivity {
    //private TextView name;
    private ImageView iv;
    private TextView rating;
    private TextView price;
    private final double M_TO_MILES = 0.000621371;
    private TextView distance;
    private TextView is_open;
    //private TextView loc;
    private Button bt;
    private ImageButton share;
    private ImageButton yelp;
    private ImageButton call;
    private static final String TAG = "EntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_entry);

        final Business e = YelpManager.entry;
        //name = (TextView) findViewById(R.id.business_name);
        iv = (ImageView) findViewById(R.id.business_pic);
        //loc = (TextView) findViewById(R.id.location_text);
        //rating = (TextView) findViewById(R.id.business_rating);
        price = (TextView) findViewById(R.id.business_price);
        share = (ImageButton) findViewById(R.id.share_button);
        call = (ImageButton) findViewById(R.id.call_button);
        distance = (TextView) findViewById(R.id.distance);
        yelp = (ImageButton) findViewById(R.id.yelp_button);
        ImageView rat = (ImageView) findViewById(R.id.rating_pic);

        double r = e.getRating();

        if (r == 5) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_5));
        } else if (r == 4.5) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_4_5));
        } else if (r == 4) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_4));
        } else if (r == 3.5) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_3_5));
        } else if (r == 3) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_3));
        } else if (r == 2.5) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_2_5));
        } else if (r == 2) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_2));
        } else if (r == 1.5) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_1_5));
        } else if (r == 1) {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_1));
        } else {
            rat.setImageDrawable(ContextCompat.getDrawable(rat.getContext(), R.drawable.stars_0));
        }

        ScrollView outer = (ScrollView) findViewById(R.id.outer);
        outer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        //Set up title and photo header
        setTitle(e.getName());
        //name.setText(e.getName());
        Glide.with(iv.getContext())
                .load(e.getImage())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        RelativeLayout rl = (RelativeLayout) findViewById(R.id.e_lay);
                        Palette p = Palette.from(resource).generate();

                        iv.setImageBitmap(resource);
                        rl.setBackgroundColor(p.getVibrantColor(
                                ContextCompat.getColor(iv.getContext(), R.color.colorAccent)));
                    }
                });

//        rating.setText("Rating: " + String.valueOf(e.getRating()));
        price.setText("Price: " + e.getPrice());
        price.setTextColor(ContextCompat.getColor(this, R.color.white));
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) price
                .getLayoutParams();
        mlp.setMargins(0, 16, 0, 7);
        StringBuilder sb = new StringBuilder();
        sb.append(e.getDistance() * M_TO_MILES);
        distance.setText("Distance: " + sb.substring(0, 4) + "mi");
        distance.setTextColor(ContextCompat.getColor(this, R.color.white));

        Log.d("EntryActivity", "Should be full");

        yelp.setOnTouchListener(new View.OnTouchListener() {
            @Override
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
        });

        yelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(e.getUrl()));
                startActivity(i);
            }
        });

        //Set address text
        //loc.setText(e.getLocation().toSingleLineString());

        bt = (Button) findViewById(R.id.direction_button);
        bt.setText(e.getLocation().toSingleLineString());
        bt.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentOld));
        bt.setOnTouchListener(new View.OnTouchListener() {
            @Override
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
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + e.getName() +
                        ",+" + e.getLocation().getCity() + "+"
                        + e.getLocation().getState());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


        share.setOnTouchListener(new View.OnTouchListener() {
            @Override
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
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sIntent = new Intent();
                sIntent.setAction(Intent.ACTION_SEND);
                sIntent.putExtra(Intent.EXTRA_TEXT, e.getName() + "\n" + e.getLocation().toString());
                sIntent.setType("text/plain");
                startActivity(Intent.createChooser(sIntent, getResources().getString(R.string.share)));
            }
        });

        final Activity a = this;
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + e.getPhone();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(uri));
                startActivity(callIntent);
            }
        });

        call.setOnTouchListener(new View.OnTouchListener() {
            @Override
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
        });

    }
}
