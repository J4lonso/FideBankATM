# FideBank ATM - Avance 2

Implementacion en Java (Swing, sin frameworks) del sistema de cajeros automaticos de FideBank,
correspondiente al Proyecto 5 del curso Programacion Cliente-Servidor Concurrente.

## Como abrir el proyecto en NetBeans

1. Abra NetBeans y seleccione **Archivo > Nuevo Proyecto > Java con Ant > Aplicacion Java**.
2. Nombre el proyecto (por ejemplo `FideBankATM`) y **desmarque** "Crear clase principal".
3. En el explorador de archivos del sistema operativo, copie todo el contenido de la carpeta
   `src/fidebank` de este entregable dentro de la carpeta `src` del proyecto que NetBeans creo.
4. En NetBeans, clic derecho sobre `Main.java` (paquete `fidebank`) y seleccione **Ejecutar archivo**.

Tambien puede compilarse por linea de comandos (requiere JDK 8+):

```
javac -d build $(find src -name "*.java")
java -cp build fidebank.Main
```

## Cuentas de demostracion

Al iniciar por primera vez se crean dos cuentas de prueba:

| Cuenta  | PIN  | Cliente          |
|---------|------|------------------|
| 100000  | 1234 | Ana Rodriguez    |
| 100001  | 5678 | Luis Fernandez   |

## Historias de usuario implementadas en este avance

- HU-01: Apertura de cuenta (`AperturaCuentaDialog`, boton "Abrir cuenta nueva" en el login).
- HU-02: Ingreso con numero de cuenta y PIN (`LoginFrame`).
- HU-03: Consulta de saldo disponible (`MenuPrincipalFrame`).
- HU-04: Retiro de fondos (`RetiroDialog`, excepcion `SaldoInsuficienteException`).
- HU-05: Deposito de dinero (`DepositoDialog`).
- HU-06: Transferencia entre cuentas (`TransferenciaDialog`, excepcion `CuentaNoEncontradaException`).
- HU-07: Impresion de comprobante (`ComprobanteDialog`, hilo `Impresora` que escribe `comprobantes.log`).
- HU-08: Historial de transacciones (`HistorialDialog`).
- HU-09: Registro de cada transaccion con fecha/hora/monto/cuenta (clase `Transaccion` y sus subclases).
- HU-10: Bloqueo de cuenta tras 3 intentos de PIN incorrectos (`Cuenta.validarPin`,
  excepcion `CuentaBloqueadaException`).

Se implementaron 10 de las 10 historias de usuario definidas en el Avance 1 (100%, supera el 40%
minimo solicitado en la rubrica).

## Conceptos de POO evidenciados

- **Clases y objetos**: Cliente, Cuenta, Transaccion, Comprobante, Banco, etc.
- **Herencia**: `Retiro`, `Deposito` y `Transferencia` heredan de la clase abstracta `Transaccion`.
- **Polimorfismo**: cada subclase sobrescribe `ejecutar()` y `getDescripcion()`; el historial
  invoca `toString()` sobre la coleccion de `Transaccion` sin conocer el tipo concreto.
- **Excepciones**: `PinInvalidoException`, `CuentaBloqueadaException`,
  `SaldoInsuficienteException`, `CuentaNoEncontradaException` (todas checked, con manejo en la GUI).
- **Colecciones**: `Map<String, Cliente>`, `Map<String, Cuenta>` en `Banco`; `List<Transaccion>`
  como historial de cada `Cuenta`.
- **Serializacion (opcional)**: `Persistencia` guarda/carga el estado del banco en `banco.dat`
  mediante `ObjectOutputStream` / `ObjectInputStream`.
- **Multihilos (opcional)**: `Impresora` corre en un hilo dedicado que consume una
  `BlockingQueue<Comprobante>` para "imprimir" sin bloquear la interfaz grafica.
- **Interfaz grafica**: Swing puro (`JFrame`, `JDialog`), sin frameworks externos.

## Pendiente para el Avance 3 / entrega final

- Validaciones adicionales de formato (cedula, correo).
- Persistencia automatica al cerrar sesion / al crear cuentas.
- Empaquetado final y publicacion del historial de commits en GitHub (rubro 3 de este avance,
  no cubierto por el codigo en si).
