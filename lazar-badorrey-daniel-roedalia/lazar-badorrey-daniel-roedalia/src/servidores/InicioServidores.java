package servidores;

public class InicioServidores {
    public static void main(String[] args) {
        System.out.println("Iniciando servidores de Roedalia...");

        new Thread(new ServidorDescansoGuerrero()).start();
        new Thread(new ServidorPlazaMercantil()).start();
        new Thread(new ServidorBarreraNorte()).start();
        new Thread(new ServidorDepositoPociones()).start();
        
        System.out.println("Todos los servidores iniciados.");
    }
}
