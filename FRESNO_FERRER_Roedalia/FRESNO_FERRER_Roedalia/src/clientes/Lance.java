package clientes;
import comun.Constantes;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Lance extends Thread {
    private int chispa = 0;
    private boolean seConocen = false;
    public LinkedBlockingQueue<String> buzon;

    public Lance(LinkedBlockingQueue<String> buzon) { this.buzon = buzon; }

    public synchronized void modificarChispa(int n) {
        // Blindaje Nivel 100
        if (this.chispa == 100) return;

        if (n == 75) {
            chispa = 75;
            seConocen = true;
        } else {
            int nueva = chispa + n;
            // Tope de 50 antes de conocerse
            if (!seConocen && n > 0) nueva = Math.min(50, nueva);
            chispa = Math.max(0, Math.min(100, nueva));
        }
        System.out.println(">>> LANCE: Chispa actual = " + chispa);
    }

    public synchronized int getChispa() { return chispa; }

    @Override
    public void run() {
        while (getChispa() < 100) {
            try {
                int r = (int)(Math.random() * 3);
                switch(r) {
                    case 0: hablarCompaneros(); break;
                    case 1: realizarGuardia(); break;
                    case 2: /* El duelo se dispara por mensaje de Caballero */ break;
                }
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
        System.out.println("⚔️ LANCE ALCANZÓ NIVEL 100 Y VIGILA EL PORTÓN.");
    }

    private void hablarCompaneros() throws InterruptedException {
        Thread.sleep(4000);
        String msg = buzon.poll();
        if (msg != null && msg.equals("OFENSA")) desafiarDuelo();
    }

    private void desafiarDuelo() throws InterruptedException {
        System.out.println("LANCE: ¡Duelo por honor!");
        Thread.sleep(5000);
        if (Math.random() < 0.20) {
            modificarChispa(-5); // Daña gravemente al oponente (Se siente mal)
        } else {
            modificarChispa(7); // Vence sin dañar
        }
    }

    private void realizarGuardia() {
        if (Math.random() < 0.5) inspeccionarPorton();
        else visitarTaberna();
    }

    private void inspeccionarPorton() {
        try (Socket s = new Socket(Constantes.HOST, Constantes.PUERTO_PORTON);
             DataOutputStream dos = new DataOutputStream(s.getOutputStream());
             DataInputStream dis = new DataInputStream(s.getInputStream())) {

            Thread.sleep(5000);
            dos.writeUTF("INSPECCIONAR");
            System.out.println("LANCE: " + dis.readUTF());
        } catch (Exception e) {}
    }

    private void visitarTaberna() {
        try (Socket s = new Socket(Constantes.HOST, Constantes.PUERTO_TABERNA);
             DataOutputStream dos = new DataOutputStream(s.getOutputStream());
             DataInputStream dis = new DataInputStream(s.getInputStream())) {

            dos.writeUTF("Lance");
            int res = dis.readInt();
            if (res > 0) modificarChispa(res == 75 ? 75 : 10);
        } catch (Exception e) {}
    }
}