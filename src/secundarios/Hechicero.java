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
        System.out.println("🧪 Un hechicero oscuro ha llegado a Roedalia con intenciones misteriosas");
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
                System.err.println("⚠️ El hechicero fue interrumpido en sus artes oscuras");
            }
        }
    }

    private void investigarGrimorios() {
        try {
            System.out.println("📖 El hechicero estudia grimorios antiguos en su torre (30 segundos)...");
            Thread.sleep(30000);
            
            double resultado = generadorAleatorio.nextDouble();
            if (resultado < 0.30) {
                invocarDeposito("ALMACENAR_E");
            } else if (resultado < 0.60) {
                invocarDeposito("ALMACENAR_L");
            } else {
                System.out.println("💥 El hechicero fracasa en su conjuro (40% de probabilidad)");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void visitarDama() {
        if (invocarDeposito("RETIRAR_E")) {
            try {
                System.out.println("🌹 El hechicero se acerca a la torre de Elisabetha con un frasco misterioso...");
                Thread.sleep(5000);

                if (generadorAleatorio.nextDouble() < 0.30) {
                    dama.actualizarEstadoAfinidad(-20);
                    System.out.println("🎭 ¡Éxito! Elisabetha bebió el elixir de belleza engañoso (-20 afinidad)");
                } else {
                    System.out.println("🛡️ ¡Lamentos! Elisabetha detectó el engaño y rechazó el elixir");
                }
            } catch (Exception e) {
                System.err.println("⚠️ El hechicero no pudo completar su visita a Elisabetha");
            }
        } else {
            System.out.println("🧪 El hechicero no tiene elixires disponibles para Elisabetha");
        }
    }

    private void visitarCaballero() {
        if (invocarDeposito("RETIRAR_L")) {
            try {
                System.out.println("⚔️ El hechicero convoca a Lance con noticias urgentes...");
                Thread.sleep(7000);

                double estrategia = generadorAleatorio.nextDouble();
                if (estrategia < 0.80) {
                    // Estrategia de engaño (80%)
                    if (generadorAleatorio.nextDouble() < 0.20) {
                        caballero.actualizarEstadoAfinidad(-20);
                        System.out.println("🎭 Lance cree haber fallado al reino (-20 afinidad)");
                    } else {
                        System.out.println("🛡️ Lance ignoró las falsas acusaciones del hechicero");
                    }
                } else {
                    // Amenaza del Frente Norte (20%)
                    if (generadorAleatorio.nextDouble() < 0.20) {
                        caballero.actualizarEstadoAfinidad(-30);
                        System.out.println("❄️ Lance teme la amenaza del Frente Norte (-30 afinidad)");
                    } else {
                        System.out.println("💪 Lance no se dejó amedrentar por las amenazas");
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ El hechicero no pudo completar su visita a Lance");
            }
        } else {
            System.out.println("🧪 El hechicero no tiene argumentos disponibles para Lance");
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
            System.err.println("⚠️ El hechicero no pudo contactar el depósito de pociones: " + e.getMessage());
            return false;
        }
    }
}
