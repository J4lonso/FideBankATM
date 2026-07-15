package fidebank.servicio;

import fidebank.modelo.Comprobante;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Simula la impresora fisica del cajero automatico (HU-07).
 * Usa un hilo dedicado (multihilos, opcional) que consume una cola de comprobantes
 * y los "imprime" (escribe en comprobantes.log) sin bloquear la interfaz grafica.
 */
public class Impresora {

    private static Impresora instancia;

    private final BlockingQueue<Comprobante> cola;
    private final Thread hiloImpresion;
    private volatile boolean activa;

    private Impresora() {
        cola = new LinkedBlockingQueue<>();
        activa = true;
        hiloImpresion = new Thread(this::procesarCola, "Hilo-Impresora-FideBank");
        hiloImpresion.setDaemon(true);
        hiloImpresion.start();
    }

    public static synchronized Impresora getInstancia() {
        if (instancia == null) {
            instancia = new Impresora();
        }
        return instancia;
    }

    /** Encola un comprobante para ser impreso de forma asincrona. */
    public void encolar(Comprobante comprobante) {
        cola.offer(comprobante);
    }

    private void procesarCola() {
        while (activa) {
            try {
                Comprobante comprobante = cola.take();
                imprimirEnArchivo(comprobante);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized void imprimirEnArchivo(Comprobante comprobante) {
        try (PrintWriter salida = new PrintWriter(new FileWriter("comprobantes.log", true))) {
            salida.println(comprobante.generar());
            salida.println();
        } catch (IOException e) {
            System.err.println("No se pudo imprimir el comprobante: " + e.getMessage());
        }
    }

    public void detener() {
        activa = false;
        hiloImpresion.interrupt();
    }
}
