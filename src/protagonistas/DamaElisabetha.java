package protagonistas;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;

public class DamaElisabetha extends Thread {
    private int nivelAfinidad = 0;
    private boolean encuentroRealizado = false;
    private ServerSocket servidorPersonal;
    private boolean servidorActivo = true;
    private Random generadorAleatorio = new Random();

    public DamaElisabetha() {
        try {
            this.servidorPersonal = new ServerSocket(ParametrosReino.PUERTO_SERVIDOR_ELISA);
            System.out.println("👑 La Dama Elisabetha ha establecido su canal privado en el puerto " + ParametrosReino.PUERTO_SERVIDOR_ELISA);
            
            // Iniciar hilo para escuchar mensajes de las damas
            new Thread(this::escucharMensajes).start();
        } catch (IOException e) {
            System.err.println("❌ La Dama no pudo establecer su canal privado: " + e.getMessage());
        }
    }

    public synchronized void actualizarEstadoAfinidad(int variacion) {
        // Protección máxima: Una vez alcanzado el nivel 100, nada puede afectarla
        if (this.nivelAfinidad == 100) return;

        if (variacion == 75) {
            nivelAfinidad = 75;
            encuentroRealizado = true;
            System.out.println("💫 ¡DESTINO CUMPLIDO! Elisabetha siente la chispa del encuentro con Lance (75 puntos)");
        } else {
            int nuevoNivel = nivelAfinidad + variacion;
            // Límite de 30 antes del encuentro fatídico
            if (!encuentroRealizado && variacion > 0) nuevoNivel = Math.min(30, nuevoNivel);
            nivelAfinidad = Math.max(0, Math.min(100, nuevoNivel));
        }
        System.out.println("🌹 Elisabetha - Nivel de Afinidad: " + nivelAfinidad + " | Encuentro con Lance: " + encuentroRealizado);
    }

    public synchronized int obtenerNivelAfinidad() { return nivelAfinidad; }

    @Override
    public void run() {
        while (obtenerNivelAfinidad() < 100) {
            try {
                int decision = generadorAleatorio.nextInt(4);
                
                if (decision == 0) {
                    atenderDoncellas();
                } else if (decision == 1) {
                    acudirEventoReal();
                } else if (decision == 2) {
                    estudiarTomosAntiguos();
                } else {
                    escaparRecinto();
                }
                
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("⚠️ Interrupción en las actividades de Elisabetha");
            }
        }
        System.out.println("🏰 Elisabetha ha alcanzado la iluminación perfecta y aguarda en la torre principal.");
        cerrarServidor();
    }

    private void escucharMensajes() {
        while (servidorActivo) {
            try {
                try (Socket conexion = servidorPersonal.accept();
                     BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                     PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true)) {
                    
                    String mensaje = lector.readLine();
                    if (mensaje != null) {
                        procesarMensajeDama(mensaje);
                        escritor.println("RECIBIDO");
                    }
                }
            } catch (IOException e) {
                if (servidorActivo) {
                    System.err.println("⚠️ Error接收消息: " + e.getMessage());
                }
            }
        }
    }

    private void procesarMensajeDama(String mensaje) {
        if ("MURMURIO".equals(mensaje)) {
            actualizarEstadoAfinidad(-5);
            System.out.println("🤫 Elisabetha escuchó un murmullo perturbador (-5 afinidad)");
        }
    }

    private void atenderDoncellas() throws InterruptedException {
        Thread.sleep(4000);
        System.out.println("👥 Elisabetha atiende a sus doncellas en los jardines del castillo");
    }

    private void acudirEventoReal() throws InterruptedException {
        // Solo el 20% de las veces asiste inevitablemente
        if (generadorAleatorio.nextDouble() < 0.20) {
            Thread.sleep(5000);
            actualizarEstadoAfinidad(-5);
            System.out.println("💃 Elisabetha fue convocada al baile real contra su voluntad (-5 afinidad)");
        } else {
            System.out.println("🌙 Elisabetha eludió hábilmente la convocatoria al baile real");
        }
    }

    private void estudiarTomosAntiguos() throws InterruptedException {
        Thread.sleep(5000);
        if (generadorAleatorio.nextDouble() < 0.5) {
            actualizarEstadoAfinidad(-7);
            System.out.println("📚 Los tomos soporíferos agotaron a Elisabetha (-7 afinidad)");
        } else {
            actualizarEstadoAfinidad(5);
            System.out.println("⚔️ Las leyendas de caballeros inspiraron a Elisabetha (+5 afinidad)");
        }
    }

    private void escaparRecinto() {
        if (generadorAleatorio.nextDouble() < 0.5) {
            explorarMercado();
        } else {
            visitarDescansoGuerrero();
        }
    }

    private void explorarMercado() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_PLAZA_MERCANTIL);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {
            
            Thread.sleep(5000);
            String articulo = lector.readLine();
            System.out.println("🛍️ Elisabetha adquirió en la plaza: " + articulo);
        } catch (Exception e) {
            System.err.println("⚠️ Elisabetha no pudo completar su visita al mercado");
        }
    }

    private void visitarDescansoGuerrero() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DESCANSO_GUERRERO);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            escritor.println("Elisabetha");
            String respuesta = lector.readLine();
            if (respuesta != null && !respuesta.equals("0")) {
                int puntos = Integer.parseInt(respuesta);
                actualizarEstadoAfinidad(puntos == 75 ? 75 : 10);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Elisabetha no pudo acceder al descanso de guerreros");
        }
    }

    private void cerrarServidor() {
        servidorActivo = false;
        try {
            if (servidorPersonal != null && !servidorPersonal.isClosed()) {
                servidorPersonal.close();
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error al cerrar el servidor personal de Elisabetha");
        }
    }
}
