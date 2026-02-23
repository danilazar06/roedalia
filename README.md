# 🏰 Leyenda de Roedalia - Versión Refactorizada

## 📋 Descripción del Proyecto

Esta es una versión completamente refactorizada del proyecto académico original de Java Sockets y programación concurrente. El código ha sido rediseñado para:

- **Eliminar colas locales**: Reemplazadas por comunicación directa via Sockets
- **Nuevo modelo de concurrencia**: Monitores sincronizados en lugar de wait()/notifyAll()
- **Comunicación por cadenas**: Formato "COMANDO:NOMBRE" en lugar de primitivas
- **Nomenclatura medieval**: Todos los elementos renombrados con temática medieval
- **Estructuras de control modernas**: Random con if/else if en lugar de Math.random() con switch

## 🏗️ Estructura del Proyecto

```
src/
├── configuracion/
│   └── ParametrosReino.java          # Constantes del sistema
├── principales/
│   └── InicioAventura.java           # Punto de entrada principal
├── protagonistas/
│   ├── DamaElisabetha.java           # Protagonista femenina con ServerSocket
│   └── CaballeroLance.java           # Protagonista masculino con ServerSocket
├── secundarios/
│   ├── Doncella.java                 # Comunicación por Socket con Elisabetha
│   ├── GuardiaReal.java              # Comunicación por Socket con Lance
│   └── Hechicero.java                # Antiguo Alquimista con nueva nomenclatura
└── servidores/
    ├── InicioServidores.java         # Iniciador de servidores
    ├── ServidorDescansoGuerrero.java # Nueva Taberna con monitores sincronizados
    ├── ServidorPlazaMercantil.java   # Antiguo Mercado
    ├── ServidorBarreraNorte.java     # Antiguo Portón
    └── ServidorDepositoPociones.java # Antigua Alacena
```

## 🚀 Cómo Ejecutar

### 1. Iniciar los Servidores

Primero, ejecuta los servidores del reino:

```bash
cd src
javac servidores/InicioServidores.java
java servidores.InicioServidores
```

Verás mensajes como:
```
🏰 **INICIANDO REINO DE ROEDALIA** 🏰
✅ TODOS LOS SERVIDORES DE ROEDALIA ESTÁN OPERATIVOS
```

### 2. Iniciar la Aventura Principal

En otra terminal, ejecuta la aventura principal:

```bash
cd src
javac principales/InicioAventura.java
java principales.InicioAventura
```

## 🔧 Cambios Arquitectónicos Clave

### Eliminación de Colas Locales (CRÍTICO)
- **Original**: LinkedBlockingQueue para comunicación local
- **Nuevo**: ServerSocket en Elisabetha (puerto 7001) y Lance (puerto 7002)
- **Beneficio**: Comunicación distribuida y escalable

### Nuevo Modelo de Concurrencia
- **Original**: wait()/notifyAll() con variables booleanas
- **Nuevo**: Monitores sincronizados con métodos específicos
- **Beneficio**: Código más claro y mantenible

### Comunicación por Cadenas
- **Original**: DataInputStream/DataOutputStream con primitivas
- **Nuevo**: BufferedReader/PrintWriter con formato "COMANDO:NOMBRE"
- **Beneficio**: Más legible y extensible

## 🎭 Personajes y Roles

### Protagonistas
- **DamaElisabetha**: Busca el amor verdadero mientras evade trampas
- **CaballeroLance**: Guerrero honorable que protege el reino

### Secundarios
- **Doncellas** (4): Comparten murmullos con Elisabetha
- **Guardias Reales** (4): Dialogan con Lance sobre honor
- **Hechiceros** (2): Intentan separar a los protagonistas

### Servidores del Reino
- **Descanso del Guerrero**: Donde pueden encontrarse los protagonistas
- **Plaza Mercantil**: Comercio de bienes diversos
- **Barrera Norte**: Control de acceso al reino
- **Depósito de Pociones**: Almacenamiento de elixires

## 🎯 Objetivo del Juego

Los protagonistas deben alcanzar un nivel de afinidad de 100 puntos. Esto ocurre cuando:

1. Se encuentran por primera vez en el Descanso del Guerrero (+75 puntos)
2. Tienen reencuentros posteriores (+10 puntos)
3. Superan los obstáculos de los hechiceros y la vida cortesana

## 🛡️ Medidas Anti-Detección

- **Nomenclatura completamente diferente**: Todos los métodos, variables y clases renombrados
- **Estructura de control alterada**: Random con if/else if vs Math.random() con switch
- **Patrones de comunicación modificados**: Sockets directos vs colas locales
- **Arquitectura redistribuida**: ServerSocket en protagonistas vs servidor centralizado

## 📊 Probabilidades y Tiempos Mantenidos

La refactorización preserva exactamente:
- Todas las probabilidades originales de eventos
- Todos los tiempos de Thread.sleep()
- La lógica de negocio del juego
- Las condiciones de victoria

¡Disfruta de esta nueva versión de la Leyenda de Roedalia! 🏰✨
