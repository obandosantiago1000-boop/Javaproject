package controller;

import model.service.bitacora;

import dao.solicitudDao;
import dao.clienteDao;

import model.entity.Solicitudes;
import model.entity.clientes;

import model.gestion.gestionSolicitudes;


import view.ingresos.ingresoSolicitudes;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import model.service.ManejadorSolicitud;
import model.service.analistaHandler;
import model.service.asesorHandler;
import model.service.gerenteHandler;
import view.menus.menuCliente;

public class controllerGestionSolicitud {

    private final solicitudDao dao = new solicitudDao();

    private final clienteDao daoCliente = new clienteDao();

    private final gestionSolicitudes gestion = new gestionSolicitudes();

    private final menuCliente menu = new menuCliente();

    private final ingresoSolicitudes ingreso = new ingresoSolicitudes();
    
    private final ManejadorSolicitud asesor= new asesorHandler();
    
    private final ManejadorSolicitud analista= new analistaHandler();
    
    private final ManejadorSolicitud gerente= new gerenteHandler();
   


    
    private void agregar() throws SQLException {

        System.out.println("\n===== REGISTRAR SOLICITUD =====");


        List<clientes> listaClientes = daoCliente.listar();

        if (listaClientes.isEmpty()) {

            System.out.println("No existen clientes registrados.");

            return;
        }

        System.out.println("\n===== CLIENTES DISPONIBLES =====");

        for (clientes cliente : listaClientes) {
            System.out.println(cliente);
        }


        long idCliente = ingreso.pedirIdCliente();


        clientes cliente = daoCliente.buscarId(idCliente);

        if (cliente == null) {

            System.out.println("No existe un cliente con ese ID.");

            return;
        }


        double monto = ingreso.pedirMonto();

        int cuotas;

        do {
         cuotas = ingreso.pedircuotas();

         if (cuotas <= 0) {
        System.out.println("El número de cuotas debe ser mayor que cero.");
         }

        } while (cuotas <= 0);


        Solicitudes nueva = new Solicitudes(
            0,
            monto,
            cuotas,
            cliente
        );

       asesor.setSiguiente(analista);
       analista.setSiguiente(gerente);
       
        boolean aprobada = asesor.manejar(nueva);

    if (aprobada) {

        gestion.sincronizar(nueva);

        System.out.println("Solicitud registrada correctamente.");
        System.out.println("ID generado: " + nueva.getId());
        bitacora.registrar("SOLICITUDES", "REGISTRAR", "ID " + nueva.getId() + " - Cliente: " + cliente.getNombre() + " - Monto: " + String.format("%.2f", monto) + " - Cuotas: " + cuotas);

    } else {

        System.out.println("La solicitud no fue registrada.");
        bitacora.registrar("SOLICITUDES", "SOLICITUD_RECHAZADA", "Cliente: " + cliente.getNombre() + " - Monto: " + String.format("%.2f", monto) + " - Cuotas: " + cuotas);
    }
    }


    private void listar() throws SQLException {

        List<Solicitudes> lista = dao.listar();


        gestion.sincronizarTodos(lista);

        if (lista.isEmpty()) {

            System.out.println("No existen solicitudes registradas.");

            return;
        }

        for (Solicitudes solicitud : lista) {

            System.out.println(solicitud);
        }
    }



    private void buscar() throws SQLException {

        long id = ingreso.pedirId();

        Solicitudes encontrada = dao.buscarId(id);

        if (encontrada == null) {

            System.out.println("No se encontró la solicitud.");

        } else {

            System.out.println(encontrada);
            bitacora.registrar("SOLICITUDES", "BUSCAR", "ID " + id);

            gestion.sincronizar(encontrada);
        }
    }



    private void eliminar() throws SQLException {

        long id = ingreso.pedirId();

        Solicitudes solicitud = dao.buscarId(id);

        if (solicitud == null) {

            System.out.println("La solicitud no existe.");

            return;
        }


        boolean eliminado = dao.eliminar(id);

        if (eliminado) {


            gestion.eliminar(id);
            bitacora.registrar("SOLICITUDES", "ELIMINAR", "ID " + id);

            System.out.println(
                "Solicitud eliminada de ambos sistemas."
            );

        } else {

            System.out.println("No se pudo eliminar la solicitud.");
        }
    }



    private void exportar() throws SQLException {

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle(
            "Guardar copia de seguridad de solicitudes"
        );

        selector.setSelectedFile(
            new File("backupSolicitudes.dat")
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

 
            List<Solicitudes> lista = dao.listar();

            gestion.sincronizarTodos(lista);


            gestion.exportarBackup(archivo.getAbsolutePath());
            bitacora.registrar("SOLICITUDES", "EXPORTAR", archivo.getName());

        } else {

            System.out.println("Exportación cancelada.");
        }
    }
    
    private void buscarPorCliente() throws SQLException {


    long idCliente = ingreso.pedirIdCliente();


    clientes cliente = daoCliente.buscarId(idCliente);

    if (cliente == null) {
        System.out.println("No existe un cliente con ese ID.");
        return;
    }


    List<Solicitudes> lista = dao.buscarPorCliente(idCliente);
    bitacora.registrar("SOLICITUDES", "BUSCAR_POR_CLIENTE", "Cliente ID " + idCliente);

    if (lista.isEmpty()) {
        System.out.println("Este cliente no tiene solicitudes registradas.");
        return;
    }

    System.out.println("\n===== SOLICITUDES DEL CLIENTE =====");

    for (Solicitudes s : lista) {

        System.out.println(s);

        gestion.sincronizar(s);
    }
}


    public void menu() throws SQLException {

        int op;

        do {

            op = menu.menuSolicitud();

            switch (op) {

                case 1 -> agregar();

                case 2 -> {
                    listar();
                    bitacora.registrar("SOLICITUDES", "LISTAR", null);
                }

                case 3 -> buscar();

                case 4 -> eliminar();

                case 5 -> exportar();
                
                case 6 -> buscarPorCliente();

                case 7 -> {
                    System.out.println(
                        "Regresando al menú principal..."
                    );
                    return;
                }

                default ->
                    System.out.println("Opción fuera de rango.");
            }

        } while (op!=8);
    }
}