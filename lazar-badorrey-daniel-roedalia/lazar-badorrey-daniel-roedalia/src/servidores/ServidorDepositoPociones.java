package servidores;

import configuracion.ParametrosReino;
import java.io.*;
import java.net.*;

public class ServidorDepositoPociones implements Runnable {
    private int elixiresDama = 0;
    private int elixiresCaballero = 0;
    private final Object monitorDeposito = new Object();

    @Override
    public void run() {
        try (ServerSocket servidor = new ServerSocket(ParametrosReino.PUERTO_DEPOSITO_POCIONES)) {
            System.out.println("[Alacena] Servidor iniciado en puerto " + ParametrosReino.PUERTO_DEPOSITO_POCIONES);

            while (true) {
                try (Socket conexion = servidor.accept();
                     BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                     PrintWriter escritor = new PrintWriter(conexion.getOutputStream(), true)) {

                    String instruccion = lector.readLine();
                    boolean operacionExitosa = gestionarPocion(instruccion);
                    
                    escritor.println(String.valueOf(operacionExitosa));
                    if (operacionExitosa) {
                        System.out.println("[Alacena] Operacion: " + instruccion);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Alacena] Error al iniciar servidor: " + e.getMessage());
        }
    }

    private boolean gestionarPocion(String instruccion) {
        synchronized (monitorDeposito) {
            if ("ALMACENAR_E".equals(instruccion)) {
                elixiresDama++;
                return true;
            } else if ("ALMACENAR_L".equals(instruccion)) {
                elixiresCaballero++;
                return true;
            } else if ("RETIRAR_E".equals(instruccion)) {
                if (elixiresDama > 0) {
                    elixiresDama--;
                    return true;
                }
            } else if ("RETIRAR_L".equals(instruccion)) {
                if (elixiresCaballero > 0) {
                    elixiresCaballero--;
                    return true;
                }
            }
            return false;
        }
    }
}
