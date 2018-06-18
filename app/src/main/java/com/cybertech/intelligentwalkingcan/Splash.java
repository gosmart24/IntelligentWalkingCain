package com.cybertech.intelligentwalkingcan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class Splash extends AppCompatActivity {

    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        loadingProgress = (ProgressBar) findViewById(R.id.progressloader);
        loadingProgress.setIndeterminate(true);

        Thread welcomethread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    // start welcome Activity and finish(); the splash screen.
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        welcomethread.start();
    }
}
