package protagonistas;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;

public class CaballeroLance extends Thread {
    private int nivelAfinidad = 0;
    private boolean encuentroRealizado = false;
    private ServerSocket servidorPersonal;
    private boolean servidorActivo = true;
    private Random generadorAleatorio = new Random();

    public CaballeroLance() {
        try {
            this.servidorPersonal = new ServerSocket(ParametrosReino.PUERTO_SERVIDOR_LANCE);
            System.out.println("⚔️ El Caballero Lance ha establecido su canal privado en el puerto " + ParametrosReino.PUERTO_SERVIDOR_LANCE);
            
            // Iniciar hilo para escuchar mensajes de los caballeros
            new Thread(this::escucharMensajes).start();
        } catch (IOException e) {
            System.err.println("❌ El Caballero no pudo establecer su canal privado: " + e.getMessage());
        }
    }

    public synchronized void actualizarEstadoAfinidad(int variacion) {
        // Protección máxima: Una vez alcanzado el nivel 100, nada puede afectarlo
        if (this.nivelAfinidad == 100) return;

        if (variacion == 75) {
            nivelAfinidad = 75;
            encuentroRealizado = true;
            System.out.println("💫 ¡DESTINO CUMPLIDO! Lance siente la chispa del encuentro con Elisabetha (75 puntos)");
        } else {
            int nuevoNivel = nivelAfinidad + variacion;
            // Límite de 50 antes del encuentro fatídico
            if (!encuentroRealizado && variacion > 0) nuevoNivel = Math.min(50, nuevoNivel);
            nivelAfinidad = Math.max(0, Math.min(100, nuevoNivel));
        }
        System.out.println("🛡️ Lance - Nivel de Afinidad: " + nivelAfinidad);
    }

    public synchronized int obtenerNivelAfinidad() { return nivelAfinidad; }

    @Override
    public void run() {
        while (obtenerNivelAfinidad() < 100) {
            try {
                int decision = generadorAleatorio.nextInt(3);
                
                if (decision == 0) {
                    dialogarCompañeros();
                } else if (decision == 1) {
                    cumplirVigilancia();
                } else {
                    // El desafío se dispara por mensaje de caballero
                }
                
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("⚠️ Interrupción en las actividades de Lance");
            }
        }
        System.out.println("🏰 Lance ha alcanzado la maestría perfecta y vigila el portón principal.");
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
                        procesarMensajeCaballero(mensaje);
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

    private void procesarMensajeCaballero(String mensaje) {
        if ("AFRENTA".equals(mensaje)) {
            lanzarDesafio();
        }
    }

    private void dialogarCompañeros() throws InterruptedException {
        Thread.sleep(4000);
        System.out.println("🗣️ Lance comparte historias de batallas con sus compañeros en la sala de armas");
    }

    private void lanzarDesafio() throws InterruptedException {
        System.out.println("⚔️ Lance acepta un desafío por el honor del reino!");
        Thread.sleep(5000);
        if (generadorAleatorio.nextDouble() < 0.20) {
            actualizarEstadoAfinidad(-5);
            System.out.println("🩸 Lance se siente culpable por herir gravemente a su oponente (-5 afinidad)");
        } else {
            actualizarEstadoAfinidad(7);
            System.out.println("🏆 Lance vence con maestría sin derramamiento de sangre (+7 afinidad)");
        }
    }

    private void cumplirVigilancia() {
        if (generadorAleatorio.nextDouble() < 0.5) {
            inspeccionarBarrera();
        } else {
            visitarDescansoGuerrero();
        }
    }

    private void inspeccionarBarrera() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_BARRERA_NORTE);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            Thread.sleep(5000);
            escritor.println("INSPECCIONAR");
            String respuesta = lector.readLine();
            System.out.println("🚪 Lance en la barrera: " + respuesta);
        } catch (Exception e) {
            System.err.println("⚠️ Lance no pudo completar la inspección de la barrera");
        }
    }

    private void visitarDescansoGuerrero() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DESCANSO_GUERRERO);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            escritor.println("Lance");
            String respuesta = lector.readLine();
            if (respuesta != null && !respuesta.equals("0")) {
                int puntos = Integer.parseInt(respuesta);
                actualizarEstadoAfinidad(puntos == 75 ? 75 : 10);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lance no pudo acceder al descanso de guerreros");
        }
    }

    private void cerrarServidor() {
        servidorActivo = false;
        try {
            if (servidorPersonal != null && !servidorPersonal.isClosed()) {
                servidorPersonal.close();
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error al cerrar el servidor personal de Lance");
        }
    }
}
