package fidebank.gui;

import java.awt.Color;
import java.awt.Font;

/** Constantes visuales compartidas por todas las pantallas del cajero (paleta FideBank). */
public final class Estilos {
    public static final Color NAVY = new Color(0x1F, 0x2D, 0x5C);
    public static final Color FONDO = new Color(0xF4, 0xF6, 0xFB);
    public static final Color BOTON_CLARO = new Color(0xDF, 0xE6, 0xF5);
    public static final Color BOTON_VERDE = new Color(0xC8, 0xE6, 0xC9);
    public static final Color BOTON_ROJO = new Color(0xF5, 0xD0, 0xD0);
    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 13);

    private Estilos() { }
}
