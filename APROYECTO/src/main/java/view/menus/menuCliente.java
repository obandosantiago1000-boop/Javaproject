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
public class menuCliente {
             validaciones v = new validaciones();
             mensajes m= new mensajes();
             
        public int menuCliente() {
        return v.validarEntero("""
                               BIENVENIDO A NUESTRO SISTEMA
                                        BANCO
                               Elija la funcion que necesite:
                               1- Solicitud.
                               2- Pagos.
                               3- Listar Prestamos.
                               4- Salir
                               """);
        
        
        
    }
        public int menuPagos() {
        m.informativo("MODULO PAGOS");
        return v.validarEntero("""
                               
                               Elija la funcion que necesite:
                               1- Registrar pago a prestamo
                               2- Listar pagos segun cliente
                               3- Salir
                               """);
    }
        
        
        public int menuSolicitud() {
        m.informativo("MODULO SOLICITUD");
        return v.validarEntero("""
                               
                               Elija la funcion que necesite:
                               1- Registrar solicitud
                               2- Listar solicitudes 
                               3- Buscar solicitudes
                               4- Eliminar solicitudes
                               5- Exportar solicitudes
                               6- Buscar solicitud por cliente
                               7- Salir
                               """);
    }
        
        
}
