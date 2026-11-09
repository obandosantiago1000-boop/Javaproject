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
public class ingresoClientes {
    validaciones v = new validaciones();

    public String pedirNombre() {
        return v.validarTexto("Ingrese el nombre de la persona");
    }


    public String pedirDocumento() {
        return v.validarCadenas("Ingrese el documento");
    }

    public String pedirTelefono() {
        return v.validarCadenas("Ingrese el telefono");
    }

    public Long pedirId() {
        return v.validarEnteroGrande("Ingrese el id:");
    }

    public String pedirCorreo() {
        return v.validarCorreo("Ingrese el correo");
    }
    }

