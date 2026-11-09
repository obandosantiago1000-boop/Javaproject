/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.menus;

import view.mensajes;
import view.validaciones;

/**
 *
 * @author ALIENWARE AURORA
 */
public class menuAdmin {
    validaciones v = new validaciones();
    mensajes m= new mensajes();

    public int menuAdmin() {
        return v.validarEntero("""
                               BIENVENIDO A NUESTRO SISTEMA
                                        BANCO
                               Elija la funcion que necesite:
                               1- Empleados.
                               2- Clientes.
                               3- Prestamos.
                               4- Pagos.
                               5- Reportes.
                               6- Salir
                               """);
    }
    
    
    public int menuEmpleados() {
        m.informativo("MODULO EMPLEADOS");
        return v.validarEntero("""
                               
                               Elija la funcion que necesite:
                               1- Registrar 
                               2- Listar 
                               3- Buscar 
                               4- Actualizar
                               5- Eliminar
                               6- Buscar por rol
                               7- Exportar empleados
                               8- Salir
                               """);
    }
    
    public int menuClientes() {
        m.informativo("MODULO CLIENTES");
        return v.validarEntero("""
                               
                               Elija la funcion que necesite:
                               1- Registrar 
                               2- Listar 
                               3- Buscar 
                               4- Actualizar
                               5- Eliminar
                               6- Buscar prestamos por cliente
                               7- Exportar clietes
                               8- Salir
                               """);
    }
    
        public int menuPrestamos() {
        m.informativo("MODULO PRESTAMOS");
        return v.validarEntero("""
                               
                               Elija la funcion que necesite:
                               1- Registrar 
                               2- Listar Prestamos
                               3- Buscar 
                               4- Listar Solicitudes
                               5- Exportar Prestamos
                               6- Ver cuotas de un prestamo
                               7- Salir
                               """);
    }

        public int menuPagos() {
        m.informativo("MODULO PAGOS");
        return v.validarEntero("""
                               
                               Elija la funcion que necesite:
                               1- Pagar cuota
                               2- Abonar dinero
                               3- Historial de pagos
                               4- Salir
                               """);
    }
        
        
        public int menuPagosConsulta() {
        m.informativo("MODULO PAGOS - CONSULTA");
        return v.validarEntero("""
                               
                               Elija la funcion que necesite:
                               1- Historial de pagos
                               2- Salir
                               """);
    }


       public int menuReportes() {
        m.informativo("MODULO REPORTES");
        return v.validarEntero("""
                               
                               Elija la funcion que necesite:
                               1- Acciones de los empleados
                               2- Acciones de los clientes
                               3- Acciones por modulo
                               4- Ultimos movimientos del sistema
                               5- Reporte general de empleados
                               6- Reporte general de clientes
                               7- Reporte general de solicitudes
                               8- Reporte general de prestamos
                               9- Reporte general de pagos y cuotas
                               10- Resumen general del sistema
                               11- Salir
                               """);
    }
    
    public int opcionesActualizarEmpleado() {

    m.informativo("ACTUALIZAR EMPLEADO");

    return v.validarEntero("""

                           ¿Qué dato desea actualizar?

                           1. Nombre
                           2. Documento
                           3. Correo
                           4. Rol
                           5. Salario
                           6. Cancelar

                           """);
}
    public int opcionesActualizarCliente() {

    m.informativo("ACTUALIZAR CLIENTE");

    return v.validarEntero("""
        ¿Qué dato desea actualizar?
        1. Nombre
        2. Documento
        3. Correo
        4. Teléfono
        5. Cancelar
        """);
}
    
}
