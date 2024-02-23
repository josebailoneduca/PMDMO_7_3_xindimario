package com.imagenprogramada.xindimario;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Clase de los disparos
 */
public class Disparo {
    public float coordenada_x, coordenada_y; //shot coordinates
    private Juego juego;
    private float velocidad;
    private MediaPlayer mediaPlayer; //to play the shot's sound
    private final float MAX_SEGUNDOS_EN_CRUZAR_PANTALLA=3;
    /*Constructor with original coordinates and shot number*/
    public Disparo(Juego j, float x, float y) {
        juego = j;
        coordenada_x = x;
        coordenada_y = y+j.mario.getHeight()/4;
        velocidad = j.anchoPantalla / MAX_SEGUNDOS_EN_CRUZAR_PANTALLA / BucleJuego.MAX_FPS;
        //adapt speed to the screen size
        Log.i(Juego.class.getSimpleName(), "Velocidad de disparo: " + velocidad);

        mediaPlayer=MediaPlayer.create(j.getContext(),
                R.raw.disparo);//replace for Explosion class
        mediaPlayer.setOnCompletionListener(new
                                                    MediaPlayer.OnCompletionListener() {
                                                        @Override
                                                        public void onCompletion(MediaPlayer mp) {
                                                            mp.release();
                                                        }
                                                    });
        mediaPlayer.start();
    }

    //just update the Y coordinate
    public void actualizaCoordenadas(){
        coordenada_x+=velocidad;
    }
    public void Dibujar(Canvas c, Paint p) {
        c.drawBitmap(juego.disparo, coordenada_x, coordenada_y, p);
    }
    public int ancho(){
        return juego.disparo.getWidth();
    }
    public int alto(){
        return juego.disparo.getHeight();
    }
    public boolean fueraDePantalla() {
        return coordenada_x > juego.anchoPantalla;
    }
}