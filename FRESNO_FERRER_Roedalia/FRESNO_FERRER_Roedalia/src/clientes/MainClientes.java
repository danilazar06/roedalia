package clientes;
import java.util.concurrent.LinkedBlockingQueue;

public class MainClientes {
    public static void main(String[] args) {
        // Colas de comunicación entre hilos locales
        LinkedBlockingQueue<String> buzonEli = new LinkedBlockingQueue<>(1);
        LinkedBlockingQueue<String> buzonLance = new LinkedBlockingQueue<>(1);

        Elisabetha eli = new Elisabetha(buzonEli);
        Lance lan = new Lance(buzonLance);

        // Arrancamos protagonistas
        eli.start();
        lan.start();

        // Arrancamos Alquimistas (2)
        new Alquimista(eli, lan).start();
        new Alquimista(eli, lan).start();

        // Arrancamos Damas (4)
        String[] dNames = {"Clara", "Rosa", "Beatriz", "Elena"};
        for(String n : dNames) new DamaLazo(n, eli).start();

        // Arrancamos Caballeros (4)
        String[] cNames = {"Gawain", "Bors", "Kay", "Bedivere"};
        for(String n : cNames) new CaballeroPorton(n, lan).start();

        // Después de arrancar todos los hilos...
        while (eli.getChispa() < 100 || lan.getChispa() < 100) {
            try {
                Thread.sleep(100); // Esperar a que ambos lleguen al final
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