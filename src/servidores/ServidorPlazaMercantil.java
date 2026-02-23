package servidores;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorPlazaMercantil implements Runnable {
    private final String[] mercancias = {"Queso artesanal", "Pan recién horneado", "Especias exóticas", "Telas finas", "Grosella silvestre", "Repelente de plagas", "Collar real", "Cucharon ceremonial"};

    @Override
    public void run() {
        try (ServerSocket servidor = new ServerSocket(ParametrosReino.PUERTO_PLAZA_MERCANTIL)) {
            System.out.println("🏪 La Plaza Mercantil de Roedalia abre en el puerto " + ParametrosReino.PUERTO_PLAZA_MERCANTIL);
            
            while (true) {
                try (Socket conexion = servidor.accept();
                     PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true)) {
                    
                    // Simular oferta limitada de 5 productos
                    List<String> ofertaDiaria = new ArrayList<>(Arrays.asList(mercancias));
                    Collections.shuffle(ofertaDiaria);
                    String articuloSeleccionado = ofertaDiaria.get(0);
                    
                    escritor.println(articuloSeleccionado);
                    System.out.println("💰 Venta realizada: " + articuloSeleccionado);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ La Plaza Mercantil no pudo abrir sus puestos: " + e.getMessage());
        }
    }
}
