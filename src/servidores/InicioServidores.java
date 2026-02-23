package servidores;

public class InicioServidores {
    public static void main(String[] args) {
        System.out.println("🏰 **INICIANDO REINO DE ROEDALIA** 🏰");
        System.out.println("Activando todos los servicios del reino...\n");

        // Lanzar los 4 servidores principales en paralelo
        new Thread(new ServidorDescansoGuerrero()).start();
        new Thread(new ServidorPlazaMercantil()).start();
        new Thread(new ServidorBarreraNorte()).start();
        new Thread(new ServidorDepositoPociones()).start();
        
        System.out.println("✅ TODOS LOS SERVIDORES DE ROEDALIA ESTÁN OPERATIVOS");
        System.out.println("El reino está listo para recibir a sus habitantes...\n");
    }
}
