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
public class ingresoSolicitudes {
    validaciones v = new validaciones();



    public double pedirMonto() {
        return v.validarDecimal("Ingrese el monto");
    }

    public Long pedirId() {
        return v.validarEnteroGrande("Ingrese el id:");
    }

    public int pedircuotas() {
        return v.validarEntero("Ingrese las cuotas");
    }
    
        public Long pedirIdCliente() {
        return v.validarEnteroGrande("Ingrese el id:");
    }
}
