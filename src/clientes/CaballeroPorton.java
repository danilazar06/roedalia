package clientes;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class CaballeroPorton extends Thread {
    private String identificador;
    private Lance protagonistaMasculino;
    private boolean requiereAsistencia = false;
    private final Random generador = new Random();

    public CaballeroPorton(String nombre, Lance l) {
        this.identificador = nombre;
        this.protagonistaMasculino = l;
    }

    public void herir() { requiereAsistencia = true; }

    @Override
    public void run() {
        while (true) {
            try {
                if (requiereAsistencia) {
                    System.out.println("[CaballeroPorton] " + identificador + ": Recuperandose (30s)");
                    Thread.sleep(30000);
                    requiereAsistencia = false;
                } else if (generador.nextDouble() < 0.5) {
                    Thread.sleep(6000);
                } else {
                    boolean entregado = protagonistaMasculino.buzon.offer(
                            generador.nextDouble() < 0.25 ? "OFENSA" : "NORMAL",
                            25, TimeUnit.SECONDS
                    );
                    if (!entregado) System.out.println("[CaballeroPorton] " + identificador + ": timeout con Lance");
                }
            } catch (InterruptedException e) {}
        }
    }
}

