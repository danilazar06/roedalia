package servidores;

import comun.Constantes;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTaberna implements Runnable {

    private boolean damaEnSala = false;
    private boolean guerreroEnSala = false;
    private boolean lazoForjadoPreviamente = false;
    private boolean selloDeCruce = false;

    @Override
    public void run() {
        ServerSocket receptor = null;
        try {
            receptor = new ServerSocket(Constantes.PUERTO_TABERNA);
            System.out.println("Servidor de la taberna iniciado correctamente en el puerto " + Constantes.PUERTO_TABERNA);
            while (true) {
                Socket conexionEntrante = receptor.accept();
                GestorDeVisitante hilo = new GestorDeVisitante(conexionEntrante);
                hilo.start();
            }
        } catch (IOException excepcion) {
            excepcion.printStackTrace();
        } finally {
            if (receptor != null) {
                try { receptor.close(); } catch (IOException ignorada) {}
            }
        }
    }

    private class GestorDeVisitante extends Thread {

        private final Socket canal;

        GestorDeVisitante(Socket canalRecibido) {
            this.canal = canalRecibido;
        }

        @Override
        public void run() {
            try (DataInputStream lector = new DataInputStream(canal.getInputStream());
                 DataOutputStream escritor = new DataOutputStream(canal.getOutputStream())) {

                String identificador = lector.readUTF();
                int puntosResultantes = 0;

                synchronized (ServidorTaberna.this) {

                    boolean esDama = "Elisabetha".equals(identificador);

                    if (esDama) {
                        damaEnSala = true;
                    } else {
                        guerreroEnSala = true;
                    }

                    if (damaEnSala && guerreroEnSala) {
                        selloDeCruce = true;
                        ServidorTaberna.this.notifyAll();
                    } else {
                        long milisDeEspera;
                        if (esDama) {
                            milisDeEspera = 8000;
                        } else {
                            milisDeEspera = 12000;
                        }
                        long instanteLimite = System.currentTimeMillis() + milisDeEspera;

                        while (!selloDeCruce && System.currentTimeMillis() < instanteLimite) {
                            long diferencia = instanteLimite - System.currentTimeMillis();
                            if (diferencia > 0) {
                                ServidorTaberna.this.wait(diferencia);
                            }
                        }
                    }

                    if (selloDeCruce) {
                        if (!lazoForjadoPreviamente) {
                            puntosResultantes = 75;
                        } else {
                            puntosResultantes = 25;
                        }
                    }

                    if (esDama) {
                        damaEnSala = false;
                    } else {
                        guerreroEnSala = false;
                    }

                    if (!damaEnSala && !guerreroEnSala) {
                        if (puntosResultantes == 75) {
                            lazoForjadoPreviamente = true;
                        }
                        selloDeCruce = false;
                    }
                }

                escritor.writeInt(puntosResultantes);

            } catch (Exception fallo) {
                fallo.printStackTrace();
            }
        }
    }
}