package secundarios;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;

public class DamaLazo extends Thread {
    private String nombre;
    private Random generadorAleatorio = new Random();

    public DamaLazo(String n) {
        this.nombre = n;
        System.out.println("[Dama del Lazo] " + nombre + " iniciada");
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (generadorAleatorio.nextDouble() < 0.5) {
                    realizarLabores();
                } else {
                    compartirMurmullo();
                }
            } catch (InterruptedException e) {
                System.err.println("[Dama del Lazo] " + nombre + " interrumpida");
            }
        }
    }

    private void realizarLabores() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("[Dama del Lazo] " + nombre + " completa labores (5s)");
    }

    private void compartirMurmullo() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_SERVIDOR_ELISA);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            conexion.setSoTimeout(20000);

            String mensaje = generadorAleatorio.nextDouble() < 0.5 ? "MURMURIO" : "CONFIDENCIA";
            escritor.println(mensaje);

            String respuesta = lector.readLine();
            if (respuesta != null && respuesta.equals("RECIBIDO")) {
                System.out.println("[Dama del Lazo] " + nombre + " envio mensaje a Elisabetha");
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[Dama del Lazo] " + nombre + ": timeout con Elisabetha");
        } catch (IOException e) {
        }
    }
}

