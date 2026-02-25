package secundarios;

import configuracion.ParametrosReino;
import protagonistas.*;
import java.io.*;
import java.net.*;
import java.util.Random;

public class Alquimista extends Thread {
    private String nombre;
    private DamaElisabetha elisabetha;
    private CaballeroLance lance;
    private Random generadorAleatorio = new Random();

    public Alquimista(String nombre, DamaElisabetha e, CaballeroLance l) {
        this.nombre = nombre;
        this.elisabetha = e;
        this.lance = l;
        System.out.println("[Alquimista " + nombre + "] Iniciado");
    }

    @Override
    public void run() {
        while (true) {
            try {
                double decision = generadorAleatorio.nextDouble();

                if (decision < 0.60) {
                    estudiarCalderos();
                } else if (decision < 0.80) {
                    visitarElisabetha();
                } else {
                    visitarLance();
                }

                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println("[Alquimista " + nombre + "] Error en actividad");
            }
        }
    }

    private void estudiarCalderos() throws InterruptedException {
        System.out.println("[Alquimista " + nombre + "] Estudiando calderos (30s)...");
        Thread.sleep(30000);

        double resultado = generadorAleatorio.nextDouble();
        if (resultado < 0.30) {
            if (invocarDeposito("ALMACENAR_E")) {
                System.out.println("[Alquimista " + nombre + "] Pocion para Elisabetha creada");
            }
        } else if (resultado < 0.60) {
            if (invocarDeposito("ALMACENAR_L")) {
                System.out.println("[Alquimista " + nombre + "] Pocion para Lance creada");
            }
        } else {
            System.out.println("[Alquimista " + nombre + "] Pocion fallida (40%)");
        }
    }

    private void visitarElisabetha() throws InterruptedException {
        if (!invocarDeposito("RETIRAR_E")) {
            System.out.println("[Alquimista " + nombre + "] Sin pociones para Elisabetha, se lamenta");
            return;
        }

        System.out.println("[Alquimista " + nombre + "] Visitando a Elisabetha (5s)...");
        Thread.sleep(5000);

        if (generadorAleatorio.nextDouble() < 0.30) {
            elisabetha.actualizarEstadoChispa(-20);
            System.out.println("[Alquimista " + nombre + "] Elisabetha acepto el tonico envenenado (-20 chispa)");
        } else {
            System.out.println("[Alquimista " + nombre + "] Elisabetha rechazo la pocima, el alquimista da grandes voces");
        }
    }

    private void visitarLance() throws InterruptedException {
        double estrategia = generadorAleatorio.nextDouble();

        if (estrategia < 0.80) {
            if (!invocarDeposito("RETIRAR_L")) {
                System.out.println("[Alquimista " + nombre + "] Sin pociones para Lance, se lamenta");
                return;
            }

            System.out.println("[Alquimista " + nombre + "] Visitando a Lance con pocima (7s)...");
            Thread.sleep(7000);

            if (generadorAleatorio.nextDouble() < 0.20) {
                lance.actualizarEstadoChispa(-20);
                System.out.println("[Alquimista " + nombre + "] Lance cae en el engano (-20 chispa)");
            } else {
                System.out.println("[Alquimista " + nombre + "] Lance resistio la pocima, el alquimista da grandes voces");
            }
        } else {
            System.out.println("[Alquimista " + nombre + "] Amenazando a Lance con el Frente Norte (7s)...");
            Thread.sleep(7000);

            if (generadorAleatorio.nextDouble() < 0.20) {
                lance.actualizarEstadoChispa(-30);
                System.out.println("[Alquimista " + nombre + "] Lance afectado por amenaza del Frente Norte (-30 chispa)");
            } else {
                System.out.println("[Alquimista " + nombre + "] Lance resiste la amenaza, el alquimista da grandes voces");
            }
        }
    }

    private boolean invocarDeposito(String comando) {
        try (Socket conexion = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DEPOSITO_POCIONES);
             PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()))) {

            escritor.println(comando);
            String respuesta = lector.readLine();
            return "true".equals(respuesta);
        } catch (Exception e) {
            System.err.println("[Alquimista " + nombre + "] Error al contactar deposito: " + e.getMessage());
            return false;
        }
    }
}

