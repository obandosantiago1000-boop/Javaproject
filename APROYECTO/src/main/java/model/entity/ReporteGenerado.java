package model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


public record ReporteGenerado(String titulo, LocalDateTime fecha, List<String> lineas)
        implements Serializable {

    private static final long serialVersionUID = 1L;

    public String contenido() {
        return String.join("\n", lineas);
    }
}
