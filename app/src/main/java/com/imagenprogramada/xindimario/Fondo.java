package com.imagenprogramada.xindimario;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Fondo {
    Bitmap imagen;
    Juego juego;
    int anchoPantalla;
    int altoPantalla;
    int posY;
    public Fondo(Juego juego, int anchoPantalla, int altoPantalla) {
        this.juego=juego;
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla =altoPantalla;
        imagen= BitmapFactory.decodeResource(juego.getResources(),R.drawable.mapamario);
        posY=(altoPantalla-imagen.getHeight())/2;
    }


    public void renderizar(Canvas canvas, Paint p, int posx){
        Rect origen = new Rect(posx, 0, anchoPantalla+100+posx, imagen.getHeight());
        //Calculating where to render the sprite on the screen
        Rect destino = new Rect(0,posY,
                anchoPantalla+100,
                posY+imagen.getHeight());
        canvas.drawBitmap(imagen, origen, destino, p);
    }
    public int getPosY(){
        return posY;
    }

    public int getH() {
        return imagen.getHeight();
    }
    public int getW(){return imagen.getWidth();}

    public void fin() {
        imagen.recycle();
    }
}
