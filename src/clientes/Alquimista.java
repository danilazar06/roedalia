package clientes;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Alquimista extends Thread {
    private Elisabetha protagonistaFemenina;
    private Lance protagonistaMasculino;
    private final Random generador = new Random();

    public Alquimista(Elisabetha e, Lance l) {
        this.protagonistaFemenina = e;
        this.protagonistaMasculino = l;
    }

    @Override
    public void run() {
        while (true) {
            try {
                double probabilidad = generador.nextDouble();
                if (probabilidad < 0.60) {
                    prepararBrebajes();
                } else if (probabilidad < 0.80) {
                    buscarDama();
                } else {
                    buscarGuerrero();
                }
                Thread.sleep(100);
            } catch (Exception e) {}
        }
    }

    private void prepararBrebajes() {
        try {
            System.out.println("[Alquimista] Preparando pociones (30s)...");
            Thread.sleep(30000);
            double suerte = generador.nextDouble();
            if (suerte < 0.30) {
                gestionarInventario("ALMACENAR_E");
            } else if (suerte < 0.60) {
                gestionarInventario("ALMACENAR_L");
            } else {
                System.out.println("[Alquimista] Pocion fallida");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void buscarDama() {
        if (gestionarInventario("RETIRAR_E")) {
            try {
                System.out.println("[Alquimista] Visitando a Elisabetha (5s)...");
                Thread.sleep(5000);
                if (generador.nextDouble() < 0.30) {
                    protagonistaFemenina.modificarChispa(-20);
                    System.out.println("[Alquimista] Elisabetha acepto pocima (-20)");
                } else {
                    System.out.println("[Alquimista] Elisabetha rechazo la pocima");
                }
            } catch (Exception e) {}
        } else {
            System.out.println("[Alquimista] Sin pociones para Elisabetha");
        }
    }

    private void buscarGuerrero() {
        try {
            System.out.println("[Alquimista] Visitando a Lance (7s)...");
            Thread.sleep(7000);

            double accion = generador.nextDouble();
            if (accion < 0.80) {
                if (gestionarInventario("RETIRAR_L")) {
                    if (generador.nextDouble() < 0.20) {
                        protagonistaMasculino.modificarChispa(-20);
                        System.out.println("[Alquimista] Lance afectado por pocima (-20)");
                    } else {
                        System.out.println("[Alquimista] Lance resistio la pocima");
                    }
                } else {
                    System.out.println("[Alquimista] Sin pociones para Lance");
                }
            } else {
                if (generador.nextDouble() < 0.20) {
                    protagonistaMasculino.modificarChispa(-30);
                    System.out.println("[Alquimista] Lance afectado por amenaza (-30)");
                } else {
                    System.out.println("[Alquimista] Lance resistio la amenaza");
                }
            }
        } catch (Exception e) {}
    }

    private boolean gestionarInventario(String comando) {
        try (Socket s = new Socket(ParametrosReino.DOMINIO_LOCAL, ParametrosReino.PUERTO_DEPOSITO_POCIONES);
             PrintWriter escritor = new PrintWriter(s.getOutputStream(), true);
             BufferedReader lector = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            escritor.println(comando);
            String respuesta = lector.readLine();
            return "true".equals(respuesta);
        } catch (Exception e) {
            return false;
        }
    }
}
