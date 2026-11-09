package model.service;

import dao.auditoriaDao;
import model.entity.sesion;

import java.sql.SQLException;


public final class bitacora {

    private static final auditoriaDao dao = new auditoriaDao();

    private bitacora() {
    }

    public static void registrar(String modulo, String accion, String detalle) {

        if (!sesion.activa()) {
            return;
        }

        guardar(sesion.getId(), sesion.getNombre(), sesion.getTipo(),
                modulo, accion, detalle);
    }


    public static void registrarComo(long id, String nombre, String tipo,
                                     String modulo, String accion, String detalle) {

        guardar(id, nombre, tipo, modulo, accion, detalle);
    }

    private static void guardar(long id, String nombre, String tipo,
                                String modulo, String accion, String detalle) {
        try {
            dao.registrar(id, nombre, tipo, modulo, accion, detalle);
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo guardar en la bitácora ("
                    + e.getMessage() + ")");
        }
    }
}
