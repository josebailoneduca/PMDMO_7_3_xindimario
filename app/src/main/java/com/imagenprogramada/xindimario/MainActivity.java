package com.imagenprogramada.xindimario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciaMusicaIntro();
        findViewById(R.id.btnEmpezarPartida).setOnClickListener(view -> {
            mediaPlayer.stop();mediaPlayer.reset();
            this.startActivity(new
                    Intent(this, ActividadJuego.class));
        });
    }

    public void iniciaMusicaIntro(){
        mediaPlayer = MediaPlayer.create(this, R.raw.musicamenu);
        mediaPlayer.setVolume(1f,1f);
        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                    }
                });
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}