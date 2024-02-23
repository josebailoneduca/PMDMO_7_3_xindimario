package com.imagenprogramada.xindimario;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

public class Juego extends SurfaceView implements SurfaceHolder.Callback, SurfaceView.OnTouchListener {
    //holder de la surface
    private SurfaceHolder holder;

    //bucle de juego
    private BucleJuego bucle;

    //player de musica de la partida
    MediaPlayer mediaPlayer;


    Context context;
    int anchoPantalla;
    int altoPantalla;

    //X del fondo
    int xFondo=0;

    Fondo fondo;
    Mario mario;


    /*Controles */
    private final int IZQUIERDA = 0;
    private final int DERECHA = 1;
    private final int DISPARO = 2;
    private final int SALTO = 3;
    public final int VELOCIDAD_HORIZONTAL; //pixels per frame

    public final int SEGUNDOS_EN_RECORRER_PANTALLA_HORIZONTAL = 4;
    private ArrayList<Toque> toques = new ArrayList<Toque>();
    boolean hayToque = false;
    Control controles[] = new Control[4];

    //datos de la partida
    private int puntos = 0;
    private int nivel = 0;
    private int PUNTOS_CAMBIO_NIVEL = 2000;
        private static final String TAG = "JJBO";



    //enemigos
    Bitmap enemigo_tonto, enemigo_listo;
    public final int TOTAL_ENEMIGOS = 500; //Enemies to win the game
    private int enemigos_minuto = 50; //Enemies per minute
    private int frames_para_nuevo_enemigo = 0; //Remaining
    //frames to generate new enemy
    private int enemigos_muertos = 0; //Kill count
    private int enemigos_creados = 0;
    private ArrayList<Enemigo>  lista_enemigos=new ArrayList<Enemigo>();


    /* Disparos */
    private ArrayList<Disparo> lista_disparos=new
            ArrayList<Disparo>();
    Bitmap disparo;
    private int frames_para_nuevo_disparo = 0;
    //Between two consecutive shots, at least MAX_FRAMES_ENTRE_DISPARO frames must pass
    private final int MAX_FRAMES_ENTRE_DISPARO = BucleJuego.MAX_FPS / 4;
    //about 4 shots per second
    private boolean nuevo_disparo = false; //Is there a pending shot?


    //exposiones
    Bitmap explosion;
//    ArrayList<Explosion> lista_explosiones=new ArrayList<Explosion> ();


    //victoria derrota
    private boolean victoria = false, derrota = false;


    /**
     * Constructor
     *
     * @param context
     * @param anchoPantalla
     * @param altoPantalla
     */
    public Juego(Activity context, int anchoPantalla, int altoPantalla) {
        super(context);
        this.context = context;
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        holder = getHolder();
        holder.addCallback(this);
        crearElementos();
        VELOCIDAD_HORIZONTAL = anchoPantalla / SEGUNDOS_EN_RECORRER_PANTALLA_HORIZONTAL / BucleJuego.MAX_FPS;
    }
    private void crearElementos(){

        fondo=new Fondo(this,anchoPantalla, altoPantalla);
        float[]posmario={anchoPantalla/2,fondo.getPosY()+fondo.getH()*90/100};
        mario = new Mario(this,posmario, anchoPantalla,altoPantalla);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //inicialización onsurfaceCreated
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //listener de toques de pantalla
        setOnTouchListener(this);

        // To catch the SurfaceView's events
        getHolder().addCallback(this);


        // Make the view fosucable, so it can catch events
        setFocusable(true);



        //controles
        cargaControles();

        //cargar bitmaps enemigos
        cargaEnemigos();


        //cargar disparo
        disparo=BitmapFactory.decodeResource(getResources(),R.drawable.shot);
//
//        //cargar explosion
//        explosion =  BitmapFactory.decodeResource(getResources(),R.drawable.explosion);

        //comenzar musica
        iniciarMusicaJuego();

        // When the surface is created, we create the game loop
        // We create the game loop
        bucle = new BucleJuego(getHolder(), this);
        //Start the game loop
        bucle.start();

    }

    //carga los controles del juego
    public void cargaControles() {
        float aux;
        //left arrow
        controles[IZQUIERDA]=new Control(getContext(),0,
                altoPantalla /5*4);
        controles[IZQUIERDA].cargar( R.drawable.flecha_izda);
        controles[IZQUIERDA].nombre="IZQUIERDA";
        //right arrow
        controles[DERECHA]=new Control(getContext(),
                controles[0].ancho()+controles[0].coordenada_x+5,
                controles[0].coordenada_y);
        controles[DERECHA].cargar(R.drawable.flecha_dcha);
        controles[DERECHA].nombre="DERECHA";
        //shoot icon
        aux=5.0f/7.0f* anchoPantalla; //en los 5/7 del ancho
        controles[DISPARO]=new Control(getContext(),
                aux,controles[0].coordenada_y);
        controles[DISPARO].cargar(R.drawable.disparo);
        controles[DISPARO].nombre="DISPARO";

        controles[SALTO]=new Control(getContext(),
                aux+controles[0].ancho()+5,controles[0].coordenada_y);
        controles[SALTO].cargar(R.drawable.flecha_arriba);
        controles[SALTO].nombre="DISPARO";
    }


