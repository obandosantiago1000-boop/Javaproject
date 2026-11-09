package model.entity;


public final class sesion {

    public static final String EMPLEADO = "EMPLEADO";
    public static final String CLIENTE = "CLIENTE";

    private static long id;
    private static String nombre;
    private static String tipo;
    private static String rol;

    private sesion() {
    }

    public static void iniciar(long id, String nombre, String tipo, String rol) {
        sesion.id = id;
        sesion.nombre = nombre;
        sesion.tipo = tipo;
        sesion.rol = rol;
    }

    public static void cerrar() {
        id = 0;
        nombre = null;
        tipo = null;
        rol = null;
    }

    public static boolean activa() {
        return tipo != null;
    }

    public static long getId() {
        return id;
    }

    public static String getNombre() {
        return nombre;
    }

    public static String getTipo() {
        return tipo;
    }

    public static String getRol() {
        return rol;
    }
}
