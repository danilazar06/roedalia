package clientes;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Lance extends Thread {

    private int gradoDeVinculo = 0;
    private boolean cruceOriginalRealizado = false;
    public LinkedBlockingQueue<String> buzon;
    private final Random dado = new Random();

    public Lance(LinkedBlockingQueue<String> buzon) {
        this.buzon = buzon;
    }

    public synchronized void modificarChispa(int n) {
        if (this.gradoDeVinculo == 100) {
            return;
        }

        int ajuste = n;

        if (ajuste == 75) {
            gradoDeVinculo = 75;
            cruceOriginalRealizado = true;
        } else {
            int resultado = gradoDeVinculo + ajuste;
            if (!cruceOriginalRealizado && ajuste > 0) {
                if (resultado > 50) {
                    resultado = 50;
                }
            }
            if (resultado < 0) {
                resultado = 0;
            }
            if (resultado > 100) {
                resultado = 100;
            }
            gradoDeVinculo = resultado;
        }

        System.out.println("[Lance] Chispa: " + gradoDeVinculo
                + " | Encuentro: " + cruceOriginalRealizado);
    }

    public synchronized int getChispa() {
        return gradoDeVinculo;
    }

    @Override
    public void run() {
        while (getChispa() < 100) {
            try {
                int decision = dado.nextInt(3);
                if (decision == 0) {
                    dialogarConGuardia();
                } else if (decision == 1) {
                    recorrerMurallas();
                }
                Thread.sleep(1000);
            } catch (Exception ignorada) {
            }
        }
        System.out.println("[Lance] Chispa maxima alcanzada (100).");
    }

    private void dialogarConGuardia() throws InterruptedException {
        Thread.sleep(4000);
        String mensaje = buzon.poll();
        if (mensaje != null && mensaje.equals("OFENSA")) {
            resolverConflicto();
        }
    }

    private void resolverConflicto() throws InterruptedException {
        Thread.sleep(5000);
        double suerte = dado.nextDouble();
        if (suerte < 0.20) {
            modificarChispa(-5);
        } else {
            modificarChispa(7);
        }
    }

    private void recorrerMurallas() {
        boolean optaPorAcceso = dado.nextBoolean();
        if (optaPorAcceso) {
            inspeccionarAcceso();
        } else {
            acudirATaberna();
        }
    }

    private void inspeccionarAcceso() {
        try (Socket enlace = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_BARRERA_NORTE);
             PrintWriter escritor = new PrintWriter(enlace.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(enlace.getInputStream()))) {

            Thread.sleep(5000);
            escritor.println("INSPECCIONAR");
            lector.readLine();
        } catch (Exception ignorada) {
        }
    }

    private void acudirATaberna() {
        try (Socket enlace = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DESCANSO_GUERRERO);
             PrintWriter escritor = new PrintWriter(enlace.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(enlace.getInputStream()))) {

            escritor.println("Lance");
            String respuesta = lector.readLine();
            if (respuesta != null && !respuesta.equals("0")) {
                int valorRecibido = Integer.parseInt(respuesta);
                if (valorRecibido > 0) {
                    if (valorRecibido == 75) {
                        modificarChispa(75);
                    } else {
                        modificarChispa(10);
                    }
                }
            }
        } catch (Exception ignorada) {
        }
    }
}
