package dao;

import database.conecction;
import model.entity.cuota;
import java.sql.Statement;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class cuotaDao {

    private final conecction c = conecction.getInstance();

    
    public void insertarLote(Connection con, List<cuota> lista)
        throws SQLException {

    String sql = """
        INSERT INTO cuota(fecha_pago, valor, prestamo_fk)
        VALUES (?, ?, ?)
        """;

    try (PreparedStatement ps = con.prepareStatement(
            sql, Statement.RETURN_GENERATED_KEYS)) {

        for (cuota q : lista) {

            ps.setDate(1, Date.valueOf(q.getFechaPago()));
            ps.setDouble(2, q.getValor());
            ps.setLong(3, q.getPrestamoId());

            ps.addBatch();
        }

        ps.executeBatch();

        
        try (ResultSet rs = ps.getGeneratedKeys()) {

            int i = 0;

            while (rs.next() && i < lista.size()) {
                lista.get(i).setId(rs.getLong(1));
                i++;
            }
        }
    }
}

    
    public List<cuota> listarPorPrestamo(long idPrestamo)
            throws SQLException {

        String sql = """
            SELECT id, fecha_pago, valor, prestamo_fk, valor_pagado, estado
            FROM cuota
            WHERE prestamo_fk = ?
            ORDER BY fecha_pago, id
            """;

        List<cuota> lista = new ArrayList<>();

        try (Connection con = c.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPrestamo);

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
}
