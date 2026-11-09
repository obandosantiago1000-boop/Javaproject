package dao;

import database.conecction;
import model.entity.Solicitudes;
import model.entity.clientes;
import model.entity.empleados;
import model.entity.prestamos;
import model.entity.cuota;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class prestamoDao {

    private final conecction c = conecction.getInstance();

    private final cuotaDao daoCuota = new cuotaDao();


    private final String SQL_BASE = """
        SELECT
            pr.id AS prestamo_id,
            pr.interes,
            pr.fecha_inicio,
            pr.estado,

            s.id AS solicitud_id,
            s.monto AS solicitud_monto,
            s.cuotas AS solicitud_cuotas,

            e.id AS empleado_id,
            e.rol AS empleado_rol,
            e.salario AS empleado_salario,
            pe.nombre AS empleado_nombre,
            pe.documento AS empleado_documento,
            pe.correo AS empleado_correo,

            cl.id AS cliente_id,
            cl.telefono AS cliente_telefono,
            pc.nombre AS cliente_nombre,
            pc.documento AS cliente_documento,
            pc.correo AS cliente_correo

        FROM prestamos pr

        INNER JOIN empleados e
            ON pr.empleado_fk = e.id

        INNER JOIN personas pe
            ON e.id = pe.id

        INNER JOIN solicitudes s
            ON pr.solicitud_fk = s.id

        INNER JOIN clientes cl
            ON s.cliente_fk = cl.id

        INNER JOIN personas pc
            ON cl.id = pc.id
        """;


public prestamos agregar(prestamos prestamo) throws SQLException {

    if (prestamo.getEmpleado() == null
            || prestamo.getEmpleado().getId() <= 0) {

        throw new SQLException("Debe seleccionar un empleado válido.");
    }

    if (prestamo.getSolicitud() == null
            || prestamo.getSolicitud().getId() <= 0) {

        throw new SQLException("Debe seleccionar una solicitud válida.");
    }

    String sql = """
        INSERT INTO prestamos(
            interes,
            fecha_inicio,
            estado,
            empleado_fk,
            solicitud_fk
        )
        VALUES (?, ?, ?, ?, ?)
        """;

    String sqlActualizar = """
        UPDATE solicitudes
        SET estado = 'UTILIZADA'
        WHERE id = ? AND estado = 'PENDIENTE'
        """;

    try (Connection con = c.conectar()) {

        con.setAutoCommit(false);

        try {

            try (PreparedStatement psActualizar =
                    con.prepareStatement(sqlActualizar)) {

                psActualizar.setLong(
                    1, prestamo.getSolicitud().getId()
                );

                int filas = psActualizar.executeUpdate();

                if (filas == 0) {
                    throw new SQLException(
                        "La solicitud ya fue utilizada o no está disponible."
                    );
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setDouble(1, prestamo.getInteres());
                ps.setDate(2, Date.valueOf(prestamo.getFechaInicio()));
                ps.setString(3, prestamo.getEstado());
                ps.setLong(4, prestamo.getEmpleado().getId());
                ps.setLong(5, prestamo.getSolicitud().getId());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (!rs.next()) {
                        throw new SQLException(
                            "No se pudo generar el ID del préstamo."
                        );
                    }

                    prestamo.setId(rs.getLong(1));
                }
            }

            List<cuota> cuotas = prestamo.generarCuotas();

            daoCuota.insertarLote(con, cuotas);

            prestamo.setCuotas(cuotas);

            con.commit();

            return prestamo;

        } catch (SQLException e) {

            con.rollback();

            throw e;

        } finally {

            con.setAutoCommit(true);
        }

    }
}


public List<prestamos> buscarPorCliente(long idCliente) throws SQLException {

    String sql = SQL_BASE + """
        WHERE cl.id = ?
        ORDER BY pr.id
        """;

    List<prestamos> lista = new ArrayList<>();

    try (Connection con = c.conectar();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setLong(1, idCliente);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirPrestamo(rs));
            }
        }
    }
        for (prestamos p : lista) {
        p.setCuotas(daoCuota.listarPorPrestamo(p.getId()));
    }


    return lista;
}
    


    private prestamos convertirPrestamo(ResultSet rs)
            throws SQLException {

        clientes cliente = new clientes(
            rs.getString("cliente_telefono"),
            rs.getLong("cliente_id"),
            rs.getString("cliente_nombre"),
            rs.getString("cliente_documento"),
            rs.getString("cliente_correo")
        );

        Solicitudes solicitud = new Solicitudes(
            rs.getLong("solicitud_id"),
            rs.getDouble("solicitud_monto"),
            rs.getInt("solicitud_cuotas"),
            cliente
        );

        empleados empleado = new empleados(
            rs.getString("empleado_rol"),
            rs.getDouble("empleado_salario"),
            rs.getLong("empleado_id"),
            rs.getString("empleado_nombre"),
            rs.getString("empleado_documento"),
            rs.getString("empleado_correo")
        );

        Date fechaSQL = rs.getDate("fecha_inicio");

        java.time.LocalDate fecha = fechaSQL != null
                ? fechaSQL.toLocalDate()
                : null;

        return new prestamos(
            rs.getLong("prestamo_id"),
            rs.getDouble("interes"),
            fecha,
            rs.getString("estado"),
            empleado,
            solicitud
        );
    }


    public prestamos buscarId(long id) throws SQLException {

        String sql = SQL_BASE + """
            WHERE pr.id = ?
            """;

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    prestamos p = convertirPrestamo(rs);
                    p.setCuotas(daoCuota.listarPorPrestamo(p.getId()));
                    return p;
                }
            }
        }

        return null;
    }


    public List<prestamos> listar() throws SQLException {

        String sql = SQL_BASE + """
            ORDER BY pr.id
            """;

        List<prestamos> lista = new ArrayList<>();

        try (
            Connection con = c.conectar();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(convertirPrestamo(rs));
            }
        }
            for (prestamos p : lista) {
        p.setCuotas(daoCuota.listarPorPrestamo(p.getId()));
    }

    return lista;


    }
}