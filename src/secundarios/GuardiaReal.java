package secundarios;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GuardiaReal extends Thread {
    private String nombre;
    private boolean herido = false;
    private Random generadorAleatorio = new Random();

    public GuardiaReal(String n) { 
        this.nombre = n; 
        System.out.println("[Caballero del Porton] " + nombre + " iniciado");
    }

    public void recibirHerida() { 
        herido = true; 
        System.out.println("[Caballero del Porton] " + nombre + " herido");
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (herido) {
                    recuperarseEnEnfermeria();
                } else if (generadorAleatorio.nextDouble() < 0.5) {
                    realizarVigilancia();
                } else {
                    dialogarConLance();
                }
            } catch (InterruptedException e) {
                System.err.println("[Caballero del Porton] " + nombre + " interrumpido");
            }
        }
    }

    private void recuperarseEnEnfermeria() throws InterruptedException {
        System.out.println("[Caballero del Porton] " + nombre + " recuperandose (30s)");
        Thread.sleep(30000);
        herido = false;
        System.out.println("[Caballero del Porton] " + nombre + " recuperado");
    }

    private void realizarVigilancia() throws InterruptedException {
        Thread.sleep(6000);
        System.out.println("[Caballero del Porton] " + nombre + " ronda completada (6s)");
    }

    private void dialogarConLance() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_SERVIDOR_LANCE);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {
            
            conexion.setSoTimeout(25000);
            
            String mensaje = generadorAleatorio.nextDouble() < 0.25 ? "AFRENTA" : "CONVERSACION";
            escritor.println(mensaje);
            
            String respuesta = lector.readLine();
            if (respuesta != null && respuesta.equals("RECIBIDO")) {
                System.out.println("[Caballero del Porton] " + nombre + " dialogo con Lance");
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[Caballero del Porton] " + nombre + ": timeout con Lance");
        } catch (IOException e) {
            System.err.println("[Caballero del Porton] " + nombre + " error de conexion: " + e.getMessage());
        }
    }
}
