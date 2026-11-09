package model.service;

public final class seguridad {

    private static final String CLAVE_POR_DEFECTO = "banco2026";

    public static final int MAX_INTENTOS = 3;

    private seguridad() {
    }

    private static String claveActual() {
        String env = System.getenv("CLAVE_GENERAL");
        return (env != null && !env.isBlank()) ? env : CLAVE_POR_DEFECTO;
    }

    public static boolean claveCorrecta(String intento) {
        return intento != null && claveActual().equals(intento);
    }
}
