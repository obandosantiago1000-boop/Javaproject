
package dao;

import database.conecction;
import model.entity.empleados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class empleadoDao {

    private final conecction c = conecction.getInstance();


    public empleados agregar(empleados empleado) throws SQLException {

        String sqlPersona = """
            INSERT INTO personas(nombre, documento, correo)
            VALUES (?, ?, ?)
            """;

        String sqlEmpleado = """
            INSERT INTO empleados(id, rol, salario)
            VALUES (?, ?, ?)
            """;

        try (Connection con = c.conectar()) {

            try {

                con.setAutoCommit(false);

                long idGenerado;


                try (PreparedStatement ps = con.prepareStatement(
                        sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, empleado.getNombre());
                    ps.setString(2, empleado.getDocumento());
                    ps.setString(3, empleado.getCorreo());

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


                try (PreparedStatement ps = con.prepareStatement(sqlEmpleado)) {

                    ps.setLong(1, idGenerado);
                    ps.setString(2, empleado.getRol());
                    ps.setDouble(3, empleado.getSalario());

                    ps.executeUpdate();
                }


                empleado.setId(idGenerado);


                con.commit();

                return empleado;

            } catch (SQLException e) {


                con.rollback();

                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }


public boolean eliminar(long id) throws SQLException {

    String sqlEmpleado = """
        DELETE FROM empleados
        WHERE id = ?
        """;

    String sqlPersona = """
        DELETE FROM personas
        WHERE id = ?
        """;

    try (Connection con = c.conectar()) {

        try {

            con.setAutoCommit(false);


            try (PreparedStatement ps = con.prepareStatement(sqlEmpleado)) {

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

public List<empleados> buscarPorRol(String rol) throws SQLException {

    String sql = """
        SELECT p.id, p.nombre, p.documento, p.correo,
               e.rol, e.salario
        FROM personas p
        INNER JOIN empleados e ON p.id = e.id
        WHERE e.rol = ?
        ORDER BY p.id
        """;

    List<empleados> lista = new ArrayList<>();

    try (
        Connection con = c.conectar();
        PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setString(1, rol);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                empleados empleado = new empleados(
                    rs.getString("rol"),
                    rs.getDouble("salario"),
                    rs.getLong("id"),
                    rs.getString("nombre"),
                    rs.getString("documento"),
                    rs.getString("correo")
                );

                lista.add(empleado);
            }
        }
    }

    return lista;
}

    public empleados buscarId(long id)
            throws SQLException {

        String sql = """
            SELECT p.id, p.nombre, p.documento, p.correo,
                   e.rol, e.salario
            FROM personas p
            INNER JOIN empleados e ON p.id = e.id
            WHERE p.id = ?
            """;

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new empleados(
                        rs.getString("rol"),
                        rs.getDouble("salario"),
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


    public List<empleados> listar() throws SQLException {

        String sql = """
            SELECT p.id, p.nombre, p.documento, p.correo,
                   e.rol, e.salario
            FROM personas p
            INNER JOIN empleados e ON p.id = e.id
            ORDER BY p.id
            """;

        List<empleados> lista = new ArrayList<>();

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                empleados empleado = new empleados(
                    rs.getString("rol"),
                    rs.getDouble("salario"),
                    rs.getLong("id"),
                    rs.getString("nombre"),
                    rs.getString("documento"),
                    rs.getString("correo")
                );

                lista.add(empleado);
            }
        }

        return lista;
    }

    public boolean actualizar(empleados empleado) throws SQLException {

    String sqlPersona = """
        UPDATE personas
        SET nombre = ?, documento = ?, correo = ?
        WHERE id = ?
        """;

    String sqlEmpleado = """
        UPDATE empleados
        SET rol = ?, salario = ?
        WHERE id = ?
        """;

    try (Connection con = c.conectar()) {

        try {

            con.setAutoCommit(false);


            try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {

                ps.setString(1, empleado.getNombre());
                ps.setString(2, empleado.getDocumento());
                ps.setString(3, empleado.getCorreo());
                ps.setLong(4, empleado.getId());

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                        "No se encontró la persona que se desea actualizar."
                    );
                }
            }


            try (PreparedStatement ps = con.prepareStatement(sqlEmpleado)) {

                ps.setString(1, empleado.getRol());
                ps.setDouble(2, empleado.getSalario());
                ps.setLong(3, empleado.getId());

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                        "No se encontró el empleado que se desea actualizar."
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

