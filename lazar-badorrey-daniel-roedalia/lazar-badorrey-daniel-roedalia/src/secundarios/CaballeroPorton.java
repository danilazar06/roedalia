package secundarios;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;

public class CaballeroPorton extends Thread {
    private String nombre;
    private volatile boolean herido = false;
    private Random generadorAleatorio = new Random();
    private final String[] lugaresVigilancia = {"Porton Norte", "muralla", "torres"};

    public CaballeroPorton(String nombre) {
        this.nombre = nombre;
        System.out.println("[Caballero " + nombre + "] Iniciado");
    }

    public void recibirHerida() {
        herido = true;
        System.out.println("[Caballero " + nombre + "] Herido gravemente en duelo");
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (herido) {
                    recuperarse();
                } else if (generadorAleatorio.nextDouble() < 0.5) {
                    realizarVigilancia();
                } else {
                    hablarConLance();
                }
            } catch (InterruptedException e) {
                System.err.println("[Caballero " + nombre + "] Interrumpido");
            }
        }
    }

    private void recuperarse() throws InterruptedException {
        System.out.println("[Caballero " + nombre + "] Recuperandose de herida (30s)...");
        Thread.sleep(30000);
        herido = false;
        System.out.println("[Caballero " + nombre + "] Recuperado");
    }

    private void realizarVigilancia() throws InterruptedException {
        Thread.sleep(6000);
        String lugar = lugaresVigilancia[generadorAleatorio.nextInt(lugaresVigilancia.length)];
        System.out.println("[Caballero " + nombre + "] Vigilancia completada en " + lugar + " (6s)");
    }

    private void hablarConLance() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_SERVIDOR_LANCE);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            conexion.setSoTimeout(25000);

            String mensaje = generadorAleatorio.nextDouble() < 0.25 ? "AFRENTA" : "CONVERSACION";
            escritor.println(mensaje);

            String respuesta = lector.readLine();
            if (respuesta != null && respuesta.equals("RECIBIDO")) {
                if ("AFRENTA".equals(mensaje)) {
                    System.out.println("[Caballero " + nombre + "] Ofende a Lance sobre Elisabetha");
                } else {
                    System.out.println("[Caballero " + nombre + "] Confidencia personal a Lance");
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[Caballero " + nombre + "] Lance no lo atendio en 25s");
        } catch (IOException e) {
        }
    }
}

