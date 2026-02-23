package secundarios;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Doncella extends Thread {
    private String nombre;
    private Random generadorAleatorio = new Random();

    public Doncella(String n) { 
        this.nombre = n; 
        System.out.println("👥 La doncella " + nombre + " ha llegado a la corte");
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (generadorAleatorio.nextDouble() < 0.5) {
                    // Realizar labores domésticas
                    realizarLabores();
                } else {
                    // Compartir murmullos con Elisabetha
                    compartirMurmullo();
                }
            } catch (InterruptedException e) {
                System.err.println("⚠️ La doncella " + nombre + " fue interrumpida en sus tareas");
            }
        }
    }

    private void realizarLabores() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("🧵 La doncella " + nombre + " completa sus labores en el castillo");
    }

    private void compartirMurmullo() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_SERVIDOR_ELISA);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {
            
            // Esperar respuesta por hasta 20 segundos
            conexion.setSoTimeout(20000);
            
            String mensaje = generadorAleatorio.nextDouble() < 0.5 ? "MURMURIO" : "CONFIDENCIA";
            escritor.println(mensaje);
            
            String respuesta = lector.readLine();
            if (respuesta != null && respuesta.equals("RECIBIDO")) {
                System.out.println("🤫 La doncella " + nombre + " compartió sus pensamientos con Elisabetha");
            }
        } catch (SocketTimeoutException e) {
            System.out.println("⏰ La doncella " + nombre + ": Elisabetha no pudo recibir el mensaje (timeout)");
        } catch (IOException e) {
            System.err.println("⚠️ La doncella " + nombre + " no pudo contactar a Elisabetha: " + e.getMessage());
        }
    }
}
