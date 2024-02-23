package com.imagenprogramada.xindimario;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;

public class Mario {
    float[] posicion = new float[2];
    float[] velocidad = new float[2];
    float[] gravedad = new float[2];
    Bitmap imagen;
    int mario_w;
    int mario_h;

    int estado_mario=0;
    final int X=0;
    final int Y=1;
    int anchoPantalla;
    int altoPantalla;
    float posicionSuelo=0;

    public final float tiempoCrucePantalla=5;
    public final float delta = 1f/BucleJuego.MAX_FPS;
    public int desplazamiento;
    boolean salto_iniciado=false;

    MediaPlayer mpSalto;
    public static int limite_pantalla = 600;

    public Mario(Juego juego,float[] posicion, int anchoPantalla, int altoPantalla) {
        this.posicion = posicion;
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla =altoPantalla;
        desplazamiento=(int)(anchoPantalla/tiempoCrucePantalla*delta);
        imagen= BitmapFactory.decodeResource(juego.getResources(),R.drawable.mario);
        mario_w=imagen.getWidth();
        mario_h=imagen.getHeight();
        posicion[Y]=posicion[Y]-imagen.getHeight()*2/3;
        posicionSuelo=posicion[Y];
        mpSalto=MediaPlayer.create(juego.context, R.raw.salto);
    }

    public void actualizar(boolean izq, boolean der,boolean salto, int  iteracion){
        if (izq)
            velocidad[X]=-desplazamiento;
        else if (der) {
            velocidad[X] = desplazamiento;
        }
        else {
            velocidad[X] = 0;

        }
        posicion[X]=posicion[X]+(velocidad[X]);

        if (posicion[X]<limite_pantalla)
            posicion[X]=limite_pantalla;
        if (posicion[X]>anchoPantalla-limite_pantalla)
            posicion[X]=anchoPantalla-limite_pantalla;

        if (der || izq){
            if (iteracion%3==0)
                estado_mario++;
            if (estado_mario>3)
                estado_mario=1;
        }else
            estado_mario=0;


        //salto


        if (salto && !salto_iniciado) {
            velocidad[Y] = -anchoPantalla / tiempoCrucePantalla * 2;
            gravedad[Y] = -velocidad[Y] * 2;
            salto_iniciado=true;
            mpSalto.start();
        }

        if (salto_iniciado){
            estado_mario=5;
            posicion[Y]=posicion[Y]+velocidad[Y]*delta;
            velocidad[Y]=velocidad[Y]+gravedad[Y]*delta;
        }

        if (posicion[Y]>posicionSuelo){
            posicion[Y]=posicionSuelo;
            velocidad[Y]=0;
            salto_iniciado=false;
        }

    }


    public void renderizar(Canvas canvas, Paint p){
        Rect fotograma = new Rect (
                estado_mario*(mario_w/21)+2,
                2,
                estado_mario*(mario_w/21)+mario_w/21,
                mario_h*2/3
        );

        Rect pos= new Rect(
                (int) posicion[X],
                (int) posicion[Y],
                (int) (posicion[X]+mario_w/21),
                (int) (posicion[Y]+mario_h*2/3));

        canvas.drawBitmap(imagen,fotograma,pos,null);

    }

    public float getWidth() {
        return imagen.getWidth();
    }

    public float getX() {
        return posicion[X];
    }

    public float getY() {
        return posicion[Y];
    }

    public int getHeight() {
        return imagen.getHeight();
    }
}
