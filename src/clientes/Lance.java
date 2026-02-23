package clientes;

import comun.Constantes;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Lance extends Thread {

    private int gradoDeVinculo = 0;
    private boolean cruceOriginalRealizado = false;
    public LinkedBlockingQueue<String> buzon;
    private final Random dado = new Random();

    public Lance(LinkedBlockingQueue<String> buzon) {
        this.buzon = buzon;
    }

    public synchronized void modificarChispa(int n) {
        if (this.gradoDeVinculo == 100) {
            return;
        }

        int ajuste = n;
        if (ajuste == -20) {
            ajuste = -10;
        } else if (ajuste <= -30) {
            ajuste = -15;
        }

        if (ajuste == 75) {
            gradoDeVinculo = 75;
            cruceOriginalRealizado = true;
        } else {
            int resultado = gradoDeVinculo + ajuste;
            if (!cruceOriginalRealizado && ajuste > 0) {
                if (resultado > 50) {
                    resultado = 50;
                }
            }
            if (resultado < 0) {
                resultado = 0;
            }
            if (resultado > 100) {
                resultado = 100;
            }
            gradoDeVinculo = resultado;
        }

        System.out.println("Caballero Lance - Chispa actual: " + gradoDeVinculo
                + " | Encuentro en Taberna: " + cruceOriginalRealizado);
    }

    public synchronized int getChispa() {
        return gradoDeVinculo;
    }

    @Override
    public void run() {
        while (getChispa() < 100) {
            try {
                int decision = dado.nextInt(3);
                if (decision == 0) {
                    dialogarConGuardia();
                } else if (decision == 1) {
                    recorrerMurallas();
                }
                Thread.sleep(1000);
            } catch (Exception ignorada) {
            }
        }
        System.out.println("Caballero Lance ha alcanzado el nivel maximo de chispa (100). Su destino se ha cumplido.");
    }

    private void dialogarConGuardia() throws InterruptedException {
        Thread.sleep(4000);
        String mensaje = buzon.poll();
        if (mensaje != null && mensaje.equals("OFENSA")) {
            resolverConflicto();
        }
    }

    private void resolverConflicto() throws InterruptedException {
        Thread.sleep(5000);
        double suerte = dado.nextDouble();
        if (suerte < 0.20) {
            modificarChispa(-5);
        } else {
            modificarChispa(7);
        }
    }

    private void recorrerMurallas() {
        boolean optaPorAcceso = dado.nextBoolean();
        if (optaPorAcceso) {
            inspeccionarAcceso();
        } else {
            acudirATaberna();
        }
    }

    private void inspeccionarAcceso() {
        try (Socket enlace = new Socket(Constantes.HOST, Constantes.PUERTO_PORTON);
             DataOutputStream flujoSalida = new DataOutputStream(enlace.getOutputStream());
             DataInputStream flujoEntrada = new DataInputStream(enlace.getInputStream())) {

            Thread.sleep(5000);
            flujoSalida.writeUTF("INSPECCIONAR");
            flujoEntrada.readUTF();
        } catch (Exception ignorada) {
        }
    }

    private void acudirATaberna() {
        try (Socket enlace = new Socket(Constantes.HOST, Constantes.PUERTO_TABERNA);
             DataOutputStream flujoSalida = new DataOutputStream(enlace.getOutputStream());
             DataInputStream flujoEntrada = new DataInputStream(enlace.getInputStream())) {

            flujoSalida.writeUTF("Lance");
            int valorRecibido = flujoEntrada.readInt();
            if (valorRecibido > 0) {
                if (valorRecibido == 75) {
                    modificarChispa(75);
                } else {
                    modificarChispa(25);
                }
            }
        } catch (Exception ignorada) {
        }
    }
}

