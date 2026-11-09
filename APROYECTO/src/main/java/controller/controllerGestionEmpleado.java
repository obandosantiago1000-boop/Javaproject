
package controller;

import model.service.bitacora;
import model.entity.sesion;

import dao.empleadoDao;
import model.entity.empleados;
import model.gestion.gestionPersonas;
import view.menus.menuAdmin;
import java.sql.SQLException;
import java.util.List;
import view.ingresos.ingresoEmpleados;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;


public class controllerGestionEmpleado {

    private final empleadoDao dao = new empleadoDao();
    private final gestionPersonas gestion = new gestionPersonas();
    private final menuAdmin menu= new menuAdmin();
    private final ingresoEmpleados ingreso = new ingresoEmpleados();
    private final Scanner sc = new Scanner(System.in);




    private void agregar() throws SQLException {

        System.out.println("\n===== REGISTRAR EMPLEADO =====");

        String nombre = ingreso.pedirNombre();

        String documento = ingreso.pedirDocumento();

        String correo = ingreso.pedirCorreo();

        String rol = ingreso.pedirRol();

        double salario = ingreso.pedirSalario();

        empleados nuevo = new empleados(
            rol, salario, 0, nombre, documento, correo
        );


        empleados registrado = dao.agregar(nuevo);


        gestion.sincronizar(registrado);

        System.out.println("Empleado registrado en MySQL y archivo.");
        System.out.println("ID generado: " + registrado.getId());
        bitacora.registrar("EMPLEADOS", "REGISTRAR", "ID " + registrado.getId() + " - " + registrado.getNombre() + " (" + registrado.getRol() + ")");
    }


    private void listar() throws SQLException {

        List<empleados> lista = dao.listar();

        if (lista.isEmpty()) {
            System.out.println("No existen empleados registrados.");
            return;
        }

        for (empleados e : lista) {

            System.out.println(e);


            gestion.sincronizar(e);
        }
    }

    private void buscar() throws SQLException {

        long id = ingreso.pedirId();

        empleados encontrado = dao.buscarId(id);

        if (encontrado == null) {
            System.out.println("No se encontró el empleado.");
        } else {

            System.out.println(encontrado);
            bitacora.registrar("EMPLEADOS", "BUSCAR", "ID " + id);


            gestion.sincronizar(encontrado);
        }
    }

   

    private void eliminar() throws SQLException {

        long id = ingreso.pedirId();

        if (id == sesion.getId()) {
            System.out.println("No puede eliminar su propia cuenta mientras tiene la sesion abierta.");
            return;
        }

        empleados empleado = dao.buscarId(id);

        if (empleado == null) {
            System.out.println("El empleado no existe.");
            return;
        }


            boolean eliminado = dao.eliminar(id);

            if (eliminado) {


                gestion.eliminar(id);

                System.out.println("Empleado eliminado de ambos sistemas.");
                bitacora.registrar("EMPLEADOS", "ELIMINAR", "ID " + id + " - " + empleado.getNombre());

            } else {
                System.out.println("No se pudo eliminar el empleado.");
            }


    }
    
    
    

private void actualizar() throws SQLException {

    System.out.println("=============== MÓDULO ACTUALIZAR EMPLEADO ===============");

    List<empleados> lista = dao.listar();

    if (lista.isEmpty()) {
        System.out.println("No existen empleados registrados.");
        return;
    }

    for (empleados e : lista) {
        System.out.println(e);
    }

    long id = ingreso.pedirId();

    empleados empleadoOld = dao.buscarId(id);

    if (empleadoOld == null) {

        System.out.println("No se encontró el empleado que desea actualizar.");

    } else {

        actualizarDatos(empleadoOld);
    }
}
    

private void actualizarDatos(empleados e) throws SQLException {

    boolean val = false;

    switch (menu.opcionesActualizarEmpleado()) {

        case 1 -> {
            e.setNombre(ingreso.pedirNombre());
            val = true;
        }

        case 2 -> {
            e.setDocumento(ingreso.pedirDocumento());
            val = true;
        }

        case 3 -> {
            e.setCorreo(ingreso.pedirCorreo());
            val = true;
        }

        case 4 -> {
            e.setRol(ingreso.pedirRol());
            val = true;
        }

        case 5 -> {
            e.setSalario(ingreso.pedirSalario());
            val = true;
        }

        case 6 -> {
            System.out.println("Operación cancelada.");
        }

        default -> {
            System.out.println("Opción fuera de rango.");
        }
    }

    if (val) {

        if (dao.actualizar(e)) {

            gestion.sincronizar(e);

            System.out.println("Empleado actualizado correctamente.");
            bitacora.registrar("EMPLEADOS", "ACTUALIZAR", "ID " + e.getId() + " - " + e.getNombre());

        } else {
            System.out.println("No se pudo actualizar el empleado.");
        }
    }
}


    private void buscarPorRol() throws SQLException {

        String rol = ingreso.pedirRol();

        List<empleados> lista = dao.buscarPorRol(rol);

        if (lista.isEmpty()) {
            System.out.println("No existen empleados con ese rol.");
            return;
        }

        for (empleados e : lista) {

            System.out.println(e);


            gestion.sincronizar(e);
        }
    }

private void exportar() {

    JFileChooser selector = new JFileChooser();

    selector.setDialogTitle("Guardar copia de seguridad");

    selector.setSelectedFile(new File("backupEmpleados.dat"));

    selector.setFileFilter(
        new FileNameExtensionFilter("Archivos DAT (*.dat)", "dat")
    );

    int resultado = selector.showSaveDialog(null);

    if (resultado == JFileChooser.APPROVE_OPTION) {

        File archivo = selector.getSelectedFile();


        if (!archivo.getName().toLowerCase().endsWith(".dat")) {
            archivo = new File(archivo.getAbsolutePath() + ".dat");
        }

        gestion.exportarBackup(archivo.getAbsolutePath());
        bitacora.registrar("EMPLEADOS", "EXPORTAR", archivo.getName());

    } else {
        System.out.println("Exportación cancelada.");
    }
}



    @SuppressWarnings("empty-statement")
    public void menu() throws SQLException {

        int op;

        do {



                op = menu.menuEmpleados();

                switch (op) {

                    case 1 -> agregar();

                    case 2 -> {
                        listar();
                        bitacora.registrar("EMPLEADOS", "LISTAR", null);
                    }

                    case 3 -> buscar();

                    case 4 -> actualizar();

                    case 5 -> eliminar();

                    case 6 -> {
                        buscarPorRol();
                        bitacora.registrar("EMPLEADOS", "BUSCAR_POR_ROL", null);
                    }

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
        
