package configuracion;

public class ParametrosReino {
    public static final String DOMINIO_LOCAL = "localhost";
    public static final int PUERTO_DESCANSO_GUERRERO = 5004;
    public static final int PUERTO_PLAZA_MERCANTIL = 5001;
    public static final int PUERTO_BARRERA_NORTE = 5002;
    public static final int PUERTO_DEPOSITO_POCIONES = 5003;
    public static final int PUERTO_SERVIDOR_ELISA = 7001;
    public static final int PUERTO_SERVIDOR_LANCE = 7002;
    public static volatile boolean dragonDespierto = false;
    public static volatile boolean tabernaDestruida = false;
    public static volatile boolean portonDestruido = false;
    public static volatile boolean mercadoDestruido = false;
    public static volatile boolean portonBloqueado = false;
    public static volatile boolean elisabethaEnMercado = false;
    public static volatile int intentosAtaqueRestantes = 2;

}
