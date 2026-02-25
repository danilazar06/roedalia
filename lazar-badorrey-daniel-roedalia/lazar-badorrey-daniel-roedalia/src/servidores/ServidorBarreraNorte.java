package servidores;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;
import java.util.Random;

public class ServidorBarreraNorte implements Runnable {
    private final String[] cargamentosProhibidos = {
        "Trigo dorado", "Vino añejo", "Queso sin fermentar", "Leche cruda", "Madera noble", "Telas extranjeras"
    };
    private Random generadorAleatorio = new Random();

    @Override
    public void run() {
        try (ServerSocket servidor = new ServerSocket(ParametrosReino.PUERTO_BARRERA_NORTE)) {
            System.out.println("[Porton Norte] Servidor iniciado en puerto " + ParametrosReino.PUERTO_BARRERA_NORTE);

            while (ParametrosReino.portonDestruido = false) {
                try (Socket conexion = servidor.accept();
                     BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                     PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true)) {

                    String comando = lector.readLine();
                    String veredicto;

                    if (generadorAleatorio.nextBoolean()) {
                        veredicto = "PERMITIDO: Ciudadano de Roedalia";
                    } else {
                        String cargamento = cargamentosProhibidos[generadorAleatorio.nextInt(cargamentosProhibidos.length)];

                        if (cargamento.equals("Queso sin fermentar") || cargamento.equals("Leche cruda")) {
                            veredicto = "DENEGADO: " + cargamento;
                        } else {
                            veredicto = "PERMITIDO: Carreta con " + cargamento;
                        }
                    }

                    escritor.println(veredicto);
                    System.out.println("[Porton Norte] " + veredicto);

                } catch (IOException e) {
                    System.err.println("[Porton Norte] Error en conexion: " + e.getMessage());
                }
            }

            reconstruirPorton();
        } catch (IOException e) {
            System.err.println("[Porton Norte] Error al iniciar servidor: " + e.getMessage());
        }
    }

    private void reconstruirPorton(){
        new Thread(() -> {
            try { Thread.sleep(20000); } catch (InterruptedException e) {}
            ParametrosReino.portonDestruido = false;
            System.out.println("[Taberna] El portón ha sido reconstruido!");
        }).start();
    }
}
