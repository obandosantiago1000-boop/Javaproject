/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.sql.SQLException;
import view.mensajes;
import view.menus.menuAdmin;


public class controllerMenuEmpleados {
    
    private final menuAdmin menu = new menuAdmin();
    private final mensajes mensajes = new mensajes();
    private final controllerGestionEmpleado menuEmpleado= new controllerGestionEmpleado();
    private final controllerGestionCliente menuCliente= new controllerGestionCliente();
    private final controllerGestionPrestamo menuPrestamo= new controllerGestionPrestamo();
    private final controllerGestionPago menuPago = new controllerGestionPago();
    private final controllerReportes menuReportes = new controllerReportes();
    public void menu() throws SQLException {
        int op;
        do {
            op = menu.menuAdmin();
            switch (op) {
                case 1 -> {
                    menuEmpleado.menu();
                }
                case 2 -> {
                    menuCliente.menu();
                }
                case 3 -> {
                    menuPrestamo.menu();
                }
                case 4 -> {
                    menuPago.menuConsulta();
                }
                case 5 -> {
                    menuReportes.menu();
                }

                case 6 -> {
                    mensajes.informativo("Regresando al menu principal...\n\n\n\n");
                    return;
                }
                default -> {
                    mensajes.error("La opcion esta fuera de rango");
                }
            }
        } while (op != 6);
    }
}
