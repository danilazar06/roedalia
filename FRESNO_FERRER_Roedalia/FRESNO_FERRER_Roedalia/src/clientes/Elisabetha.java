package clientes;
import comun.Constantes;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Elisabetha extends Thread {
    private int chispa = 0;
    private boolean seConocen = false;
    public LinkedBlockingQueue<String> buzon;

    public Elisabetha(LinkedBlockingQueue<String> buzon) { this.buzon = buzon; }

    public synchronized void modificarChispa(int n) {
        // Blindaje Nivel 100: Una vez alcanzado, nada lo baja
        if (this.chispa == 100) return;

        if (n == 75) {
            chispa = 75;
            seConocen = true;
        } else {
            int nueva = chispa + n;
            // Tope de 30 antes de conocerse
            if (!seConocen && n > 0) nueva = Math.min(30, nueva);
            chispa = Math.max(0, Math.min(100, nueva));
        }
        System.out.println(">>> ELISABETHA: Chispa = " + chispa + " (Conoce a Lance: " + seConocen + ")");
    }

    public synchronized int getChispa() { return chispa; }

    @Override
    public void run() {
        while (getChispa() < 100) {
            try {
                int r = (int)(Math.random() * 4); // 25% cada acción
                switch(r) {
                    case 0: atenderDamas(); break;
                    case 1: asistirBaile(); break;
                    case 2: leerPergaminos(); break;
                    case 3: escaparse(); break;
                }
                Thread.sleep(1000); // Pausa entre acciones
            } catch (Exception e) {}
        }
        System.out.println("💖 ELISABETHA ALCANZÓ NIVEL 100 Y ESPERA EN LA VENTANA.");
    }

    private void atenderDamas() throws InterruptedException {
        Thread.sleep(4000);
        String msg = buzon.poll();
        if (msg != null && msg.equals("RUMOR")) modificarChispa(-5);
    }

    private void asistirBaile() throws InterruptedException {
        // Solo el 20% de las veces asiste inevitablemente
        if (Math.random() < 0.20) {
            Thread.sleep(5000);
            modificarChispa(-5);
            System.out.println("ELISABETHA: Asistió forzada a un baile (-5)");
        } else {
            System.out.println("ELISABETHA: Esquivó el baile con éxito.");
        }
    }

    private void leerPergaminos() throws InterruptedException {
        Thread.sleep(5000);
        if (Math.random() < 0.5) {
            modificarChispa(-7); // Pergaminos soporíferos
        } else {
            modificarChispa(5); // Leyendas de caballeros
        }
    }

    private void escaparse() {
        if (Math.random() < 0.5) visitarMercado();
        else visitarTaberna();
    }

    private void visitarMercado() {
        try (Socket s = new Socket(Constantes.HOST, Constantes.PUERTO_MERCADO);
             DataInputStream dis = new DataInputStream(s.getInputStream())) {
            Thread.sleep(5000);
            String item = dis.readUTF();
            System.out.println("ELISABETHA: Compró en el mercado: " + item);
        } catch (Exception e) {}
    }

    private void visitarTaberna() {
        try (Socket s = new Socket(Constantes.HOST, Constantes.PUERTO_TABERNA);
             DataOutputStream dos = new DataOutputStream(s.getOutputStream());
             DataInputStream dis = new DataInputStream(s.getInputStream())) {

            dos.writeUTF("Elisabetha");
            int res = dis.readInt();
            if (res > 0) modificarChispa(res == 75 ? 75 : 10);
        } catch (Exception e) {}
    }
}