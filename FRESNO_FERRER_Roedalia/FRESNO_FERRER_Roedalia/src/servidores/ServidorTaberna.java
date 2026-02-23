package servidores;
import comun.Constantes;
import java.io.*;
import java.net.*;

public class ServidorTaberna implements Runnable {
    private boolean elisabethaIn = false;
    private boolean lanceIn = false;
    private boolean yaSeConocieron = false;

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
                    // 1. Entrar y anunciar
                    if (nombre.equals("Elisabetha")) elisabethaIn = true;
                    else lanceIn = true;

                    System.out.println("TABERNA: " + nombre + " ha entrado.");
                    ServidorTaberna.this.notifyAll(); // Avisar al que ya esté esperando

                    // 2. Esperar al otro (máximo el tiempo de estancia)
                    long tiempoEstancia = nombre.equals("Elisabetha") ? 5000 : 8000;
                    long fin = System.currentTimeMillis() + tiempoEstancia;

                    while (System.currentTimeMillis() < fin && (!elisabethaIn || !lanceIn)) {
                        ServidorTaberna.this.wait(fin - System.currentTimeMillis());
                    }

                    // 3. Determinar puntos si coinciden
                    if (elisabethaIn && lanceIn) {
                        if (!yaSeConocieron) {
                            puntosAEntregar = 75; // Código especial para "Conocerse"
                        } else {
                            puntosAEntregar = 10; // Reencuentro
                        }
                    }
                }

                // 4. Salir y enviar resultado
                dos.writeInt(puntosAEntregar);

                synchronized (ServidorTaberna.this) {
                    if (nombre.equals("Elisabetha")) elisabethaIn = false;
                    else lanceIn = false;
                    if (puntosAEntregar == 75) yaSeConocieron = true; // Solo marcamos éxito al salir
                }

            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}