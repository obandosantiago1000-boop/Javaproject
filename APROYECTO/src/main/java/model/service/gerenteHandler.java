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
public class gerenteHandler extends ManejadorSolicitud{
    private final solicitudDao dao = new solicitudDao();
    private final mensajes m = new mensajes();
    @Override
    public boolean manejar(Solicitudes solicitud) {
        if (solicitud.getCuotas() >=24) {

            try {
                dao.agregar(solicitud);
                m.informativo("SOLICITUD APROBADA POR EL GERENTE");
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