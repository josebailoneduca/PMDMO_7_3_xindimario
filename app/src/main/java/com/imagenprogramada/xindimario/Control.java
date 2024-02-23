package com.imagenprogramada.xindimario;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Clse de control en pantalla
 */
public class Control {
    public boolean pulsado=false; //Has the control been pressed?
    public float coordenada_x, coordenada_y; //screen coordinates
    //where the control is rendered
    private Bitmap imagen; //the control’s picture
    private Context mContexto;
    public String nombre;
    public Control(Context c, float x, float y){
        coordenada_x=x;
        coordenada_y=y;
        mContexto=c;
    }
    //load the control's bitmap
    public void cargar(int recurso){
        imagen= BitmapFactory.decodeResource(
                mContexto.getResources(), recurso);
    }
    //Render the control on the canvas, using a Paint object
    public void dibujar(Canvas c, Paint p){
        c.drawBitmap(imagen,coordenada_x,coordenada_y,p);
    }
    //Check if the control has been pressed
    public void compruebaPulsado(int x, int y){
        if(x>coordenada_x && x<coordenada_x+ ancho()
                && y>coordenada_y && y<coordenada_y+ alto()){
            pulsado=true;
        }
    }
    public void compruebaSoltado(ArrayList<Toque> lista){
        boolean aux=false;
        for(Toque t:lista){
            if(t.x>coordenada_x && t.x<coordenada_x+ ancho()
                    && t.y>coordenada_y && t.y<coordenada_y+ alto()) {
                aux = true;
            }
        }
        if(!aux){
            pulsado=false;
        }
    }
    public int ancho(){
        return imagen.getWidth();
    }
    public int alto(){
        return imagen.getHeight();
    }
}
