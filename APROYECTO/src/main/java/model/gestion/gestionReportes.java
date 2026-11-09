package model.gestion;

import dao.reporteDao;
import model.entity.ReporteGenerado;

import java.sql.SQLException;
import java.util.List;


public class gestionReportes {

    private static final String ARCHIVO = "reportes_archivo.dat";

    private final Almacen<ReporteGenerado> almacen = new Almacen<>(ARCHIVO);

    private final reporteDao dao = new reporteDao();

    public void guardar(ReporteGenerado reporte) {

        try {
            dao.guardarReporte(reporte.titulo(), reporte.fecha(), reporte.contenido());
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo guardar el reporte en la base de datos ("
                    + e.getMessage() + ")");
        }

        List<ReporteGenerado> lista = almacen.cargar();
        lista.add(reporte);
        almacen.guardar(lista);
    }

    public List<ReporteGenerado> listar() {
        return almacen.cargar();
    }
}
