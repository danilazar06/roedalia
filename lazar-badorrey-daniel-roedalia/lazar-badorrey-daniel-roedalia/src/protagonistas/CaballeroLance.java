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
    private volatile boolean servidorActivo = true;
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
        if (nivelChispa >= 100) return;

        if (variacion == 75) {
            nivelChispa = 75;
            encuentroRealizado = true;
            System.out.println("[Lance] La chispa adecuada ha nacido: nivel 75");
        } else {
            if (encuentroRealizado && variacion < 0) {
                if (nivelChispa >= 85) return;
                variacion = variacion / 3;
            }
            int nuevoNivel = nivelChispa + variacion;
            if (!encuentroRealizado && variacion > 0) {
                nuevoNivel = Math.min(50, nuevoNivel);
            }
            nivelChispa = Math.max(0, Math.min(100, nuevoNivel));
        }
        System.out.println("[Lance] Chispa: " + nivelChispa + " | Encuentro: " + encuentroRealizado);
    }

    public synchronized int obtenerNivelChispa() {
        return nivelChispa;
    }

    public synchronized boolean haAlcanzadoChispaMaxima() {
        return nivelChispa >= 100;
    }

    @Override
    public void run() {
        while (!haAlcanzadoChispaMaxima()) {
            try {
                int decision = generadorAleatorio.nextInt(4);

                switch (decision) {
                    case 0: hablarConCompaneros(); break;
                    case 1: desafiarCompanero(); break;
                    case 2: realizarGuardia(); break;
                }

                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println("[Lance] Error en actividad principal");
            }
        }
        System.out.println("[Lance] Chispa maxima (100). Espera en el Porton Norte a que llegue Elisabetha...");
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

    private void hablarConCompaneros() throws InterruptedException {
        Thread.sleep(4000);
        String mensaje = colaMensajesCaballeros.poll();
        if (mensaje != null) {
            if ("AFRENTA".equals(mensaje)) {
                System.out.println("[Lance] Recibe una ofensa sobre Elisabetha, reta a duelo!");
                ejecutarDuelo();
            } else {
                System.out.println("[Lance] Confidencia de un companero (sin efecto)");
            }
        } else {
            System.out.println("[Lance] Ningun companero tiene nada que contarle");
        }
    }

    private void desafiarCompanero() throws InterruptedException {
        String mensaje = colaMensajesCaballeros.poll();
        if (mensaje != null && "AFRENTA".equals(mensaje)) {
            System.out.println("[Lance] Ofensa pendiente, reta a duelo!");
            ejecutarDuelo();
        } else {
            if (mensaje != null) {
                System.out.println("[Lance] Confidencia de un companero (sin efecto)");
            } else {
                System.out.println("[Lance] No hay afrentas pendientes, entrena con el estafermo (5s)");
                Thread.sleep(5000);
            }
        }
    }

    private void ejecutarDuelo() throws InterruptedException {
        System.out.println("[Lance] Duelo en curso (5s)...");
        Thread.sleep(5000);
        if (generadorAleatorio.nextDouble() < 0.20) {
            actualizarEstadoChispa(-5);
            System.out.println("[Lance] Vence pero hiere gravemente al oponente (-5 chispa)");
        } else {
            int bonus = encuentroRealizado ? 10 : 7;
            actualizarEstadoChispa(bonus);
            System.out.println("[Lance] Vence sin danar al oponente (+" + bonus + " chispa)");
        }
    }

    private void realizarGuardia() {
        double probabilidadTaberna = encuentroRealizado ? 0.85 : 0.50;
        if (generadorAleatorio.nextDouble() < probabilidadTaberna) {
            vigilarTaberna();
        } else {
            vigilarPortonNorte();
        }
    }

    private void vigilarPortonNorte() {
        // NUEVO: Comprobar si el porton esta bloqueado por el dragon
        if (ParametrosReino.portonBloqueado) {
            System.out.println("[Lance] El Porton Norte esta bloqueado por el fuego del dragon!");
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
            return;
        }

        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_BARRERA_NORTE);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            Thread.sleep(5000);
            escritor.println("INSPECCIONAR");
            String respuesta = lector.readLine();
            System.out.println("[Lance] Porton Norte: " + respuesta);
        } catch (Exception e) {
            System.err.println("[Lance] Error en Porton Norte, puede que se esté prendiendo fuego");
        }
    }

    private void vigilarTaberna() {
        if (ParametrosReino.tabernaDestruida) {
            System.out.println("[Lance] La taberna esta en llamas! No puede vigilar");
            return;
        }

        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DESCANSO_GUERRERO);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            escritor.println("Lance");
            String respuesta = lector.readLine();
            if (respuesta != null && !respuesta.equals("0")) {
                int puntos = Integer.parseInt(respuesta);
                actualizarEstadoChispa(puntos);
            } else {
                System.out.println("[Lance] Vigila la taberna pero Elisabetha no esta");
            }
        } catch (Exception e) {
            System.err.println("[Lance] Error al acceder a taberna, puede que se esté prendiendo fuego");
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
}
