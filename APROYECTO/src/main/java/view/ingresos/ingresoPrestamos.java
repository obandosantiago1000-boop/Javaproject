/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.ingresos;

import view.validaciones;

/**
 *
 * @author ALIENWARE AURORA
 */
public class ingresoPrestamos {
    validaciones v = new validaciones();



    public double pedirInteres() {
        return v.validarDecimal("Ingrese el valor del interes");
    }

    public Long pedirId() {
        return v.validarEnteroGrande("Ingrese el id:");
    }
}
