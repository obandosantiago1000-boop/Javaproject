package model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public record registroAccion(
        long id,
        LocalDateTime fecha,
        long usuarioId,
        String usuario,
        String tipoUsuario,
        String modulo,
        String accion,
        String detalle) {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        String texto = String.format("%s | %-9s | %-18s | %s",
                fecha.format(FORMATO), tipoUsuario, modulo + "/" + accion,
                usuario + " (ID " + usuarioId + ")");

        if (detalle != null && !detalle.isBlank()) {
            texto += "\n      -> " + detalle;
        }

        return texto;
    }
}
