package com.cwenham.dodgingduedates;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private GameView gameView;
    private Handler handler = new Handler();
    private final static long TIMER_INTERVAL = 30;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        gameView = new GameView(this);
        setContentView(gameView);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        gameView.invalidate();
                    }
                });
            }
        }, 0, TIMER_INTERVAL);

        //Display Ad when a user first launches the app.
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                //Ad finished loading.
                interstitialAd.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(gameView.getGameState() == 1)
            gameView.soundPlayer.playBGM();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.soundPlayer.pauseBGM();
    }
}
