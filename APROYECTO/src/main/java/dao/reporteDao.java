package dao;

import database.conecction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;


public class reporteDao {

    private final conecction c = conecction.getInstance();

    private List<String[]> filas(String sql) throws SQLException {

        List<String[]> lista = new ArrayList<>();

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData md = rs.getMetaData();
            int columnas = md.getColumnCount();

            while (rs.next()) {

                String[] fila = new String[columnas];

                for (int i = 0; i < columnas; i++) {
                    fila[i] = formatear(rs.getObject(i + 1));
                }

                lista.add(fila);
            }
        }

        return lista;
    }

    private String formatear(Object valor) {

        if (valor == null) {
            return "-";
        }

        if (valor instanceof BigDecimal || valor instanceof Double
                || valor instanceof Float) {
            return String.format("%,.2f", ((Number) valor).doubleValue());
        }

        return valor.toString();
    }


    public List<String[]> empleadosPorRol() throws SQLException {
        return filas("""
            SELECT rol, COUNT(*) AS total,
                   COALESCE(SUM(salario), 0) AS nomina,
                   COALESCE(AVG(salario), 0) AS promedio,
                   COALESCE(MAX(salario), 0) AS mayor
            FROM empleados
            GROUP BY rol
            ORDER BY rol
            """);
    }

    public List<String[]> empleadosDesempeno() throws SQLException {
        return filas("""
            SELECT p.id, p.nombre, e.rol,
                   COUNT(pr.id) AS prestamos,
                   COALESCE(SUM(s.monto), 0) AS capital
            FROM empleados e
            INNER JOIN personas p ON p.id = e.id
            LEFT JOIN prestamos pr ON pr.empleado_fk = e.id
            LEFT JOIN solicitudes s ON s.id = pr.solicitud_fk
            GROUP BY p.id, p.nombre, e.rol
            ORDER BY capital DESC, p.id
            """);
    }


    public List<String[]> clientesResumen() throws SQLException {
        return filas("""
            SELECT
              (SELECT COUNT(*) FROM clientes) AS total,
              (SELECT COUNT(DISTINCT cliente_fk) FROM solicitudes) AS con_solicitudes,
              (SELECT COUNT(DISTINCT s.cliente_fk)
                 FROM prestamos pr
                 INNER JOIN solicitudes s ON s.id = pr.solicitud_fk) AS con_prestamos,
              (SELECT COUNT(DISTINCT s.cliente_fk)
                 FROM prestamos pr
                 INNER JOIN solicitudes s ON s.id = pr.solicitud_fk
                 WHERE pr.estado = 'ACTIVO') AS con_prestamo_activo
            """);
    }

    public List<String[]> clientesDetalle() throws SQLException {
        return filas("""
            SELECT p.id, p.nombre,
              (SELECT COUNT(*) FROM solicitudes s
                 WHERE s.cliente_fk = c.id) AS solicitudes,
              (SELECT COUNT(*) FROM prestamos pr
                 INNER JOIN solicitudes s ON s.id = pr.solicitud_fk
                 WHERE s.cliente_fk = c.id) AS prestamos,
              (SELECT COALESCE(SUM(s.monto), 0) FROM prestamos pr
                 INNER JOIN solicitudes s ON s.id = pr.solicitud_fk
                 WHERE s.cliente_fk = c.id) AS prestado,
              (SELECT COALESCE(SUM(q.valor - q.valor_pagado), 0) FROM cuota q
                 INNER JOIN prestamos pr ON pr.id = q.prestamo_fk
                 INNER JOIN solicitudes s ON s.id = pr.solicitud_fk
                 WHERE s.cliente_fk = c.id AND q.estado = 'PENDIENTE') AS deuda
            FROM clientes c
            INNER JOIN personas p ON p.id = c.id
            ORDER BY prestado DESC, p.id
            """);
    }


    public List<String[]> solicitudesResumen() throws SQLException {
        return filas("""
            SELECT COUNT(*) AS total,
                   COALESCE(SUM(monto), 0) AS suma,
                   COALESCE(AVG(monto), 0) AS promedio,
                   COALESCE(MAX(monto), 0) AS mayor,
                   COALESCE(MIN(monto), 0) AS menor,
                   COALESCE(AVG(cuotas), 0) AS cuotas_prom
            FROM solicitudes
            """);
    }

    public List<String[]> solicitudesPorEstado() throws SQLException {
        return filas("""
            SELECT estado, COUNT(*) AS total,
                   COALESCE(SUM(monto), 0) AS suma,
                   COALESCE(AVG(monto), 0) AS promedio
            FROM solicitudes
            GROUP BY estado
            ORDER BY total DESC
            """);
    }


    public List<String[]> prestamosPorEstado() throws SQLException {
        return filas("""
            SELECT pr.estado, COUNT(*) AS total,
                   COALESCE(SUM(s.monto), 0) AS capital,
                   COALESCE(SUM(s.monto * pr.interes), 0) AS intereses,
                   COALESCE(SUM(s.monto * (1 + pr.interes)), 0) AS total_a_pagar
            FROM prestamos pr
            INNER JOIN solicitudes s ON s.id = pr.solicitud_fk
            GROUP BY pr.estado
            ORDER BY pr.estado
            """);
    }

    public List<String[]> prestamosIndicadores() throws SQLException {
        return filas("""
            SELECT
              (SELECT COUNT(*) FROM prestamos) AS total,
              (SELECT COALESCE(AVG(interes), 0) * 100 FROM prestamos) AS interes_prom,
              (SELECT COALESCE(AVG(s.monto), 0) FROM prestamos pr
                 INNER JOIN solicitudes s ON s.id = pr.solicitud_fk) AS monto_prom,
              (SELECT COALESCE(SUM(monto), 0) FROM pagos) AS recaudado,
              (SELECT COALESCE(SUM(valor - valor_pagado), 0) FROM cuota
                 WHERE estado = 'PENDIENTE') AS por_cobrar
            """);
    }


    public List<String[]> pagosResumen() throws SQLException {
        return filas("""
            SELECT COUNT(*) AS total,
                   COALESCE(SUM(monto), 0) AS recaudado,
                   COALESCE(AVG(monto), 0) AS promedio,
                   COALESCE(MAX(monto), 0) AS mayor
            FROM pagos
            """);
    }

    public List<String[]> pagosPorMetodo() throws SQLException {
        return filas("""
            SELECT COALESCE(m.metodo, 'SIN METODO') AS metodo,
                   COUNT(*) AS total,
                   COALESCE(SUM(p.monto), 0) AS recaudado
            FROM pagos p
            LEFT JOIN metodo_pagos m ON m.id = p.metodo_fk
            GROUP BY COALESCE(m.metodo, 'SIN METODO')
            ORDER BY recaudado DESC
            """);
    }

    public List<String[]> pagosPorMes() throws SQLException {
        return filas("""
            SELECT DATE_FORMAT(fecha_pago, '%Y-%m') AS mes,
                   COUNT(*) AS total,
                   COALESCE(SUM(monto), 0) AS recaudado
            FROM pagos
            GROUP BY DATE_FORMAT(fecha_pago, '%Y-%m')
            ORDER BY mes DESC
            LIMIT 12
            """);
    }

    public List<String[]> cuotasPorEstado() throws SQLException {
        return filas("""
            SELECT estado, COUNT(*) AS total,
                   COALESCE(SUM(valor), 0) AS valor,
                   COALESCE(SUM(valor_pagado), 0) AS pagado
            FROM cuota
            GROUP BY estado
            ORDER BY estado
            """);
    }

    public List<String[]> cuotasVencidas() throws SQLException {
        return filas("""
            SELECT COUNT(*) AS cuotas,
                   COUNT(DISTINCT prestamo_fk) AS prestamos,
                   COALESCE(SUM(valor - valor_pagado), 0) AS monto
            FROM cuota
            WHERE estado = 'PENDIENTE' AND fecha_pago < CURDATE()
            """);
    }


    private void asegurarTablaReportes() throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS reportes_generados (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                titulo VARCHAR(100) NOT NULL,
                fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                contenido LONGTEXT NOT NULL
            )
            """;

        try (Connection con = c.conectar();
             Statement st = con.createStatement()) {

            st.executeUpdate(sql);
        }
    }

    public void guardarReporte(String titulo, LocalDateTime fecha, String contenido)
            throws SQLException {

        asegurarTablaReportes();

        String sql = "INSERT INTO reportes_generados(titulo, fecha, contenido) VALUES (?, ?, ?)";

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, titulo);
            ps.setTimestamp(2, Timestamp.valueOf(fecha));
            ps.setString(3, contenido);
            ps.executeUpdate();
        }
    }
}
