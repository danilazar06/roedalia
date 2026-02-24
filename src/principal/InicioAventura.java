package principal;

import protagonistas.*;
import secundarios.*;
import servidores.*;

public class InicioAventura {
    public static void main(String[] args) {
        System.out.println("LA CHISPA ADECUADA");

        new Thread(new ServidorDescansoGuerrero()).start();
        new Thread(new ServidorPlazaMercantil()).start();
        new Thread(new ServidorBarreraNorte()).start();
        new Thread(new ServidorDepositoPociones()).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        DamaElisabetha elisabetha = new DamaElisabetha();
        CaballeroLance lance = new CaballeroLance();

        elisabetha.start();
        lance.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        new Alquimista(elisabetha, lance).start();
        new Alquimista(elisabetha, lance).start();

        String[] nombresDamas = {"Lucia", "Carmen", "Ana", "Sara"};
        for (String nombre : nombresDamas) {
            new DamaLazo(nombre).start();
        }

        String[] nombresCaballeros = {"Pablo", "Pedro", "Jorge", "Miguel"};
        for (String nombre : nombresCaballeros) {
            new CaballeroPorton(nombre).start();
        }

        while (elisabetha.obtenerNivelChispa() < 100 || lance.obtenerNivelChispa() < 100) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException("La aventura fue interrumpida", e);
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Elisabetha y Lance han alcanzado 100 de chispa cada uno.");

        System.exit(0);
    }
}
