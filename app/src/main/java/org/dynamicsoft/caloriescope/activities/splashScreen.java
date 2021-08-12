package org.dynamicsoft.caloriescope.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.dynamicsoft.caloriescope.R;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread splash = new Thread()
        {
            @Override
            public void run ()
            {
                try {
                    sleep(4000);
                    Intent splashIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(splashIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splash.start();
    }
}
