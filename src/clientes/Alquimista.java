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
            System.out.println("Alquimista: Analizando formulas en sus calderos (30s)...");
            Thread.sleep(30000);
            double suerte = generador.nextDouble();
            if (suerte < 0.30) {
                gestionarInventario("GUARDAR_E");
            } else if (suerte < 0.60) {
                gestionarInventario("GUARDAR_L");
            } else {
                System.out.println("Alquimista: La mezcla no alcanzo la consistencia deseada, descartando residuos.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void buscarDama() {
        if (gestionarInventario("SACAR_E")) {
            try {
                System.out.println("Alquimista: Preparando una pocima destinada a la Dama Elisabetha...");
                Thread.sleep(5000);
                if (generador.nextDouble() < 0.30) {
                    protagonistaFemenina.modificarChispa(-20);
                    System.out.println("Alquimista: Elisabetha acepto la pocima sin sospechar del engano.");
                } else {
                    System.out.println("Alquimista: Elisabetha rechazo la pocima con cautela.");
                }
            } catch (Exception e) {}
        } else {
            System.out.println("Alquimista: No dispongo de elixires preparados para la Dama en este momento.");
        }
    }

    private void buscarGuerrero() {
        if (gestionarInventario("SACAR_L")) {
            try {
                System.out.println("Alquimista: Enviando un emisario con un mensaje cifrado para Lance...");
                Thread.sleep(7000);

                double accion = generador.nextDouble();
                if (accion < 0.80) {
                    if (generador.nextDouble() < 0.20) {
                        protagonistaMasculino.modificarChispa(-20);
                        System.out.println("Alquimista: Lance ha caido en la trampa del falso mensajero (-20).");
                    } else {
                        System.out.println("Alquimista: El Caballero Lance desestimo las provocaciones del alquimista.");
                    }
                } else {
                    if (generador.nextDouble() < 0.20) {
                        protagonistaMasculino.modificarChispa(-30);
                        System.out.println("Alquimista: Las sombras del Norte perturban al Caballero (-30).");
                    } else {
                        System.out.println("Alquimista: Lance demostro una voluntad inquebrantable ante la amenaza.");
                    }
                }
            } catch (Exception e) {}
        } else {
            System.out.println("Alquimista: Carezco de motivos suficientes para importunar al Caballero.");
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

