package dao;

import database.conecction;
import model.entity.Solicitudes;
import model.entity.clientes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class solicitudDao {

    private final conecction c = conecction.getInstance();

    private final String SQL_BASE = """
        SELECT s.id AS solicitud_id,
               s.monto,
               s.cuotas,
               p.id AS cliente_id,
               p.nombre,
               p.documento,
               p.correo,
               cl.telefono
        FROM solicitudes s
        INNER JOIN clientes cl ON s.cliente_fk = cl.id
        INNER JOIN personas p ON cl.id = p.id
        """;


    public Solicitudes agregar(Solicitudes solicitud) throws SQLException {

        if (solicitud.getCliente() == null
                || solicitud.getCliente().getId() <= 0) {

            throw new SQLException("Debe seleccionar un cliente válido.");
        }

        String sql = """
            INSERT INTO solicitudes(monto, cuotas, cliente_fk)
            VALUES (?, ?, ?)
            """;

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
            )
        ) {

            ps.setDouble(1, solicitud.getMonto());
            ps.setInt(2, solicitud.getCuotas());
            ps.setLong(3, solicitud.getCliente().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (!rs.next()) {
                    throw new SQLException(
                        "No se pudo generar el ID de la solicitud."
                    );
                }

                solicitud.setId(rs.getLong(1));
            }

            return solicitud;
        }
    }


    public boolean eliminar(long id) throws SQLException {

        String sql = """
            DELETE FROM solicitudes
            WHERE id = ?
            """;

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            return ps.executeUpdate() == 1;
        }
    }


    private Solicitudes convertirSolicitud(ResultSet rs)
            throws SQLException {

        clientes cliente = new clientes(
            rs.getString("telefono"),
            rs.getLong("cliente_id"),
            rs.getString("nombre"),
            rs.getString("documento"),
            rs.getString("correo")
        );

        return new Solicitudes(
            rs.getLong("solicitud_id"),
            rs.getDouble("monto"),
            rs.getInt("cuotas"),
            cliente
        );
    }


    public Solicitudes buscarId(long id) throws SQLException {

        String sql = SQL_BASE + """
            WHERE s.id = ?
            """;

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return convertirSolicitud(rs);
                }
            }
        }

        return null;
    }
    
    
    public List<Solicitudes> listarDisponibles() throws SQLException {

    String sql = SQL_BASE
            + " WHERE s.estado = 'PENDIENTE'";

    List<Solicitudes> lista = new ArrayList<>();

    try (Connection con = c.conectar();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(convertirSolicitud(rs));
        }
    }

    return lista;
}


    public List<Solicitudes> listar() throws SQLException {

        String sql = SQL_BASE + """
            ORDER BY s.id
            """;

        List<Solicitudes> lista = new ArrayList<>();

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Solicitudes solicitud = convertirSolicitud(rs);

                lista.add(solicitud);
            }
        }

        return lista;
    }
    
    
    public List<Solicitudes> buscarPorCliente(long idCliente) throws SQLException {

    String sql = SQL_BASE + " WHERE s.cliente_fk = ? ORDER BY s.id";

    List<Solicitudes> lista = new ArrayList<>();

    try (Connection con = c.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, idCliente);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirSolicitud(rs));
            }
        }
    }

    return lista;
}
}
