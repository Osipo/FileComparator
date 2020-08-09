package ru.osipov.deskapps.vocabularies;
import java.awt.Color;
public enum ColorStyle {
    BLACK(Color.BLACK),MAGENTA(Color.MAGENTA),BLUE(Color.BLUE),CYAN(Color.CYAN),RED(Color.RED),GREEN(Color.GREEN),GRAY(Color.GRAY),
    DARK_GRAY(Color.DARK_GRAY),BROWN(102,51,0),ORANGE(Color.ORANGE),PINK(Color.PINK),
    LIGHT_GRAY(Color.LIGHT_GRAY),YELLOW(Color.YELLOW),WHITE(Color.WHITE),GOLD(255,204,51),PURPLE(102,0,153),
    DARK_BLUE(0,0,204),VERY_DARK_BLUE(0,0,153),MIDNIGHT_BLUE(0,0,41)
    ;
    private Color c;

    ColorStyle(Color c){
        this.c = c;
    }

    ColorStyle(int r,int g,int b){
        this.c = new Color(r,g,b);
    }

    public Color getColor() {
        return c;
    }
}
