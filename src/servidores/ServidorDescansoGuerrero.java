package servidores;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;

public class ServidorDescansoGuerrero implements Runnable {
    private boolean presenciaElisabetha = false;
    private boolean presenciaLance = false;
    private boolean encuentroPrevio = false;
    private final Object monitorSincronizacion = new Object();

    @Override
    public void run() {
        try (ServerSocket servidor = new ServerSocket(ParametrosReino.PUERTO_DESCANSO_GUERRERO)) {
            System.out.println("🍺 El Descanso del Guerrero abre sus puertas en el puerto " + ParametrosReino.PUERTO_DESCANSO_GUERRERO);
            
            while (true) {
                try (Socket conexion = servidor.accept();
                     BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                     PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true)) {
                    
                    String nombre = lector.readLine();
                    int puntosDestino = procesarVisita(nombre);
                    escritor.println(String.valueOf(puntosDestino));
                    
                    System.out.println("🍻 " + nombre + " abandona el Descanso del Guerrero con " + puntosDestino + " puntos de destino");
                }
            }
        } catch (IOException e) {
            System.err.println("❌ El Descanso del Guerrero no pudo abrir sus puertas: " + e.getMessage());
        }
    }

    private int procesarVisita(String nombre) {
        synchronized (monitorSincronizacion) {
            // Registrar entrada del visitante
            registrarEntrada(nombre);
            
            // Esperar por el otro protagonista o timeout
            int puntos = esperarCoincidencia(nombre);
            
            // Registrar salida del visitante
            registrarSalida(nombre, puntos);
            
            return puntos;
        }
    }

    private void registrarEntrada(String nombre) {
        if ("Elisabetha".equals(nombre)) {
            presenciaElisabetha = true;
            System.out.println("👑 Elisabetha cruza el umbral del Descanso del Guerrero");
        } else if ("Lance".equals(nombre)) {
            presenciaLance = true;
            System.out.println("⚔️ Lance entra al Descanso del Guerrero buscando descanso");
        }
        
        // Notificar a posibles hilos esperando
        monitorSincronizacion.notifyAll();
    }

    private int esperarCoincidencia(String nombre) {
        long tiempoMaximoEstancia = "Elisabetha".equals(nombre) ? 5000 : 8000;
        long momentoEntrada = System.currentTimeMillis();
        long momentoLimite = momentoEntrada + tiempoMaximoEstancia;
        
        // Esperar activamente por el otro protagonista
        while (System.currentTimeMillis() < momentoLimite && (!presenciaElisabetha || !presenciaLance)) {
            try {
                long tiempoRestante = momentoLimite - System.currentTimeMillis();
                if (tiempoRestante > 0) {
                    monitorSincronizacion.wait(tiempoRestante);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        return determinarPuntosDestino();
    }

    private int determinarPuntosDestino() {
        if (presenciaElisabetha && presenciaLance) {
            if (!encuentroPrevio) {
                return 75; // Primer encuentro fatídico
            } else {
                return 10; // Reencuentro fortuito
            }
        }
        return 0; // No hubo coincidencia
    }

    private void registrarSalida(String nombre, int puntos) {
        if ("Elisabetha".equals(nombre)) {
            presenciaElisabetha = false;
        } else if ("Lance".equals(nombre)) {
            presenciaLance = false;
        }
        
        // Marcar el encuentro como realizado solo si se entregaron puntos
        if (puntos == 75) {
            encuentroPrevio = true;
            System.out.println("💫 ¡El destino ha unido a Elisabetha y Lance en el Descanso del Guerrero!");
        }
        
        // Notificar a posibles hilos esperando
        monitorSincronizacion.notifyAll();
    }
}