    //carga los bitmaps de los enemigos
    public void cargaEnemigos() {

        frames_para_nuevo_enemigo=bucle.MAX_FPS*60/enemigos_minuto;
        enemigo_tonto = BitmapFactory.decodeResource(
                getResources(), R.drawable.enemigo_tonto);
        enemigo_listo = BitmapFactory.decodeResource(
                getResources(), R.drawable.enemigo_listo);
    }

    /**
     * Crear un enemigo
     */
    public void crearNuevoEnemigo() {
        if(TOTAL_ENEMIGOS-enemigos_creados>0) {
            lista_enemigos.add(new
                    Enemigo(this, nivel));
            enemigos_creados++;
        }
    }


    /**
     * ACTUALIZAR
     * Actualiza el estado de diferentes parte del juego
     */
    public void actualizar() {
        //fondo
        if (controles[IZQUIERDA].pulsado) {
            if (mario.posicion[0]<=Mario.limite_pantalla)
                xFondo-=mario.desplazamiento;
        }

        if (controles[DERECHA].pulsado){
            if (mario.posicion[0]>=anchoPantalla-Mario.limite_pantalla)
                xFondo+=mario.desplazamiento;

        }
        if (xFondo<0)
            xFondo=0;
        if (xFondo>=fondo.getW()-anchoPantalla)
            xFondo=fondo.getW()-anchoPantalla;


        mario.actualizar(controles[IZQUIERDA].pulsado,controles[DERECHA].pulsado,controles[SALTO].pulsado,bucle.iteraciones);

        //ver creacion de enemigos
        if (frames_para_nuevo_enemigo == 0) {
            crearNuevoEnemigo();
            //nuevo ciclo de enemigos
            frames_para_nuevo_enemigo =
                    bucle.MAX_FPS * 60 / enemigos_minuto;
        }
        frames_para_nuevo_enemigo--;


        //actualizar posicion enemigos
        for(Enemigo e: lista_enemigos){
            e.actualizaCoordenadas();
        }


        //crear disparos
        if (!derrota) {
            if (controles[DISPARO].pulsado)
                nuevo_disparo = true;
            if (frames_para_nuevo_disparo == 0) {
                if (nuevo_disparo) {
                    creaDisparo();
                    nuevo_disparo = false;
                }
                //new shot cycle
                frames_para_nuevo_disparo =
                        MAX_FRAMES_ENTRE_DISPARO;
            }
            frames_para_nuevo_disparo--;
        }


        //actualizar disparos
        for(Iterator<Disparo> it_disparos = lista_disparos.iterator();
            it_disparos.hasNext();) {
            Disparo d=it_disparos.next();
            d.actualizaCoordenadas();
            if(d.fueraDePantalla()) {
                it_disparos.remove();
            }
        }

        //detectar colisiones
//        for(Iterator<Enemigo> it_enemigos= lista_enemigos.iterator();
//            it_enemigos.hasNext();) {
//            Enemigo e = it_enemigos.next();
//            for(Iterator<Disparo> it_disparos=lista_disparos.iterator();
//                it_disparos.hasNext();) {
//                Disparo d=it_disparos.next();
//                if (colision(e, d)) {
//                    /* Create an Explosion object */
//                    lista_explosiones.add(new Explosion(this,
//                            e.coordenada_x, e.coordenada_y));
//                    /* Remove both the shot and the enemy */
//                    try {
//                        it_enemigos.remove();
//                        it_disparos.remove();
//                    }
//                    catch(Exception ex){}
//                    enemigos_muertos++; //One step closer to victory!
//                    /*Points*/
//                    if(e.tipo_enemigo==e.ENEMIGO_INTELIGENTE)
//                        puntos +=50;
//                    else
//                        puntos +=10;
//                }
//            }
//        }

        //actualizar anim explosiontes
//        for(Iterator<Explosion> it_explosiones=lista_explosiones.iterator();
//            it_explosiones.hasNext();){
//            Explosion exp=it_explosiones.next();
//            exp.actualizarEstado();
//            if(exp.haTerminado()) it_explosiones.remove();
//        }

        //actualizar dificultad
//        if (nivel != puntos
//                / PUNTOS_CAMBIO_NIVEL) {
//            nivel = puntos /
//                    PUNTOS_CAMBIO_NIVEL;
//            enemigos_minuto += (20 * nivel);
//        }
//        if (!victoria && !derrota)
//            compruebaFinJuego();
    }


    /**
     * comprobacion de si el juego ha terminado
     */
    public void compruebaFinJuego() {
//        for(Enemigo e:lista_enemigos){
//            if(colisionNave(e)){
//                lista_explosiones.add(new Explosion(
//                        this,e.coordenada_x, e.coordenada_y));
//                derrota=true;
//                terminar();
//            }
//        }
//        if(!derrota)
//            if(enemigos_muertos==TOTAL_ENEMIGOS) {
//                victoria = true;
//                terminar();
//            }
    }
//
//    /**
//     * colisiones de la nave
//     *
//     * @param e
//     * @return
//     */
//    public boolean colisionNave(Enemigo e){
//        return Colision.hayColision(e.bitmap(),
//                (int)e.coordenada_x,(int)e.coordenada_y,
//                nave.getBitmap(),(int)nave.getX(),(int)nave.getY());
//    }


