package com.imagenprogramada.xindimario;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * Clase queu se encarga de dibujar y actualizar un enemigo
 */
public class Enemigo {
    public final int ENEMIGO_INTELIGENTE=0; //clever enemy, follows the spaceship
    public final int ENEMIGO_TONTO=1; //not-so-clever enemy, moves randomly
    public final float VELOCIDAD_ENEMIGO_INTELIGENTE=5;
    public final float VELOCIDAD_ENEMIGO_TONTO=2;
    public float velocidad;
    public float coordenada_x, coordenada_y; //screen coordinates to draw the enemy
    public int tipo_enemigo; //what kind of enemy this is
    public float direccion_vertical=1; //initial direction: downward
    public float direccion_horizontal=1; //inicial direction: rightward

    private int nivel;
    private Juego juego;


    /**
     * constructor
     * @param j Referencia al juego
     * @param n Nivel
     */
    public Enemigo(Juego j, int n){
        juego=j;
        nivel =n;
        //initial speed=20 seconds to cross the screen * intelligenece factor and level
        float VELOCIDAD_ENEMIGO=j.altoPantalla /20f/BucleJuego.MAX_FPS;
        //clever enemy probability: 20%
        if(Math.random()>0.20) {
            tipo_enemigo = ENEMIGO_TONTO;
            velocidad = (VELOCIDAD_ENEMIGO_TONTO+ nivel)*VELOCIDAD_ENEMIGO;
            Log.i(Juego.class.getSimpleName(),"Velocidad de los enemigos tontos "+velocidad);
        }
        else {
            tipo_enemigo = ENEMIGO_INTELIGENTE;
            velocidad = (VELOCIDAD_ENEMIGO_INTELIGENTE+ nivel)*VELOCIDAD_ENEMIGO;
            Log.i(Juego.class.getSimpleName(),"Velocidad de los enemigos inteligentes "+velocidad);
        }
        //random movement direction for not-so-clever enemies
        if(Math.random()>0.5)
            direccion_horizontal=1; //rightward
        else
            direccion_horizontal=-1; //leftward
        if(Math.random()>0.5)
            direccion_vertical=1; //downward
        else
            direccion_vertical=-1; //upward
        calculaCoordenadas();
    }
    public void calculaCoordenadas(){
        double x,y; //random
        /* enemy's starting position */
        //between 0 and 0.125 pops up on the left (x=0, y=randowm(1/5) screen)
        //between 0.125 and 0.25 pops up on the right (x=AnchoPantalla-anchobitmap,y=Math.random(1/5);
        //>0.25 pops up at the top (y=0, x=random between 0 y AnchoPantalla-AnchoBitmap)
        x=Math.random();
        if(x<=0.25){
            //25% probability of the enemy popping up from one side of the screen
            if(x<0.125) //pops up on the left
                coordenada_x = 0;
            else
                coordenada_x = juego.anchoPantalla -juego.enemigo_tonto.getWidth();
            coordenada_y = (int) (Math.random()*juego.altoPantalla /5);
        }else{
            coordenada_x=(int)(Math.random()* (juego.anchoPantalla -juego.enemigo_tonto.getWidth()));
            coordenada_y=0;
        }
    }
    //Updates enemy coordinates taking into accound spaceship coordinates
    public void actualizaCoordenadas(){
        if(tipo_enemigo==ENEMIGO_INTELIGENTE) {
            if (juego.mario.getX() > coordenada_x)
                coordenada_x+=velocidad;
            else if (juego.mario.getX() < coordenada_x)
                coordenada_x-=velocidad;
            if(Math.abs(coordenada_x-juego.mario.getX())<velocidad)
                coordenada_x=juego.mario.getX(); //if it's close enough it takes the same position
            if( coordenada_y>=juego.altoPantalla -juego.enemigo_listo.getHeight()
                    && direccion_vertical==1)
                direccion_vertical=-1;
            if(coordenada_y<=0 && direccion_vertical ==-1)
                direccion_vertical=1;
            coordenada_y+=direccion_vertical*velocidad;
        }
        else{
            //dumb enemies ignore spaceship position, just moving around the screen
            coordenada_x+=direccion_horizontal*velocidad;
            coordenada_y+=direccion_vertical*velocidad;
            //Direction change when reaching the screen limits
            if(coordenada_x<=0 && direccion_horizontal==-1)
                direccion_horizontal=1;
            if(coordenada_x>juego.anchoPantalla -juego.enemigo_tonto.getWidth() &&
                    direccion_horizontal==1)
                direccion_horizontal=-1;
            if(coordenada_y>=juego.altoPantalla && direccion_vertical ==1)
                direccion_vertical=-1;
            if(coordenada_y<=0 && direccion_vertical==-1)
                direccion_vertical=1;
        }
    }
    public void dibujar(Canvas c, Paint p){
        if(tipo_enemigo==ENEMIGO_TONTO)
            c.drawBitmap(juego.enemigo_tonto,coordenada_x,coordenada_y,p);
        else
            c.drawBitmap(juego.enemigo_listo,coordenada_x,coordenada_y,p);
    }
    public int ancho(){
        if(tipo_enemigo==ENEMIGO_TONTO)
            return juego.enemigo_tonto.getWidth();
        else
            return juego.enemigo_listo.getWidth();
    }
    public int alto(){
        if(tipo_enemigo==ENEMIGO_TONTO)
            return juego.enemigo_tonto.getHeight();
        else
            return juego.enemigo_listo.getHeight();
    }
    public Bitmap bitmap(){
        if(tipo_enemigo==ENEMIGO_TONTO)
            return juego.enemigo_tonto;
        else
            return juego.enemigo_listo;
    }
}
