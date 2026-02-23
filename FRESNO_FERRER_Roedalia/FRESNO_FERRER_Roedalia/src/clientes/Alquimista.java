package clientes;

import comun.Constantes;
import java.io.*;
import java.net.Socket;

public class Alquimista extends Thread {
    private Elisabetha eli;
    private Lance lan;

    public Alquimista(Elisabetha e, Lance l) {
        this.eli = e;
        this.lan = l;
    }

    @Override
    public void run() {
        while (true) {
            try {
                double r = Math.random();
                if (r < 0.60) {
                    estudiar();
                } else if (r < 0.80) {
                    visitarEli();
                } else {
                    visitarLan();
                }
                // Breve pausa para no saturar el procesador en el ciclo infinito
                Thread.sleep(100);
            } catch (Exception e) {
                // Silencio en la mazmorra
            }
        }
    }

    private void estudiar() {
        try {
            System.out.println("ALQUIMISTA: Estudiando calderos (30s)...");
            Thread.sleep(30000); // Tiempo de estudio según Canto Quinto
            double ex = Math.random();
            if (ex < 0.30) {
                llamarAlacena("GUARDAR_E");
            } else if (ex < 0.60) {
                llamarAlacena("GUARDAR_L");
            } else {
                System.out.println("ALQUIMISTA: Fracaso en la poción (40%).");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void visitarEli() {
        // Solo puede visitar si hay poción disponible
        if (llamarAlacena("SACAR_E")) {
            try {
                System.out.println("ALQUIMISTA: Visitando a Elisabetha...");
                Thread.sleep(5000); // Duración de la visita

                // 30% de probabilidad de engaño con tónico de belleza
                if (Math.random() < 0.30) {
                    eli.modificarChispa(-20);
                    System.out.println("ALQUIMISTA: ¡Éxito! Elisabetha bebió el tónico.");
                } else {
                    System.out.println("ALQUIMISTA: ¡Lamentos! Elisabetha no cayó en el engaño.");
                }
            } catch (Exception e) {}
        } else {
            System.out.println("ALQUIMISTA: Sin pociones para Elisabetha. Me lamento.");
        }
    }

    private void visitarLan() {
        // Solo visita si hay excusa/poción disponible
        if (llamarAlacena("SACAR_L")) {
            try {
                System.out.println("ALQUIMISTA: Citando a Lance al orden...");
                Thread.sleep(7000); // Duración de la visita

                double r = Math.random();
                if (r < 0.80) { // Intento de Engaño (80%)
                    if (Math.random() < 0.20) { // Probabilidad de éxito 20%
                        lan.modificarChispa(-20);
                        System.out.println("ALQUIMISTA: Lance cree haber fallado al reino (-20).");
                    } else {
                        System.out.println("ALQUIMISTA: Lance ignoró la supuesta afrenta.");
                    }
                } else { // Amenaza Frente Norte (20%)
                    if (Math.random() < 0.20) { // Probabilidad de éxito 20%
                        lan.modificarChispa(-30);
                        System.out.println("ALQUIMISTA: Lance teme el Frente Norte (-30).");
                    } else {
                        System.out.println("ALQUIMISTA: Lance no se deja amedrentar.");
                    }
                }
            } catch (Exception e) {}
        } else {
            System.out.println("ALQUIMISTA: No tengo excusas para citar a Lance.");
        }
    }

    private boolean llamarAlacena(String cmd) {
        try (Socket s = new Socket(Constantes.HOST, Constantes.PUERTO_ALACENA);
             DataOutputStream dos = new DataOutputStream(s.getOutputStream());
             DataInputStream dis = new DataInputStream(s.getInputStream())) {
            dos.writeUTF(cmd);
            return dis.readBoolean();
        } catch (Exception e) {
            return false;
        }
    }
}