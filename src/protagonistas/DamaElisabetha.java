package protagonistas;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class DamaElisabetha extends Thread {
    private int nivelChispa = 0;
    private boolean encuentroRealizado = false;
    private ServerSocket servidorPersonal;
    private boolean servidorActivo = true;
    private Random generadorAleatorio = new Random();
    private LinkedBlockingQueue<String> colaMensajesDamas = new LinkedBlockingQueue<>();

    public DamaElisabetha() {
        try {
            this.servidorPersonal = new ServerSocket(ParametrosReino.PUERTO_SERVIDOR_ELISA);
            System.out.println("[Elisabetha] Servidor iniciado en puerto " + ParametrosReino.PUERTO_SERVIDOR_ELISA);

            new Thread(this::escucharMensajes).start();
        } catch (IOException e) {
            System.err.println("[Elisabetha] Error al iniciar servidor: " + e.getMessage());
        }
    }

    public synchronized void actualizarEstadoChispa(int variacion) {
        if (this.nivelChispa == 100) return;

        if (variacion == 75) {
            nivelChispa = 75;
            encuentroRealizado = true;
            System.out.println("[Elisabetha] Encuentro con Lance: +75 puntos");
        } else {
            int nuevoNivel = nivelChispa + variacion;
            if (!encuentroRealizado && variacion > 0) nuevoNivel = Math.min(30, nuevoNivel);
            nivelChispa = Math.max(0, Math.min(100, nuevoNivel));
        }
        System.out.println("[Elisabetha] Chispa: " + nivelChispa + " | Encuentro: " + encuentroRealizado);
    }

    public synchronized int obtenerNivelChispa() { return nivelChispa; }

    @Override
    public void run() {
        while (obtenerNivelChispa() < 100) {
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
                
                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println("[Elisabetha] Error en actividad principal");
            }
        }
        System.out.println("[Elisabetha] Chispa maxima alcanzada (100).");
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
                        colaMensajesDamas.offer(mensaje);
                        escritor.println("RECIBIDO");
                    }
                }
            } catch (IOException e) {
                if (servidorActivo) {
                    System.err.println("[Elisabetha] Error al recibir mensaje: " + e.getMessage());
                }
            }
        }
    }

    private void atenderDoncellas() throws InterruptedException {
        Thread.sleep(4000);
        String mensaje = colaMensajesDamas.poll();
        if (mensaje != null) {
            if ("MURMURIO".equals(mensaje)) {
                actualizarEstadoChispa(-3);
                System.out.println("[Elisabetha] Rumor recibido de una dama (-3 chispa)");
            } else {
                System.out.println("[Elisabetha] Confidencia recibida (sin efecto)");
            }
        } else {
            System.out.println("[Elisabetha] Ninguna dama tiene nada que contarle");
        }
    }

    private void acudirEventoReal() throws InterruptedException {
        if (generadorAleatorio.nextDouble() < 0.10) {
            Thread.sleep(5000);
            actualizarEstadoChispa(-3);
            System.out.println("[Elisabetha] Baile real obligado (-3 chispa)");
        } else {
            System.out.println("[Elisabetha] Evito el baile real");
        }
    }

    private void estudiarTomosAntiguos() throws InterruptedException {
        Thread.sleep(5000);
        if (generadorAleatorio.nextDouble() < 0.4) {
            actualizarEstadoChispa(-3);
            System.out.println("[Elisabetha] Lectura agotadora (-3 chispa)");
        } else {
            actualizarEstadoChispa(8);
            System.out.println("[Elisabetha] Lectura inspiradora (+8 chispa)");
        }
    }

    private void escaparRecinto() {
        if (generadorAleatorio.nextDouble() < 0.3) {
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
            System.out.println("[Elisabetha] Compra en mercado: " + articulo);
        } catch (Exception e) {
            System.err.println("[Elisabetha] Error al visitar mercado");
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
                actualizarEstadoChispa(puntos == 75 ? 75 : 15);
            }
        } catch (Exception e) {
            System.err.println("[Elisabetha] Error al acceder a taberna");
        }
    }

    private void cerrarServidor() {
        servidorActivo = false;
        try {
            if (servidorPersonal != null && !servidorPersonal.isClosed()) {
                servidorPersonal.close();
            }
        } catch (IOException e) {
            System.err.println("[Elisabetha] Error al cerrar servidor");
        }
    }
}
