package com.imagenprogramada.xindimario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

public class ActividadJuego extends AppCompatActivity {

    //referencia al juego
    private Juego j;
    //ancho de la pantalla
    int anchoPantalla;

    //alto de la pantalla
    int altoPantalla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //calcular tamaño de pantalla
        calculaTamañoPantalla();

        //crear el juego
        j = new Juego(this, anchoPantalla, altoPantalla);
        //esconder botones y barras del sistema
        hideSystemUI();

        setContentView(j);

    }

    /**
     * Esconde barra y botones del sistema
     */
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            //Kitkat or later versions
            j.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            //When a physical button is pressed (i.e. volume), visibility
            //changes and we need to hide it again
            j.setOnSystemUiVisibilityChangeListener(new
                                                            View.OnSystemUiVisibilityChangeListener() {
                                                                @Override
                                                                public void onSystemUiVisibilityChange(int visibility) {
                                                                    hideSystemUI();
                                                                }
                                                            });
        }
    }

    /**
     * Calcular el tamaño de pantalla
     */
    public void calculaTamañoPantalla() {
        if (Build.VERSION.SDK_INT > 13) {
            Display display = this.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            anchoPantalla = size.x;
            altoPantalla = size.y;
        } else {
            Display display = this.getWindowManager().getDefaultDisplay();
            anchoPantalla = display.getWidth(); // deprecated
            altoPantalla = display.getHeight(); // deprecated
        }
        Log.i(Juego.class.getSimpleName(), "alto:" + altoPantalla + "," + "ancho:" +
                anchoPantalla);
    }


    //terminar la actividad del juego
    public void terminar() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }
}