package clientes;
import java.util.concurrent.LinkedBlockingQueue;

public class MainClientes {
    public static void main(String[] args) {
        LinkedBlockingQueue<String> buzonEli = new LinkedBlockingQueue<>(1);
        LinkedBlockingQueue<String> buzonLance = new LinkedBlockingQueue<>(1);

        Elisabetha eli = new Elisabetha(buzonEli);
        Lance lan = new Lance(buzonLance);

        eli.start();
        lan.start();

        new Alquimista(eli, lan).start();
        new Alquimista(eli, lan).start();

        String[] dNames = {"Clara", "Rosa", "Beatriz", "Elena"};
        for(String n : dNames) new DamaLazo(n, eli).start();

        String[] cNames = {"Gawain", "Bors", "Kay", "Bedivere"};
        for(String n : cNames) new CaballeroPorton(n, lan).start();

        while (eli.getChispa() < 100 || lan.getChispa() < 100) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\n***************************************************");
        System.out.println("   ¡LA CHISPA ADECUADA HA TRIUNFADO! ");
        System.out.println("   Elisabetha y Lance se encuentran en el Portón.");
        System.out.println("   Roedalia celebra la unión más pura del reino.");
        System.out.println("***************************************************");
        System.exit(0); // Esto cerrará todos los hilos (Damas, Alquimistas, etc.)
    }
}