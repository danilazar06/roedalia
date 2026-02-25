package secundarios;

import configuracion.ParametrosReino;
import protagonistas.*;
import java.io.*;
import java.net.*;
import java.util.Random;


public class DragonCarmesi extends Thread {
    private DamaElisabetha elisabetha;
    private Random generadorAleatorio = new Random();

    public DragonCarmesi(DamaElisabetha elisabetha) {
        this.elisabetha = elisabetha;
        System.out.println("[Dragon Carmesi] Despierta de las montañas");
    }

    @Override
    public void run() {
        while (!ParametrosReino.dragonDespierto) {
            try {
                Thread.sleep(120000);

                ParametrosReino.dragonDespierto=true;

                int probabilidadAtaqueReino = generadorAleatorio.nextInt(4);

                if (probabilidadAtaqueReino == 3){
                    Thread.sleep(6000);
                    int ataque = generadorAleatorio.nextInt(4);
                    switch (ataque) {
                        case 0: atacarMercado(); break;
                        case 1: atacarTaberna(); break;
                        case 2: atacarPorton(); break;
                    }
                }
                else {
                    ParametrosReino.dragonDespierto = false;
                }

            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("[Dragon Carmesi] El dragon sigue durmiendo");
    }

    private void atacarMercado() {
        ParametrosReino.intentosAtaqueRestantes = ParametrosReino.intentosAtaqueRestantes - 1;
        int probabilidadDestruccionMercado = generadorAleatorio.nextInt(10);
        System.out.println("[Dragon Carmesi] Escupe fuego sobre el mercado!");
        if (probabilidadDestruccionMercado!=1 || probabilidadDestruccionMercado!=2){
            ParametrosReino.mercadoDestruido = true;
            System.out.println("[Dragon Carmesi] El mercado arde en llamas! (Destrucción lograda)");
        }else {
            System.out.println("[Dragon Carmesi] Voy a probar otro sitio. (Destrucción no lograda)");
        }
        int probabilidadDestruccionSiguienteDestino = generadorAleatorio.nextInt(2);
        if (probabilidadDestruccionSiguienteDestino==1 && ParametrosReino.intentosAtaqueRestantes>1){
            atacarTaberna();
        }
        else if (ParametrosReino.intentosAtaqueRestantes>1){
            atacarPorton();
        }
        else{
            ParametrosReino.dragonDespierto=false;
        }
    }

    private void atacarTaberna() {
        ParametrosReino.intentosAtaqueRestantes = ParametrosReino.intentosAtaqueRestantes - 1;

        int probabilidadDestruccionTaberna = generadorAleatorio.nextInt(10);
        System.out.println("[Dragon Carmesi] Escupe fuego sobre la Taberna!");
        if (probabilidadDestruccionTaberna==1 || probabilidadDestruccionTaberna==2 || probabilidadDestruccionTaberna==3){
            ParametrosReino.tabernaDestruida = true;
            System.out.println("[Dragon Carmesi] La taberna arde en llamas! (Destrucción lograda)");
        }
        System.out.println("[Dragon Carmesi] Voy a probar otro sitio. (Destrucción no lograda)");

        int probabilidadDestruccionSiguienteDestino = generadorAleatorio.nextInt(2);
        if (probabilidadDestruccionSiguienteDestino==1 && ParametrosReino.intentosAtaqueRestantes>1){
            atacarPorton();
        }
        else if (ParametrosReino.intentosAtaqueRestantes>1){
            atacarMercado();
        }
        else{
            ParametrosReino.dragonDespierto=false;
        }

    }

    private void atacarPorton() {
        ParametrosReino.intentosAtaqueRestantes = ParametrosReino.intentosAtaqueRestantes - 1;

        int probabilidadDestruccionPorton = generadorAleatorio.nextInt(5);
        System.out.println("[Dragon Carmesi] Escupe fuego sobre la Taberna!");
        if (probabilidadDestruccionPorton==1 || probabilidadDestruccionPorton==2 || probabilidadDestruccionPorton==3){
            ParametrosReino.portonDestruido = true;
            System.out.println("[Dragon Carmesi] El portón arde en llamas! (Destrucción lograda)");
        }
        System.out.println("[Dragon Carmesi] Voy a probar otro sitio. (Destrucción no lograda)");

        int probabilidadDestruccionSiguienteDestino = generadorAleatorio.nextInt(2);
        if (probabilidadDestruccionSiguienteDestino==1 && ParametrosReino.intentosAtaqueRestantes>1){
            atacarMercado();
        }
        else if (ParametrosReino.intentosAtaqueRestantes>1){
            atacarTaberna();
        }
        else{
            ParametrosReino.dragonDespierto=false;
        }
    }

}

