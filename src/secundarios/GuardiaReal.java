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
        System.out.println("🛡️ El guardia real " + nombre + " ha tomado su puesto");
    }

    public void recibirHerida() { 
        herido = true; 
        System.out.println("🩸 El guardia " + nombre + " ha sido herido en combate");
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
                System.err.println("⚠️ El guardia " + nombre + " fue interrumpido en su servicio");
            }
        }
    }

    private void recuperarseEnEnfermeria() throws InterruptedException {
        System.out.println("🏥 El guardia " + nombre + " se recupera en la enfermería (30 segundos)");
        Thread.sleep(30000);
        herido = false;
        System.out.println("💪 El guardia " + nombre + " ha sido dado de alta");
    }

    private void realizarVigilancia() throws InterruptedException {
        Thread.sleep(6000);
        System.out.println("👁️ El guardia " + nombre + " completa su ronda de vigilancia");
    }

    private void dialogarConLance() {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_SERVIDOR_LANCE);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {
            
            // Esperar respuesta por hasta 25 segundos
            conexion.setSoTimeout(25000);
            
            String mensaje = generadorAleatorio.nextDouble() < 0.25 ? "AFRENTA" : "CONVERSACION";
            escritor.println(mensaje);
            
            String respuesta = lector.readLine();
            if (respuesta != null && respuesta.equals("RECIBIDO")) {
                System.out.println("🗣️ El guardia " + nombre + " ha dialogado con Lance");
            }
        } catch (SocketTimeoutException e) {
            System.out.println("⏰ El guardia " + nombre + ": Lance no pudo recibir el mensaje (timeout)");
        } catch (IOException e) {
            System.err.println("⚠️ El guardia " + nombre + " no pudo contactar a Lance: " + e.getMessage());
        }
    }
}
