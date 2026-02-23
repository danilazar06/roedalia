package servidores;
import comun.Constantes;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorMercado implements Runnable {
    private final String[] productos = {"Queso", "Pan", "Especias", "Telas", "Grosella", "Repelente", "Collar", "Cucharon"};

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(Constantes.PUERTO_MERCADO)) {
            System.out.println("LOG SERV: Mercado abierto en puerto " + Constantes.PUERTO_MERCADO);
            while (true) {
                Socket s = server.accept();
                try (DataOutputStream dos = new DataOutputStream(s.getOutputStream())) {
                    List<String> oferta = new ArrayList<>(List.of(productos));
                    Collections.shuffle(oferta);
                    String item = oferta.get(0);
                    dos.writeUTF(item);
                    System.out.println("MERCADO: Vendido " + item);
                }
                s.close();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}