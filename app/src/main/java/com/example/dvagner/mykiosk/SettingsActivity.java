package com.example.dvagner.mykiosk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.paypal.android.MEP.CheckoutButton;


public class SettingsActivity extends Activity {

    EditText etText;
    EditText etTimer;
    private Integer timer_settings;
    final String URL = "saved_url";
    final String Timmer = "saved_timer";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        etText = (EditText) findViewById(R.id.editUrl);
        etTimer = (EditText) findViewById(R.id.editTimer);
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedText = preferences.getString(URL, null);
        timer_settings = Integer.parseInt( preferences.getString(Timmer, "0"));
        etText.setText(savedText);
        etTimer.setText(timer_settings.toString());
        Toast.makeText(this, "Settings loaded", Toast.LENGTH_SHORT).show();
        super.onResume();
    }

    @Override
    protected void onPause() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(URL, etText.getText().toString());
        editor.putString(Timmer, etTimer.getText().toString());
        editor.commit();
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        super.onPause();
    }
}
