/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.sql.SQLException;
import view.mensajes;

import view.menus.menuGeneral;

/**
 *
 * @author ALIENWARE AURORA
 */
public class controllerMenugeneral {
    private final menuGeneral menu = new menuGeneral();
    private final mensajes mensajes = new mensajes();
    private final controllerMenuEmpleados menuEmpleado = new controllerMenuEmpleados();
    private final controllerMenuCliente menuCliente= new controllerMenuCliente();
    private final controllerLogin login = new controllerLogin();
    
    public void gestionarMenu() throws SQLException {
        int op;
        do {
            op = menu.menu();
            switch (op) {
                case 1 -> {
                    if (login.ingresarEmpleado()) {
                        try {
                            menuEmpleado.menu();
                        } finally {
                            login.cerrarSesion();
                        }
                    }
                }
                case 2 -> {
                    if (login.ingresarCliente()) {
                        try {
                            menuCliente.menu();
                        } finally {
                            login.cerrarSesion();
                        }
                    }
                }
                case 3 -> {
                    mensajes.informativo("Gracias por usar nuestra aplicacion!");
                }
                default -> {
                    mensajes.error("Opcion no encontrada!");
                }
            }
        } while (op != 3);
    }
}
