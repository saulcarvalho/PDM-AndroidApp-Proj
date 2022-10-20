package ipleiria.pdm.maintenanceapppdm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;

/**
 * Classe do tipo atividade que contém o ecrã de animação de entrada na aplicação
 */
@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity implements Config {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Método usada para cobrir o ecrã inteiro
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        // Roda a figura na splash screen
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(rotateTime);
        rotate.setInterpolator(new LinearInterpolator());
        ImageView image= findViewById(R.id.imageViewSplashLogo);
        image.startAnimation(rotate);

        // Cria um media player com o som de login
        MediaPlayer mp = MediaPlayer.create(this, R.raw.login_sound);
        if (!mp.isPlaying()) {
            final float volume = (float) (1 - (Math.log(maxVolume - soundVolumeSplashScreen)
                    / Math.log(maxVolume )));
            mp.setVolume(volume, volume); // define o volume máximo de som
            mp.start();

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 250};  // vibra durante 250ms
            v.vibrate(pattern, -1); // -1 significa que só vibra 1 vez

            // liberta o media player quando o som termina
            mp.setOnCompletionListener(MediaPlayer::release);
        }

        // Quando a splashscreen dá timeout, o intent coloca a main activity como principal
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreenActivity.this,
                    MainActivity.class);
            startActivity(i); // invoca MainActivity
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, splashTimeout);
    }
}