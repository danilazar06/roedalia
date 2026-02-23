package principal;

import protagonistas.*;
import secundarios.*;

public class InicioAventura {
    public static void main(String[] args) {
        System.out.println("🏰 **LA LEYENDA DE ROEDALIA COMIENZA** 🏰");
        System.out.println("Donde el destino une a dos almas en un reino de magia y honor...\n");

        // Crear protagonistas principales (sin colas locales)
        DamaElisabetha elisabetha = new DamaElisabetha();
        CaballeroLance lance = new CaballeroLance();

        // Iniciar los hilos de los protagonistas
        elisabetha.start();
        lance.start();

        // Dar tiempo para que los servidores personales se inicien
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Iniciar Hechiceros (2)
        new Hechicero(elisabetha, lance).start();
        new Hechicero(elisabetha, lance).start();

        // Iniciar Doncellas (4)
        String[] nombresDoncellas = {"Clara", "Rosa", "Beatriz", "Elena"};
        for (String nombre : nombresDoncellas) {
            new Doncella(nombre).start();
        }

        // Iniciar Guardias Reales (4)
        String[] nombresGuardias = {"Gawain", "Bors", "Kay", "Bedivere"};
        for (String nombre : nombresGuardias) {
            new GuardiaReal(nombre).start();
        }

        // Esperar a que ambos protagonistas alcancen el nivel máximo
        while (elisabetha.obtenerNivelAfinidad() < 100 || lance.obtenerNivelAfinidad() < 100) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException("⚠️ La aventura fue interrumpida", e);
            }
        }

        // Final épico
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   ✨ ¡EL VÍNCULO DESTINAL HA TRIUNFADO! ✨");
        System.out.println("   Elisabetha y Lance se encuentran en la Barrera Norte.");
        System.out.println("   Roedalia celebra la unión más pura del reino.");
        System.out.println("   El destino ha escrito su capítulo final...");
        System.out.println("=".repeat(60));
        
        System.exit(0); // Cerrar todos los hilos secundarios
    }
}
