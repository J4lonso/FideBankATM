package fidebank.red;

import java.io.Serializable;

/**
 * Operaciones que el cajero (cliente) puede solicitar al servidor FideBank.
 * Forma parte del protocolo de comunicacion cliente-servidor por sockets.
 */
public enum TipoOperacion implements Serializable {
    AUTENTICAR,
    ABRIR_CUENTA,
    CONSULTAR_SALDO,
    RETIRAR,
    DEPOSITAR,
    TRANSFERIR,
    HISTORIAL
}
