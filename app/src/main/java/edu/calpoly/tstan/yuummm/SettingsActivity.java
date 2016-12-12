package edu.calpoly.tstan.yuummm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import static android.widget.TableLayout.OnClickListener;
import static edu.calpoly.tstan.yuummm.MainActivity.buttonOnTouchDefault;
import static edu.calpoly.tstan.yuummm.MainActivity.editor;
import static edu.calpoly.tstan.yuummm.MainActivity.sharedPreferences;

/**
 * Settings will change the API call slightly utilizing
 * shared preferences as the main tool for saving
 */
public class SettingsActivity extends AppCompatActivity {

    public static int checkedColor;
    public static int uncheckedColor;

    @Override
    protected void onPause() {
        super.onPause();
        final Button opennow = (Button) findViewById(R.id.open_now);

        //this is kind of hacky...
        opennow.callOnClick();
        opennow.callOnClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        checkedColor = ContextCompat.getColor(this, R.color.colorAccent);
        uncheckedColor = ContextCompat.getColor(this, R.color.colorFont);

        setupCategoryButtons();

        final Button price1 = (Button) findViewById(R.id.one_dollar);
        setupPriceButton(price1, "1");
        final Button price2 = (Button) findViewById(R.id.two_dollar);
        setupPriceButton(price2, "2");

        final Button price3 = (Button) findViewById(R.id.three_dollar);
        setupPriceButton(price3, "3");

        final Button price4 = (Button) findViewById(R.id.four_dollar);
        setupPriceButton(price4, "4");

        final Button opennow = (Button) findViewById(R.id.open_now);
        if (sharedPreferences.getBoolean("open_now", false)) {
            opennow.setBackgroundColor(checkedColor);
        }
        opennow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sharedPreferences.getBoolean("open_now", false)) {
                    editor.putBoolean("open_now", true);
                    opennow.setBackgroundColor(checkedColor);
                } else {
                    editor.putBoolean("open_now", false);
                    opennow.setBackgroundColor(uncheckedColor);
                }
                editor.apply();
            }
        });

        Spinner dropdown = (Spinner) findViewById(R.id.distance_max);
        final String[] items = new String[]
                {"5 miles (<= 10 min drive)",
                        "10 miles (<= 18 min drive)",
                        "15 miles (<= 24 min drive)",
                        "25 miles (<= 30 min drive)"};
        final ArrayList<Integer> distances = new ArrayList<>();

        distances.add(8047);
        distances.add(16094);
        distances.add(24141);
        distances.add(39999);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(distances.indexOf(sharedPreferences.getInt("radius", 39999)));
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("radius", distances.get(i));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("nothing selected");
            }
        });
    }

    public void setupCategoryButtons() {
        final Button chinese_button = (Button) findViewById(R.id.chinese_button);
        setupCategoryButton(chinese_button, "chinese");

        final Button american_button = (Button) findViewById(R.id.american_button);
        setupCategoryButton(american_button, "newamerican");

        final Button italian_button = (Button) findViewById(R.id.italian_button);
        setupCategoryButton(italian_button, "italian");

        final Button japanese_button = (Button) findViewById(R.id.japanese_button);
        setupCategoryButton(japanese_button, "japanese");

        final Button mexican_button = (Button) findViewById(R.id.mexican_button);
        setupCategoryButton(mexican_button, "mexican");

        final Button mediterranean_button = (Button) findViewById(R.id.mediterranean_button);
        setupCategoryButton(mediterranean_button, "mediterranean");

        final Button indian_button = (Button) findViewById(R.id.indian_button);
        setupCategoryButton(indian_button, "indpak");

        final Button comfort_button = (Button) findViewById(R.id.comfort_button);
        setupCategoryButton(comfort_button, "comfortfood");

        final Button brunch_button = (Button) findViewById(R.id.breakfast_brunch_button);
        setupCategoryButton(brunch_button, "breakfast_brunch");
    }

    public void setupCategoryButton(final Button button, final String string) {
        setColor(button, string);
        button.setOnTouchListener(buttonOnTouchDefault());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = addOrRemoveCategory(string);
                if (result == 1) {
                    button.setBackgroundColor(uncheckedColor);
                } else if (result == 2) {
                    button.setBackgroundColor(checkedColor);
                }
                editor.apply();
            }
        });

    }

    public void setPriceColor(final Button button, String value) {
        if (sharedPreferences.getStringSet("prices", null).contains(value)) {
            button.setBackgroundColor(checkedColor);
        }
    }

    public void setupPriceButton(final Button button, final String value) {
        setPriceColor(button, value);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addOrRemovePrice(value)) {
                    button.setBackgroundColor(uncheckedColor);
                } else {
                    button.setBackgroundColor(checkedColor);
                }
            }
        });
    }

    public boolean addOrRemovePrice(String value) {
        Set<String> prices = sharedPreferences.getStringSet("prices", null);

        if (prices.contains(value)) {
            if (sharedPreferences.getStringSet("prices", null).size() < 2) {
                Log.d("SettingsActivity", "At least one price should be checked.");
                Toast.makeText(this, "At least one price range must be selected!", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                prices.remove(value);
                editor.putStringSet("prices", prices);
                editor.apply();
                return true;
            }
        }
        prices.add(value);
        editor.putStringSet("prices", prices);
        editor.apply();
        return false;
    }

    public void setColor(Button button, String string) {
        if (sharedPreferences.getStringSet("categories", null).contains(string)) {
            button.setBackgroundColor(checkedColor);
        }
    }

    // 0 = fail, 1 = uncheck, 2 = checked;
    public int addOrRemoveCategory(String category) {
        int color = 0;

        if (sharedPreferences.getStringSet("categories", null).contains(category)) {
            if (sharedPreferences.getStringSet("categories", null).size() < 2) {
                Log.d("SettingsActivity", "At least one category should be selected.");
                Toast.makeText(this, "At least one category must be selected!", Toast.LENGTH_SHORT).show();
            } else {
                Set<String> categories = sharedPreferences.getStringSet("categories", null);
                categories.remove(category);
                editor.putStringSet("categories", categories);
                color = 1;
            }
        } else {
            Set<String> categories = sharedPreferences.getStringSet("categories", null);
            categories.add(category);
            editor.putStringSet("categories", categories);
            color = 2;
        }
        editor.apply();

        return color;
    }

    public void backToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
