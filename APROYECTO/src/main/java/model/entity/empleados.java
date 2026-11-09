/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

/**
 *
 * @author ALIENWARE AURORA
 */
public class empleados extends personas{
    
    private String rol;
    private double salario;

    public empleados(String rol, double salario, long id, String nombre, String documento, String correo) {
        super(id, nombre, documento, correo);
        this.rol = rol;
        this.salario = salario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

     @Override
    public String toString() {

        return super.toString() +
                "\nROL: " + rol +
                "\nSALARIO: " + String.format("%.2f", salario);
    }
    
    
    
}
