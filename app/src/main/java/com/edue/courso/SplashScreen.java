package com.edue.courso;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    TextView splashTextView;
    ImageView splashImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashImageView = findViewById(R.id.splash_logo);
        splashTextView = findViewById(R.id.splash_text);

        Animation fadeInAnim = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade_in);
        splashTextView.startAnimation(fadeInAnim);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        final Intent i = new Intent(this, SignIn.class);

        Thread timer = new Thread(){
            public void run (){
                try{
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();

                }
            }
        };
        timer.start();
    }

}
