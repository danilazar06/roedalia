package clientes;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Elisabetha extends Thread {

    private int gradoDeVinculo = 0;
    private boolean cruceOriginalRealizado = false;
    public LinkedBlockingQueue<String> buzon;
    private final Random dado = new Random();

    public Elisabetha(LinkedBlockingQueue<String> buzon) {
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
                if (resultado > 30) {
                    resultado = 30;
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

        System.out.println("[Elisabetha] Chispa: " + gradoDeVinculo
                + " | Encuentro: " + cruceOriginalRealizado);
    }

    public synchronized int getChispa() {
        return gradoDeVinculo;
    }

    @Override
    public void run() {
        while (getChispa() < 100) {
            try {
                int decision = dado.nextInt(4);
                if (decision == 0) {
                    revisarCorrespondencia();
                } else if (decision == 1) {
                    asistirAlBaileReal();
                } else if (decision == 2) {
                    consultarBiblioteca();
                } else {
                    abandonarResidencia();
                }
                Thread.sleep(1000);
            } catch (Exception ignorada) {
            }
        }
        System.out.println("[Elisabetha] Chispa maxima alcanzada (100).");
    }

    private void revisarCorrespondencia() throws InterruptedException {
        Thread.sleep(4000);
        String mensaje = buzon.poll();
        if (mensaje != null && mensaje.equals("RUMOR")) {
            modificarChispa(-5);
        }
    }

    private void asistirAlBaileReal() throws InterruptedException {
        double probabilidad = dado.nextDouble();
        if (probabilidad < 0.20) {
            Thread.sleep(5000);
            modificarChispa(-5);
            System.out.println("[Elisabetha] Baile real (-5)");
        }
    }

    private void consultarBiblioteca() throws InterruptedException {
        Thread.sleep(5000);
        boolean resultadoNegativo = dado.nextBoolean();
        if (resultadoNegativo) {
            modificarChispa(-7);
        } else {
            modificarChispa(5);
        }
    }

    private void abandonarResidencia() {
        boolean optaPorComercio = dado.nextBoolean();
        if (optaPorComercio) {
            visitarMercado();
        } else {
            acudirATaberna();
        }
    }

    private void visitarMercado() {
        try (Socket enlace = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_PLAZA_MERCANTIL);
             BufferedReader lector = new BufferedReader(new InputStreamReader(enlace.getInputStream()))) {
            Thread.sleep(5000);
            lector.readLine();
        } catch (Exception ignorada) {
        }
    }

    private void acudirATaberna() {
        try (Socket enlace = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DESCANSO_GUERRERO);
             PrintWriter escritor = new PrintWriter(enlace.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(enlace.getInputStream()))) {

            escritor.println("Elisabetha");
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
