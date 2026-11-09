package dao;

import database.conecction;
import model.entity.cuota;
import model.entity.pago;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class pagoDao {

    private final conecction c = conecction.getInstance();

    private static final double TOLERANCIA = 0.01;


    public pago pagarCuota(long prestamoId, String metodo)
            throws SQLException {

        return procesar(prestamoId, 0, metodo, true);
    }


    public pago abonar(long prestamoId, double monto, String metodo)
            throws SQLException {

        return procesar(prestamoId, monto, metodo, false);
    }


    private pago procesar(long prestamoId, double monto,
                          String metodo, boolean soloUnaCuota)
            throws SQLException {

        try (Connection con = c.conectar()) {

            con.setAutoCommit(false);

            try {

                String sqlPrestamo = """
                    SELECT estado
                    FROM prestamos
                    WHERE id = ?
                    FOR UPDATE
                    """;

                try (PreparedStatement ps = con.prepareStatement(sqlPrestamo)) {

                    ps.setLong(1, prestamoId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("El préstamo no existe.");
                        }

                        if (!"ACTIVO".equals(rs.getString("estado"))) {
                            throw new SQLException("El préstamo ya está pagado.");
                        }
                    }
                }

                List<cuota> pendientes = cuotasPendientes(con, prestamoId);

                if (pendientes.isEmpty()) {
                    throw new SQLException(
                        "El préstamo no tiene cuotas pendientes."
                    );
                }

                double saldoTotal = 0;

                for (cuota q : pendientes) {
                    saldoTotal += q.getSaldo();
                }

                if (soloUnaCuota) {
                    monto = pendientes.get(0).getSaldo();
                }

                monto = redondear(monto);

                if (monto <= 0) {
                    throw new SQLException("El monto debe ser mayor a cero.");
                }

                if (monto > saldoTotal + TOLERANCIA) {
                    throw new SQLException(
                        "El monto supera el saldo pendiente ("
                        + String.format("%.2f", saldoTotal) + ")."
                    );
                }

                long metodoId = obtenerMetodo(con, metodo);

                LocalDate hoy = LocalDate.now();

                long pagoId = insertarPago(con, hoy, monto, prestamoId, metodoId);

                boolean quedanPendientes = repartir(con, pendientes, monto);

                if (!quedanPendientes) {

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE prestamos SET estado = 'PAGADO' WHERE id = ?")) {

                        ps.setLong(1, prestamoId);
                        ps.executeUpdate();
                    }
                }

                con.commit();

                return new pago(pagoId, hoy, monto, prestamoId, metodo);

            } catch (SQLException e) {

                con.rollback();

                throw e;

            } finally {

                con.setAutoCommit(true);
            }
        }
    }


    private List<cuota> cuotasPendientes(Connection con, long prestamoId)
            throws SQLException {

        String sql = """
            SELECT id, fecha_pago, valor, prestamo_fk, valor_pagado, estado
            FROM cuota
            WHERE prestamo_fk = ? AND estado = 'PENDIENTE'
            ORDER BY fecha_pago, id
            FOR UPDATE
            """;

        List<cuota> lista = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, prestamoId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(new cuota(
                        rs.getLong("id"),
                        rs.getDate("fecha_pago").toLocalDate(),
                        rs.getDouble("valor"),
                        rs.getLong("prestamo_fk"),
                        rs.getDouble("valor_pagado"),
                        rs.getString("estado")
                    ));
                }
            }
        }

        return lista;
    }



    private boolean repartir(Connection con, List<cuota> pendientes,
                             double monto) throws SQLException {

        String sql = """
            UPDATE cuota
            SET valor_pagado = ?, estado = ?
            WHERE id = ?
            """;

        double restante = monto;
        boolean quedanPendientes = false;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (cuota q : pendientes) {

                if (restante < 0.005) {
                    quedanPendientes = true;
                    break;
                }

                double aplicado = Math.min(restante, q.getSaldo());

                double nuevoPagado = redondear(q.getValorPagado() + aplicado);

                boolean completa = nuevoPagado >= q.getValor() - 0.005;

                if (completa) {
                    nuevoPagado = q.getValor();
                } else {
                    quedanPendientes = true;
                }

                ps.setDouble(1, nuevoPagado);
                ps.setString(2, completa ? "PAGADA" : "PENDIENTE");
                ps.setLong(3, q.getId());
                ps.executeUpdate();

                restante = redondear(restante - aplicado);
            }
        }

        return quedanPendientes;
    }


    private long insertarPago(Connection con, LocalDate fecha, double monto,
                              long prestamoId, long metodoId)
            throws SQLException {

        String sql = """
            INSERT INTO pagos(fecha_pago, monto, prestamo_fk, metodo_fk)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(fecha));
            ps.setDouble(2, monto);
            ps.setLong(3, prestamoId);
            ps.setLong(4, metodoId);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (!rs.next()) {
                    throw new SQLException("No se pudo generar el ID del pago.");
                }

                return rs.getLong(1);
            }
        }
    }


    private long obtenerMetodo(Connection con, String metodo)
            throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id FROM metodo_pagos WHERE metodo = ? LIMIT 1")) {

            ps.setString(1, metodo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO metodo_pagos(metodo) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, metodo);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (!rs.next()) {
                    throw new SQLException(
                        "No se pudo registrar el método de pago."
                    );
                }

                return rs.getLong(1);
            }
        }
    }


    public List<pago> historialPorPrestamo(long prestamoId)
            throws SQLException {

        String sql = """
            SELECT p.id, p.fecha_pago, p.monto, p.prestamo_fk, m.metodo
            FROM pagos p
            LEFT JOIN metodo_pagos m
                ON p.metodo_fk = m.id
            WHERE p.prestamo_fk = ?
            ORDER BY p.fecha_pago, p.id
            """;

        List<pago> lista = new ArrayList<>();

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, prestamoId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(new pago(
                        rs.getLong("id"),
                        rs.getDate("fecha_pago").toLocalDate(),
                        rs.getDouble("monto"),
                        rs.getLong("prestamo_fk"),
                        rs.getString("metodo")
                    ));
                }
            }
        }

        return lista;
    }


    public List<pago> listarTodos() throws SQLException {

        String sql = """
            SELECT p.id, p.fecha_pago, p.monto, p.prestamo_fk, m.metodo
            FROM pagos p
            LEFT JOIN metodo_pagos m
                ON p.metodo_fk = m.id
            ORDER BY p.fecha_pago, p.id
            """;

        List<pago> lista = new ArrayList<>();

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                lista.add(new pago(
                    rs.getLong("id"),
                    rs.getDate("fecha_pago").toLocalDate(),
                    rs.getDouble("monto"),
                    rs.getLong("prestamo_fk"),
                    rs.getString("metodo")
                ));
            }
        }

        return lista;
    }


    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
