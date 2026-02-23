package clientes;
import java.util.concurrent.TimeUnit;

public class DamaLazo extends Thread {
    private String nombre;
    private Elisabetha eli;

    public DamaLazo(String n, Elisabetha e) { this.nombre = n; this.eli = e; }

    @Override
    public void run() {
        while (true) {
            try {
                if (Math.random() < 0.5) { // Labores
                    Thread.sleep(5000);
                } else { // Rumores (Espera 20s)
                    boolean ok = eli.buzon.offer(
                            Math.random() < 0.5 ? "RUMOR" : "CONFIDENCIA",
                            20, TimeUnit.SECONDS
                    );
                    if (!ok) System.out.println("DAMA " + nombre + ": Elisabetha me ignoró (Timeout).");
                }
            } catch (InterruptedException e) {}
        }
    }
}