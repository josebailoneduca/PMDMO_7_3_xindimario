package com.imagenprogramada.xindimario;


import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class BucleJuego extends Thread {

    // Frames per second we're targeting
    public final static int 	MAX_FPS = 30;
    // Maximum number of frames to skip if needed
    private final static int	MAX_FRAMES_SALTADOS = 5;
    // Frame period
    private final static int	TIEMPO_FRAME = 1000 / MAX_FPS;

    private Juego juego;

    public int iteraciones;
    public long tiempoTotal;

    public boolean JuegoEnEjecucion=true;
    private static final String TAG = Juego.class.getSimpleName();
    private SurfaceHolder surfaceHolder;

    public int maxX,maxY; //The current screen's height and length

    BucleJuego(SurfaceHolder sh, Juego s){
        juego=s;
        surfaceHolder=sh;

        //Dimensions of the Canvas
        Canvas c=sh.lockCanvas();
        maxX = c.getWidth();
        maxY = c.getHeight();
        sh.unlockCanvasAndPost(c);
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Comienza el game loop");


        long tiempoComienzo;		// Time since the current cycle started
        long tiempoDiferencia;		// Time the current circle took
        int tiempoDormir;		// Time the current cycle must sleep (<0 if it took too long)
        int framesASaltar;	// Number of frames skipped

        tiempoDormir = 0;

        while (JuegoEnEjecucion) {
            canvas = null;
            // Lock the canvas so that no other thread writes on it
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {

                    tiempoComienzo = System.currentTimeMillis();
                    framesASaltar = 0;	// Reset the number of frames skipped
                    // Update the game's current state
                    juego.actualizar();
                    // Render the current image
                    if(canvas!=null)
                        juego.renderizar(canvas);
                    iteraciones++;
                    // How long did this cycle take?
                    tiempoDiferencia = System.currentTimeMillis() - tiempoComienzo;

                    // Calculate the time to sleep until the next iteration
                    tiempoDormir = (int)(TIEMPO_FRAME - tiempoDiferencia);

                    tiempoTotal+=tiempoDiferencia+tiempoDormir;

                    if (tiempoDormir > 0) {
                        // if sleepTime > 0 we're doing good
                        try {
                            // Put the thread to sleep (saving some battery)
                            Thread.sleep(tiempoDormir);
                        } catch (InterruptedException e) {}
                    }

                    while (tiempoDormir < 0 && framesASaltar < MAX_FRAMES_SALTADOS) {
                        // We took to long in this cycle, so we'll skip rendering one or more
                        juego.actualizar(); // Just update the game's state
                        tiempoDormir += TIEMPO_FRAME;	// Update the time to sleep
                        framesASaltar++; // Add 1 to the counter of skipped frames
                    }


                }
            } finally {
                // unlock the canvas
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            //Log.d(TAG, "Nueva iteración!");
        }
    }


    //termina el bucle de juego
    public void fin() {
        JuegoEnEjecucion = false;
    }
}
