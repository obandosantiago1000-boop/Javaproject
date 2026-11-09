package controller;

import model.service.bitacora;

import dao.clienteDao;
import dao.prestamoDao;
import model.entity.clientes;
import model.gestion.gestionPersonas;
import view.menus.menuAdmin;

import java.sql.SQLException;
import java.util.List;

import view.ingresos.ingresoClientes;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import model.entity.prestamos;

public class controllerGestionCliente {
    
    private final prestamoDao daoPrestamo = new prestamoDao();
    
    private final clienteDao dao = new clienteDao();

    private final gestionPersonas gestion = new gestionPersonas();

    private final menuAdmin menu = new menuAdmin();

    private final ingresoClientes ingreso = new ingresoClientes();

    private void agregar() throws SQLException {

        System.out.println("\n===== REGISTRAR CLIENTE =====");

        String nombre = ingreso.pedirNombre();

        String documento = ingreso.pedirDocumento();

        String correo = ingreso.pedirCorreo();

        String telefono = ingreso.pedirTelefono();

        clientes nuevo = new clientes(
            telefono, 0, nombre, documento, correo
        );


        clientes registrado = dao.agregar(nuevo);


        gestion.sincronizar(registrado);

        System.out.println("Cliente registrado en MySQL y archivo.");
        System.out.println("ID generado: " + registrado.getId());
        bitacora.registrar("CLIENTES", "REGISTRAR", "ID " + registrado.getId() + " - " + registrado.getNombre());
    }



    private void listar() throws SQLException {

        List<clientes> lista = dao.listar();

        if (lista.isEmpty()) {
            System.out.println("No existen clientes registrados.");
            return;
        }

        for (clientes cliente : lista) {

            System.out.println(cliente);


            gestion.sincronizar(cliente);
        }
    }
    
    

private void mostrarPrestamos() throws SQLException {


    List<clientes> clientes = dao.listar();

    if (clientes.isEmpty()) {
        System.out.println("No existen clientes registrados.");
        return;
    }

    System.out.println("\n===== CLIENTES REGISTRADOS =====");

    for (clientes c : clientes) {
        System.out.println(c);
    }


    System.out.println("\nIngrese el ID del cliente:");
    long idCliente = ingreso.pedirId();


    clientes cliente = dao.buscarId(idCliente);

    if (cliente == null) {
        System.out.println("El cliente no existe.");
        return;
    }

    List<prestamos> lista = daoPrestamo.buscarPorCliente(idCliente);
    bitacora.registrar("CLIENTES", "CONSULTAR_PRESTAMOS", "Cliente ID " + idCliente);

    if (lista.isEmpty()) {
        System.out.println("Este cliente no tiene préstamos registrados.");
        return;
    }

    System.out.println("\n===== PRÉSTAMOS DEL CLIENTE =====");

    for (prestamos p : lista) {
        System.out.println(p);
    }
}



    private void buscar() throws SQLException {

        long id = ingreso.pedirId();

        clientes encontrado = dao.buscarId(id);

        if (encontrado == null) {

            System.out.println("No se encontró el cliente.");

        } else {

            System.out.println(encontrado);
            bitacora.registrar("CLIENTES", "BUSCAR", "ID " + id);


            gestion.sincronizar(encontrado);
        }
    }



    private void eliminar() throws SQLException {

        long id = ingreso.pedirId();

        clientes cliente = dao.buscarId(id);

        if (cliente == null) {

            System.out.println("El cliente no existe.");
            return;
        }
        
        
        if (dao.tieneSolicitudes(id)) {
        System.out.println("NO SE PUEDE ELIMINAR EL CLIENTE.");
        System.out.println("El cliente tiene solicitudes registradas.");
        return;
    }



        boolean eliminado = dao.eliminar(id);

        if (eliminado) {


            gestion.eliminar(id);

            System.out.println("Cliente eliminado de ambos sistemas.");
            bitacora.registrar("CLIENTES", "ELIMINAR", "ID " + id + " - " + cliente.getNombre());

        } else {

            System.out.println("No se pudo eliminar el cliente.");
        }
    }



    private void actualizar() throws SQLException {

        System.out.println("=============== MÓDULO ACTUALIZAR CLIENTE ===============");

        List<clientes> lista = dao.listar();

        if (lista.isEmpty()) {

            System.out.println("No existen clientes registrados.");
            return;
        }

        for (clientes cliente : lista) {
            System.out.println(cliente);
        }

        long id = ingreso.pedirId();

        clientes clienteOld = dao.buscarId(id);

        if (clienteOld == null) {

            System.out.println("No se encontró el cliente que desea actualizar.");

        } else {

            actualizarDatos(clienteOld);
        }
    }



    private void actualizarDatos(clientes cliente) throws SQLException {

        boolean val = false;

        switch (menu.opcionesActualizarCliente()) {

            case 1 -> {
                cliente.setNombre(ingreso.pedirNombre());
                val = true;
            }

            case 2 -> {
                cliente.setDocumento(ingreso.pedirDocumento());
                val = true;
            }

            case 3 -> {
                cliente.setCorreo(ingreso.pedirCorreo());
                val = true;
            }

            case 4 -> {
                cliente.setTelefono(ingreso.pedirTelefono());
                val = true;
            }

            case 5 -> {
                System.out.println("Operación cancelada.");
            }

            default -> {
                System.out.println("Opción fuera de rango.");
            }
        }

        if (val) {

            if (dao.actualizar(cliente)) {

                gestion.sincronizar(cliente);

                System.out.println("Cliente actualizado correctamente.");
                bitacora.registrar("CLIENTES", "ACTUALIZAR", "ID " + cliente.getId() + " - " + cliente.getNombre());

            } else {

                System.out.println("No se pudo actualizar el cliente.");
            }
        }
    }


  



    private void exportar() {

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle("Guardar copia de seguridad de clientes");

        selector.setSelectedFile(new File("backupClientes.dat"));

        selector.setFileFilter(
            new FileNameExtensionFilter("Archivos DAT (*.dat)", "dat")
        );

        int resultado = selector.showSaveDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {

            File archivo = selector.getSelectedFile();


            if (!archivo.getName().toLowerCase().endsWith(".dat")) {

                archivo = new File(
                    archivo.getAbsolutePath() + ".dat"
                );
            }

            gestion.exportarBackup(archivo.getAbsolutePath());
            bitacora.registrar("CLIENTES", "EXPORTAR", archivo.getName());

        } else {

            System.out.println("Exportación cancelada.");
        }
    }



    public void menu() throws SQLException {

        int op;

        do {

            op = menu.menuClientes();

            switch (op) {

                case 1 -> agregar();

                case 2 -> {
                    listar();
                    bitacora.registrar("CLIENTES", "LISTAR", null);
                }

                case 3 -> buscar();

                case 4 -> actualizar();

                case 5 -> eliminar();

                case 6 ->  mostrarPrestamos();

                case 7 -> exportar();

                case 8 -> {
                    System.out.println("Regresando al menú principal...");
                    return;
                }

                default ->
                    System.out.println("Opción fuera de rango.");
            }

        } while (op!=8);
    }
}
