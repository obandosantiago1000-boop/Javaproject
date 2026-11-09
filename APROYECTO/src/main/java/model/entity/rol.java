/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;




import view.validaciones;

public enum rol {
    ADMINISTRADOR,
    EMPLEADO;

    public static String validarEnum() {
        validaciones v = new validaciones();
        String dato = null;
        do {
            try {
                String opciones = "";
                for (rol valor : values()) {
                    opciones += valor + "\n";
                }
                dato = v.validarTexto("Ingrese el rol del empleado \n" + opciones);
                rol.valueOf(dato);
                break;
            } catch (Exception e) {
            }
            System.out.println("Error, intente nuevamente!");
        } while (true);
        return dato;
    }
}
