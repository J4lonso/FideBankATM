# FideBank ATM - Presentacion Final

Implementacion en Java (sin frameworks) del sistema de cajeros automaticos de FideBank,
correspondiente al Proyecto 5 del curso Programacion Cliente-Servidor Concurrente.

La aplicacion tiene ahora dos programas separados que se comunican por red:

- **Servidor** (`fidebank.red.ServidorFideBank`): concentra la logica de negocio (`Banco`),
  persiste en **MySQL** y atiende **varios cajeros a la vez**, cada uno en su propio hilo.
- **Cliente** (`fidebank.Main`): la interfaz grafica del cajero automatico (Swing). Ya no
  contiene logica de negocio; solo envia peticiones al servidor por **sockets TCP** y
  muestra la respuesta.

## Requisitos

- JDK 8 o superior.
- MySQL Server corriendo en `localhost:3306` (o ajuste `ConexionMySQL.java`).
- Descargar el conector oficial **MySQL Connector/J** (`mysql-connector-j-x.x.x.jar`) desde
  el sitio de MySQL y agregarlo al classpath al compilar/ejecutar (no se incluye en este
  repositorio por su tamano).

## Como correrlo

1. Cree la base de datos vacia (opcional, el codigo la crea sola si el usuario de MySQL
   tiene permisos): `CREATE DATABASE fidebank;`
2. Edite `src/fidebank/persistencia/ConexionMySQL.java` con su usuario/contrasena de MySQL.
3. Compile todo el proyecto:
   ```
   javac -cp mysql-connector-j-8.x.x.jar -d build $(find src -name "*.java")
   ```
4. Inicie el **servidor** (dejelo corriendo en una terminal):
   ```
   java -cp "build;mysql-connector-j-8.x.x.jar" fidebank.red.ServidorFideBank
   ```
   (en Linux/Mac use `:` en vez de `;` como separador de classpath)
5. En otra terminal (o varias, para simular varios cajeros), inicie el **cliente**:
   ```
   java -cp build fidebank.Main
   ```

La primera vez que el servidor arranca sin datos en MySQL, siembra dos cuentas de
demostracion:

| Cuenta  | PIN  | Cliente          |
|---------|------|------------------|
| 100000  | 1234 | Ana Rodriguez    |
| 100001  | 5678 | Luis Fernandez   |

## Tambien se puede abrir en NetBeans

Puede crear dos proyectos NetBeans (uno para el servidor, otro para el cliente) apuntando
ambos al mismo `src`, o un unico proyecto y ejecutar la clase principal que necesite en
cada momento (`fidebank.red.ServidorFideBank` o `fidebank.Main`) con "Ejecutar archivo".
Agregue el jar de MySQL Connector/J en Propiedades del proyecto > Bibliotecas.

## Arquitectura y protocolo de red

El cliente y el servidor intercambian objetos serializables (`fidebank.red.Peticion` /
`fidebank.red.Respuesta`) a traves de `ObjectOutputStream`/`ObjectInputStream` sobre un
`Socket` TCP. El servidor usa un `ExecutorService` (pool de hilos) y atiende cada conexion
entrante en un hilo independiente (`ManejadorCliente`), lo que permite que varios cajeros
operen al mismo tiempo sin bloquearse entre si.

```
Cajero 1 (Swing) --\
Cajero 2 (Swing) ---> Socket TCP :5050 --> ServidorFideBank (hilo por cliente) --> Banco --> MySQL
Cajero N (Swing) --/
```

## Historias de usuario implementadas

Las 10 historias de usuario definidas en el Avance 1 siguen cubiertas, ahora operando en
red contra el servidor: HU-01 (apertura de cuenta), HU-02 (ingreso con PIN), HU-03 (saldo),
HU-04 (retiro), HU-05 (deposito), HU-06 (transferencia), HU-07 (comprobante impreso
localmente en el cajero), HU-08 (historial), HU-09 (registro de cada transaccion) y HU-10
(bloqueo tras 3 PIN incorrectos).

## Conceptos evidenciados en esta entrega final

- **Redes**: arquitectura cliente-servidor real por sockets TCP (`fidebank.red`), con un
  protocolo propio de peticion/respuesta serializado.
- **Concurrencia**: el servidor atiende cada cajero conectado en un hilo separado
  (`ExecutorService`), probado con varios clientes conectados simultaneamente.
- **Bases de datos**: persistencia en MySQL via JDBC puro (`fidebank.persistencia.BancoDAO`
  y `ConexionMySQL`), con tablas `clientes`, `cuentas` y `transacciones` (esta ultima como
  bitacora/auditoria de toda operacion). El servidor carga los saldos vigentes desde MySQL
  al iniciar, para que sobrevivan a un reinicio.
- **Clases y objetos, herencia, polimorfismo, excepciones y colecciones**: heredados del
  Avance 2 (ver `fidebank.modelo`, `fidebank.excepciones`, `fidebank.servicio.Banco`).
- **Serializacion de objetos Java**: se sigue usando para el protocolo de red (Peticion y
  Respuesta viajan serializados) y queda tambien disponible la persistencia por archivo del
  Avance 2 (`fidebank.persistencia.Persistencia`) como capacidad adicional, aunque el flujo
  principal ahora usa MySQL.
- **Multihilos**: el hilo por conexion en el servidor, mas el hilo impresor
  (`fidebank.servicio.Impresora`) que ya existia en el cliente desde el Avance 2.
- **Interfaz grafica**: Swing puro, sin frameworks externos.

## Limitaciones conocidas / simplificaciones

- Al reiniciar el servidor, el historial en memoria de cada `Cuenta` se reconstruye vacio;
  el registro completo de transacciones para auditoria queda en la tabla `transacciones` de
  MySQL, pero no se "reproduce" automaticamente hacia los objetos `Transaccion` en memoria.
- El protocolo de red no esta cifrado (no hay TLS); es apropiado para este entregable
  academico pero no para produccion.
