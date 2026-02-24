package clientes;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class DamaLazo extends Thread {
    private String identificador;
    private Elisabetha protagonistaFemenina;
    private final Random generador = new Random();

    public DamaLazo(String nombre, Elisabetha e) {
        this.identificador = nombre;
        this.protagonistaFemenina = e;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (generador.nextDouble() < 0.5) {
                    Thread.sleep(5000);
                } else {
                    boolean entregado = protagonistaFemenina.buzon.offer(
                            generador.nextDouble() < 0.5 ? "RUMOR" : "CONFIDENCIA",
                            20, TimeUnit.SECONDS
                    );
                    if (!entregado) System.out.println("[DamaLazo] " + identificador + ": timeout con Elisabetha");
                }
            } catch (InterruptedException e) {}
        }
    }
}

