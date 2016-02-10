package com.example.dvagner.mykiosk;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    SharedPreferences sPref;
    final String URL = "saved_url";
    final String Timmer = "saved_timer";
    private String url_settings;
    private Integer timer_settings;
    private WebView mWebView;
    protected PowerManager.WakeLock mWakeLock;
    private Timer autoUpdate;
    private Timer autoUpdateScreen;
    private  Long seconds;

    private ContentResolver cResolver;
    private Window window;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        window =getWindow();
        cResolver = getContentResolver();

        autoUpdate = new Timer();
        sPref = getPreferences(MODE_PRIVATE);
        url_settings =  sPref.getString(URL, "");
        timer_settings = sPref.getInt(Timmer, 0);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new HelloWebViewClient());
        autoUpdateScreen = new Timer();
        autoUpdateScreen.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        screenSettings();

                    }
                });
            }
        }, 0, 10000); // updates each 40 secs

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_reload)
        {
           mWebView.reload();
        }
        return super.onOptionsItemSelected(item);
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {
            webview.loadUrl(url);
            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode==KeyEvent.KEYCODE_BACK) && mWebView.canGoBack())
        {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onResume() {
        screenSettings();
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        url_settings = sPref.getString(URL, null);
        timer_settings = Integer.parseInt(sPref.getString(Timmer, "0"));
        //Toast.makeText(this, "Timer  is "+timer_settings.toString(), Toast.LENGTH_SHORT).show();
        mWebView.loadUrl(url_settings);
        super.onResume();
        if (timer_settings>0) {
            autoUpdate.cancel();
            autoUpdate.purge();
            seconds = new Long(timer_settings * 1000);
            autoUpdate = new Timer();
            autoUpdate.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            updateHTML();
                        }
                    });
                }
            }, 0, seconds.longValue()); // updates each 40 secs
        }
        else
        {
            //Toast.makeText(this, "Timer  canceled ", Toast.LENGTH_SHORT).show();
            autoUpdate.cancel();
            autoUpdate.purge();
        }

    }

    private void screenSettings() {
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = 1;
        window.setAttributes(lp);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

    }

    private void updateHTML(){
       // Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        mWebView.reload();
        screenSettings();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
