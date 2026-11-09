package controller;

import dao.clienteDao;
import dao.empleadoDao;
import model.entity.clientes;
import model.entity.empleados;
import model.entity.sesion;
import model.gestion.gestionPersonas;
import model.service.bitacora;
import model.service.seguridad;
import view.ingresos.ingresoEmpleados;
import view.mensajes;
import view.validaciones;

import java.sql.SQLException;
import java.util.List;


public class controllerLogin {

    private final empleadoDao daoEmpleado = new empleadoDao();
    private final clienteDao daoCliente = new clienteDao();
    private final gestionPersonas gestion = new gestionPersonas();
    private final ingresoEmpleados ingresoEmpleado = new ingresoEmpleados();
    private final validaciones v = new validaciones();
    private final mensajes m = new mensajes();


    public boolean ingresarEmpleado() throws SQLException {

        List<empleados> admins = daoEmpleado.buscarPorRol("ADMINISTRADOR");

        if (admins.isEmpty()) {
            return crearPrimerAdministrador();
        }

        m.informativo("INICIO DE SESION - EMPLEADOS");
        System.out.println("Administradores registrados:");

        for (empleados e : admins) {
            System.out.printf("  ID %-5d %s%n", e.getId(), e.getNombre());
        }

        while (true) {

            long id = v.validarEnteroGrande(
                    "\nIngrese su ID (0 para volver):");

            if (id == 0) {
                return false;
            }

            empleados elegido = null;

            for (empleados e : admins) {
                if (e.getId() == id) {
                    elegido = e;
                    break;
                }
            }

            if (elegido == null) {
                m.error("Ese ID no corresponde a un administrador de la lista.");
                continue;
            }

            if (pedirClave(elegido)) {
                abrirSesionEmpleado(elegido);
                return true;
            }

            m.error("Demasiados intentos fallidos. Regresando al menu principal.");
            return false;
        }
    }

    private boolean pedirClave(empleados elegido) {

        for (int intento = 1; intento <= seguridad.MAX_INTENTOS; intento++) {

            String clave = v.validarClave("Ingrese la clave general:");

            if (seguridad.claveCorrecta(clave)) {
                return true;
            }

            bitacora.registrarComo(elegido.getId(), elegido.getNombre(),
                    sesion.EMPLEADO, "SESION", "CLAVE_INCORRECTA",
                    "Intento " + intento + " de " + seguridad.MAX_INTENTOS);

            m.error("Clave incorrecta. Intento " + intento
                    + " de " + seguridad.MAX_INTENTOS + ".");
        }

        return false;
    }

    private void abrirSesionEmpleado(empleados e) {

        sesion.iniciar(e.getId(), e.getNombre(), sesion.EMPLEADO, e.getRol());
        bitacora.registrar("SESION", "INICIO_SESION", "Rol: " + e.getRol());
        m.exito("Bienvenido(a), " + e.getNombre() + " (" + e.getRol() + ")");
    }

    private boolean crearPrimerAdministrador() throws SQLException {

        m.informativo("NO HAY ADMINISTRADORES REGISTRADOS");
        System.out.println("Para crear el primer administrador ingrese la clave general.");

        boolean autorizado = false;

        for (int i = 1; i <= seguridad.MAX_INTENTOS && !autorizado; i++) {
            autorizado = seguridad.claveCorrecta(
                    v.validarClave("Ingrese la clave general:"));

            if (!autorizado) {
                m.error("Clave incorrecta.");
            }
        }

        if (!autorizado) {
            return false;
        }

        String nombre = ingresoEmpleado.pedirNombre();
        String documento = ingresoEmpleado.pedirDocumento();
        String correo = ingresoEmpleado.pedirCorreo();
        double salario = ingresoEmpleado.pedirSalario();

        empleados nuevo = daoEmpleado.agregar(
                new empleados("ADMINISTRADOR", salario, 0, nombre, documento, correo));

        gestion.sincronizar(nuevo);

        abrirSesionEmpleado(nuevo);
        bitacora.registrar("EMPLEADOS", "REGISTRAR",
                "Primer administrador creado: ID " + nuevo.getId()
                + " - " + nuevo.getNombre());

        return true;
    }


    public boolean ingresarCliente() throws SQLException {

        List<clientes> lista = daoCliente.listar();

        if (lista.isEmpty()) {
            m.error("No hay clientes registrados. Un administrador debe registrarlos primero.");
            return false;
        }

        m.informativo("INICIO DE SESION - CLIENTES");
        System.out.println("Clientes registrados:");

        for (clientes c : lista) {
            System.out.printf("  ID %-5d %s%n", c.getId(), c.getNombre());
        }

        while (true) {

            long id = v.validarEnteroGrande(
                    "\nIngrese su ID (0 para volver):");

            if (id == 0) {
                return false;
            }

            for (clientes c : lista) {

                if (c.getId() == id) {

                    sesion.iniciar(c.getId(), c.getNombre(), sesion.CLIENTE, null);
                    bitacora.registrar("SESION", "INICIO_SESION", null);
                    m.exito("Bienvenido(a), " + c.getNombre());
                    return true;
                }
            }

            m.error("Ese ID no corresponde a ningun cliente de la lista.");
        }
    }


    public void cerrarSesion() {

        if (sesion.activa()) {
            bitacora.registrar("SESION", "CIERRE_SESION", null);
            sesion.cerrar();
        }
    }
}
