package secundarios;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;

public class DamaLazo extends Thread {
    private String nombre;
    private Random generadorAleatorio = new Random();
    private final String[] labores = {"montar a caballo", "practicar esgrima", "enterarse de rumores"};

    public DamaLazo(String nombre) {
        this.nombre = nombre;
        System.out.println("[Dama del Lazo " + nombre + "] Iniciada");
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (generadorAleatorio.nextDouble() < 0.5) {
                    realizarLabores();
                } else {
                    confesarAElisabetha();
                }
            } catch (InterruptedException e) {
                System.err.println("[Dama del Lazo " + nombre + "] Interrumpida");
            }
        }
    }

    private void realizarLabores() throws InterruptedException {
        Thread.sleep(5000);
        String labor = labores[generadorAleatorio.nextInt(labores.length)];
        System.out.println("[Dama del Lazo " + nombre + "] Completa labor: " + labor + " (5s)");
    }

    private void confesarAElisabetha() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_SERVIDOR_ELISA);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            conexion.setSoTimeout(20000);

            String mensaje;
            if (generadorAleatorio.nextDouble() < 0.50) {
                mensaje = "CONFIDENCIA";
            } else {
                mensaje = "RUMOR";
            }
            escritor.println(mensaje);

            String respuesta = lector.readLine();
            if (respuesta != null && respuesta.equals("RECIBIDO")) {
                System.out.println("[Dama del Lazo " + nombre + "] Envio " + mensaje.toLowerCase() + " a Elisabetha");
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[Dama del Lazo " + nombre + "] Elisabetha no la atendio en 20s");
        } catch (IOException e) {
        }
    }
}

