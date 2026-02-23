package clientes;
import java.util.concurrent.TimeUnit;

public class CaballeroPorton extends Thread {
    private String nombre;
    private Lance lan;
    private boolean herido = false;

    public CaballeroPorton(String n, Lance l) { this.nombre = n; this.lan = l; }

    public void herir() { herido = true; }

    @Override
    public void run() {
        while (true) {
            try {
                if (herido) {
                    System.out.println("CABALLERO " + nombre + ": Herido... recuperando (30s).");
                    Thread.sleep(30000);
                    herido = false;
                } else if (Math.random() < 0.5) { // Vigilancia
                    Thread.sleep(6000);
                } else { // Hablar (Espera 25s)
                    boolean ok = lan.buzon.offer(
                            Math.random() < 0.25 ? "OFENSA" : "NORMAL",
                            25, TimeUnit.SECONDS
                    );
                    if (!ok) System.out.println("CABALLERO " + nombre + ": Lance me ignoró (Timeout).");
                }
            } catch (InterruptedException e) {}
        }
    }
}