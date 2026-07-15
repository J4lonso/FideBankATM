package fidebank.modelo;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

/**
 * Comprobante generado a partir de una transaccion ejecutada.
 * Historia de usuario relacionada: HU-07.
 */
public class Comprobante implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private int idComprobante;
    private Transaccion transaccion;

    public Comprobante(Transaccion transaccion) {
        this.idComprobante = transaccion.getIdTransaccion();
        this.transaccion = transaccion;
    }

    /** Construye el texto del comprobante listo para mostrar/imprimir. */
    public String generar() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== FideBank ==========\n");
        sb.append("Comprobante No: ").append(idComprobante).append("\n");
        sb.append("Fecha: ").append(transaccion.getFecha().format(FORMATO)).append("\n");
        sb.append("Cuenta: ").append(transaccion.getNumeroCuenta()).append("\n");
        sb.append("Transaccion: ").append(transaccion.getDescripcion()).append("\n");
        sb.append("Monto: CRC ").append(String.format("%,.2f", transaccion.getMonto())).append("\n");
        sb.append("===============================");
        return sb.toString();
    }

    /** HU-07: simula la impresion fisica del comprobante (delegado a un hilo impresor). */
    public void imprimir() {
        fidebank.servicio.Impresora.getInstancia().encolar(this);
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    @Override
    public String toString() {
        return generar();
    }
}
