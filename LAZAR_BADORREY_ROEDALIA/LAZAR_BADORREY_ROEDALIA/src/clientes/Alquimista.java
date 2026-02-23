package clientes;

import comun.Constantes;
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
            System.out.println("[Hechicero] Preparando ingredientes oscuros en silencio (30s)...");
            Thread.sleep(30000);
            double suerte = generador.nextDouble();
            if (suerte < 0.30) {
                gestionarInventario("GUARDAR_E");
            } else if (suerte < 0.60) {
                gestionarInventario("GUARDAR_L");
            } else {
                System.out.println("[Hechicero] La mezcla se ha arruinado.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void buscarDama() {
        if (gestionarInventario("SACAR_E")) {
            try {
                System.out.println("[Hechicero] Acechando a la dama Elisabetha...");
                Thread.sleep(5000);
                if (generador.nextDouble() < 0.30) {
                    protagonistaFemenina.modificarChispa(-20);
                    System.out.println("[Hechicero] Excelente, la dama cayó en la trampa del falso tónico.");
                } else {
                    System.out.println("[Hechicero] Maldición, Elisabetha desconfió de mi ofrenda.");
                }
            } catch (Exception e) {}
        } else {
            System.out.println("[Hechicero] No dispongo de elixires para la dama en este momento.");
        }
    }

    private void buscarGuerrero() {
        if (gestionarInventario("SACAR_L")) {
            try {
                System.out.println("[Hechicero] Enviando mensajero oscuro a Lance...");
                Thread.sleep(7000);

                double accion = generador.nextDouble();
                if (accion < 0.80) {
                    if (generador.nextDouble() < 0.20) {
                        protagonistaMasculino.modificarChispa(-20);
                        System.out.println("[Hechicero] Lance duda de su lealtad al reino (-20).");
                    } else {
                        System.out.println("[Hechicero] El caballero ignoró mis provocaciones.");
                    }
                } else {
                    if (generador.nextDouble() < 0.20) {
                        protagonistaMasculino.modificarChispa(-30);
                        System.out.println("[Hechicero] Las sombras del Norte aterrorizan al guerrero (-30).");
                    } else {
                        System.out.println("[Hechicero] El guerrero mostró una voluntad de hierro.");
                    }
                }
            } catch (Exception e) {}
        } else {
            System.out.println("[Hechicero] Carezco de motivos para molestar al caballero.");
        }
    }

    private boolean gestionarInventario(String comando) {
        try (Socket s = new Socket(Constantes.HOST, Constantes.PUERTO_ALACENA);
             DataOutputStream dos = new DataOutputStream(s.getOutputStream());
             DataInputStream dis = new DataInputStream(s.getInputStream())) {
            dos.writeUTF(comando);
            return dis.readBoolean();
        } catch (Exception e) {
            return false;
        }
    }
}