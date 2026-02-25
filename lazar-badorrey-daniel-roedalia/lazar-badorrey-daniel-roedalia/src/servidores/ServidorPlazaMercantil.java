package servidores;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorPlazaMercantil implements Runnable {
    private final String[] mercancias = {
        "Queso", "Pan recien horneado", "Especias del lejano oriente",
        "Telas para vestidos", "Jugo de grosella", "Repelente de gatos",
        "Collares de ratona", "Cucharas de boj"
    };

    @Override
    public void run() {
        try (ServerSocket servidor = new ServerSocket(ParametrosReino.PUERTO_PLAZA_MERCANTIL)) {
            System.out.println("[Mercado] Servidor iniciado en puerto " + ParametrosReino.PUERTO_PLAZA_MERCANTIL);

            while (ParametrosReino.mercadoDestruido = false) {
                try (Socket conexion = servidor.accept();
                     PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true)) {

                    List<String> ofertaDiaria = new ArrayList<>(Arrays.asList(mercancias));
                    Collections.shuffle(ofertaDiaria);
                    List<String> oferta = ofertaDiaria.subList(0, 5);

                    escritor.println(String.join(", ", oferta));
                    System.out.println("[Mercado] Oferta: " + String.join(", ", oferta));
                }
            }

        } catch (IOException e) {
            System.err.println("[Mercado] Error al iniciar servidor: " + e.getMessage());
        }
    }

    private void reconstruirMercado(){
        new Thread(() -> {
            try { Thread.sleep(20000); } catch (InterruptedException e) {}
            ParametrosReino.mercadoDestruido = false;
            System.out.println("[Taberna] El mercado ha sido reconstruido!");
        }).start();
    }
}
