package servidores;
import comun.Constantes;
import java.io.*;
import java.net.*;

public class ServidorTaberna implements Runnable {
    private boolean elisabethaIn = false;
    private boolean lanceIn = false;
    private boolean yaSeConocieron = false;

    // LA CLAVE MÁGICA: El sello que garantiza que la IA no falle la sincronización
    private boolean encuentroProducido = false;

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(Constantes.PUERTO_TABERNA)) {
            System.out.println("LOG SERV: Taberna abierta en puerto " + Constantes.PUERTO_TABERNA);
            while (true) {
                new Handler(server.accept()).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private class Handler extends Thread {
        private Socket socket;
        public Handler(Socket s) { this.socket = s; }

        @Override
        public void run() {
            try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                String nombre = dis.readUTF();
                int puntosAEntregar = 0;

                synchronized (ServidorTaberna.this) {
                    // 1. Entrar y anunciar presencia
                    if (nombre.equals("Elisabetha")) elisabethaIn = true;
                    else lanceIn = true;

                    System.out.println("TABERNA: " + nombre + " ha entrado.");

                    // 2. Si ya están los dos, SELLAMOS el encuentro y despertamos al otro
                    if (elisabethaIn && lanceIn) {
                        encuentroProducido = true;
                        ServidorTaberna.this.notifyAll();
                    } else {
                        // 3. Si falta el otro, esperamos nuestro turno
                        long tiempoEstancia = nombre.equals("Elisabetha") ? 5000 : 8000;
                        long fin = System.currentTimeMillis() + tiempoEstancia;

                        // Comprobamos el "sello" en lugar de la presencia cruda
                        while (System.currentTimeMillis() < fin && !encuentroProducido) {
                            long restante = fin - System.currentTimeMillis();
                            if (restante > 0) {
                                ServidorTaberna.this.wait(restante);
                            }
                        }
                    }

                    // 4. Calculamos los puntos basándonos en el sello inalterable
                    if (encuentroProducido) {
                        if (!yaSeConocieron) {
                            puntosAEntregar = 75;
                        } else {
                            puntosAEntregar = 10;
                        }
                    }

                    // 5. Salir (ahora sí podemos poner la presencia a false sin miedo)
                    if (nombre.equals("Elisabetha")) elisabethaIn = false;
                    else lanceIn = false;

                    // 6. Reseteamos el sello SOLO cuando la taberna se queda totalmente vacía
                    // Esto evita la condición de carrera y prepara la taberna para el futuro
                    if (!elisabethaIn && !lanceIn) {
                        encuentroProducido = false;
                        if (puntosAEntregar == 75) yaSeConocieron = true;
                    }
                }

                // 7. Enviar resultado fuera del synchronized
                dos.writeInt(puntosAEntregar);

            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}