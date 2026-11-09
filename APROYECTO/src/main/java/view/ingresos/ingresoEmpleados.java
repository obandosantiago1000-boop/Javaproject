/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.ingresos;

import model.entity.rol;
import view.validaciones;

/**
 *
 * @author ALIENWARE AURORA
 */
public class ingresoEmpleados {
    validaciones v = new validaciones();

    public String pedirNombre() {
        return v.validarTexto("Ingrese el nombre de la persona");
    }

    public String pedirRol() {
        return rol.validarEnum();
    }

    public String pedirDocumento() {
        return v.validarCadenas("Ingrese el documento");
    }

    public double pedirSalario() {
        return v.validarDecimal("Ingrese el salario");
    }

    public Long pedirId() {
        return v.validarEnteroGrande("Ingrese el id:");
    }

    public String pedirCorreo() {
        return v.validarCorreo("Ingrese el correo");
    }
}
