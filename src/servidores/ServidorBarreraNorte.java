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
            System.out.println("🚪 La Barrera Norte de Roedalia se activa en el puerto " + ParametrosReino.PUERTO_BARRERA_NORTE);

            while (true) {
                try (Socket conexion = servidor.accept();
                     BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                     PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true)) {

                    String comando = lector.readLine(); // Recibe "INSPECCIONAR"
                    String veredicto;

                    // Lógica: 50% probabilidad de visitante local vs extranjero
                    if (generadorAleatorio.nextBoolean()) {
                        // Visitante local: Paso libre
                        veredicto = "PERMITIDO: Ciudadano de Roedalia. ¡Bienvenido, hermano!";
                    } else {
                        // Visitante extranjero: Inspección de cargamento
                        String cargamento = cargamentosProhibidos[generadorAleatorio.nextInt(cargamentosProhibidos.length)];

                        // Filtro de mercancías restringidas
                        if (cargamento.equals("Queso sin fermentar") || cargamento.equals("Leche cruda")) {
                            veredicto = "DENEGADO: Acceso prohibido por " + cargamento + " (riesgo de contaminación)";
                        } else {
                            veredicto = "PERMITIDO: Caravana extranjera con " + cargamento + " autorizada";
                        }
                    }

                    escritor.println(veredicto);
                    System.out.println("🛡️ Inspección completada -> " + veredicto);

                } catch (IOException e) {
                    System.err.println("⚠️ Error en la conexión de la Barrera Norte: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ La Barrera Norte no pudo activarse: " + e.getMessage());
        }
    }
}
