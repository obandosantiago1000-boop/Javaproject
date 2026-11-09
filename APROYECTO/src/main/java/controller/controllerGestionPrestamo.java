package controller;
import model.service.bitacora;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import dao.prestamoDao;
import dao.cuotaDao;
import dao.solicitudDao;
import dao.empleadoDao;

import model.entity.Solicitudes;
import model.entity.empleados;
import model.entity.prestamos;
import model.entity.cuota;

import model.gestion.gestionPrestamos;

import view.ingresos.ingresoPrestamos;

import java.sql.SQLException;
import java.util.List;
import view.menus.menuAdmin;

public class controllerGestionPrestamo {

    private final prestamoDao dao = new prestamoDao();

    private final cuotaDao daoCuota = new cuotaDao();

    private final solicitudDao daoSolicitud = new solicitudDao();

    private final empleadoDao daoEmpleado = new empleadoDao();

    private final gestionPrestamos gestion = new gestionPrestamos();

    private final ingresoPrestamos ingreso = new ingresoPrestamos();
    
    private final menuAdmin menu = new menuAdmin();



    private void agregar() throws SQLException {

        System.out.println("\n===== REGISTRAR PRÉSTAMO =====");



        List<Solicitudes> listaSolicitudes = daoSolicitud.listarDisponibles();

        if (listaSolicitudes.isEmpty()) {

            System.out.println("No existen solicitudes registradas.");

            return;
        }

        System.out.println("\n===== SOLICITUDES DISPONIBLES =====");

        for (Solicitudes solicitud : listaSolicitudes) {

            System.out.println(solicitud);
        }
        
        

        long idSolicitud = ingreso.pedirId();

        Solicitudes solicitud = daoSolicitud.buscarId(idSolicitud);

        if (solicitud == null) {

            System.out.println("No existe una solicitud con ese ID.");

            return;
        }



        List<empleados> listaEmpleados = daoEmpleado.listar();

        if (listaEmpleados.isEmpty()) {

            System.out.println("No existen empleados registrados.");

            return;
        }

        System.out.println("\n===== EMPLEADOS DISPONIBLES =====");

        for (empleados empleado : listaEmpleados) {

            System.out.println(empleado);
        }


       



        long idEmpleado = ingreso.pedirId();

        empleados empleado = daoEmpleado.buscarId(idEmpleado);

        if (empleado == null) {

            System.out.println("No existe un empleado con ese ID.");

            return;
        }



        double porcentaje;

        do {

            porcentaje = ingreso.pedirInteres();

            if (porcentaje < 0) {

                System.out.println(
                    "El interés no puede ser negativo."
                );
            }

        } while (porcentaje < 0);



        double interes = porcentaje / 100.0;



        prestamos nuevo = new prestamos(
            0,
            interes,
            empleado,
            solicitud
        );



        prestamos registrado = dao.agregar(nuevo);



        gestion.sincronizar(registrado);



        System.out.println("\nPRÉSTAMO REGISTRADO CORRECTAMENTE.");

        System.out.println("ID generado: " + registrado.getId());

        bitacora.registrar("PRESTAMOS", "REGISTRAR", "ID " + registrado.getId() + " - Cliente: " + solicitud.getCliente().getNombre() + " - Monto: " + String.format("%.2f", solicitud.getMonto()) + " - Empleado asignado: " + empleado.getNombre());

        System.out.println(registrado);

        
    }
    
    

private void listarSolicitudes() throws SQLException {

    List<Solicitudes> lista = daoSolicitud.listarDisponibles();

    if (lista.isEmpty()) {

        System.out.println("No existen solicitudes registradas.");

        return;
    }

    System.out.println("\n===== SOLICITUDES REGISTRADAS =====");

    for (Solicitudes solicitud : lista) {

        System.out.println(solicitud);
    }
}



    public void listar() throws SQLException {

        List<prestamos> lista = dao.listar();

        gestion.sincronizarTodos(lista);

        if (lista.isEmpty()) {

            System.out.println("No existen préstamos registrados.");

            return;
        }

        System.out.println("\n===== PRÉSTAMOS REGISTRADOS =====");

        for (prestamos prestamo : lista) {

            System.out.println(prestamo);
        }
    }


    private void buscar() throws SQLException {

        long id = ingreso.pedirId();

        prestamos encontrado = dao.buscarId(id);

        if (encontrado == null) {

            System.out.println("No se encontró el préstamo.");

        } else {

            System.out.println(encontrado);
            bitacora.registrar("PRESTAMOS", "BUSCAR", "ID " + id);

            gestion.sincronizar(encontrado);
        }
    }


    private void verCuotas() throws SQLException {

        long id = ingreso.pedirId();

        prestamos p = dao.buscarId(id);

        if (p == null) {

            System.out.println("No se encontró el préstamo.");

            return;
        }

        List<cuota> lista = daoCuota.listarPorPrestamo(id);
        bitacora.registrar("PRESTAMOS", "VER_CUOTAS", "Prestamo ID " + id);

        System.out.println("\n===== CUOTAS DEL PRÉSTAMO " + id + " =====");

        for (int i = 0; i < lista.size(); i++) {
    System.out.println(lista.get(i).toString(i + 1));
}
    }


private void exportar() throws SQLException {

    JFileChooser selector = new JFileChooser();

    selector.setDialogTitle(
        "Guardar copia de seguridad de préstamos"
    );

    selector.setSelectedFile(
        new File("backupPrestamos.dat")
    );

    selector.setFileFilter(
        new FileNameExtensionFilter(
            "Archivos DAT (*.dat)", "dat"
        )
    );

    int resultado = selector.showSaveDialog(null);

    if (resultado == JFileChooser.APPROVE_OPTION) {

        File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith(".dat")) {

            archivo = new File(
                archivo.getAbsolutePath() + ".dat"
            );
        }


        List<prestamos> lista = dao.listar();


        gestion.sincronizarTodos(lista);


        gestion.exportarBackup(archivo.getAbsolutePath());
        bitacora.registrar("PRESTAMOS", "EXPORTAR", archivo.getName());

    } else {

        System.out.println("Exportación cancelada.");
    }
}
    
    

    public void menu() throws SQLException {

        int op;

        do {

            op = menu.menuPrestamos();

            switch (op) {

                case 1 -> agregar();

                case 2 -> {
                    listar();
                    bitacora.registrar("PRESTAMOS", "LISTAR", null);
                }

                case 3 -> buscar();

                case 4 -> {
                    listarSolicitudes();
                    bitacora.registrar("PRESTAMOS", "LISTAR_SOLICITUDES", null);
                }
                
                case 5 -> exportar();

                case 6 -> verCuotas();

                case 7 -> {
                    System.out.println(
                        "Regresando al menú principal..."
                    );
                    return;
                }

                default ->
                    System.out.println("Opción fuera de rango.");
            }

        } while (op != 7);
    }
}