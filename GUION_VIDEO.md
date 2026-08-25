# Guion - Video Presentacion Final (FideBank ATM)

Duracion sugerida total: 8-12 minutos. Grabar en Teams, subir a OneDrive y compartir el
link (no subir el archivo .mp4 directamente).

Antes de grabar: tenga MySQL corriendo, el servidor (`ServidorFideBank`) ya iniciado en una
terminal visible, y dos o tres ventanas del cliente (`Main`) listas para abrir.

---

## 1. Introduccion (1-2 min)

**Presentacion del estudiante**
- Diga su nombre completo, carne/identificacion si el curso lo pide, y el nombre del curso
  (Programacion Cliente-Servidor Concurrente).

**Presentacion de la tematica del proyecto**
- "Elegi el Proyecto 5: un sistema de cajeros automaticos para una empresa de FinTech
  llamada FideBank. La aplicacion permite a los clientes abrir cuentas, autenticarse con
  PIN, y ejecutar retiros, depositos, transferencias entre cuentas y consultar su
  historial, todo desde un cajero automatico."
- Mencione brevemente que la solucion evolucino en tres entregas: diseno (Avance 1),
  implementacion de la logica y la interfaz (Avance 2), y en esta entrega final se agrego
  una arquitectura cliente-servidor real por red y persistencia en base de datos MySQL.

---

## 2. Estructura de la solucion (3-4 min)

**Presentar las clases utilizadas y la interaccion entre componentes/subsistemas**

Sugerencia de guion, mostrando el arbol de carpetas del proyecto en el IDE mientras habla:

1. "El proyecto se organiza en paquetes por responsabilidad."
2. `fidebank.modelo`: "Aqui estan las entidades del dominio: `Cliente`, `Cuenta`, y
   `Transaccion`, que es una clase abstracta de la que heredan `Retiro`, `Deposito` y
   `Transferencia` -- ahi esta la herencia y el polimorfismo, cada una sobrescribe
   `ejecutar()` y `getDescripcion()` a su manera. `Comprobante` arma el texto que se
   imprime al final de cada operacion."
3. `fidebank.excepciones`: "Cree excepciones propias para los errores de negocio:
   `PinInvalidoException`, `CuentaBloqueadaException`, `SaldoInsuficienteException` y
   `CuentaNoEncontradaException`, en vez de usar excepciones genericas de Java."
4. `fidebank.servicio.Banco`: "Es la fachada de negocio: guarda los clientes y cuentas en
   colecciones (`Map`) y expone las operaciones -- abrir cuenta, autenticar, retirar,
   depositar, transferir, consultar historial."
5. `fidebank.red`: "Esta es la parte nueva de esta entrega. `Peticion` y `Respuesta` son el
   protocolo que viaja por el socket. `ServidorFideBank` escucha conexiones TCP y atiende
   cada cajero conectado en un hilo separado usando un `ExecutorService` -- por eso varios
   cajeros pueden operar al mismo tiempo sin bloquearse. `ClienteRed` es el lado del cajero:
   abre el socket y envia/recibe esos objetos serializados."
6. `fidebank.persistencia`: "Aqui esta `ConexionMySQL`, que abre la conexion JDBC, y
   `BancoDAO`, que crea las tablas `clientes`, `cuentas` y `transacciones` si no existen, y
   guarda o actualiza cada operacion. El servidor carga los saldos vigentes desde MySQL al
   arrancar, para que sobrevivan a un reinicio."
7. `fidebank.gui`: "La interfaz Swing del cajero: login con PIN, menu principal, y un
   dialogo por cada transaccion. Ninguna pantalla llama a `Banco` directamente ahora; todas
   pasan por `ClienteRed` para hablar con el servidor."

Muestre en pantalla, aunque sea unos segundos, el diagrama de clases del Avance 1 para
conectar el diseno original con el codigo final.

---

## 3. Flujo de ejecucion (demostracion) (3-4 min)

Guion sugerido para la demo en vivo:

1. Muestre la terminal con el servidor ya corriendo: "Aqui esta el servidor escuchando en
   el puerto 5050, y ya cargo dos cuentas desde MySQL."
2. Abra la consola de MySQL (o un cliente grafico) y muestre las tablas `clientes`,
   `cuentas` y `transacciones` vacias o con los datos demo, para dejar claro que es una
   base de datos real, no solo memoria.
3. Abra el cliente (`Main`) e inicie sesion con la cuenta demo (100000 / PIN 1234).
4. Haga un retiro, muestre el comprobante y el cambio de saldo en pantalla.
5. Haga un deposito y una transferencia hacia la otra cuenta demo (100001).
6. Abra "Ver historial" y muestre que las tres transacciones quedaron registradas.
7. Vuelva a la consola de MySQL y corra `SELECT * FROM transacciones;` para mostrar que el
   servidor SI esta escribiendo en la base de datos en tiempo real.
8. (Opcional pero recomendado) Abra una SEGUNDA ventana del cliente, inicie sesion con la
   otra cuenta demo, y haga una operacion al mismo tiempo que la primera ventana, para
   demostrar que el servidor atiende varios cajeros simultaneamente.
9. Muestre un caso de error controlado: ingrese un PIN incorrecto 3 veces seguidas y
   muestre el mensaje de cuenta bloqueada.

---

## 4. Retos y lecciones (1-2 min)

Ideas para adaptar con su propia experiencia (evite copiar textualmente, hablelo con sus
palabras):

- **Tiempo**: dividir el proyecto en avances (diseno, luego logica+GUI, luego red+BD) ayudo
  a no intentar todo junto; lo mas demandante en tiempo fue separar la logica de negocio de
  la interfaz para que pudiera vivir en el servidor.
- **Decisiones**: opte por sockets TCP con objetos serializados en vez de un protocolo de
  texto plano, porque ya tenia las clases del modelo listas y reutilizar esa serializacion
  simplifico el envio de datos.
- **Organizacion**: separar el codigo en paquetes (`modelo`, `excepciones`, `servicio`,
  `red`, `persistencia`, `gui`) desde el Avance 2 hizo mucho mas facil agregar la parte de
  red despues, porque la logica de negocio ya estaba aislada de la interfaz.
- **Implementacion**: el mayor reto tecnico fue decidir que pasaba con el PIN y el saldo
  cuando el servidor se reinicia -- la solucion fue cargar todo desde MySQL al arrancar en
  vez de depender solo de la memoria.
- Mencione cualquier dificultad real que haya tenido usted (por ejemplo, configurar MySQL
  localmente, o depurar errores de conexion) -- eso es lo que la rubrica busca en el punto
  de "pensamiento propio y critico".

---

## Cierre

- Agradezca y cierre indicando que el codigo esta disponible en su repositorio de GitHub
  (mencione el link si el curso lo requiere).
