package com.imagenprogramada.xindimario;

public class Toque {
    public int x; //x coordinate of this touch
    public int y; //y coordinate of this touch
    public int index; //pointer’s index
    Toque(int mIndex, int mX, int mY){
        index=mIndex;
        x=mX;
        y=mY;
    }
}
