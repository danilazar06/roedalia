package servidores;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;

public class ServidorDescansoGuerrero implements Runnable {
    private boolean presenciaElisabetha = false;
    private boolean presenciaLance = false;
    private boolean encuentroPrevio = false;
    private boolean selloDeCruce = false;
    private int puntosDelCruce = 0;
    private final Object monitorSincronizacion = new Object();

    @Override
    public void run() {
        try (ServerSocket servidor = new ServerSocket(ParametrosReino.PUERTO_DESCANSO_GUERRERO)) {
            System.out.println("[Descanso del Guerrero] Servidor iniciado en puerto " + ParametrosReino.PUERTO_DESCANSO_GUERRERO);

            while (ParametrosReino.tabernaDestruida == false) {
                Socket conexion = servidor.accept();
                new Thread(() -> atenderCliente(conexion)).start();
            }
            reconstruirTaberna();

        } catch (IOException e) {
            System.err.println("[Descanso del Guerrero] Error al iniciar servidor: " + e.getMessage());
        }
    }

    private void atenderCliente(Socket conexion) {
        while (ParametrosReino.tabernaDestruida = false){
            try (BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                 PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true)) {

                String nombre = lector.readLine();
                int puntosDestino = procesarVisita(nombre);
                escritor.println(String.valueOf(puntosDestino));

                System.out.println("[Descanso del Guerrero] " + nombre + " sale con " + puntosDestino + " puntos");
            } catch (IOException e) {
                System.err.println("[Descanso del Guerrero] Error atendiendo cliente: " + e.getMessage());
            } finally {
                try { conexion.close(); } catch (IOException ignored) {}
            }
        }
        System.out.println("[Descanso del guerrero] No puedo atenderleeee! La taberna se está quemandoo!");
    }

    private int procesarVisita(String nombre) {
        synchronized (monitorSincronizacion) {
            while (ParametrosReino.tabernaDestruida = false){
                registrarEntrada(nombre);

                esperarCoincidencia(nombre);

                int puntos = selloDeCruce ? puntosDelCruce : 0;

                registrarSalida(nombre, puntos);

                return puntos;
            }
        }
        System.out.println("[Descanso del guerrero] Todo el mundo fuera ahora mismo, se está quemando la tabernaaaaa!");
        return 0;
    }

    private void registrarEntrada(String nombre) {

        while (ParametrosReino.tabernaDestruida = false){
            if ("Elisabetha".equals(nombre)) {
                presenciaElisabetha = true;
                System.out.println("[Descanso del Guerrero] Elisabetha entra");
            } else if ("Lance".equals(nombre)) {
                presenciaLance = true;
                System.out.println("[Descanso del Guerrero] Lance entra");
            }

            if (presenciaElisabetha && presenciaLance && !selloDeCruce) {
                selloDeCruce = true;
                if (!encuentroPrevio) {
                    puntosDelCruce = 75;
                    System.out.println("[Descanso del Guerrero] Primer encuentro entre Elisabetha y Lance");
                } else {
                    puntosDelCruce = 10;
                }
            }

            monitorSincronizacion.notifyAll();
        }
        System.out.println("[Descanso del guerrero] Aquí no entra ni dios, ni los enamoradooooos");
    }

    private void esperarCoincidencia(String nombre) {

        while (ParametrosReino.tabernaDestruida=false){
            long tiempoMaximoEstancia = "Elisabetha".equals(nombre) ? 5000 : 8000;
            long momentoLimite = System.currentTimeMillis() + tiempoMaximoEstancia;

            while (System.currentTimeMillis() < momentoLimite && !selloDeCruce) {
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
        }
        System.out.println("[Descanso del guerrero] Todo el mundo fueraaa!");
    }

    private void registrarSalida(String nombre, int puntos) {
        while (ParametrosReino.tabernaDestruida=false){
            if ("Elisabetha".equals(nombre)) {
                presenciaElisabetha = false;
            } else if ("Lance".equals(nombre)) {
                presenciaLance = false;
            }

            if (puntos == 75) {
                encuentroPrevio = true;
            }

            if (!presenciaElisabetha && !presenciaLance) {
                selloDeCruce = false;
                puntosDelCruce = 0;
            }

            monitorSincronizacion.notifyAll();
        }
    }

    private void reconstruirTaberna(){
        new Thread(() -> {
            try { Thread.sleep(20000); } catch (InterruptedException e) {}
            ParametrosReino.tabernaDestruida = false;
            System.out.println("[Taberna] La taberna ha sido reconstruida!");
        }).start();
    }
}
