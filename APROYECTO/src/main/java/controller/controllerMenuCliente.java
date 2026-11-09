/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.sql.SQLException;
import model.service.bitacora;
import view.mensajes;
import view.menus.menuCliente;

/**
 *
 * @author ALIENWARE AURORA
 */
public class controllerMenuCliente {
    private final menuCliente menu = new menuCliente();
    private final mensajes mensajes = new mensajes();
    private final controllerGestionSolicitud solicitud= new controllerGestionSolicitud();
    private final controllerGestionPrestamo prestamo= new controllerGestionPrestamo();
    private final controllerGestionPago pago= new controllerGestionPago();

    public void menu() throws SQLException {
        int op;
        do {
            op = menu.menuCliente();
            switch (op) {
                case 1 -> {
                    solicitud.menu();
                }
                case 2 -> {
                    pago.menu();
                }
                case 3 -> {
                    prestamo.listar();
                    bitacora.registrar("PRESTAMOS", "LISTAR", null);
                }
                    
                case 4 -> {
                    mensajes.informativo("Regresando al menu principal...\n\n\n\n");
                    return;
                }
                default -> {
                    mensajes.error("La opcion esta fuera de rango");
                }
            }
        } while (op!=4);
    }
}
