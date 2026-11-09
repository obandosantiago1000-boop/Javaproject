package dao;

import database.conecction;
import model.entity.clientes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class clienteDao {

    private final conecction c = conecction.getInstance();


    public clientes agregar(clientes cliente) throws SQLException {

        String sqlPersona = """
            INSERT INTO personas(nombre, documento, correo)
            VALUES (?, ?, ?)
            """;

        String sqlCliente = """
            INSERT INTO clientes(id, telefono)
            VALUES (?, ?)
            """;

        try (Connection con = c.conectar()) {

            try {
                con.setAutoCommit(false);

                long idGenerado;


                try (PreparedStatement ps = con.prepareStatement(
                        sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getDocumento());
                    ps.setString(3, cliente.getCorreo());

                    ps.executeUpdate();


                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                "No se pudo generar el ID de la persona."
                            );
                        }

                        idGenerado = rs.getLong(1);
                    }
                }


                try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {

                    ps.setLong(1, idGenerado);
                    ps.setString(2, cliente.getTelefono());

                    ps.executeUpdate();
                }


                cliente.setId(idGenerado);


                con.commit();

                return cliente;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }


    public boolean eliminar(long id) throws SQLException {

        String sqlCliente = """
            DELETE FROM clientes
            WHERE id = ?
            """;

        String sqlPersona = """
            DELETE FROM personas
            WHERE id = ?
            """;

        try (Connection con = c.conectar()) {

            try {
                con.setAutoCommit(false);


                try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {

                    ps.setLong(1, id);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {

                    ps.setLong(1, id);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                            "No se pudo eliminar la persona."
                        );
                    }
                }


                con.commit();

                return true;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }
    
    
    public boolean tieneSolicitudes(long idCliente) throws SQLException {

    String sql = "SELECT COUNT(*) FROM solicitudes WHERE cliente_fk = ?";

    try (Connection con = c.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, idCliente);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }

    return false;
}

    


    public clientes buscarId(long id) throws SQLException {

        String sql = """
            SELECT p.id, p.nombre, p.documento, p.correo,
                   c.telefono
            FROM personas p
            INNER JOIN clientes c ON p.id = c.id
            WHERE p.id = ?
            """;

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new clientes(
                        rs.getString("telefono"),
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("documento"),
                        rs.getString("correo")
                    );
                }
            }
        }

        return null;
    }

    public List<clientes> listar() throws SQLException {

        String sql = """
            SELECT p.id, p.nombre, p.documento, p.correo,
                   c.telefono
            FROM personas p
            INNER JOIN clientes c ON p.id = c.id
            ORDER BY p.id
            """;

        List<clientes> lista = new ArrayList<>();

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                clientes cliente = new clientes(
                    rs.getString("telefono"),
                    rs.getLong("id"),
                    rs.getString("nombre"),
                    rs.getString("documento"),
                    rs.getString("correo")
                );

                lista.add(cliente);
            }
        }

        return lista;
    }


    public boolean actualizar(clientes cliente) throws SQLException {

        String sqlPersona = """
            UPDATE personas
            SET nombre = ?, documento = ?, correo = ?
            WHERE id = ?
            """;

        String sqlCliente = """
            UPDATE clientes
            SET telefono = ?
            WHERE id = ?
            """;

        try (Connection con = c.conectar()) {

            try {

                con.setAutoCommit(false);


                try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {

                    ps.setString(1, cliente.getNombre());
                    ps.setString(2, cliente.getDocumento());
                    ps.setString(3, cliente.getCorreo());
                    ps.setLong(4, cliente.getId());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                            "No se encontró la persona que se desea actualizar."
                        );
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {

                    ps.setString(1, cliente.getTelefono());
                    ps.setLong(2, cliente.getId());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                            "No se encontró el cliente que se desea actualizar."
                        );
                    }
                }


                con.commit();

                return true;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }
        }
    }
}