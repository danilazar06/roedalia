package secundarios;

import configuracion.ParametrosReino;
import protagonistas.*;
import java.io.*;
import java.net.*;
import java.util.Random;

public class Hechicero extends Thread {
    private DamaElisabetha dama;
    private CaballeroLance caballero;
    private Random generadorAleatorio = new Random();

    public Hechicero(DamaElisabetha e, CaballeroLance l) {
        this.dama = e;
        this.caballero = l;
        System.out.println("[Alquimista] Iniciado");
    }

    @Override
    public void run() {
        while (true) {
            try {
                double decision = generadorAleatorio.nextDouble();
                
                if (decision < 0.60) {
                    investigarGrimorios();
                } else if (decision < 0.80) {
                    visitarDama();
                } else {
                    visitarCaballero();
                }
                
                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println("[Alquimista] Error en actividad");
            }
        }
    }

    private void investigarGrimorios() {
        try {
            System.out.println("[Alquimista] Preparando pociones (30s)...");
            Thread.sleep(30000);
            
            double resultado = generadorAleatorio.nextDouble();
            if (resultado < 0.30) {
                invocarDeposito("ALMACENAR_E");
            } else if (resultado < 0.60) {
                invocarDeposito("ALMACENAR_L");
            } else {
                System.out.println("[Alquimista] Pocion fallida");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void visitarDama() {
        if (invocarDeposito("RETIRAR_E")) {
            try {
                System.out.println("[Alquimista] Visitando a Elisabetha (5s)...");
                Thread.sleep(5000);

                if (generadorAleatorio.nextDouble() < 0.30) {
                    dama.actualizarEstadoChispa(-20);
                    System.out.println("[Alquimista] Elisabetha acepto pocima (-20 chispa)");
                } else {
                    System.out.println("[Alquimista] Elisabetha rechazo la pocima");
                }
            } catch (Exception e) {
                System.err.println("[Alquimista] Error visitando a Elisabetha");
            }
        } else {
            System.out.println("[Alquimista] Sin pociones para Elisabetha");
        }
    }

    private void visitarCaballero() {
        try {
            System.out.println("[Alquimista] Visitando a Lance (7s)...");
            Thread.sleep(7000);

            double estrategia = generadorAleatorio.nextDouble();
            if (estrategia < 0.80) {
                if (invocarDeposito("RETIRAR_L")) {
                    if (generadorAleatorio.nextDouble() < 0.20) {
                        caballero.actualizarEstadoChispa(-20);
                        System.out.println("[Alquimista] Lance afectado por pocima (-20 chispa)");
                    } else {
                        System.out.println("[Alquimista] Lance resistio la pocima");
                    }
                } else {
                    System.out.println("[Alquimista] Sin pociones para Lance");
                }
            } else {
                if (generadorAleatorio.nextDouble() < 0.20) {
                    caballero.actualizarEstadoChispa(-30);
                    System.out.println("[Alquimista] Lance afectado por amenaza del Frente Norte (-30 chispa)");
                } else {
                    System.out.println("[Alquimista] Lance resistio la amenaza");
                }
            }
        } catch (Exception e) {
            System.err.println("[Alquimista] Error visitando a Lance");
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
            System.err.println("[Alquimista] Error al contactar deposito: " + e.getMessage());
            return false;
        }
    }
}
