/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

/**
 *
 * @author ALIENWARE AURORA
 */
public class clientes extends personas{
    
   private String telefono;

    public clientes(String telefono, long id, String nombre, String documento, String correo) {
        super(id, nombre, documento, correo);
        this.telefono = telefono;
    }

     @Override
    public String toString() {

        return super.toString() +
                "\nTELEFONO: " + telefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
   
    
    
}