    //auxiliar detectar colision
//    public boolean colision(Enemigo e, Disparo d){
//        Bitmap enemigo=e.bitmap();
//        Bitmap disparo=this.disparo;
//        return Colision.hayColision(enemigo,(int) e.coordenada_x,
//                (int)e.coordenada_y,
//                disparo,(int)d.coordenada_x,(int)d.coordenada_y);
//    }


    //auxiliary method:
    public void creaDisparo() {
        lista_disparos.add(new Disparo(this,mario.getX(),mario.getY()));
    }

    /**
     * This methods renders the next frame of the game
     */
    public void renderizar(Canvas canvas) {
//        //limpiar fondo a negro
        canvas.drawColor(Color.BLACK);
//
//        //Display information helpful for development
        Paint p=new Paint();
        fondo.renderizar(canvas,p, xFondo);
        mario.renderizar(canvas,p);



//        if(!derrota)
//            nave.render(canvas);
        for (Control c :
                controles) {
            c.dibujar(canvas,p);
        }
//
//        //render enemies
        for(Enemigo e: lista_enemigos){
            e.dibujar(canvas,p);
        }


//        //render each shot
        for(Disparo d:lista_disparos){
            d.Dibujar(canvas,p);
        }
//
//        //render explosiones
//        for(Iterator<Explosion> it_explosiones=lista_explosiones.iterator(); it_explosiones.hasNext();) {
//            Explosion exp = it_explosiones.next();
//            exp.dibujar(canvas,p);
//        }

//        //renderizar puntos y nivel
//        p.setColor(Color.WHITE);
//        //Display the score
//        p.setTextSize(anchoPantalla /25);
//        //25 is the approx. number
//        // of letters that fit in a
//        // single line
//        canvas.drawText("PUNTOS " + puntos + " - Nivel "
//                + nivel, 50, 50, p);
//        canvas.drawText("Enemigos por matar "+
//                        (TOTAL_ENEMIGOS-enemigos_muertos),
//                50, 100, p);


//        //renderizar mensaje de victoria si toca
//        if(victoria){
//            p.setAlpha(0);
//            p.setColor(Color.WHITE);
//            p.setTextSize(anchoPantalla /10);
//            canvas.drawText("VICTORIA!!", 50, altoPantalla /2-100, p);
//            p.setTextSize(anchoPantalla /20);
//            canvas.drawText("Las tropas enemigas han sido derrotadas", 50,
//                    altoPantalla /2+100, p);
//        }

//        //renderizar mensaje de derrota si toca
//        if(derrota) {
//            p.setAlpha(0);
//            p.setColor(Color.WHITE);
//            p.setTextSize(anchoPantalla /10);
//            canvas.drawText("DERROTA!!", 50, altoPantalla /2-100, p);
//            p.setTextSize(anchoPantalla /20);
//            canvas.drawText("La raza humana está condenada!!!!", 50,
//                    altoPantalla /2+100, p);
//        }
    }


    //terminar la partida. Deja el bucle de juego funcionando por 5 segundos
    //y luego realmente termina la actividad del juego
    private void terminar() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ActividadJuego) context).terminar();
            }
        }, 5000);
    }


    //limpieza cuando se destruye la surface
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Juego destruido!");
        // Close the thread and wait for it to end
        boolean retry = true;
        while (retry) {
            try {
                fin();
                bucle.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }


    /**
     * escucha de los eventos ontouch
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int index = event.getActionIndex();
                int x = (int) event.getX(index);
                int y = (int) event.getY(index);
                hayToque = true;
                synchronized (this) {
                    toques.add(index, new Toque(index, x, y));
                }
                //check if any of the controls was pressed
                for (int i = 0; i < controles.length; i++)
                    controles[i].compruebaPulsado(x, y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                synchronized (this) {
                    index = event.getActionIndex();
                    toques.remove(index);
                }
                //check if any of the controls were released
                for (int i = 0; i < controles.length; i++)
                    controles[i].compruebaSoltado(toques);
                break;
            case MotionEvent.ACTION_UP:
                synchronized (this) {
                    toques.clear();
                }
                hayToque = false;
                //check if any of the controls were released
                for (int i = 0; i < controles.length; i++)
                    controles[i].compruebaSoltado(toques);
                break;
        }
        return true;
    }


    //inicia la musica de la partida
    private void iniciarMusicaJuego() {
        mediaPlayer = MediaPlayer.create(context, R.raw.musicajuego);
        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                    }
                });
        mediaPlayer.start();
    }

    //hace limpieza de todos los elementos del juego
    public void fin() {
        bucle.fin();
        mediaPlayer.release();
        fondo.fin();
//
//        nave.fin();
//        enemigo_listo.recycle();
//        enemigo_tonto.recycle();
//        disparo.recycle();
    }
}