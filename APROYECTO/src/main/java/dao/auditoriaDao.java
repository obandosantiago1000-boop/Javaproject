package dao;

import database.conecction;
import model.entity.registroAccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class auditoriaDao {

    private final conecction c = conecction.getInstance();

    private static final String SELECT_BASE = """
        SELECT a.id, a.fecha_hora, a.usuario_id, p.nombre AS usuario_nombre,
               a.tipo_usuario, a.modulo, a.accion, a.detalle
        FROM auditoria a
        JOIN personas p ON p.id = a.usuario_id
        """;

    public void registrar(long usuarioId, String usuarioNombre, String tipo,
                          String modulo, String accion, String detalle)
            throws SQLException {

        String sql = """
            INSERT INTO auditoria(usuario_id, tipo_usuario,
                                  modulo, accion, detalle)
            VALUES (?, ?, ?, ?, ?)
            """;

        if (detalle != null && detalle.length() > 500) {
            detalle = detalle.substring(0, 500);
        }

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, usuarioId);
            ps.setString(2, tipo);
            ps.setString(3, modulo);
            ps.setString(4, accion);
            ps.setString(5, detalle);

            ps.executeUpdate();
        }
    }

    private List<registroAccion> consultar(String where, String orden,
                                           int limite, Object... params)
            throws SQLException {

        String sql = SELECT_BASE + where + " ORDER BY " + orden
                + (limite > 0 ? " LIMIT " + limite : "");

        List<registroAccion> lista = new ArrayList<>();

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(new registroAccion(
                            rs.getLong("id"),
                            rs.getTimestamp("fecha_hora").toLocalDateTime(),
                            rs.getLong("usuario_id"),
                            rs.getString("usuario_nombre"),
                            rs.getString("tipo_usuario"),
                            rs.getString("modulo"),
                            rs.getString("accion"),
                            rs.getString("detalle")));
                }
            }
        }

        return lista;
    }

    public List<registroAccion> porUsuario(long usuarioId, String tipo)
            throws SQLException {

        return consultar("WHERE a.usuario_id = ? AND a.tipo_usuario = ?",
                "a.fecha_hora DESC, a.id DESC", 0, usuarioId, tipo);
    }

    public List<registroAccion> porModulo(String modulo) throws SQLException {

        return consultar("WHERE a.modulo = ?",
                "a.fecha_hora DESC, a.id DESC", 0, modulo);
    }

    public List<registroAccion> ultimas(int limite) throws SQLException {

        return consultar("", "a.fecha_hora DESC, a.id DESC", limite);
    }

    public List<String[]> resumenPorUsuario(String tipo) throws SQLException {

        String sql = """
            SELECT a.usuario_id, p.nombre AS usuario_nombre, COUNT(*) AS total,
                   SUM(CASE WHEN a.accion = 'INICIO_SESION' THEN 1 ELSE 0 END) AS sesiones,
                   MAX(a.fecha_hora) AS ultima
            FROM auditoria a
            JOIN personas p ON p.id = a.usuario_id
            WHERE a.tipo_usuario = ?
            GROUP BY a.usuario_id, p.nombre
            ORDER BY total DESC
            """;

        List<String[]> filas = new ArrayList<>();

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp ultima = rs.getTimestamp("ultima");

                    filas.add(new String[]{
                        String.valueOf(rs.getLong("usuario_id")),
                        rs.getString("usuario_nombre"),
                        String.valueOf(rs.getLong("total")),
                        String.valueOf(rs.getLong("sesiones")),
                        ultima == null ? "-" : ultima.toString().substring(0, 19)
                    });
                }
            }
        }

        return filas;
    }

    public List<String[]> resumenPorModulo() throws SQLException {

        String sql = """
            SELECT modulo, accion, COUNT(*) AS total
            FROM auditoria
            GROUP BY modulo, accion
            ORDER BY modulo, total DESC
            """;

        List<String[]> filas = new ArrayList<>();

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                filas.add(new String[]{
                    rs.getString("modulo"),
                    rs.getString("accion"),
                    String.valueOf(rs.getLong("total"))
                });
            }
        }

        return filas;
    }
}