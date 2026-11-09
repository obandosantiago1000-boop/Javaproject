/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.service;

import dao.solicitudDao;
import java.sql.SQLException;
import model.entity.Solicitudes;
import view.mensajes;

/**
 *
 * @author ALIENWARE AURORA
 */
public class asesorHandler extends ManejadorSolicitud {

    private final solicitudDao dao = new solicitudDao();
    private final mensajes m = new mensajes();

    @Override
    public boolean manejar(Solicitudes solicitud) {
        if (solicitud.getMonto() < 1000000) {
            m.error("EL MONTO MINIMO DEBE SER DE $1.000.000");
            return false;
        }

        if (solicitud.getCuotas() <= 12) {

            try {
                dao.agregar(solicitud);
                m.informativo("SOLICITUD APROBADA POR EL ASESOR");
                return true;

            } catch (SQLException ex) {
                m.error("ERROR AL GUARDAR LA SOLICITUD");
                return false;
            }

        } else if (siguiente != null) {

            siguiente.manejar(solicitud);

        } else {
            m.error("NO HAY UN NIVEL DE APROBACION DISPONIBLE");
        }
        return false;


        
    }
}
