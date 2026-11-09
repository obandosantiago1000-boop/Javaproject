package model.gestion;

import model.entity.pago;

import java.util.ArrayList;
import java.util.List;


public class gestionPagos {

    private static final String ARCHIVO = "pagos_archivo.dat";

    private final Almacen<pago> almacen = new Almacen<>(ARCHIVO);

    private List<pago> pagos;

    public gestionPagos() {
        pagos = almacen.cargar();
    }


    public void sincronizarTodos(List<pago> lista) {

        pagos = new ArrayList<>(lista);
        almacen.guardar(pagos);
    }

    public List<pago> getPagos() {
        return List.copyOf(pagos);
    }

    public boolean exportarBackup(String ruta) {
        return almacen.exportar(pagos, ruta);
    }
}
