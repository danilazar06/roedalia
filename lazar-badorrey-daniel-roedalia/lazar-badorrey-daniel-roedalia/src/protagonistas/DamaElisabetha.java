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
    private volatile boolean servidorActivo = true;
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
        if (nivelChispa >= 100) return;

        if (variacion == 75) {
            nivelChispa = 75;
            encuentroRealizado = true;
            System.out.println("[Elisabetha] La chispa adecuada ha nacido: nivel 75");
        } else {
            if (encuentroRealizado && variacion < 0) {
                if (nivelChispa >= 85) return;
                variacion = variacion / 3;
            }
            int nuevoNivel = nivelChispa + variacion;
            if (!encuentroRealizado && variacion > 0) {
                nuevoNivel = Math.min(30, nuevoNivel);
            }
            nivelChispa = Math.max(0, Math.min(100, nuevoNivel));
        }
        System.out.println("[Elisabetha] Chispa: " + nivelChispa + " | Encuentro: " + encuentroRealizado);
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
                int decision = generadorAleatorio.nextInt(5);

                switch (decision) {
                    case 0: atenderDoncellas(); break;
                    case 1: asistirBaileCorte(); break;
                    case 2: leerPergaminos(); break;
                    case 3: escaparseDeNoche(); break;
                }

                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println("[Elisabetha] Error en actividad principal");
            }
        }
        System.out.println("[Elisabetha] Chispa maxima (100). Espera en su ventana a que llegue Lance...");
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
            if ("RUMOR".equals(mensaje)) {
                actualizarEstadoChispa(-5);
                System.out.println("[Elisabetha] Rumor infundado de una dama (-5 chispa)");
            } else if ("INVITACION_BAILE".equals(mensaje)) {
                if (generadorAleatorio.nextDouble() < 0.20) {
                    System.out.println("[Elisabetha] No puede evitar la invitacion al baile");
                    asistirBaileCorte();
                } else {
                    System.out.println("[Elisabetha] Esquiva la invitacion al baile (80%)");
                }
            } else {
                System.out.println("[Elisabetha] Confidencia recibida (sin efecto)");
            }
        } else {
            System.out.println("[Elisabetha] Ninguna dama tiene nada que contarle");
        }
    }

    private void asistirBaileCorte() throws InterruptedException {
        Thread.sleep(5000);
        actualizarEstadoChispa(-5);
        System.out.println("[Elisabetha] Asiste a un baile de la Corte (-5 chispa)");
    }

    private void leerPergaminos() throws InterruptedException {
        Thread.sleep(5000);
        if (generadorAleatorio.nextDouble() < 0.5) {
            actualizarEstadoChispa(-7);
            System.out.println("[Elisabetha] Pergamino soporifero sobre historia del reino (-7 chispa)");
        } else {
            int bonus = encuentroRealizado ? 10 : 5;
            actualizarEstadoChispa(bonus);
            System.out.println("[Elisabetha] Leyenda de valientes caballeros (+" + bonus + " chispa)");
        }
    }

    private void escaparseDeNoche() {
        double probabilidadTaberna = encuentroRealizado ? 0.85 : 0.50;
        if (generadorAleatorio.nextDouble() < probabilidadTaberna) {
            visitarTaberna();
        } else {
            visitarMercado();
        }
    }

    private void visitarMercado() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_PLAZA_MERCANTIL);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            ParametrosReino.elisabethaEnMercado = true;
            Thread.sleep(5000);
            String oferta = lector.readLine();
            String[] productos = oferta.split(", ");
            String elegido = productos[generadorAleatorio.nextInt(productos.length)];
            System.out.println("[Elisabetha] Mercado ofrece: " + oferta);
            System.out.println("[Elisabetha] Compra: " + elegido);
            ParametrosReino.elisabethaEnMercado = false;


        } catch (Exception e) {
            ParametrosReino.elisabethaEnMercado = false;
            System.err.println("[Elisabetha] Error al visitar mercado, puede que se esté prendiendo fuego");
        }
    }

    private void visitarTaberna() {
        if (ParametrosReino.tabernaDestruida) {
            System.out.println("[Elisabetha] La taberna esta en llamas! No puede entrar");
            return;
        }

        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DESCANSO_GUERRERO);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            escritor.println("Elisabetha");
            String respuesta = lector.readLine();
            if (respuesta != null && !respuesta.equals("0")) {
                int puntos = Integer.parseInt(respuesta);
                actualizarEstadoChispa(puntos);
            } else {
                System.out.println("[Elisabetha] Visita la taberna pero Lance no esta");
            }
        } catch (Exception e) {
            System.err.println("[Elisabetha] Error al acceder a taberna, puede que se esté prendiendo fuego");
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
