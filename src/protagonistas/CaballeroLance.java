package protagonistas;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class CaballeroLance extends Thread {
    private int nivelChispa = 0;
    private boolean encuentroRealizado = false;
    private ServerSocket servidorPersonal;
    private boolean servidorActivo = true;
    private Random generadorAleatorio = new Random();
    private LinkedBlockingQueue<String> colaMensajesCaballeros = new LinkedBlockingQueue<>();

    public CaballeroLance() {
        try {
            this.servidorPersonal = new ServerSocket(ParametrosReino.PUERTO_SERVIDOR_LANCE);
            System.out.println("[Lance] Servidor iniciado en puerto " + ParametrosReino.PUERTO_SERVIDOR_LANCE);

            new Thread(this::escucharMensajes).start();
        } catch (IOException e) {
            System.err.println("[Lance] Error al iniciar servidor: " + e.getMessage());
        }
    }

    public synchronized void actualizarEstadoChispa(int variacion) {
        if (this.nivelChispa == 100) return;

        if (variacion == 75) {
            nivelChispa = 75;
            encuentroRealizado = true;
            System.out.println("[Lance] Encuentro con Elisabetha: +75 puntos");
        } else {
            int nuevoNivel = nivelChispa + variacion;
            if (!encuentroRealizado && variacion > 0) nuevoNivel = Math.min(50, nuevoNivel);
            nivelChispa = Math.max(0, Math.min(100, nuevoNivel));
        }
        System.out.println("[Lance] Chispa: " + nivelChispa);
    }

    public synchronized int obtenerNivelChispa() { return nivelChispa; }

    @Override
    public void run() {
        while (obtenerNivelChispa() < 100) {
            try {
                int decision = generadorAleatorio.nextInt(3);
                
                if (decision == 0) {
                    dialogarCompaneros();
                } else if (decision == 1) {
                    intentarDesafio();
                } else if (decision == 2){
                    cumplirVigilancia();
                } else {
                    intentarRobarAlDragon();
                };
                
                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println("[Lance] Error en actividad principal");
            }
        }
        System.out.println("[Lance] Chispa maxima alcanzada (100).");
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
                        colaMensajesCaballeros.offer(mensaje);
                        escritor.println("RECIBIDO");
                    }
                }
            } catch (IOException e) {
                if (servidorActivo) {
                    System.err.println("[Lance] Error al recibir mensaje: " + e.getMessage());
                }
            }
        }
    }

    private void dialogarCompaneros() throws InterruptedException {
        Thread.sleep(4000);
        String mensaje = colaMensajesCaballeros.poll();
        if (mensaje != null) {
            if ("AFRENTA".equals(mensaje)) {
                lanzarDesafio();
            } else {
                System.out.println("[Lance] Confidencia de un companero (sin efecto)");
            }
        } else {
            System.out.println("[Lance] Ningun companero tiene nada que contarle");
        }
    }

    private void intentarDesafio() throws InterruptedException {
        String mensaje = colaMensajesCaballeros.poll();
        if (mensaje != null && "AFRENTA".equals(mensaje)) {
            lanzarDesafio();
        } else {
            System.out.println("[Lance] No hay ofensas pendientes");
        }
    }

    private void lanzarDesafio() throws InterruptedException {
        System.out.println("[Lance] Desafio (5s)");
        Thread.sleep(5000);
        if (generadorAleatorio.nextDouble() < 0.20) {
            actualizarEstadoChispa(-2);
            System.out.println("[Lance] Vence pero hiere al oponente (-2 chispa)");
        } else {
            actualizarEstadoChispa(10);
            System.out.println("[Lance] Vence sin danar al oponente (+10 chispa)");
        }
    }

    private void cumplirVigilancia() {
        if (generadorAleatorio.nextDouble() < 0.3) {
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
            System.out.println("[Lance] Porton Norte: " + respuesta);
        } catch (Exception e) {
            System.err.println("[Lance] Error en Porton Norte");
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
                actualizarEstadoChispa(puntos == 75 ? 75 : 15);
            }
        } catch (Exception e) {
            System.err.println("[Lance] Error al acceder a taberna");
        }
    }

    private void cerrarServidor() {
        servidorActivo = false;
        try {
            if (servidorPersonal != null && !servidorPersonal.isClosed()) {
                servidorPersonal.close();
            }
        } catch (IOException e) {
            System.err.println("[Lance] Error al cerrar servidor");
        }
    }

    private void intentarRobarAlDragon() {
        // Lance se conecta al puerto 5006 (La Guarida del Dragón)
        try (Socket enlace = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DRAGON);
             DataOutputStream flujoSalida = new DataOutputStream(enlace.getOutputStream());
             DataInputStream flujoEntrada = new DataInputStream(enlace.getInputStream())) {

            // 1. Lance avisa de quién es
            flujoSalida.writeUTF("Caballero Lance");

            // 2. Lance escucha el estado del Dragón
            String estadoDragon = flujoEntrada.readUTF();

            if (estadoDragon.equals("DORMIDO")) {
                System.out.println("[Lance] ¡He robado una Escama de Dragon! (+20 de chispa)");
                actualizarEstadoChispa(20);
            } else {
                System.out.println("[Lance] ¡Maldicion, el dragon estaba DESPIERTO y me ha quemado! (-20 de chispa)");
                actualizarEstadoChispa(-20);
            }

        } catch (Exception ignorada) {
            // Si el Dragón no está activo, Lance simplemente no hace nada
        }
    }
}
