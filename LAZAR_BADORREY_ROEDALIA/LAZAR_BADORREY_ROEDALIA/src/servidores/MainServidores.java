package servidores;

public class MainServidores {
    public static void main(String[] args) {
        new Thread(new ServidorTaberna()).start();
        new Thread(new ServidorMercado()).start();
        new Thread(new ServidorPorton()).start();
        new Thread(new ServidorAlacena()).start();
        System.out.println(">>> TODOS LOS SERVIDORES DE ROEDALIA ESTÁN ONLINE <<<");
    }
}