package servidores;

import comun.Constantes;
import java.io.*;
import java.net.*;
import java.util.Random;

public class ServidorPorton implements Runnable {
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

                    dis.readUTF();

                    Random rnd = new Random();
                    String respuesta;

                    if (rnd.nextBoolean()) {
                        respuesta = "ACEPTADO: Ratón local. ¡Buen día, camarada!";
                    } else {
                        String item = mercancias[rnd.nextInt(mercancias.length)];

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