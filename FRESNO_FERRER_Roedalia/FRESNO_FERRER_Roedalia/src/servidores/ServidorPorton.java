package servidores;

import comun.Constantes;
import java.io.*;
import java.net.*;
import java.util.Random;

public class ServidorPorton implements Runnable {
    // Mercancías posibles que traen los ratones extranjeros
    private final String[] mercancias = {
            "Trigo", "Vino", "Queso sin fermentar", "Leche cruda", "Madera", "Telas"
    };

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(Constantes.PUERTO_PORTON)) {
            System.out.println("LOG SERV: Portón Norte vigilando en puerto " + Constantes.PUERTO_PORTON);

            while (true) {
                try (Socket s = server.accept();
                     DataInputStream dis = new DataInputStream(s.getInputStream());
                     DataOutputStream dos = new DataOutputStream(s.getOutputStream())) {

                    dis.readUTF(); // Recibe "INSPECCIONAR"

                    Random rnd = new Random();
                    String respuesta;

                    // Lógica: 50% probabilidad de que sea un ratón de la ciudad o extranjero
                    if (rnd.nextBoolean()) {
                        // Ratón de la ciudad: Pasa con saludo
                        respuesta = "ACEPTADO: Ratón local. ¡Buen día, camarada!";
                    } else {
                        // Ratón extranjero: Se inspecciona la carga
                        String item = mercancias[rnd.nextInt(mercancias.length)];

                        // Filtro de productos prohibidos
                        if (item.equals("Queso sin fermentar") || item.equals("Leche cruda")) {
                            respuesta = "PROHIBIDO: Se deniega el acceso por " + item + " (riesgo de plaga).";
                        } else {
                            respuesta = "ACEPTADO: Carretera extranjera con " + item + ".";
                        }
                    }

                    dos.writeUTF(respuesta);
                    System.out.println("PORTON: Inspección finalizada -> " + respuesta);

                } catch (IOException e) {
                    System.err.println("Error en conexión de Portón: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}