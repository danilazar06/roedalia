package servidores;
import comun.Constantes;
import java.io.*;
import java.net.*;

public class ServidorAlacena implements Runnable {
    private int pocionesEli = 0;
    private int pocionesLance = 0;

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(Constantes.PUERTO_ALACENA)) {
            System.out.println("LOG SERV: Alacena lista en puerto " + Constantes.PUERTO_ALACENA);
            while (true) {
                Socket s = server.accept();
                try (DataInputStream dis = new DataInputStream(s.getInputStream());
                     DataOutputStream dos = new DataOutputStream(s.getOutputStream())) {

                    String accion = dis.readUTF(); // GUARDAR_E, GUARDAR_L, SACAR_E, SACAR_L
                    boolean exito = false;

                    synchronized (this) {
                        switch (accion) {
                            case "GUARDAR_E": pocionesEli++; exito = true; break;
                            case "GUARDAR_L": pocionesLance++; exito = true; break;
                            case "SACAR_E":
                                if (pocionesEli > 0) { pocionesEli--; exito = true; }
                                break;
                            case "SACAR_L":
                                if (pocionesLance > 0) { pocionesLance--; exito = true; }
                                break;
                        }
                    }
                    dos.writeBoolean(exito);
                    if(exito) System.out.println("ALACENA: Acción " + accion + " realizada.");
                }
                s.close();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}