/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.menus;

import view.validaciones;

/**
 *
 * @author ALIENWARE AURORA
 */
public class menuGeneral {
    validaciones v = new validaciones();

    public int menu() {
        return v.validarEntero("""
                               BIENVENIDO A NUESTRO SISTEMA
                                        BANCO
                               Elija su metodo de acceso:
                               1- Empleado.
                               2- Cliente.
                               3- Salir
                               """);
    }
}
