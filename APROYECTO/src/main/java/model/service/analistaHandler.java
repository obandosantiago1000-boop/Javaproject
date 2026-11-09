package model.service;

import dao.solicitudDao;
import model.entity.Solicitudes;
import view.mensajes;

import java.sql.SQLException;

public class analistaHandler extends ManejadorSolicitud {

    private final solicitudDao dao = new solicitudDao();
    private final mensajes m = new mensajes();

    @Override
    public boolean manejar(Solicitudes solicitud) {
        if (solicitud.getCuotas() >= 12  && solicitud.getCuotas() < 24) {

            try {
                dao.agregar(solicitud);
                m.informativo("SOLICITUD APROBADA POR EL ANALISTA");
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